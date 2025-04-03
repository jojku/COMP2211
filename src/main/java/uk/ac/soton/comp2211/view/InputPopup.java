package uk.ac.soton.comp2211.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import uk.ac.soton.comp2211.controller.listeners.SetListener;
import uk.ac.soton.comp2211.model.Airport;
import uk.ac.soton.comp2211.model.Obstacle;
import uk.ac.soton.comp2211.model.Runway;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class InputPopup<E> extends Popup {

  final Button tryButtonDef;
  final Button tryButtonImp;
  final Button swapInputMethod;
  final Button closeTab;
  final VBox box;
  final HBox topBar;
  boolean defining;
  Pane definePane;
  final MainScene mainScene;
  final Notification notif = new Notification();

  public InputPopup(String name, MainScene mainScene) {
    requestFocus();
    this.mainScene = mainScene;
    var nameText = new Text(name);

    swapInputMethod = new Button("Import xml");
    swapInputMethod.setAlignment(Pos.CENTER_LEFT);
    swapInputMethod.setPrefWidth(100);

    closeTab = new Button("X");
    closeTab.setOnAction(e -> this.hide());

    topBar = new HBox(nameText, swapInputMethod, closeTab);
    topBar.setSpacing(20);
    tryButtonDef = new Button("Create parameter");
    tryButtonDef.setOnAction(e -> tryInput());
    StackPane.setAlignment(tryButtonDef, Pos.BOTTOM_CENTER);
    tryButtonImp = new Button("Create parameter");
    tryButtonImp.setOnAction(e -> tryInput());
    StackPane.setAlignment(tryButtonImp, Pos.BOTTOM_CENTER);

    definePane = createDefinePane();

    defining = true;

    box = new VBox();
    box.getChildren().addAll(topBar, definePane);
    box.setAlignment(Pos.CENTER);
    box.setPadding(new Insets(10));
    box.setBackground(
        new Background(new BackgroundFill(Paint.valueOf("WHITE"), new CornerRadii(20), null)));
    box.setBorder(
        new Border(
            new BorderStroke(
                Paint.valueOf("Black"), BorderStrokeStyle.SOLID, new CornerRadii(20), null)));
    this.getContent().add(box);
    sizeToScene();
    centerOnScreen();
  }

  abstract void tryInput();

  abstract Pane createDefinePane();

  abstract void setValues(E value);

  public static HBox getHBox(String label, TextField text) {
    var lbl = new Label(label);
    lbl.setPrefWidth(60);
    text.setPrefWidth(140);
    var hBox = new HBox(lbl, text);
    hBox.setSpacing(5);
    return hBox;
  }

  public static HBox getMidHBox(String label, TextField text) {
    var lbl = new Label(label);
    lbl.setPrefWidth(90);
    text.setPrefWidth(110);
    var hBox = new HBox(lbl, text);
    hBox.setSpacing(5);
    return hBox;
  }

  public static HBox getLongHBox(String label, TextField text) {
    var lbl = new Label(label);
    lbl.setPrefWidth(150);
    text.setPrefWidth(50);
    var hBox = new HBox(lbl, text);
    hBox.setSpacing(5);
    return hBox;
  }

  public static HBox getHBox(String label, Text text) {
    var lbl = new Label(label);
    lbl.setPrefWidth(80);
    text.maxWidth(105);
    var hBox = new HBox(lbl, text);
    hBox.setSpacing(5);
    return hBox;
  }

  public static HBox getHBox(String label, TextField text, TextField text2) {
    var lbl = new Label(label);
    lbl.setPrefWidth(80);
    lbl.setAlignment(Pos.CENTER);
    text.setPrefWidth(60);
    text2.setPrefWidth(60);
    var hBox = new HBox(text, lbl, text2);
    hBox.setSpacing(5);
    return hBox;
  }

  public static class AirportInputPopup extends InputPopup<Airport> {

    TextField name, minAsc, blastDis, fileName;
    Airport editAirport = null;

    public AirportInputPopup(MainScene mainScene) {
      super("Airport", mainScene);
      swapInputMethod.setOnAction(e -> {
        try {
          this.hide();
          FileChooser fileChooser = new FileChooser();
          fileChooser.setTitle("Select an Airport xml file ");
          fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
          File selected = fileChooser.showOpenDialog(mainScene.getWindow());

          mainScene.airportImportListener.importAirport(selected);
          this.hide();
          notif.display(this.getOwnerWindow(), "XML File imported successfully");
        } catch (Exception ex) {
          ex.printStackTrace();
          notif.display(this.getOwnerWindow(), ex.getMessage());
        }
      });
    }

    void tryInput() {
      try {
        if (editAirport != null) {
          mainScene.getAirportRemoveListener().removeParam(editAirport);
          mainScene.airportCreationListener.createParameter(
              new Airport(
                  name.getText(),
                  editAirport.getRunways(),
                  Float.parseFloat(minAsc.getText()),
                  Integer.parseInt(blastDis.getText())));
        } else {
          mainScene.airportCreationListener.createParameter(
              new Airport(
                  name.getText(),
                  new ArrayList<>(),
                  Float.parseFloat(minAsc.getText()),
                  Integer.parseInt(blastDis.getText())));
        }
        this.hide();
      } catch (Exception e) {
        notif.display(this.getOwnerWindow(), e.getMessage());
      }
    }

    protected Pane createDefinePane() {
      var pane = new StackPane();
      pane.setPrefSize(200, 200);
      Label info = new Label("Populated with default values");
      info.setWrapText(true);
      info.setPrefWidth(200);
      var box = new VBox();
      pane.getChildren().add(box);
      box.getChildren()
          .addAll(
              getHBox("Name:", name = new TextField("Name")),
              getHBox("Runways:", new Text("Defined later")),
              getMidHBox("Min ascent ratio:", minAsc = new TextField("50")),
              getMidHBox("Blast distance:", blastDis = new TextField("300")));
      pane.getChildren().add(tryButtonDef);
      return pane;
    }

    @Override
    public void setValues(Airport a) {
      swapInputMethod.setVisible(false);
      tryButtonDef.setText("Change airport");
      editAirport = a;
      name.setText(a.getName());
      minAsc.setText(Float.toString(a.getMinAscentRatio()));
      blastDis.setText(Integer.toString(a.getBlastDistance()));
    }
  }

  public static class RunwayInputPopup extends InputPopup<Runway> {
    TextField heading,
        stripEnd,
        RESA,
        TORA1,
        TODA1,
        ASDA1,
        LDA1,
        thresholdDisplace1,
        TORA2,
        TODA2,
        ASDA2,
        LDA2,
        thresholdDisplace2,
        fileName;
    ChoiceBox<String> designator;
    Runway editRunway = null;

    public RunwayInputPopup(MainScene mainScene) {
      super("Runway", mainScene);
      swapInputMethod.setOnAction( e -> {
        try {
          this.hide();
          FileChooser fileChooser = new FileChooser();
          fileChooser.setTitle("Select a Runway xml file ");
          fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter ("XML file", "*xml"));
          File selected = fileChooser.showOpenDialog(mainScene.getWindow());

          mainScene.runwayImportListener.importRunway(selected);
          notif.display(this.getOwnerWindow(), "XML File imported successfully");
        } catch (Exception ex) {
          notif.display(this.getOwnerWindow(), ex.getMessage());
        }
      });
    }

    void tryInput() {

      try {
        Runway newRunway =
            new Runway(
                Integer.parseInt(heading.getText()),
                designator.getValue().charAt(0),
                Integer.parseInt(stripEnd.getText()),
                Integer.parseInt(RESA.getText()),
                Integer.parseInt(TORA1.getText()),
                Integer.parseInt(TODA1.getText()),
                Integer.parseInt(ASDA1.getText()),
                Integer.parseInt(LDA1.getText()),
                Integer.parseInt(thresholdDisplace1.getText()),
                Integer.parseInt(TORA2.getText()),
                Integer.parseInt(TODA2.getText()),
                Integer.parseInt(ASDA2.getText()),
                Integer.parseInt(LDA2.getText()),
                Integer.parseInt(thresholdDisplace2.getText()));
        if (editRunway != null) mainScene.getRunwayRemoveListener().removeParam(editRunway);
        mainScene.runwayCreationListener.createParameter(newRunway);
        this.hide();
      } catch (Exception e) {
        notif.display(this.getOwnerWindow(), e.getMessage());
      }
    }

    protected Pane createDefinePane() {
      var pane = new StackPane();
      var cbValues = new ArrayList<>(List.of(new String[] {"L", "C", "R"}));
      designator = new ChoiceBox<>(FXCollections.observableArrayList(cbValues));
      pane.setPrefSize(200, 400);
      Label info =
          new Label(
              "Populated with default values, values of lower heading on the left, values of higher heading on the right");
      info.setWrapText(true);
      info.setPrefWidth(200);
      var box = new VBox();
      pane.getChildren().add(box);
      box.getChildren()
          .addAll(
              info,
              getHBox("Strip end:", stripEnd = new TextField("60")),
              getHBox("RESA:", RESA = new TextField("240")),
              getMidHBox("Lower heading:", heading = new TextField("0")),
              new HBox(new Label("Designator:"), designator),
              getHeadingHBox(),
              getHBox("TORA:", TORA1 = new TextField("1000"), TORA2 = new TextField("1000")),
              getHBox("TODA:", TODA1 = new TextField("1000"), TODA2 = new TextField("1000")),
              getHBox("ASDA:", ASDA1 = new TextField("1000"), ASDA2 = new TextField("1000")),
              getHBox("LDA:", LDA1 = new TextField("1000"), LDA2 = new TextField("1000")),
              getHBox(
                  "Threshold:",
                  thresholdDisplace1 = new TextField("0"),
                  thresholdDisplace2 = new TextField("0")));
      pane.getChildren().add(tryButtonDef);
      designator.setValue("C");
      return pane;
    }

    private HBox getHeadingHBox() {
      var lb1 = new Label("Lower");
      var lb2 = new Label("Heading");
      var lb3 = new Label("Higher");
      lb1.setPrefWidth(60);
      lb2.setAlignment(Pos.CENTER);
      lb2.setPrefWidth(80);
      lb2.setAlignment(Pos.CENTER);
      lb3.setPrefWidth(60);
      lb3.setAlignment(Pos.CENTER);
      return new HBox(lb1, lb2, lb3);
    }

    @Override
    public void setValues(Runway r) {
      swapInputMethod.setVisible(false);
      tryButtonDef.setText("Change runway");
      editRunway = r;
      stripEnd.setText(Integer.toString(r.getStripEnd()));
      RESA.setText(Integer.toString(r.getRESA()));

      TORA1.setText(Integer.toString(r.getLogicalRunway1().getOldTORA()));
      TODA1.setText(Integer.toString(r.getLogicalRunway1().getOldTODA()));
      ASDA1.setText(Integer.toString(r.getLogicalRunway1().getOldASDA()));
      LDA1.setText(Integer.toString(r.getLogicalRunway1().getOldLDA()));
      thresholdDisplace1.setText(
          Integer.toString(r.getLogicalRunway1().getThresholdDisplacement()));

      TORA2.setText(Integer.toString(r.getLogicalRunway2().getOldTORA()));
      TODA2.setText(Integer.toString(r.getLogicalRunway2().getOldTODA()));
      ASDA2.setText(Integer.toString(r.getLogicalRunway2().getOldASDA()));
      LDA2.setText(Integer.toString(r.getLogicalRunway2().getOldLDA()));
      thresholdDisplace2.setText(
          Integer.toString(r.getLogicalRunway2().getThresholdDisplacement()));
    }
  }

  public static class ObstacleInputPopup extends InputPopup<Obstacle> {

    TextField name, height, width, length, dThreshold1, dThreshold2, dCenter, fileName;
    Obstacle editObstacle = null;

    public ObstacleInputPopup(MainScene mainScene) {
      super("Obstacle", mainScene);
      swapInputMethod.setOnAction( e -> {
        try {
          this.hide();
          FileChooser fileChooser = new FileChooser();
          fileChooser.setTitle("Select an Obstacle xml file ");
          fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter ("XML file", "*xml"));
          File selected = fileChooser.showOpenDialog(mainScene.getWindow());

          mainScene.obstacleImportListener.importObstacle(selected);
          notif.display(this.getOwnerWindow(), "XML File imported successfully");
          this.hide();
        } catch (Exception ex) {
          notif.display(this.getOwnerWindow(), ex.getMessage());
        }
      });
    }

    void tryInput() {
      try {
        var obstacle =
            new Obstacle(
                name.getText(),
                Integer.parseInt(height.getText()),
                Integer.parseInt(width.getText()),
                Integer.parseInt(length.getText()));
        obstacle.setdThreshold1(Integer.parseInt(dThreshold1.getText()));
        obstacle.setdThreshold2(Integer.parseInt(dThreshold2.getText()));
        obstacle.setdCenter(Integer.parseInt(dCenter.getText()));
        if (editObstacle != null) mainScene.getObstacleRemoveListener().removeParam(editObstacle);
        mainScene.obstacleCreationListener.createParameter(obstacle);
        this.hide();
      } catch (Exception e) {
        notif.display(this.getOwnerWindow(), e.getMessage());
      }
    }

    protected Pane createDefinePane() {
      var pane = new StackPane();
      pane.setPrefSize(200, 200);
      Label info = new Label("Populated with default values");
      info.setWrapText(true);
      info.setPrefWidth(200);
      var box = new VBox();
      pane.getChildren().add(box);
      box.getChildren()
          .addAll(
              getHBox("Name:", name = new TextField("Name")),
              getHBox("Height:", height = new TextField("5")),
              getHBox("Width:", width = new TextField("1")),
              getHBox("Length:", length = new TextField("1")),
              getLongHBox("Distance to lower threshold:", dThreshold1 = new TextField("0")),
              getLongHBox("Distance to upper threshold:", dThreshold2 = new TextField("0")),
              getLongHBox("Distance to center line:", dCenter = new TextField("0")));
      pane.getChildren().add(tryButtonDef);
      return pane;
    }

    @Override
    public void setValues(Obstacle o) {
      swapInputMethod.setVisible(false);
      tryButtonDef.setText("Change obstacle");
      name.setText(o.getName());
      height.setText(Integer.toString(o.getHeight()));
      width.setText(Integer.toString(o.getWidth()));
      length.setText(Integer.toString(o.getLength()));
      dThreshold1.setText(Integer.toString(o.getHeight()));
      height.setText(Integer.toString(o.getHeight()));
    }
  }

  public static class ObstacleSetInputPopup extends InputPopup<Obstacle> {
    TextField dThreshold1, dThreshold2, dCenter;
    final Obstacle obstacle;
    final SetListener<Obstacle> obstacleSetListener;

    public ObstacleSetInputPopup(MainScene mainScene, Obstacle obstacle, SetListener<Obstacle> obstacleSetListener) {
      super("Obstacle", mainScene);
      this.obstacle = obstacle;
      this.obstacleSetListener = obstacleSetListener;
      topBar.getChildren().remove(swapInputMethod);
      closeTab.setOnAction(e -> {
        var errorNotif = new Notification();
        errorNotif.display(mainScene.getWindow(), "Obstacle was not given distances relative to the runway\nDefaulting to 0s.");
        this.hide();
      });
    }

    void tryInput() {
      try {
        System.out.println("Defining");
        obstacle.setdThreshold1(Integer.parseInt(dThreshold1.getText()));
        obstacle.setdThreshold2(Integer.parseInt(dThreshold2.getText()));
        obstacle.setdCenter(Integer.parseInt(dCenter.getText()));
        obstacleSetListener.setParameter(obstacle);
        this.hide();
      } catch (Exception e) {
        notif.display(this.getOwnerWindow(), e.getMessage());
      }
    }

    protected Pane createDefinePane() {
      var pane = new StackPane();
      pane.setPrefSize(200, 200);
      Label info = new Label("Populated with default values");
      info.setWrapText(true);
      info.setPrefWidth(200);
      var box = new VBox();
      pane.getChildren().add(box);
      box.getChildren()
          .addAll(
              getLongHBox("Distance to lower threshold:", dThreshold1 = new TextField("0")),
              getLongHBox("Distance to upper threshold:", dThreshold2 = new TextField("0")),
              getLongHBox("Distance to center line:", dCenter = new TextField("0")));
      pane.getChildren().add(tryButtonDef);
      return pane;
    }

    @Override
    public void setValues(Obstacle o) {
      // This won't be called
    }
  }
}
