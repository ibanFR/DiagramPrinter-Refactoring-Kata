import os
import threading
import logging

from documents import DiagramSummary, PrintableDiagram
from printing import *

class DiagramPrinter:
    SPREADSHEET = "Spreadsheet"
    PDF = "PDF"

    def __init__(self):
        self._logger = logging.getLogger("DiagramPrinter")

    def print_summary(self, diagram, language):
        if diagram is None:
            return False, ""

        summary = DiagramSummary(language)
        summary.add_title(diagram.name(), diagram.serial_number())
        summary.add_header(diagram.summary_information())
        summary.add_image(diagram.flowchart_thumbnail())
        summary_text = summary.export()
        return True, summary_text

    def print_diagram(self, diagram, folder=None, filename=None):
        if diagram is None:
            return False

        printable_diagram = PrintableDiagram(diagram)
        return self._print_diagram(printable_diagram, folder, filename)

    def _print_diagram(self, printable_diagram, folder, filename):
        info = printable_diagram.get_diagram_metadata()
        target_filename = self._get_target_filename(folder, filename)

        if self.is_pdf(info):
            return self.print_pdf(info, printable_diagram, target_filename)

        if self.is_spreadsheet(info):
            return self.print_spreadsheet(info, printable_diagram, target_filename)

        diagram_physical_printer = DiagramPhysicalPrinter()
        return diagram_physical_printer.do_print(printable_diagram, info, target_filename)

    def print_spreadsheet(self, info, printable_diagram, target_filename):
        if not target_filename.endswith(".xls"):
            target_filename += ".xls"
        self._logger.info(f"Printing Excel to file {target_filename}")
        return printable_diagram.print_to_file(info.full_filename, target_filename)

    def is_spreadsheet(self, info):
        return info.file_type == self.SPREADSHEET

    def print_pdf(self, info, printable_diagram, target_filename):
        self._logger.info(f"Printing PDF to file {target_filename}")
        return printable_diagram.print_to_file(info.full_filename, target_filename)

    def is_pdf(self, info):
        return info.file_type == self.PDF

    @staticmethod
    def _get_target_filename(folder, filename):
        folder = folder or os.path.expanduser("~")
        filename = filename or "tempfile.tmp"
        return os.path.join(folder, filename)

class DiagramPhysicalPrinter:
    def __init__(self, physical_printer=None, print_queue=None):
        self._physical_printer = physical_printer or PhysicalPrinter()
        self._print_queue = print_queue or PrintQueue(self._physical_printer)
        self._logger = logging.getLogger("DiagramPhysicalPrinter")

    def do_print(self, printable_diagram, info, target_filename):
        printer_driver = PrinterDriverFactory.get_instance().create_driver_for_print()
        printer_driver.set_diagram(printable_diagram.diagram)

        data = PrintMetadata(info.file_type)
        mutex = threading.Lock()
        success = False

        with mutex:
            if not self._physical_printer.is_available:
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
