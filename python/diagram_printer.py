import os
import logging

from documents import DiagramSummary, FlowchartDiagram, PrintableDiagram
from physical_printer import DiagramPhysicalPrinter


# This is a class you'd like to get under test so you can change it safely.
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

    def print_diagram(self, diagram: FlowchartDiagram, folder: str = None, filename: str = None):
        if diagram is None:
            return False

        printable_diagram = PrintableDiagram(diagram)
        return self._print_diagram(printable_diagram, folder, filename)

    def _print_diagram(self, printable_diagram: PrintableDiagram, folder, filename):
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


