package backend.model;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShapeMessage {
    private String action;
    private Shape shape;

    public ShapeMessage() {}

    public ShapeMessage(String action, Shape shape) {
        this.action = action;
        this.shape = shape;
    }

}
