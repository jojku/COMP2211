package uk.ac.soton.comp2211.view;

import javafx.scene.Scene;

import java.util.Objects;

public class CSSLoader {
    Scene scene;
    String styleSheet;

    public CSSLoader(Scene scene, String styleSheet) {
        this.scene = scene;
        loadStyleSheet(styleSheet);
    }

    public void loadStyleSheet (String styleSheet) {
        this.styleSheet = styleSheet;
        String newStylesheet = Objects.requireNonNull(this.getClass().getResource(styleSheet)).toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(newStylesheet);
    }

    public String getStylesheet() {
        return styleSheet;
    }
}
