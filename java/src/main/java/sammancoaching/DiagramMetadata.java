package sammancoaching;

public class DiagramMetadata {
    protected String fullFilename;
    protected String fileType;
    protected boolean fileAvailable;

    public DiagramMetadata(DiagramWrapper aDiagramWrapper, FlowchartDiagram diagram) {
        this.fullFilename = aDiagramWrapper.getName() + "_" + aDiagramWrapper.getSerialNumber();
        this.fileType = aDiagramWrapper.getName().contains("Flowchart") ? "PDF" : "Spreadsheet";
        this.fileAvailable = !isDisposed(aDiagramWrapper);
    }

    private static boolean isDisposed(DiagramWrapper aDiagramWrapper) {
        return aDiagramWrapper.diagram()
                .isDisposed();
    }

    protected DiagramMetadata() {
        // Enable subclassing for testing purposes
    }
}
