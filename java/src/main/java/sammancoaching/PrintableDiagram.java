package sammancoaching;

public interface PrintableDiagram {
    boolean isDisposed();

    PngDocument getFlowchartThumbnail();

    String getSummaryInformation();

    String getSerialNumber();

    String getName();
}
