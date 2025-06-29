package sammancoaching;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        PrintableDiagram diagram = new FakeDiagram("name");
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
        PrintableDiagram diagram = new FakeDiagram("Flowchart");
        boolean result = DiagramPrinter.printDiagram(diagram, "some/folder", "diagram.pdf");
        assertTrue(result);
    }
    @Test
    @Disabled("can't construct SpreadsheetDocument in test")
    void shouldPrintSpreadsheetDiagram() throws IOException {
        PrintableDiagram diagram = new FakeDiagram("Spreadsheet");
        boolean result = DiagramPrinter.printDiagram(diagram, "some/folder", "diagram.xls");
        assertTrue(result);
    }


    private record FakeDiagram(String name) implements PrintableDiagram {
        @Override
        public FlowchartDiagram getDiagram() {
            return null;
        }

        @Override
        public SpreadsheetDocument getFlowchartDataAsSpreadsheet() {
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
            return "";
        }

        @Override
        public String getName() {
            return name;
        }

        public boolean printPdf(String fullFilename, String targetFilename) {
            return true;
        }
    }
}

