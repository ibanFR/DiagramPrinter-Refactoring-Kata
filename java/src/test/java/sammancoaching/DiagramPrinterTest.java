package sammancoaching;

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
        PrintableDiagram diagram = new FakeDiagram();
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
        DiagramPrinter printer = new DiagramPrinter();
        PrintableDiagram diagram = new FakeDiagram();
        boolean result = printer.printDiagram(diagram, "some/folder", "diagram.pdf");
        assertTrue(result);
    }


    private record FakeDiagram() implements PrintableDiagram {
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
            return "FlowchartDiagram";
        }

        public boolean printPdf(String fullFilename, String targetFilename) {
            return true;
        }
    }
}

