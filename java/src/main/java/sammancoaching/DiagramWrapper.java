package sammancoaching;

public record DiagramWrapper(FlowchartDiagram diagram) {
    public String getSummaryInformation() {
        return diagram()
                .getSummaryInformation();
    }

    public String getSerialNumber() {
        return diagram()
                .getSerialNumber();
    }

    public String getName() {
        return diagram()
                .getName();
    }
}
