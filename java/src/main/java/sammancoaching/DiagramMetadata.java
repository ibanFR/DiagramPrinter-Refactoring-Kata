package sammancoaching;

public class DiagramMetadata {
    protected String fullFilename;
    protected String fileType;
    protected boolean fileAvailable;

    public DiagramMetadata(PrintableDiagram aPrintableDiagram) {
        this.fullFilename = aPrintableDiagram.getName() + "_" + aPrintableDiagram.getSerialNumber();
        this.fileType = aPrintableDiagram.getName().contains("Flowchart") ? "PDF" : "Spreadsheet";
        this.fileAvailable = !aPrintableDiagram.isDisposed();
    }

    protected DiagramMetadata() {
        // Enable subclassing for testing purposes
    }
}
