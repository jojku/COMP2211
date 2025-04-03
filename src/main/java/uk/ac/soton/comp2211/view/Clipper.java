package uk.ac.soton.comp2211.view;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class Clipper {
    private final Rectangle clipRegion;
    private final Region content;

    InvalidationListener onResize = new InvalidationListener() {
        @Override
        public void invalidated(Observable unused) {
            clipRegion.setWidth(content.getWidth());
            clipRegion.setHeight(content.getHeight());
        }
    };

    /**
     * Clips a region according to its bounds, automatically re-clips on resizing the region.
     * @param content Region to be clipped
     */
    public Clipper(Region content) {
        this.clipRegion = new Rectangle(content.getWidth(), content.getHeight());
        this.content = content;

        content.setClip(clipRegion);
        content.layoutBoundsProperty().addListener(onResize);
    }
}
