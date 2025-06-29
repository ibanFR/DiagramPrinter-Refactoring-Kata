package sammancoaching;

public interface PrintableDiagram {

    default boolean printSpreadsheet(String fullFilename, String targetFilename) {
        return getFlowchartDataAsSpreadsheet()
                .copyFile(fullFilename, targetFilename, true);
    }

    FlowchartDiagram getDiagram();

    SpreadsheetDocument getFlowchartDataAsSpreadsheet();

    boolean isDisposed();

    PngDocument getFlowchartThumbnail();

    String getSummaryInformation();

    String getSerialNumber();

    String getName();

    boolean printPdf(String fullFilename, String targetFilename);
}
