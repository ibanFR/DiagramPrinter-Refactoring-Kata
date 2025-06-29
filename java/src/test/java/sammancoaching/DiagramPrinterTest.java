package sammancoaching;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DiagramPrinterTest {

    @Test
    void translatingEmptyDocumentFails() {
        DiagramPrinter printer = new DiagramPrinter();
        StringBuilder output = new StringBuilder();
        FlowchartDiagram diagram = null;
        boolean result = printer.printSummary(diagram, "swedish", output);
        assertFalse(result);
    }

    @Test
    void shouldPrintSummaryForValidDiagram(){
        DiagramPrinter printer = new DiagramPrinter();
        StringBuilder output = new StringBuilder();
        PrintableDiagram diagram = new FakeDiagram("name", "SN001", new StringBuilder());
        boolean result = printer.printSummary(diagram, "swedish", output);
        assertTrue(result);
    }

    @Test
    void printingEmptyDocumentFails() throws IOException {
        DiagramPrinter printer = new DiagramPrinter();
        FlowchartDiagram diagram = null;
        boolean result = printer.printDiagram(diagram, null, null);
        assertFalse(result);
    }

    @Test
    void shouldPrintPDFDiagram() throws IOException {
        StringBuilder spy = new StringBuilder();
        PrintableDiagram diagram = new FakeDiagram("Flowchart","SN001",spy);
        boolean result = DiagramPrinter.printDiagram(diagram, "some/folder", "diagram.pdf");
        assertTrue(result);
        assertEquals("printing pdf Flowchart_SN001", spy.toString());
    }
    @Test
    void shouldPrintSpreadsheetDiagram() throws IOException {
        StringBuilder spy = new StringBuilder();
        PrintableDiagram diagram = new FakeDiagram("Spreadsheet", "SN002", spy);
        boolean result = DiagramPrinter.printDiagram(diagram, "some/folder", "diagram.xls");
        assertTrue(result);
        assertEquals("printing spreadsheet Spreadsheet_SN002", spy.toString());
    }


    private record FakeDiagram(String name, String serialNumber, StringBuilder spy) implements PrintableDiagram {
        @Override
        public FlowchartDiagram getDiagram() {
            return null;
        }

        @Override
        public boolean isDisposed() {
            return false;
        }

        @Override
        public PngDocument getFlowchartThumbnail() {
            return new PngDocument("fake-thumbnail.png");
        }

        @Override
        public String getSummaryInformation() {
            return "";
        }

        @Override
        public String getSerialNumber() {
            return serialNumber;
        }

        @Override
        public String getName() {
            return name;
        }

        public boolean printPdf(String fullFilename, String targetFilename) {
            spy.append("printing pdf ").append(fullFilename);
            return true;
        }

        public boolean printSpreadsheet(String fullFilename, String targetFilename) {
            spy.append("printing spreadsheet ").append(fullFilename);
            return true;
        }
    }
}

