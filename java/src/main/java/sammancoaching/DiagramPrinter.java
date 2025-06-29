package sammancoaching;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * This is a class you'd like to get under test so you can change it safely.
 */
public class DiagramPrinter {
    public static final String SPREADSHEET = "Spreadsheet";
    public static final String PDF = "PDF";

    public boolean printSummary(FlowchartDiagram diagram, String language, StringBuilder summaryText) {

        PrintableDiagram printableDiagram = new DiagramWrapper(diagram);

        if (diagram == null) {
            summaryText.setLength(0); // Clear text
            return false;
        }

        return printSummary(printableDiagram, language, summaryText);
    }

    public boolean printSummary(PrintableDiagram aPrintableDiagram, String language, StringBuilder summaryText) {
        DiagramSummary summary = new DiagramSummary(language);
        summary.addTitle(aPrintableDiagram.getName(), aPrintableDiagram.getSerialNumber());
        summary.addHeader(aPrintableDiagram.getSummaryInformation());
        summary.addImage(aPrintableDiagram.getFlowchartThumbnail());
        summaryText.append(summary.export());
        return true;
    }

    public boolean printDiagram(FlowchartDiagram diagram, String folder, String filename) throws IOException {
        if (diagram == null) {
            return false;
        }

        return printDiagram(new DiagramWrapper(diagram), folder, filename);
    }

    public static boolean printDiagram(PrintableDiagram aPrintableDiagram, String folder, String filename) throws IOException {

        DiagramMetadata info = new DiagramMetadata(aPrintableDiagram);

        if (PDF.equals(info.fileType)) {
            String targetFilename = getTargetFilename(folder, filename);
            String fullFilename = info.fullFilename;
            return printPdf(aPrintableDiagram, fullFilename, targetFilename);
        }

        if (SPREADSHEET.equals(info.fileType)) {
            String targetFilename = getTargetFilename(folder, filename);
            if (!targetFilename.endsWith(".xls")) {
                targetFilename += ".xls";
            }
            return aPrintableDiagram.getFlowchartDataAsSpreadsheet()
                    .copyFile(info.fullFilename, targetFilename, true);
        }

        // Default case - print to a physical printer
        return printToPhysicalPrinter(aPrintableDiagram, folder, filename, info);
    }

    private static boolean printPdf(PrintableDiagram aPrintableDiagram, String fullFilename, String targetFilename) {
        return aPrintableDiagram.getFlowchartAsPdf()
                .copyFile(fullFilename, targetFilename, true);
    }

    private static boolean printToPhysicalPrinter(PrintableDiagram aPrintableDiagram, String folder, String filename, DiagramMetadata info) throws IOException {
        return new DiagramPhysicalPrinter().doPrint(aPrintableDiagram.getDiagram(),
                                                    info,
                                                    getTargetFilename(folder, filename));
    }

    private static String getTargetFilename(String folder, String filename) {
        if (folder == null) {
            folder = System.getProperty("java.io.tmpdir"); // Equivalent to Path.GetTempPath()
        }

        if (filename == null) {
            filename = Paths.get(folder, "tempFile").toString(); // Simulate temp file creation
        }

        return Paths.get(folder, filename).toString();
    }
}
