package sammancoaching;

public interface PrintableDiagram {
    PdfDocument getFlowchartAsPdf();

    boolean isDisposed();

    PngDocument getFlowchartThumbnail();

    String getSummaryInformation();

    String getSerialNumber();

    String getName();
}
