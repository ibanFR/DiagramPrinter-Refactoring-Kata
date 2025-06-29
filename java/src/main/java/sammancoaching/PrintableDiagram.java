package sammancoaching;

public interface PrintableDiagram {

    FlowchartDiagram getDiagram();

    SpreadsheetDocument getFlowchartDataAsSpreadsheet();

    PdfDocument getFlowchartAsPdf();

    boolean isDisposed();

    PngDocument getFlowchartThumbnail();

    String getSummaryInformation();

    String getSerialNumber();

    String getName();

    boolean printPdf(String fullFilename, String targetFilename);
}
