import logging
import os
import threading

from printing import PhysicalPrinter, PrintQueue, PrinterDriverFactory, PrintMetadata, Toner


# This is a class you'd like to get under test so you can change it safely.
class DiagramPhysicalPrinter:
    def __init__(self, physical_printer: PhysicalPrinter = None, print_queue: PrintQueue = None):
        self._physical_printer = physical_printer or PhysicalPrinter()
        self._print_queue = print_queue or PrintQueue(self._physical_printer)
        self._logger = logging.getLogger("DiagramPhysicalPrinter")

    def do_print(self, printable_diagram, info, target_filename):
        printer_driver = PrinterDriverFactory.get_instance().create_driver_for_print()
        printer_driver.set_diagram(printable_diagram.diagram)

        data = PrintMetadata(info.file_type)
        mutex = threading.Lock()
        success = False

        with ((mutex)):
            if not self._physical_printer.is_available or not (self._physical_printer.toner_levels[Toner.Black] > 0 and
                                                               self._physical_printer.toner_levels[Toner.Cyan] > 0 and
                                                               self._physical_printer.toner_levels[Toner.Magenta] > 0 and
                                                               self._physical_printer.toner_levels[Toner.Yellow] > 0):
                self._logger.info("Physical Printer Unavailable")
            elif self.job_count_inconsistent():
                self._logger.info("Physical Printer Unavailable Due to Job Count Inconsistency")
            else:
                success = self.print_to_physical_printer(data, printable_diagram, printer_driver)

                if success:
                    self.save_backup_pdf(data, printable_diagram, target_filename)

        printer_driver.release_diagram()
        return success

    def save_backup_pdf(self, data, printable_diagram, target_filename):
        if os.path.exists(data.filename):
            self._logger.info(f"Saving backup of printed document as PDF to file {target_filename}")
            printable_diagram.print_to_file(data.filename, target_filename)

    def print_to_physical_printer(self, data, printable_diagram, printer_driver):
        success = False
        self._print_queue.add(data)
        summary_information = printable_diagram.summary_information()
        self._logger.info(f"Diagram Summary Information {summary_information}")
        is_summary = len(summary_information) > 10
        if self._physical_printer.start_document(not is_summary, False, "DiagramPhysicalPrinter"):
            if printer_driver.print_to(self._physical_printer):
                self._logger.info("Physical Printer Successfully printed")
                success = True
            self._physical_printer.end_document()
        return success

    def job_count_inconsistent(self):
        return self._physical_printer.job_count < 0
