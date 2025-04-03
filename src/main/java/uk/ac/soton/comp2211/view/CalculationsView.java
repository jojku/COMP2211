package uk.ac.soton.comp2211.view;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class CalculationsView extends Scene {

    private UIPaneBuilder.OutputBox outputBox;
    private StackPane rootPane;

    public CalculationsView (StackPane rootPane, UIPaneBuilder.OutputBox outputBox) {
        super(rootPane, 300, 400, Color.rgb(255, 255, 148));
        this.outputBox = outputBox;
        this.rootPane = rootPane;
    }

    public void setOutputBox(UIPaneBuilder.OutputBox outputBox) {
        this.rootPane.getChildren().remove(this.outputBox);
        this.outputBox = outputBox;
        this.rootPane.getChildren().add(outputBox);
    }
}