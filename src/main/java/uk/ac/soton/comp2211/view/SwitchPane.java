package uk.ac.soton.comp2211.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import uk.ac.soton.comp2211.model.Obstacle;
import uk.ac.soton.comp2211.model.Runway;


public class SwitchPane extends StackPane {
    final Notification help = new Notification();
    private final MainScene mainScene;

    private Visualisations currentView;
    private final TopView topView;
    private final SideView sideView;

    final Button switchViewButton;
    final Button switchDirectionButton;
    final CheckBox checkCompassSnap;
    final Button helpButton;
    final Button exportButton;
    final Button colorModeButton;

    private Runway currentRunway;
    private Obstacle currentObstacle;

    public SwitchPane(MainScene mainScene,TopView topView, SideView sideView) {
        super(topView);
        this.topView = topView;
        this.sideView = sideView;
        this.mainScene = mainScene;
        currentView = topView;

        switchViewButton = new Button("Top View");
        StackPane.setMargin(switchViewButton, new Insets(15));
        StackPane.setAlignment(switchViewButton, Pos.TOP_CENTER);

        switchDirectionButton = new Button("Switch direction");
        StackPane.setMargin(switchDirectionButton, new Insets(15));
        StackPane.setAlignment(switchDirectionButton, Pos.TOP_RIGHT);

        checkCompassSnap = new CheckBox("Snap top-down view to compass");
        StackPane.setMargin(checkCompassSnap, new Insets(45));
        StackPane.setAlignment(checkCompassSnap, Pos.TOP_CENTER);

        helpButton = new Button("Zoom/Pan Help");
        StackPane.setMargin(helpButton, new Insets(45,0,0,15));
        StackPane.setAlignment(helpButton, Pos.TOP_LEFT);

        exportButton = new Button("Export current view");
        StackPane.setMargin(exportButton, new Insets(15));
        StackPane.setAlignment(exportButton, Pos.TOP_LEFT);

        colorModeButton = new Button("Switch color mode");
        StackPane.setMargin(colorModeButton, new Insets(45,15,0,0));
        StackPane.setAlignment(colorModeButton, Pos.TOP_RIGHT);

        initialise();
    }

    private void initialise() {
        this.setMinSize(mainScene.getWidth()-420, mainScene.getHeight());
        this.getChildren().set(0, currentView);

        this.getChildren().addAll(switchViewButton, checkCompassSnap, helpButton, switchDirectionButton, exportButton, colorModeButton);
        this.getStyleClass().add("lawn");

        switchViewButton.setOnAction(e -> switchButtonPressed());
        checkCompassSnap.setOnAction(e -> checkChanged());
        helpButton.setOnAction(e -> helpButtonPressed());
        switchDirectionButton.setOnAction(e -> switchDirectionPressed());
        exportButton.setOnAction(e -> exportView());
        colorModeButton.setOnAction(e -> switchColor());

            this.addEventHandler(KeyEvent.KEY_PRESSED, (key)->{
                if (currentView instanceof TopView) {
                    switch (key.getCode()) {
                        case W -> currentView.getPanner().pan(0, 30);
                        case S -> currentView.getPanner().pan(0, -30);
                        case A -> currentView.getPanner().pan(30, 0);
                        case D -> currentView.getPanner().pan(-30, 0);
                        case Q -> currentView.getZoomer().zoom(1.25f, 1.25f);
                        case Z -> currentView.getZoomer().zoom(.8f, .8f);
                    }
                }
            });
    }

    /**
     * Updates the runway in the view
     * @param redeclaredRunway runway
     */
    public void setRunway(Runway redeclaredRunway) {
        //Change later to pass the redeclared runway
        topView.setRunway(redeclaredRunway);
        sideView.setRunway(redeclaredRunway);
        this.currentRunway = redeclaredRunway;
    }

    /**
     * Update the obstacle in the view
     * @param obstacle obstacle
     */
    public void setObstacle(Obstacle obstacle) {
        this.currentObstacle = obstacle;
        topView.setObstacle(obstacle);
        sideView.setObstacle(obstacle);
    }

    public void redeclareCalled() {
        topView.setObstacle(currentObstacle);
        sideView.setObstacle(currentObstacle);
    }

    private void switchButtonPressed() {
        if (currentView instanceof SideView) {
            switchViewButton.setText("Side view");
            currentView = topView;
            this.getStyleClass().clear();
            this.getStyleClass().add("lawn");
            checkCompassSnap.setVisible(true);
        }
        else {
            currentView = sideView;
            switchViewButton.setText("Top view");
            this.getStyleClass().clear();
            this.getStyleClass().add("sideView");
            checkCompassSnap.setVisible(false);
        }

        this.getChildren().set(0, currentView);
    }

    private void switchDirectionPressed() {
        System.out.println("Pressed");
        currentView.switchDirection();
    }

    private void checkChanged() {
        if (checkCompassSnap.isSelected()) {
            if (currentRunway != null) {
                topView.getRotator().rotateTo(currentRunway.getHeading() * 10);
                topView.rotated(true);
            }
        } else {
            topView.getRotator().reset();
            topView.rotated(false);
        }
    }

    private void helpButtonPressed() {
        help.display(mainScene.getWindow(), """
                W/A/S/D: Pan
                    Q/Z: Zoom
                """);
    }

    public void initializeTransformers() {
        Clipper clipper = new Clipper(this);
        topView.initializeTransformers();
    }

    public void exportView() {
        try {
            currentView.export();
            help.display(mainScene.getWindow(), "Image exported successfully");
        } catch (Exception e) {
            help.display(mainScene.getWindow(), "Error exporting view: "+e.getMessage());
        }
    }

    public void switchColor(){
        System.out.println("Color switcher pressed");
        CSSLoader loader = mainScene.getLoader();
        String css = loader.getStylesheet();
        if (css.equals("/default.css")) {
            loader.loadStyleSheet("/colorBlind.css");
        } else {
            loader.loadStyleSheet("/default.css");
        }
    }
}

