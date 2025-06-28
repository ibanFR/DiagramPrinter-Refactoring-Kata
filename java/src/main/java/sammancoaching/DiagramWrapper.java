package sammancoaching;

public record DiagramWrapper(FlowchartDiagram diagram) {
    public String getName() {
        return diagram()
                .getName();
    }
}
