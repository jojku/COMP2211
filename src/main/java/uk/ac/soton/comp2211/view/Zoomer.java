package uk.ac.soton.comp2211.view;

import javafx.scene.Node;
import javafx.scene.transform.Scale;

public class Zoomer {
    private float currentZoomX;
    private float currentZoomY;
    private final Node object;
    private final float xCentre;
    private final float yCentre;

    public Zoomer(Node object, float xCentre, float yCentre) {
        this.currentZoomX = 1;
        this.currentZoomY = 1;
        this.object = object;
        this.xCentre = xCentre;
        this.yCentre = yCentre;
    }

    public void zoom(float x, float y) {
        object.getTransforms().add(new Scale(x, y, xCentre, yCentre));
        currentZoomX *= x;
        currentZoomY *= y;
    }

    public void reset() {
        object.getTransforms().add(new Scale(1/currentZoomX, 1/currentZoomY, xCentre, yCentre));
        currentZoomX *= 1;
        currentZoomY *= 1;
    }
}
