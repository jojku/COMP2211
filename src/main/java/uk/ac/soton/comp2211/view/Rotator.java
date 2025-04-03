package uk.ac.soton.comp2211.view;

import javafx.scene.Node;
import javafx.scene.transform.Rotate;

public class Rotator {
    private int currentAngle;
    private final Node object;
    private final float xPivot;
    private final float yPivot;

    /**
     * Enables smart rotation of a node about a fixed pivot
     * @param object Node to be rotated
     * @param xPivot X-coordinate of the pivot
     * @param yPivot Y-coordinate of the pivot
     */
    public Rotator(Node object, float xPivot, float yPivot) {
        this.currentAngle = 0;
        this.object = object;
        this.xPivot = xPivot;
        this.yPivot = yPivot;
    }

    /**
     * Rotates from the current transformation
     * @param angle Angle to rotate by
     */
    public void rotate(int angle){
        object.getTransforms().add(new Rotate(angle, xPivot, yPivot));
        currentAngle += angle;
    }

    /**
     * Rotate to an absolute angle
     * @param angle Angle to rotate to
     */
    public void rotateTo(int angle){
        rotate(-currentAngle);
        rotate(angle);
    }

    /**
     * Resets rotation
     */
    public void reset(){
        rotate(-currentAngle);
    }
}
