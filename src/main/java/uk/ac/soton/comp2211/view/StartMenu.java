package uk.ac.soton.comp2211.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import uk.ac.soton.comp2211.controller.listeners.StartImportListener;
import uk.ac.soton.comp2211.controller.listeners.StartNormal;
import uk.ac.soton.comp2211.controller.listeners.StartWithNothingListener;
import uk.ac.soton.comp2211.model.Airport;
import uk.ac.soton.comp2211.model.Obstacle;
import uk.ac.soton.comp2211.model.Runway;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class StartMenu extends Scene {

  private final StackPane mainPane;
  private final Pane tutorialPane;
  private final Pane settingsPane;

  StartImportListener startImportListener;
  StartNormal startNormal;
  StartWithNothingListener startWithNothingListener;

  public StartMenu(StackPane pane, double width, double height) {
    super(pane, width, height);
    this.mainPane = pane;
    setupMainPane(width, height);
    tutorialPane = getTutorialPane();
    settingsPane = getSettingsPane();
  }

  private void setupMainPane(double width, double height) {
    mainPane.setPrefSize(width, height);
    Pane background = makeBackground(width, height);

    Button withDefault = new Button("Start with example parameters");
    withDefault.setStyle("-fx-font-size: " + height/40);
    Button withImport = new Button("Import parameters to start with");
    withImport.setStyle("-fx-font-size: " + height/40);
    Button withNothing = new Button("Start with no parameters loaded");
    withNothing.setStyle("-fx-font-size: " + height/40);

    VBox startButtons = new VBox(withDefault, withImport, withNothing);
    startButtons.setAlignment(Pos.CENTER);
    startButtons.setMaxSize(width/2, height*0.6);
    startButtons.setPadding(new Insets(height*0.4, 0, 0, width*0.05));
    startButtons.setSpacing(height/10);
    StackPane.setAlignment(startButtons, Pos.CENTER);

    mainPane.getChildren().addAll(background, startButtons);


    withDefault.setOnAction(e -> {
      try {
        this.startNormal.startNormal();
      } catch (Exception ex) {
        var notif = new Notification();
        notif.display(this.getWindow(), ex.getMessage());
        ex.printStackTrace();
      }
    });

    withImport.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Select an Airport xml file ");
      fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter ("XML File", "*.xml"));
      File selected = fileChooser.showOpenDialog(this.getWindow());

      try {
        this.startImportListener.startImport(selected);
      } catch (Exception ex) {
        var notif = new Notification();
        notif.display(this.getWindow(), ex.getMessage());
        ex.printStackTrace();
      }
    });

    withNothing.setOnAction(e -> {
      try {
        this.startWithNothingListener.startWithNothing();
      } catch (Exception ex) {
        var notif = new Notification();
        notif.display(this.getWindow(), ex.getMessage());
        ex.printStackTrace();
      }

    });

  }

  private Pane makeBackground(double width, double height) {
    HBox dashes = new HBox();
    dashes.getChildren().addAll(new Rectangle(width*0.1, height*0.04, Color.WHITE), new Rectangle(width*0.1, height*0.04, Color.WHITE));
    dashes.setSpacing(width*0.7);
    dashes.setAlignment(Pos.CENTER);
    StackPane.setAlignment(dashes, Pos.CENTER);
    Label welcome = new Label("Runway Redeclaration Tool");
    welcome.setStyle("-fx-font-size: " + height/15 + "; -fx-text-fill: white");
    StackPane.setAlignment(welcome, Pos.CENTER);
    Pane runway = new StackPane(new Rectangle(width, height*0.4, Color.GRAY), dashes, welcome);
    runway.setMinSize(width, height*0.3);
    runway.setMaxSize(width, height*0.3);
    StackPane.setAlignment(runway, Pos.TOP_CENTER);
//    VBox box1 = new VBox(runway, new Rectangle(width, height*0.7, Color.GREEN));
//    box1.setMaxSize(width, height);
//    box1.setMinSize(width, height);
//    return box1;
    return runway;
  }

  private Pane getTutorialPane() {
    return null;
  }

  private Pane getSettingsPane() {
    return null;
  }

  public void setStartImportListener(StartImportListener listener) { this.startImportListener = listener; }

  public void setStartNormal(StartNormal listener) { this.startNormal = listener; }

  public void setStartWithNothingListener(StartWithNothingListener listener) { this.startWithNothingListener = listener; }
}
