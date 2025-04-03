package uk.ac.soton.comp2211.view;

import javafx.scene.Node;
import javafx.scene.transform.Translate;

public class Panner {
    private float currentDX;
    private float currentDY;
    private final Node object;

    public Panner(Node object) {
        this.currentDX = 0;
        this.currentDY = 0;
        this.object = object;
    }

    public void pan (float dx, float dy) {
        object.getTransforms().add(new Translate(dx, dy));
        currentDX += dx;
        currentDY += dy;
    }

    public void centre() {
        object.getTransforms().add(new Translate(-currentDX, -currentDY));
    }
}
