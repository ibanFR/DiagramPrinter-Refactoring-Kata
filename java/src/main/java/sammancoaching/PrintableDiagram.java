package sammancoaching;

public interface PrintableDiagram {
    SpreadsheetDocument getFlowchartDataAsSpreadsheet();

    PdfDocument getFlowchartAsPdf();

    boolean isDisposed();

    PngDocument getFlowchartThumbnail();

    String getSummaryInformation();

    String getSerialNumber();

    String getName();
}
