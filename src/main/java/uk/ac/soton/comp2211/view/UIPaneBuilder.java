package uk.ac.soton.comp2211.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import uk.ac.soton.comp2211.controller.listeners.PDFExportListener;
import uk.ac.soton.comp2211.controller.listeners.SetListener;
import uk.ac.soton.comp2211.model.Airport;
import uk.ac.soton.comp2211.model.Obstacle;
import uk.ac.soton.comp2211.model.Runway;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

public class UIPaneBuilder extends StackPane {

  private static final Logger logger = LogManager.getLogger(UIPaneBuilder.class);

  public static ParameterPane<Airport> airportParameterPane(
      MainScene mainScene,
      ArrayList<Airport> airportList,
      Airport currentAirport,
      SetListener<Airport> listener) {
    var airportParameterPane =
        new ParameterPane<>(
            mainScene,
            airportList,
            currentAirport,
            new AirportBox(currentAirport, mainScene),
            "Airport:");
    airportParameterPane.setListener(
        e -> {
          airportParameterPane.setFields(e);
          listener.setParameter(e);
        });
    airportParameterPane.setFields(currentAirport);
    return airportParameterPane;
  }

  public static ParameterPane<Runway> runwayParameterPane(
      MainScene mainScene,
      ArrayList<Runway> runwayList,
      Runway currentRunway,
      SetListener<Runway> listener) {
    var runwayParameterPane =
        new ParameterPane<>(
            mainScene,
            runwayList,
            currentRunway,
            new RunwayBox(currentRunway, mainScene),
            "Runway:");
    runwayParameterPane.setListener(
        e -> {
          runwayParameterPane.setFields(e);
          listener.setParameter(e);
        });
    runwayParameterPane.setFields(currentRunway);
    return runwayParameterPane;
  }

  public static ParameterPane<Obstacle> obstacleParameterPane(
      MainScene mainScene,
      ArrayList<Obstacle> obstacleList,
      Obstacle currentObstacle,
      SetListener<Obstacle> listener) {
    var obstacleParameterPane =
        new ObstacleParameterPane(
            mainScene,
            obstacleList,
            currentObstacle,
            new ObstacleBox(currentObstacle, mainScene),
            "Obstacle:");
    obstacleParameterPane.setListener(
        e -> {
          obstacleParameterPane.setFields(e);
          listener.setParameter(e);
        });
    obstacleParameterPane.setFields(currentObstacle);
    return obstacleParameterPane;
  }

  /**
   * Creates the pane for the parameterBox and the HashChoiceBox to sit in
   *
   * @param <E> Type of the box: Airport, Runway or Obstacle
   */
  public static class ParameterPane<E> extends VBox {
    protected ParameterBox<E> parameterBox;
    protected E selected;
    protected final HashChoiceBox<E> hashBox;
    protected final Button editParam;
    protected final Button addParam;
    protected final Button removeParam;
    protected final Button saveParam;
    protected final MainScene mainScene;

    public ParameterPane(
        MainScene mainScene,
        ArrayList<E> list,
        E selected,
        ParameterBox<E> parameterBox,
        String name) {
      setMaxWidth(210);
      this.mainScene = mainScene;
      this.parameterBox = parameterBox;
      this.selected = selected;
      parameterBox.setParameterPane(this);

      setBorder(
          new Border(
              new BorderStroke(
                  Paint.valueOf("Black"), BorderStrokeStyle.SOLID, new CornerRadii(20), null)));

      hashBox = getHashBox(list);
      HBox hb = hashBox.getChoiceBox(name);
      hb.setAlignment(Pos.CENTER);
      hb.setPrefSize(150, 30);

      editParam = new Button();
      var editIcon = new ImageView(new Image(
          Objects.requireNonNull(this.getClass().getResource("/edit_icon.png")).toExternalForm()
      ));
      editIcon.setPreserveRatio(true);
      editIcon.setFitHeight(17);
      editParam.setGraphic(editIcon);
      editParam.setTooltip(new Tooltip("Edit a parameter"));
      editParam.setOnAction(e -> parameterBox.editParam());

      addParam = new Button("+");
      addParam.setAlignment(Pos.CENTER);
      addParam.setOnAction(e -> parameterBox.addParam());
      addParam.setTooltip(new Tooltip("Add a parameter"));

      removeParam = new Button("-");
      removeParam.setAlignment(Pos.CENTER);
      removeParam.setOnAction(e -> parameterBox.removeParam());
      removeParam.setTooltip(new Tooltip("Remove a parameter"));

      saveParam = new Button();
      var saveIcon =
          new ImageView(
              new Image(
                  Objects.requireNonNull(this.getClass().getResource("/save_icon.png"))
                      .toExternalForm()));
      saveIcon.setPreserveRatio(true);
      saveIcon.setFitHeight(17);
      saveParam.setGraphic(saveIcon);
      saveParam.setTooltip(new Tooltip("Save a parameter"));

      saveParam.setAlignment(Pos.CENTER);
      saveParam.setOnAction(
          e -> {
            var notif = new Notification();

            try {
              FileChooser fileChooser = new FileChooser();
              fileChooser.setTitle("Select a file to save to ");
              fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter ("XML File", "*.xml"));
              File selectedFile = fileChooser.showSaveDialog(mainScene.getWindow());

              var currentObj = this.parameterBox.getCurrent();
              if(currentObj instanceof Airport) mainScene.airportExportListener.exportAirport((Airport) currentObj, selectedFile);
              else if (currentObj instanceof Runway) mainScene.runwayExportListener.exportRunway((Runway) currentObj,selectedFile);
              else if (currentObj instanceof Obstacle) mainScene.obstacleExportListener.exportObstacle((Obstacle) currentObj, selectedFile);
              else  notif.display(this.getScene().getWindow(), "Illegal object type selected for saving");
              notif.display(this.getScene().getWindow(), "XML File saved successfully");
            } catch (NullPointerException ex) {
              notif.display(this.getScene().getWindow(), "The file was not saved as no filename was provided");
            } catch (Exception ex) {
              ex.printStackTrace();
              notif.display(this.getScene().getWindow(), ex.getMessage());
            }});

      HBox buttons = new HBox(editParam, addParam, removeParam, saveParam);
      buttons.setAlignment(Pos.CENTER);


      var dropAndButton = new VBox(hb, buttons);
      dropAndButton.getStyleClass().add("paramBox");
      hb.getStyleClass().add("paramBox");
      buttons.getStyleClass().add("paramBox");
      dropAndButton.setPrefSize(200, 30);

      getChildren().addAll(dropAndButton, parameterBox);

      if (selected != null) hashBox.getCbEntity().setValue(selected.toString());

      parameterBox.setNone();
    }

    protected HashChoiceBox<E> getHashBox(ArrayList<E> list) {
      return new HashChoiceBox<>(list, mainScene);
    }

    public void setListener(SetListener<E> listener) {
      hashBox.setSetListener(listener);
    }

    public ParameterBox<E> getParameterBox() {
      return parameterBox;
    }

    public void setParams(ArrayList<E> params) {
      hashBox.setParams(params);
    }

    public void setFields(E updatedObj) {
      if (updatedObj != null) parameterBox.setValues(updatedObj);
    }

    void addParam(E param) throws Exception {
      hashBox.addParam(param);
    }

    void removeParam(E param) throws Exception {
      hashBox.removeParam(param);
    }

    private void trySave() {

    }
  }

  public static class ObstacleParameterPane extends ParameterPane<Obstacle> {

    public ObstacleParameterPane(
        MainScene mainScene,
        ArrayList<Obstacle> list,
        Obstacle selected,
        ParameterBox<Obstacle> parameterBox,
        String name) {
      super(mainScene, list, selected, parameterBox, name);
      this.setMaxWidth(209);


      saveParam.setOnAction(
              e -> {
                var notif = new Notification();


                var popup = new Popup();
                var oneObst = new Button("Save selected obstacle");
                var allObst = new Button("Save all obstacles");
                var closeButton = new Button("X");

                var box = new VBox( closeButton,
                        new Label("Choose a save option"),
                        new HBox(oneObst, allObst));
                closeButton.setOnAction(ec -> popup.hide());

                box.setAlignment(Pos.CENTER);
                box.setPadding(new Insets(5));
                box.setBackground(
                        new Background(
                                new BackgroundFill(Paint.valueOf("White"), new CornerRadii(10), null)));
                box.setBorder(
                        new Border(
                                new BorderStroke(
                                        Paint.valueOf("Black"),
                                        BorderStrokeStyle.SOLID,
                                        new CornerRadii(10),
                                        new BorderWidths(2))));

                popup.getContent().add(box);


                oneObst.setOnAction(ec -> {
                try {
                  popup.hide();

                  FileChooser fileChooser = new FileChooser();
                  fileChooser.setTitle("Select a file to save to ");
                  fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter ("XML file", "*xml"));
                  File selectedFile = fileChooser.showSaveDialog(mainScene.getWindow());

                  var currentObj = this.parameterBox.getCurrent();

                  mainScene.obstacleExportListener.exportObstacle(currentObj, selectedFile);

                } catch (NullPointerException ex) {
                  notif.display(this.getScene().getWindow(), "The file was not saved as no filename was provided");
                } catch (Exception ex) {
                  ex.printStackTrace();
                  notif.display(this.getScene().getWindow(), ex.getMessage());
                }

                });

                allObst.setOnAction(ec -> {
                try{
                  popup.hide();

                  ArrayList<Obstacle> obstacles = new ArrayList<>(this.hashBox.getHashMap().values());

                  FileChooser fileChooser = new FileChooser();
                  fileChooser.setTitle("Select a file to save to ");
                  fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter ("XML file", "*xml"));
                  File selectedFile = fileChooser.showSaveDialog(mainScene.getWindow());

                  mainScene.obstaclesExportListener.exportObstacles(selectedFile, obstacles);

                } catch (NullPointerException ex) {
                  notif.display(this.getScene().getWindow(), "The file was not saved as no filename was provided");
                }
                catch (Exception ex) {
                  ex.printStackTrace();
                  notif.display(this.getScene().getWindow(), ex.getMessage());
                }
                });

                popup.show(mainScene.getWindow());
              });
    }

    @Override
    protected HashChoiceBox<Obstacle> getHashBox(ArrayList<Obstacle> list) {
      return new ObstacleHashChoiceBox(list, mainScene);
    }
  }

  /** Creates a box with all the parameters in, in Label: Text format */
  public abstract static class ParameterBox<E> extends VBox {
    ParameterPane<E> parameterPane;
    E currentObject;
    final Notification notif = new Notification();

    public ParameterBox() {
      this.setPadding(new Insets(5));
      this.setSpacing(5);
      this.setMinWidth(200);
      this.setMaxWidth(200);
      this.setPrefHeight(300);
    }

    void setNone() {
      if (parameterPane == null) {
        System.out.println("ParameterPane null!");
        return;
      }
      System.out.println("Attempting to set none");
      parameterPane.hashBox.setNone();
    }

    void setParameterPane(ParameterPane<E> parameterPane) {
      this.parameterPane = parameterPane;
    }

    abstract void addParam();

    abstract void editParam();

    abstract void removeParam();

    abstract void setValues(E updatedObj);

    abstract String getSavePopupText();

    abstract E getCurrent();

    public static HBox getHBox(String label, Text text) {
      var lbl = new Label(label);
      lbl.setPrefWidth(60);
      text.maxWidth(140);
      var hBox = new HBox(lbl, text);
      hBox.setSpacing(5);
      return hBox;
    }

    public static HBox getMidHBox(String label, Text text) {
      var lbl = new Label(label);
      lbl.setPrefWidth(90);
      text.maxWidth(110);
      var hBox = new HBox(lbl, text);
      hBox.setSpacing(5);
      return hBox;
    }

    public static HBox getLongHBox(String label, Text text) {
      var lbl = new Label(label);
      lbl.setPrefWidth(150);
      text.maxWidth(50);
      var hBox = new HBox(lbl, text);
      hBox.setSpacing(5);
      return hBox;
    }

    public static HBox getHBox(String label, Text text, Text text2) {
      var lbl = new Label(label);
      lbl.setPrefWidth(80);
      text.maxWidth(60);
      text.minWidth(60);
      text2.maxWidth(60);
      lbl.setAlignment(Pos.CENTER);
      var hBox = new HBox(text, lbl, text2);
      hBox.setSpacing(5);
      hBox.setAlignment(Pos.CENTER);
      return hBox;
    }

    public static HBox getBoldHBox(String label, Text text, Text text2) {
      var lbl = new Label(label);
      lbl.setPrefWidth(80);
      lbl.setStyle("-fx-font-weight: bold");
      text.maxWidth(60);
      text.minWidth(60);
      text.setStyle("-fx-font-weight: bold");
      text2.maxWidth(60);
      text2.setStyle("-fx-font-weight: bold");
      lbl.setAlignment(Pos.CENTER);
      var hBox = new HBox(text, lbl, text2);
      hBox.setSpacing(5);
      hBox.setAlignment(Pos.CENTER);
      return hBox;
    }
  }

  private static class AirportBox extends ParameterBox<Airport> {
    final Text name, runways, minAsc, blastDis;
    final Text noParam;
    final MainScene mainScene;

    public AirportBox(Airport a, MainScene mainScene) {
      this.mainScene = mainScene;
      this.setPrefHeight(250);
      noParam = new Text("Please select a parameter, save to update runways");
      noParam.setVisible(false);
      noParam.setWrappingWidth(200);
      if (a == null) {
        noParam.setVisible(true);
        a = new Airport();
      }
      this.currentObject = a;
      getChildren()
          .addAll(
              getHBox("Name:", name = new Text(a.getName())),
              getHBox("Runways:", runways = new Text(a.getRunways().size() + " loaded runways")),
              getMidHBox(
                  "Min ascent ratio:", minAsc = new Text(Float.toString(a.getMinAscentRatio()))),
              getMidHBox(
                  "Blast distance:", blastDis = new Text(Integer.toString(a.getBlastDistance()))),
              noParam);
      for (Node n: getChildren()) {
        n.getStyleClass().add("listBox");
      }
    }

    public void addParam() {
      var popup = new InputPopup.AirportInputPopup(mainScene);
      popup.show(mainScene.getWindow());
    }

    public void removeParam() {
      try {
        mainScene.getAirportRemoveListener().removeParam(null);
      } catch (Exception e) {
        // e.printStackTrace();
        notif.display(mainScene.getWindow(), e.getMessage());
      }
    }

    public void editParam() {
      var popup = new InputPopup.AirportInputPopup(mainScene);
      popup.setValues(currentObject);
      popup.show(mainScene.getWindow());
    }

    void setValues(Airport updatedObj) {
      if (updatedObj == null) {
        setNone();
        return;
      }
      noParam.setVisible(false);
      name.setText(updatedObj.getName());
      runways.setText(updatedObj.getRunways().size() + " loaded runways");
      minAsc.setText(Float.toString(updatedObj.getMinAscentRatio()));
      blastDis.setText(Integer.toString(updatedObj.getBlastDistance()));
      this.currentObject = updatedObj;
    }

    @Override
    void setNone() {
      super.setNone();
      name.setText("-");
      runways.setText("-");
      minAsc.setText("-");
      blastDis.setText("-");
    }

    public String getSavePopupText() {
      return "This will save the current airport parameters and all runways in the current list";
    }

    public Airport getCurrent() {
      return this.currentObject;
    }

  }

  private static class RunwayBox extends ParameterBox<Runway> {
    final MainScene mainScene;
    final Text name,
        heading,
        heading2,
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
        thresholdDisplace2;
    final Text noParam;

    public RunwayBox(Runway r, MainScene mainScene) {
      this.mainScene = mainScene;
      this.setPrefHeight(400);
      noParam = new Text("Please select a parameter");
      noParam.setVisible(false);
      if (r == null) {
        noParam.setVisible(true);
        r = new Runway();
      }
      this.currentObject = r;
      var log1 = r.getLogicalRunway1();
      var log2 = r.getLogicalRunway2();
      getChildren()
          .addAll(
              getHBox("Name:", name = new Text(r.getName())),
              getHBox("Strip end:", stripEnd = new Text(Integer.toString(r.getStripEnd()))),
              getHBox("RESA:", RESA = new Text(Integer.toString(r.getRESA()))),
              getBoldHBox(
                  "Heading:",
                  heading = new Text(Integer.toString(r.getHeading())),
                  heading2 = new Text(Integer.toString(r.getHeading() + 18))),
              getHBox(
                  "TORA:",
                  TORA1 = new Text(Integer.toString(log1.getOldTORA())),
                  TORA2 = new Text(Integer.toString(log2.getOldTORA()))),
              getHBox(
                  "TODA:",
                  TODA1 = new Text(Integer.toString(log1.getOldTODA())),
                  TODA2 = new Text(Integer.toString(log2.getOldTODA()))),
              getHBox(
                  "ASDA:",
                  ASDA1 = new Text(Integer.toString(log1.getOldASDA())),
                  ASDA2 = new Text(Integer.toString(log2.getOldASDA()))),
              getHBox(
                  "LDA:",
                  LDA1 = new Text(Integer.toString(log1.getOldLDA())),
                  LDA2 = new Text(Integer.toString(log2.getOldLDA()))),
              getHBox(
                  "Threshold:",
                  thresholdDisplace1 = new Text(Integer.toString(log1.getThresholdDisplacement())),
                  thresholdDisplace2 =
                      new Text(Integer.toString(log2.getThresholdDisplacement()))));
      for (Node n: getChildren()) {
        n.getStyleClass().add("listBox");
      }
    }

    void addParam() {
      if (mainScene.getAirport.getParam() == null) {
        var error = new Notification();
        error.display(mainScene.getWindow(), "Please set an airport first");
        return;
      }
      var popup = new InputPopup.RunwayInputPopup(mainScene);
      popup.show(mainScene.getWindow());
    }

    public void removeParam() {
      try {
        mainScene.getRunwayRemoveListener().removeParam(null);
      } catch (Exception e) {
        // e.printStackTrace();
        notif.display(mainScene.getWindow(), e.getMessage());
      }
    }

    public void editParam() {
      var popup = new InputPopup.RunwayInputPopup(mainScene);
      popup.setValues(currentObject);
      popup.show(mainScene.getWindow());
    }

    void setValues(Runway updatedObj) {
      if (updatedObj == null) {
        setNone();
        return;
      }
      var log1 = updatedObj.getLogicalRunway1();
      var log2 = updatedObj.getLogicalRunway2();
      noParam.setVisible(false);
      name.setText(updatedObj.getName());
      stripEnd.setText(Integer.toString(updatedObj.getStripEnd()));
      RESA.setText(Integer.toString(updatedObj.getRESA()));
      heading.setText(Integer.toString(updatedObj.getHeading()));
      heading2.setText(Integer.toString(updatedObj.getHeading() + 18));
      TORA1.setText(Integer.toString(log1.getOldTORA()));
      TORA2.setText(Integer.toString(log2.getOldTORA()));
      TODA1.setText(Integer.toString(log1.getOldTODA()));
      TODA2.setText(Integer.toString(log2.getOldTODA()));
      ASDA1.setText(Integer.toString(log1.getOldASDA()));
      ASDA2.setText(Integer.toString(log2.getOldASDA()));
      LDA1.setText(Integer.toString(log1.getOldLDA()));
      LDA2.setText(Integer.toString(log2.getOldLDA()));
      thresholdDisplace1.setText(Integer.toString(log1.getThresholdDisplacement()));
      thresholdDisplace2.setText(Integer.toString(log2.getThresholdDisplacement()));
      this.currentObject =  updatedObj;
    }

    @Override
    void setNone() {
      super.setNone();
      name.setText("-");
      stripEnd.setText("-");
      RESA.setText("-");
      heading.setText("-");
      heading2.setText("-");
      TORA1.setText("-");
      TORA2.setText("-");
      TODA1.setText("-");
      TODA2.setText("-");
      ASDA1.setText("-");
      ASDA2.setText("-");
      LDA1.setText("-");
      LDA2.setText("-");
      thresholdDisplace1.setText("-");
      thresholdDisplace2.setText("-");
    }

    public String getSavePopupText() {
      return "This will save the current runway parameters (without the obstacle)";
    }

    Runway getCurrent() {
      return currentObject;
    }
  }

  private static class ObstacleBox extends ParameterBox<Obstacle> {
    final Text name, height, width, length;
    final Text dThreshold1, dThreshold2, dCenter;
    final Text noParam;
    final MainScene mainScene;

    public ObstacleBox(Obstacle o, MainScene mainScene) {
      this.mainScene = mainScene;
      this.setPrefHeight(250);
      noParam = new Text("Please select a parameter");
      noParam.setVisible(false);
      getStyleClass().add("listBox");
      if (o == null) {
        noParam.setVisible(true);
        o = new Obstacle();
      }
      this.currentObject = o;
      getChildren()
          .addAll(
              getHBox("Name:", name = new Text(o.getName())),
              getHBox("Height:", height = new Text(Integer.toString(o.getHeight()))),
              getHBox("Width:", width = new Text(Integer.toString(o.getWidth()))),
              getHBox("Length", length = new Text(Integer.toString(o.getLength()))),
              new Rectangle(200, 10),
              getLongHBox(
                  "Distance to lower threshold",
                  dThreshold1 = new Text(Integer.toString(o.getdThreshold1()))),
              getLongHBox(
                  "Distance to lower threshold",
                  dThreshold2 = new Text(Integer.toString(o.getdThreshold2()))),
              getLongHBox(
                  "Distance to center line", dCenter = new Text(Integer.toString(o.getdCenter()))));
      for (Node n: getChildren()) {
        n.getStyleClass().add("listBox");
      }
    }

    public void addParam() {
      if (mainScene.getRunway.getParam() == null) {
        var error = new Notification();
        error.display(mainScene.getWindow(), "Please set a runway first");
        return;
      }
      var popup = new InputPopup.ObstacleInputPopup(mainScene);
      popup.show(mainScene.getWindow());
    }

    public void removeParam() {
      try {
        mainScene.getObstacleRemoveListener().removeParam(null);
      } catch (Exception e) {
        // e.printStackTrace();
        notif.display(mainScene.getWindow(), e.getMessage());
      }
    }

    public void editParam() {
      var popup = new InputPopup.ObstacleInputPopup(mainScene);
      popup.setValues(currentObject);
      popup.show(mainScene.getWindow());
    }

    void setValues(Obstacle updatedObj) {
      if (updatedObj == null) {
        setNone();
        return;
      }
      noParam.setVisible(false);
      name.setText(updatedObj.getName());
      height.setText(Integer.toString(updatedObj.getHeight()));
      width.setText(Integer.toString(updatedObj.getWidth()));
      length.setText(Integer.toString(updatedObj.getLength()));
      dThreshold1.setText(Integer.toString(updatedObj.getdThreshold1()));
      dThreshold2.setText(Integer.toString(updatedObj.getdThreshold2()));
      dCenter.setText(Integer.toString(updatedObj.getdCenter()));
      this.currentObject = updatedObj;
    }

    @Override
    void setNone() {
      super.setNone();
      name.setText("-");
      height.setText("-");
      width.setText("-");
      length.setText("-");
      dThreshold1.setText("-");
      dThreshold2.setText("-");
      dCenter.setText("-");
    }

    public String getSavePopupText() {
      return "This will save the current obstacle parameters";
    }

    @Override
    Obstacle getCurrent() {
      return this.currentObject;
    }
  }

  public static class OutputBox extends ParameterBox<Runway> {
    final Text name,
        heading,
        heading2,
        TORA1,
        TODA1,
        ASDA1,
        LDA1,
        thresholdDisplace1,
        TORA2,
        TODA2,
        ASDA2,
        LDA2,
        thresholdDisplace2;
    SimpleStringProperty TORA1calc,
        TORA2calc,
        TODA1calc,
        TODA2calc,
        ASDA1calc,
        ASDA2calc,
        LDA1calc,
        LDA2calc;
    final Text noParam;
    final MainScene mainScene;
    PDFExportListener PDFExportListener;
    Button export = new Button("Export Results");

    public OutputBox(Runway r, MainScene mainScene) {
      this.mainScene = mainScene;
      this.setPrefHeight(400);
      noParam = new Text("Please select a parameter");
      noParam.setVisible(false);
      export.setVisible(true);
      if (r == null) {
        noParam.setVisible(true);
        export.setVisible(false);
            r = new Runway();
      }
      var expBox = new HBox(export);
      expBox.setMinWidth(200);
      expBox.setMinHeight(100);
      expBox.setAlignment(Pos.BOTTOM_CENTER);
      export.setOnAction(e -> export());

      var log1 = r.getLogicalRunway1();
      var log2 = r.getLogicalRunway2();
      Label hoverForCalc = new Label("Hover over a result to see its calculation");
      hoverForCalc.setWrapText(true);

      Button viewAllCalc = new Button("View all calculations");
      VBox calcButtonBox = new VBox(viewAllCalc);
      calcButtonBox.setAlignment(Pos.CENTER);
      getChildren()
          .addAll(
              getHBox("Name:", name = new Text(r.getName())),
              getBoldHBox(
                  "Heading:",
                  heading = new Text(Integer.toString(r.getHeading())),
                  heading2 = new Text(Integer.toString(r.getHeading() + 18))),
              getHBox(
                  "TORA:",
                  TORA1 = new Text(Integer.toString(log1.getNewTORA())),
                  TORA2 = new Text(Integer.toString(log2.getNewTORA()))),
              getHBox(
                  "TODA:",
                  TODA1 = new Text(Integer.toString(log1.getNewTODA())),
                  TODA2 = new Text(Integer.toString(log2.getNewTODA()))),
              getHBox(
                  "ASDA:",
                  ASDA1 = new Text(Integer.toString(log1.getNewASDA())),
                  ASDA2 = new Text(Integer.toString(log2.getNewASDA()))),
              getHBox(
                  "LDA:",
                  LDA1 = new Text(Integer.toString(log1.getNewLDA())),
                  LDA2 = new Text(Integer.toString(log2.getNewLDA()))),
              getHBox(
                  "Threshold:",
                  thresholdDisplace1 = new Text(Integer.toString(log1.getThresholdDisplacement())),
                  thresholdDisplace2 = new Text(Integer.toString(log2.getThresholdDisplacement()))),
              hoverForCalc, calcButtonBox, expBox);
      bindCalcText(TORA1, TORA1calc = new SimpleStringProperty("Not calculated"));
      bindCalcText(TORA2, TORA2calc = new SimpleStringProperty("Not calculated"));
      bindCalcText(TODA1, TODA1calc = new SimpleStringProperty("Not calculated"));
      bindCalcText(TODA2, TODA2calc = new SimpleStringProperty("Not calculated"));
      bindCalcText(TORA1, TORA1calc = new SimpleStringProperty("Not calculated"));
      bindCalcText(ASDA1, ASDA1calc = new SimpleStringProperty("Not calculated"));
      bindCalcText(ASDA2, ASDA2calc = new SimpleStringProperty("Not calculated"));
      bindCalcText(LDA1, LDA1calc = new SimpleStringProperty("Not calculated"));
      bindCalcText(LDA2, LDA2calc = new SimpleStringProperty("Not calculated"));
      setNone();
      viewAllCalc.setOnAction(
          e -> {
            var popup = new Popup();
            Button button = new Button("X");
            var vBox = new VBox(10.0);
            button.setOnAction(event -> popup.hide());
            vBox.setPrefWidth(50);
            Text label1 =
                new Text(
                    "TORA = Original TORA - Blast Protection - Distance From Threshold - Threshold Displacement");
            Text label2 = new Text(TORA1calc.get());
            label2.setWrappingWidth(500);
            label1.setWrappingWidth(500);
            Text label3 = new Text("TODA = TORA + STOPWAY");
            Text label4 = new Text(TODA1calc.get());
            label3.setWrappingWidth(500);
            label4.setWrappingWidth(500);
            Text label5 = new Text("ASDA = TORA + CLEARWAY");
            Text label6 = new Text(ASDA1calc.get());
            label5.setWrappingWidth(500);
            label6.setWrappingWidth(500);
            Text label7 =
                new Text(
                    "LDA = Original LDA - Distance from Threshold - Strip End - Slope Calculation");
            Text label8 = new Text(LDA1calc.get());
            label7.setWrappingWidth(500);
            label8.setWrappingWidth(500);

            vBox.getChildren()
                .addAll(label1, label2, label3, label4, label5, label6, label7, label8, button);
            vBox.setAlignment(Pos.CENTER);
            vBox.setPadding(new Insets(5));
            vBox.setBackground(
                new Background(
                    new BackgroundFill(Paint.valueOf("White"), new CornerRadii(10), null)));
            vBox.setBorder(
                new Border(
                    new BorderStroke(
                        Paint.valueOf("Black"),
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(10),
                        new BorderWidths(2))));
            popup.getContent().add(vBox);
            popup.show(this.getScene().getWindow());
          });
    }

    // Should not be called in this class
    public void addParam() {}

    public void removeParam() {}

    public void editParam() {}

    public void bindCalcText(Text text, SimpleStringProperty calcText) {
      var calcPopup = new Popup();
      var calcLabel = new Text();
      calcLabel.textProperty().bindBidirectional(calcText);
      var calcBox = new VBox(calcLabel);
      calcBox.setPrefSize(150, 50);
      calcBox.setPadding(new Insets(10));
      calcBox.setAlignment(Pos.CENTER);
      calcBox.setBackground(
          new Background(new BackgroundFill(Paint.valueOf("WHITE"), new CornerRadii(20), null)));
      calcBox.setBorder(
          new Border(
              new BorderStroke(
                  Paint.valueOf("Black"), BorderStrokeStyle.SOLID, new CornerRadii(20), null)));
      calcPopup.getContent().add(calcBox);

      text.setOnMouseEntered(
          e -> {
            System.out.println("Popup showing " + calcText.get());
            calcPopup.show(mainScene.getWindow());
            calcPopup.centerOnScreen();
          });
      text.setOnMouseExited(e -> calcPopup.hide());
    }

    void setValues(Runway updatedObj) {
      if (updatedObj == null) {
        setNone();
        return;
      }
      var log1 = updatedObj.getLogicalRunway1();
      var log2 = updatedObj.getLogicalRunway2();
      noParam.setVisible(false);
      export.setVisible(true);
      name.setText(updatedObj.getName());
      heading.setText(Integer.toString(updatedObj.getHeading()));
      heading2.setText(Integer.toString(updatedObj.getHeading() + 18));
      TORA1.setText(Integer.toString(log1.getNewTORA()));
      TORA1calc.set(log1.getTORACalcBreakdown());
      TORA2.setText(Integer.toString(log2.getNewTORA()));
      TORA2calc.set(log2.getTORACalcBreakdown());
      TODA1.setText(Integer.toString(log1.getNewTODA()));
      TODA1calc.set(log1.getTODACalcBreakdown());
      TODA2.setText(Integer.toString(log2.getNewTODA()));
      TODA2calc.set(log2.getTODACalcBreakdown());
      ASDA1.setText(Integer.toString(log1.getNewASDA()));
      ASDA1calc.set(log1.getASDACalcBreakdown());
      ASDA2.setText(Integer.toString(log2.getNewASDA()));
      ASDA2calc.set(log2.getASDACalcBreakdown());
      LDA1.setText(Integer.toString(log1.getNewLDA()));
      LDA1calc.set(log1.getLDACalcBreakdown());
      LDA2.setText(Integer.toString(log2.getNewLDA()));
      LDA2calc.set(log2.getLDACalcBreakdown());
      thresholdDisplace1.setText(Integer.toString(log1.getThresholdDisplacement()));
      thresholdDisplace2.setText(Integer.toString(log2.getThresholdDisplacement()));
    }

    @Override
    void setNone() {
      super.setNone();
      name.setText("-");
      heading.setText("-");
      heading2.setText("-");
      TORA1.setText("-");
      TORA1calc.set("Not calculated");
      TORA2.setText("-");
      TORA2calc.set("Not calculated");
      TODA1.setText("-");
      TODA1calc.set("Not calculated");
      TODA2.setText("-");
      TODA2calc.set("Not calculated");
      ASDA1.setText("-");
      ASDA1calc.set("Not calculated");
      ASDA2.setText("-");
      ASDA2calc.set("Not calculated");
      LDA1.setText("-");
      LDA1calc.set("Not calculated");
      LDA2.setText("-");
      LDA2calc.set("Not calculated");
      thresholdDisplace1.setText("-");
      thresholdDisplace2.setText("-");
    }

    public String getSavePopupText() {
      return "This will save the current runway with the current obstacle (redeclared values)";
    }

    public void setExportListener(PDFExportListener listener) {
      this.PDFExportListener = listener;
    }

    /**
     * Exports the current recalculation results to a PDF file using FileChooser
     */
    private void export() {
      try {
        FileChooser dirChooser = new FileChooser();
        dirChooser.setTitle("Select a location and enter filename to save results to");
        dirChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter ("PDF File", "*.pdf"));
        File selected = dirChooser.showSaveDialog(mainScene.getWindow());
        if (selected == null) {  // If FileChooser cancelled
          throw new Exception("File Explorer closed");
        } else {
          int ind = selected.getName().lastIndexOf('.');
          if (ind == -1) {  // No file extension
            selected = new File(selected.getParent(), selected.getName()+".pdf");
          } else if (!selected.getName().substring(ind).equals("pdf")) {  // Extension exists but not lowercase "pdf"
            selected = new File(selected.getParent(), selected.getName().substring(0,ind)+".pdf");
          }
        }
        PDFExportListener.export(selected);
      } catch (Exception e) {
        notif.display(mainScene.getWindow(), e.getMessage());
      }
    }

    Runway getCurrent() {
      return null;
    }
  }

  /**
   * Create a drop-down menu that has a listener to choose Runway/Airport/Obstacle
   *
   * @param <E> Type of the box: Airport, Runway or Obstacle
   */
  public static class HashChoiceBox<E> {
    protected final HashMap<String, E> hashMap;
    protected final ChoiceBox<String> cb;
    protected SetListener<E> listener = null;
    protected final MainScene mainScene;

    public HashChoiceBox(ArrayList<E> choices, MainScene mainScene) {
      hashMap = new HashMap<>();
      this.mainScene = mainScene;
      if (choices != null) {
        var stream = choices.stream();
        stream.forEach(x -> hashMap.put(x.toString(), x));
      }
      // hashMap.put(null, null); // IDK why this is here but no null items in list plz
      var keys = FXCollections.observableArrayList(hashMap.keySet());

      cb = new ChoiceBox<>(FXCollections.observableArrayList(keys));
      cb.setPrefSize(100, 20);
      cb.setOnAction(e -> System.out.println("No listener set"));
    }

    public ChoiceBox<String> getCbEntity() {
      return cb;
    }

    public void addParam(E param) throws Exception {
      boolean conflictingName = hashMap.containsKey(param.toString());
      if (conflictingName) throw new Exception("Repeated key: \"" + param + "\"");
      hashMap.put(param.toString(), param);
      cb.setValue(param.toString());
      cb.getItems().add(param.toString());
      cb.getItems().sort(String::compareTo);
      // cb.getItems().sort((String s1,String s2)-> -s1.compareTo(s2));// Reversed order testing
    }

    public void removeParam(E param) throws Exception {
      boolean nameExists = hashMap.containsKey(param.toString());
      if (!nameExists) {
        throw new Exception("No key found: \"" + param + "\"");
      }
      hashMap.remove(param.toString(), param);
      cb.getItems().remove(param.toString());
      this.setNone();
    }

    public void setParams(ArrayList<E> params) {
      ArrayList<String> keys = new ArrayList<>(hashMap.keySet());
      for (String curr : keys) {
        hashMap.remove(curr);
        cb.getItems().remove(curr);
      }
      cb.setOnAction(e -> {});
      for (E param : params) {
        System.out.println("Adding run: " + param.toString());
        try {
          addParam(param);
        } catch (Exception e) {
        }
      }
      setNone();
    }

    public void setNone() {
      cb.setOnAction(e -> {});
      cb.setValue(null);
      setSetListener(listener);
    }

    public HBox getChoiceBox(String name) {
      var nameLabel = new Label(name);
      nameLabel.setPrefWidth(60);
      return new HBox(nameLabel, cb);
    }

    public HashMap<String, E> getHashMap() {
      return hashMap;
    }

    public void setSetListener(SetListener<E> listener) {
      if (listener == null) return;
      this.listener = listener;
      cb.setOnAction(
          e -> {
            try {
              listener.setParameter(hashMap.get(cb.getValue()));
            } catch (Exception ex) {
              var notif = new Notification();
              ex.printStackTrace();
              notif.display(mainScene.getWindow(), ex.getMessage());
            }
          });
    }
  }

  public static class ObstacleHashChoiceBox extends HashChoiceBox<Obstacle> {

    public ObstacleHashChoiceBox(ArrayList<Obstacle> choices, MainScene mainScene) {
      super(choices, mainScene);
    }

    @Override
    public void addParam(Obstacle param) throws Exception {
      cb.setOnAction(e -> System.out.println("Adding new param"));
      super.addParam(param);
      setSetListener(listener);
    }

    @Override
    public void removeParam(Obstacle param) throws Exception {
      cb.setOnAction(e -> System.out.println("Removing param"));
      super.removeParam(param);
      setSetListener(listener);
    }

    @Override
    public void setSetListener(SetListener<Obstacle> listener) {
      if (listener == null) return;
      this.listener = listener;
      cb.setOnAction(
          e -> {
            try {
              listener.setParameter(hashMap.get(cb.getValue()));
              var popup =
                  new InputPopup.ObstacleSetInputPopup(
                      mainScene, hashMap.get(cb.getValue()), listener);
              popup.show(mainScene.getWindow());
            } catch (Exception ex) {
              var notif = new Notification();
              notif.display(mainScene.getWindow(), ex.getMessage());
            }
          });
    }
  }
}
