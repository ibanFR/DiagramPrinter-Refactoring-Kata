package sammancoaching;

public interface PrintableDiagram {
    PngDocument getFlowchartThumbnail();

    String getSummaryInformation();

    String getSerialNumber();

    String getName();
}
