package sammancoaching;

public record DiagramWrapper(FlowchartDiagram diagram) implements PrintableDiagram {

    @Override
    public FlowchartDiagram getDiagram() {
        return diagram();
    }

    @Override
    public SpreadsheetDocument getFlowchartDataAsSpreadsheet() {
        return diagram()
                .getFlowchartDataAsSpreadsheet();
    }

    @Override
    public boolean isDisposed() {
        return diagram()
                .isDisposed();
    }

    @Override
    public PngDocument getFlowchartThumbnail() {
        return diagram()
                .getFlowchartThumbnail();
    }

    @Override
    public String getSummaryInformation() {
        return diagram()
                .getSummaryInformation();
    }

    @Override
    public String getSerialNumber() {
        return diagram()
                .getSerialNumber();
    }

    @Override
    public String getName() {
        return diagram()
                .getName();
    }

    @Override
    public boolean printPdf(String fullFilename, String targetFilename) {
        return this.diagram()
                .getFlowchartAsPdf()
                .copyFile(fullFilename, targetFilename, true);
    }

    @Override
    public boolean printSpreadsheet(String fullFilename, String targetFilename) {
        return getFlowchartDataAsSpreadsheet()
                .copyFile(fullFilename, targetFilename, true);
    }
}
