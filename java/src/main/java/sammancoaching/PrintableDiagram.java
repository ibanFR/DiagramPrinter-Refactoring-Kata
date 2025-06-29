package sammancoaching;

public interface PrintableDiagram {

    FlowchartDiagram getDiagram();

    boolean isDisposed();

    PngDocument getFlowchartThumbnail();

    String getSummaryInformation();

    String getSerialNumber();

    String getName();

    boolean printPdf(String fullFilename, String targetFilename);

    boolean printSpreadsheet(String fullFilename, String targetFilename);
}
