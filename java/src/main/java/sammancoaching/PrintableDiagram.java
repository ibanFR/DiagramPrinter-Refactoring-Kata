package sammancoaching;

public interface PrintableDiagram {
    default boolean printPdf(String fullFilename, String targetFilename) {
        return getFlowchartAsPdf()
                .copyFile(fullFilename, targetFilename, true);
    }

    FlowchartDiagram getDiagram();

    SpreadsheetDocument getFlowchartDataAsSpreadsheet();

    PdfDocument getFlowchartAsPdf();

    boolean isDisposed();

    PngDocument getFlowchartThumbnail();

    String getSummaryInformation();

    String getSerialNumber();

    String getName();
}
