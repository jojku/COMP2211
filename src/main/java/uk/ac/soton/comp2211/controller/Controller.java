package uk.ac.soton.comp2211.controller;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp2211.model.*;
import uk.ac.soton.comp2211.view.MainScene;
import uk.ac.soton.comp2211.view.Notification;
import uk.ac.soton.comp2211.view.StartMenu;

import java.io.File;
import java.util.ArrayList;

/** Application controller Handles communication between the model and view */
public class Controller {
  private static final Logger logger = LogManager.getLogger(Controller.class);

  // Pointer to main class of scene and model
  final MainScene mainScene;
  final StartMenu startMenu;
  final MasterModel masterModel;
  final Stage stage;

  /**
   * Create a new controller (handles initialising the view and model)
   *
   * @param uiWidth width of the UI
   * @param uiHeight height of the UI
   */
  public Controller(int uiWidth, int uiHeight, Stage stage) {
    // Initialise a MainScene and MasterModel
    masterModel = new MasterModel();
    mainScene = new MainScene(new GridPane(), uiWidth, uiHeight);
    startMenu = new StartMenu(new StackPane(), uiWidth, uiHeight);
    this.stage = stage;

    // Bind all listeners between the model and view
    setViewListeners();
    setModelListeners();

    mainScene.initialiseScene();
    //inputMasterModelParams();

    logger.info("Controller initialised");
  }

  /** Bind all the listeners from the view to the model (when buttons are clicked, etc.) */
  private void setViewListeners() {
    mainScene.setRedeclareListener(masterModel::redeclare);
    mainScene.setExportListener(masterModel::export);

    mainScene.setAirportCreationListener(masterModel::createAirport);
    mainScene.setRunwayCreationListener(masterModel::createRunway);
    mainScene.setObstacleCreationListener(masterModel::createObstacle);

    mainScene.setObstacleImportListener(masterModel::importObstacles);
    mainScene.setRunwayImportListener(masterModel :: importRunway);
    mainScene.setAirportImportListener(masterModel::importAirport);

    mainScene.setObstacleExportListener(masterModel::exportObstacle);
    mainScene.setObstaclesExportListener(masterModel::exportObstacles);
    mainScene.setRunwayExportListener(masterModel::exportRunway);
    mainScene.setAirportExportListener(masterModel::exportAirport);

    mainScene.setAirportSetListener(masterModel::setCurrentAirport);
    mainScene.setRunwaySetListener(masterModel::setCurrentRunway);
    mainScene.setObstacleSetListener(masterModel::setCurrentObstacle);

    mainScene.setAirportRemoveListener(masterModel::removeCurrentAirport);
    mainScene.setRunwayRemoveListener(masterModel::removeCurrentRunway);
    mainScene.setObstacleRemoveListener(masterModel::removeCurrentObstacle);

    mainScene.setGetAirport(masterModel::getCurrentAirport);
    mainScene.setGetRunway(masterModel::getCurrentRunway);
    mainScene.setGetObstacle(masterModel::getCurrentObstacle);

    startMenu.setStartNormal(this::inputMasterModelParams);
    startMenu.setStartImportListener(this::inputFromImport);
    startMenu.setStartWithNothingListener(this::inputNothing);
  }

  /**
   * Bind all the listeners from the model to the view (when new parameters are successfully
   * selected, etc.)
   */
  private void setModelListeners() {
    masterModel.setSetAirportListener(mainScene::setAirport);
    masterModel.setSetRunwayListener(mainScene::setRunway);
    masterModel.setSetObstacleListener(mainScene::setObstacle);

    masterModel.setCreateAirportListener(mainScene::createNewAirport);
    masterModel.setCreateRunwayListener(mainScene::createNewRunway);
    masterModel.setCreateObstacleListener(mainScene::createNewObstacle);

    masterModel.setAirportRemoveListener(mainScene::removeAirport);
    masterModel.setRunwayRemoveListener(mainScene::removeRunway);
    masterModel.setObstacleRemoveListener(mainScene::removeObstacle);
  }


  private void createObstacles() throws Exception{
    masterModel.createObstacle(new Obstacle("Standing bus", 12, 3, 3));
    masterModel.createObstacle(new Obstacle("Tower", 25, 2, 4));
    masterModel.createObstacle(new Obstacle("Pole", 15, 1, 1));
    masterModel.createObstacle(new Obstacle("Catering truck", 6,3,5));
    masterModel.createObstacle(new Obstacle("Large Box", 3, 2, 2));
  }
  private void inputMasterModelParams() {
    try {
      masterModel.createAirport(new Airport("Heathrow", new ArrayList<>(), 50, 300));

      masterModel.createRunway(new Runway(9,'L',60, 240,3902,3902,3902,3595,306,3884,3962,3884,3884,0));
      masterModel.createRunway(new Runway(9,'R',60,240,3660,3660,3660,3353,307,3660,3660,3660,3660,0));
      System.out.println("Making obstacles");
      this.createObstacles();

      stage.setScene(mainScene);
      mainScene.runAfterShow();
    } catch (Exception e) {
      // Shouldn't happen
      e.printStackTrace();
      var notif = new Notification();
      notif.display(startMenu.getWindow(), e.getMessage());
      System.out.println("Making failed");
    }
  }

  private void inputFromImport(File file) {
    var notif = new Notification();
    try {
      masterModel.importAirport(file);
      this.createObstacles();

      stage.setScene(mainScene);
      mainScene.runAfterShow();
      notif.display(mainScene.getWindow(), "XML File imported successfully");
    } catch (Exception e) {
      notif.display(startMenu.getWindow(), e.getMessage());
    }
  }

  private void inputNothing() {
    try {
      this.createObstacles();
      stage.setScene(mainScene);
    } catch (Exception e) {
      e.printStackTrace();
      var notif = new Notification();
      notif.display(startMenu.getWindow(), e.getMessage());
    }
    stage.setScene(mainScene);
    mainScene.runAfterShow();
  }

  /**
   * Get the Main Scene for the Application to bind to the stage
   *
   * @return MainScene
   */
  public MainScene getScene() {
    return mainScene;
  }

  public StartMenu getStartMenu() { return  startMenu; }
}
