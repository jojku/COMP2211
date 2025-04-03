package uk.ac.soton.comp2211.controller;

import javafx.application.Application;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp2211.view.StartMenu;

/**
 * Runway Redeclaration Tool
 * Authors: Thomas Cutts, Charlie Spiers, Sundy Lau, JJ Snedden, Anh Hach and Georgy Markov
 * Date of creation: 06/03/2022
 */

public class App extends Application {
    private static final Logger logger = LogManager.getLogger(App.class);

    //Base application size
    static int width = 900;
    static int height = 700;

    /**
     * Start the app
     * @param args commandline arguments
     */
    public static void main(String[] args) {
        logger.info("Starting client");
        launch();
    }

    /**
     * Called by JavaFX with the primary stage as a parameter.
     * Begins the app by instantiating a MainScene
     * @param stage the default stage, main window
     */
    public void start(Stage stage) {
        logger.info("Initialising controller");
        var controller = new Controller(width, height, stage);
        var scene = controller.getScene();
        var startMenu = controller.getStartMenu();

        stage.setScene(startMenu);

        stage.setTitle("RunwayTool");
        stage.setMinWidth(width);
        stage.setMinHeight(height + 20);
        stage.setOnCloseRequest(e -> shutdown());
        stage.show();
        logger.info("Initialisation complete");

    }

    /**
     * Shutdown the app
     */
    public void shutdown() {
        logger.info("Shutting down");
        System.exit(0);
    }
}
