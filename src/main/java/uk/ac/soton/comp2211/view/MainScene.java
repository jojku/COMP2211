package uk.ac.soton.comp2211.view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp2211.controller.listeners.*;
import uk.ac.soton.comp2211.model.Airport;
import uk.ac.soton.comp2211.model.Obstacle;
import uk.ac.soton.comp2211.model.Runway;

public class MainScene extends Scene {


  public CSSLoader getLoader() {
    return loader;
  }

  private CSSLoader loader;

  private static final Logger logger = LogManager.getLogger(MainScene.class);

  CreationListener<Airport> airportCreationListener;
  CreationListener<Runway> runwayCreationListener;
  CreationListener<Obstacle> obstacleCreationListener;

  ObstacleImportListener obstacleImportListener;
  RunwayImportListener runwayImportListener;
  AirportImportListener airportImportListener;

  ObstacleExportListener obstacleExportListener;
  ObstaclesExportListener obstaclesExportListener;
  RunwayExportListener runwayExportListener;
  AirportExportListener airportExportListener;

  SetListener<Airport> airportSetListener;
  SetListener<Runway> runwaySetListener;
  SetListener<Obstacle> obstacleSetListener;

  RemoveListener<Airport> airportRemoveListener;
  RemoveListener<Runway> runwayRemoveListener;
  RemoveListener<Obstacle> obstacleRemoveListener;

  GetParamListener<Airport> getAirport;
  GetParamListener<Runway> getRunway;
  GetParamListener<Obstacle> getObstacle;

  RedeclareListener redeclareListener;
  PDFExportListener PDFExportListener;

  final Button calculateButton;
  final GridPane mainPane;
  UIPaneBuilder.ParameterPane<Airport> airportPane;
  UIPaneBuilder.ParameterBox<Airport> airportBox;
  UIPaneBuilder.ParameterPane<Runway> runwayPane;
  UIPaneBuilder.ParameterBox<Runway> runwayBox;
  UIPaneBuilder.ParameterBox<Obstacle> obstacleBox;
  UIPaneBuilder.ParameterPane<Obstacle> obstaclePane;
  UIPaneBuilder.OutputBox outputBox;
  private SwitchPane switchPane;

  final Notification notif = new Notification();

  /**
   * Create a new scene to be displayed
   */
  public MainScene(GridPane pane, int width, int height) {
    super(pane, width, height, Color.BLACK);
    logger.info("Creating Main Scene");
    calculateButton = new Button("Calculate");
    mainPane = pane;
  }

  public void initialiseScene() {
    // represents whole left side of screen
    logger.info("Initialising main scene");

    airportPane = UIPaneBuilder.airportParameterPane(this, null, null, getAirportSetListener());
    airportPane.setPrefSize(200, 300);
    airportPane.getStyleClass().add("paramBox");
    airportBox = airportPane.getParameterBox();

    runwayPane = UIPaneBuilder.runwayParameterPane(this, null, null, getRunwaySetListener());
    runwayPane.setPrefSize(200, 500);
    airportPane.getStyleClass().add("paramBox");
    runwayBox = runwayPane.getParameterBox();

    obstaclePane = UIPaneBuilder.obstacleParameterPane(this, null, null, getObstacleSetListener());
    obstaclePane.setPrefSize(200, 200);
    obstaclePane.getStyleClass().add("paramBox");
    obstacleBox = obstaclePane.getParameterBox();

    outputBox = new UIPaneBuilder.OutputBox(null, this);
    outputBox.getStyleClass().add("paramBox");
    outputBox.setExportListener(PDFExportListener);

    calculateButton.setOnAction(e -> calcButtonPressed());
    StackPane calcButton = new StackPane(calculateButton);
    calcButton.setAlignment(Pos.BOTTOM_CENTER);
    calcButton.setPrefSize(300, 300);

    logger.info("building view");
    var topView = new TopView(this);
    var sideView = new SideView(this);
    switchPane = new SwitchPane(this, topView, sideView);
    StackPane.setAlignment(switchPane, Pos.CENTER);

    var column0 = new ColumnConstraints(210);
    var column1 = new ColumnConstraints(this.getWidth() - 420);
    var column2 = new ColumnConstraints(210);
    var row0 = new RowConstraints(200);
    row0.setPercentHeight(35);
    mainPane.setPadding(new Insets(0, 100, 0, 100)); //Just the left and right insets
    mainPane.getColumnConstraints().addAll(column0, column1, column2);
    mainPane.getRowConstraints().addAll(row0);
    mainPane.add(airportPane, 0, 0);
    mainPane.add(runwayPane, 0, 1);
    mainPane.add(obstaclePane, 2, 0);
    mainPane.add(switchPane, 1, 0);
    GridPane.setRowSpan(switchPane, 2);
    mainPane.add(calcButton, 1, 1);
    mainPane.add(outputBox, 2, 1);
    mainPane.setAlignment(Pos.CENTER);

    for (Node child : mainPane.getChildren()
    ) {
      GridPane.setHalignment(child, HPos.CENTER);
    }

    loader = new CSSLoader(this, "/default.css");
  }

  public void runAfterShow() {
    switchPane.initializeTransformers();
  }

  private void calcButtonPressed() {
    var redeclaredRunway = redeclareListener.redeclare();
    outputBox.setValues(redeclaredRunway);
    // call method in SwitchPane
    switchPane.redeclareCalled();
  }

  public void createNewAirport(Airport airport) throws Exception {
    airportPane.addParam(airport);
    runwayPane.setParams(airport.getRunways());
  }

  public void createNewRunway(Runway runway) throws Exception {
    runwayPane.addParam(runway);
  }

  public void createNewObstacle(Obstacle obstacle) throws Exception {
    obstaclePane.addParam(obstacle);
    switchPane.setObstacle(obstacle);
  }

  public void setRunway(Runway runway) {
    runwayBox.setValues(runway);
    switchPane.setRunway(runway);
  }

  public void setAirport(Airport airport) {
    airportBox.setValues(airport);
    runwayPane.setParams(airport.getRunways());
  }

  public void setObstacle(Obstacle obstacle) {
    obstacleBox.setValues(obstacle);
    switchPane.setObstacle(obstacle);
  }

  public void removeAirport(Airport airport) throws Exception {
    airportPane.removeParam(airport);
  }

  public void removeRunway(Runway runway) throws Exception {
    runwayPane.removeParam(runway);
    switchPane.setRunway(null);
  }

  public void removeObstacle(Obstacle obstacle) throws Exception {
    obstaclePane.removeParam(obstacle);
    switchPane.setObstacle(null);
  }

  // Get the listeners for setting a parameter
  public SetListener<Airport> getAirportSetListener() {
    return airportSetListener;
  }

  public SetListener<Runway> getRunwaySetListener() {
    return runwaySetListener;
  }

  public SetListener<Obstacle> getObstacleSetListener() {
    return obstacleSetListener;
  }

  // Get the listeners for removing a parameter
  public RemoveListener<Airport> getAirportRemoveListener() {
    return airportRemoveListener;
  }

  public RemoveListener<Runway> getRunwayRemoveListener() {
    return runwayRemoveListener;
  }

  public RemoveListener<Obstacle> getObstacleRemoveListener() {
    return obstacleRemoveListener;
  }

  // Setters for the controller listeners to use below

  // Set listeners for when a parameter is created
  public void setAirportCreationListener(CreationListener<Airport> listener) {
    this.airportCreationListener = listener;
  }

  public void setRunwayCreationListener(CreationListener<Runway> listener) {
    this.runwayCreationListener = listener;
  }

  public void setObstacleCreationListener(CreationListener<Obstacle> listener) {
    this.obstacleCreationListener = listener;
  }

  // Set listeners for when a parameter is imported
  public void setObstacleImportListener(ObstacleImportListener listener) {
    this.obstacleImportListener = listener;
  }

  public void setRunwayImportListener(RunwayImportListener listener) {
    this.runwayImportListener = listener;
  }

  public void setAirportImportListener(AirportImportListener listener) {
    this.airportImportListener = listener;
  }

  // Set listeners for when a parameter is exported
  public void setObstacleExportListener(ObstacleExportListener listener) {
    this.obstacleExportListener = listener;
  }

  public void setObstaclesExportListener(ObstaclesExportListener listener) {
    this.obstaclesExportListener = listener;
  }


  public void setRunwayExportListener(RunwayExportListener listener) {
    this.runwayExportListener = listener;
  }

  public void setAirportExportListener(AirportExportListener listener) {
    this.airportExportListener = listener;
  }

  // Set listeners for when a new parameter is set
  public void setAirportSetListener(SetListener<Airport> airportSetListener) {
    this.airportSetListener = airportSetListener;
  }

  public void setRunwaySetListener(SetListener<Runway> runwaySetListener) {
    this.runwaySetListener = runwaySetListener;
  }

  public void setObstacleSetListener(SetListener<Obstacle> obstacleSetListener) {
    this.obstacleSetListener = obstacleSetListener;
  }

  // Set listeners for when a parameter is removed
  public void setAirportRemoveListener(RemoveListener<Airport> airportRemoveListener) {
    this.airportRemoveListener = airportRemoveListener;
  }

  public void setRunwayRemoveListener(RemoveListener<Runway> runwayRemoveListener) {
    this.runwayRemoveListener = runwayRemoveListener;
  }

  public void setObstacleRemoveListener(RemoveListener<Obstacle> obstacleRemoveListener) {
    this.obstacleRemoveListener = obstacleRemoveListener;
  }

  public void setGetAirport(GetParamListener<Airport> getAirport) {
    this.getAirport = getAirport;
  }

  public void setGetRunway(GetParamListener<Runway> getRunway) {
    this.getRunway = getRunway;
  }

  public void setGetObstacle(GetParamListener<Obstacle> getObstacle) {
    this.getObstacle = getObstacle;
  }

  /**
   * Set the listener for when the calculate button is set
   *
   * @param listener listener
   */
  public void setRedeclareListener(RedeclareListener listener) {
    this.redeclareListener = listener;
  }

  public void setExportListener(PDFExportListener listener) {
    this.PDFExportListener = listener;
  }
}


