package uk.ac.soton.comp2211.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp2211.controller.listeners.*;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MasterModel {

  private static final Logger logger = LogManager.getLogger(MasterModel.class);

  ArrayList<Obstacle> obstacles;
  ArrayList<Airport> airports;
  Airport currentAirport;
  Runway currentRunway;
  Obstacle currentObstacle;

  SetListener<Airport> setAirportListener;
  SetListener<Runway> setRunwayListener;
  SetListener<Obstacle> setObstacleListener;

  CreationListener<Airport> createAirportListener;
  CreationListener<Runway> createRunwayListener;
  CreationListener<Obstacle> createObstacleListener;

  RemoveListener<Airport> airportRemoveListener;
  RemoveListener<Runway> runwayRemoveListener;
  RemoveListener<Obstacle> obstacleRemoveListener;

  PDFHandler pdfHandler;

  public MasterModel() {
    logger.info("Starting Master Model");

    this.currentRunway = null;
    this.currentAirport = null;
    this.currentObstacle = null;
    this.obstacles = new ArrayList<>();
    this.airports = new ArrayList<>();

    this.pdfHandler = new PDFHandler();
  }

  /**
   * Handle when the calculate button is pressed
   */
  public Runway redeclare() {
    if (currentRunway == null || currentAirport == null) return null;
    currentRunway.redeclare(currentAirport.getMinAscentRatio(), currentAirport.getBlastDistance());
    logger.info(currentRunway.getName() + " was redeclared");

    return currentRunway;
  }

  public void export(File location) throws Exception {
    pdfHandler.export(location);
    logger.info("Exporting results");
  }

  //Handle creating new parameters
  public void createAirport(Airport newAirport) throws Exception {
    createAirportListener.createParameter(newAirport);

    airports.add(newAirport);
    this.setCurrentAirport(newAirport);
    logger.info(newAirport.getName() + "created");
  }
  public void createRunway(Runway newRunway) throws Exception {
    createRunwayListener.createParameter(newRunway);

    currentAirport.addRunway(newRunway);
    logger.info(newRunway.getName() + " added to " + currentAirport.getName());
    this.setCurrentRunway(newRunway);
    logger.info("current runway: " + currentRunway.getName());
  }
  public void createObstacle(Obstacle newObstacle) throws Exception {
    createObstacleListener.createParameter(newObstacle);

    obstacles.add(newObstacle);
    logger.info(newObstacle.getName() + " created");
    setCurrentObstacle(newObstacle);
    logger.info("current obstacle: " + newObstacle.getName());
  }

  //Functions to set current parameter
  public void setCurrentAirport(Airport currentAirport) throws Exception {
    this.currentAirport = currentAirport;
    setAirportListener.setParameter(currentAirport);
    setCurrentRunway(null);
    pdfHandler.setAirport(currentAirport);

    logger.info("current airport: " + currentAirport + ". No runway selected");
  }

  public void setCurrentObstacle(Obstacle obstacle) throws Exception {
    currentObstacle = obstacle;
    if (currentRunway != null) currentRunway.setObstacle(obstacle);
    setObstacleListener.setParameter(obstacle);
    pdfHandler.setObstacle(obstacle);

    logger.info("current obstacle: " + obstacle);
  }

  public void setCurrentRunway(Runway currentRunway) throws Exception {
    if (this.currentRunway != null) this.currentRunway.setObstacle(null);
    this.currentRunway = currentRunway;
    setRunwayListener.setParameter(currentRunway);
    setCurrentObstacle(null);
    pdfHandler.setRunway(currentRunway);

    logger.info("current runway: " + currentRunway + ". No obstacle selected");
  }

  //Functions to remove current parameter
  public void removeCurrentAirport(Airport setNull) throws Exception {
    airports.remove(currentAirport);
    airportRemoveListener.removeParam(currentAirport);
    setCurrentAirport(null);
    //TODO: Update the UI (runway dropdown) to show that the runways have been cleared
  }
  public void removeCurrentRunway(Runway setNull) throws Exception {
    currentAirport.removeRunway(currentRunway);
    runwayRemoveListener.removeParam(currentRunway);
    setCurrentRunway(null);
  }
  public void removeCurrentObstacle(Obstacle setNull) throws Exception {
    obstacles.remove(currentObstacle);
    obstacleRemoveListener.removeParam(currentObstacle);
    setCurrentObstacle(null);
  }

  //Functions to handle parameter imports
  public void importObstacles(File file) throws Exception {
    var newObstacles = XMLHandler.loadObstacles(file);

    for(Obstacle obstacle : newObstacles) {
      this.createObstacle(obstacle);
      logger.info(obstacle.getName() + " selected from XML file");
    }
  }

  public void importRunway(File file) throws Exception {
    var newRunway = XMLHandler.loadRunway(file);
    this.createRunway(newRunway);

    logger.info(newRunway.getName() + " selected from XML file");
  }
  public void importAirport(File file) throws Exception {
    var newAirport = XMLHandler.loadAirport(file);
    this.createAirport(newAirport);
    var newRunways = newAirport.getRunways();

      for (Iterator<Runway> itr = newRunways.iterator(); itr.hasNext();) {
      var runway = itr.next();
      createRunwayListener.createParameter(runway);
      logger.info("current runway: " + currentRunway.getName());
      this.setCurrentRunway(runway);
    }
    logger.info(newAirport.getName() + " selected from XML file");
  }

  public void exportObstacle(Obstacle obstacle, File file) throws Exception{
    var obstacles = new ArrayList<Obstacle>();
    if (file.exists() && file.length() != 0) {
      obstacles = XMLHandler.loadObstacles(file);

      Iterator<Obstacle> itr = obstacles.iterator();
      while (itr.hasNext()) {
        var obst = itr.next();
        if(obst.getName().equals(obstacle.getName())) {
          itr.remove();
          System.out.println(obst.getName() + " has been removed");
        }
      }
      obstacles.add(obstacle);
      XMLHandler.saveObstacles(file, obstacles);
    } else XMLHandler.saveObstacle(file, obstacle);
  }

  public void exportObstacles(File file, ArrayList<Obstacle> obstacles) throws Exception {
    XMLHandler.saveObstacles(file, obstacles);
  }

  public void exportRunway(Runway runway, File file) throws Exception {
    XMLHandler.saveRunway(file, runway);
  }

  public void exportAirport(Airport airport, File file) throws Exception{
      XMLHandler.saveAirport(file, airport);
  }

  //Set listeners for when a parameter is set
  public void setSetAirportListener(SetListener<Airport> listener) {
    this.setAirportListener = listener;
  }
  public void setSetRunwayListener(SetListener<Runway> listener) {
    this.setRunwayListener = listener;
  }
  public void setSetObstacleListener(SetListener<Obstacle> listener) {
    this.setObstacleListener = listener;
  }

  //Set listeners for when a parameter is created
  public void setCreateAirportListener(CreationListener<Airport> createAirportListener) {
    this.createAirportListener = createAirportListener;
  }
  public void setCreateRunwayListener(CreationListener<Runway> listener) {
    this.createRunwayListener = listener;
  }
  public void setCreateObstacleListener(CreationListener<Obstacle> createObstacleListener) {
    this.createObstacleListener = createObstacleListener;
  }

  //Set listeners for when a parameter is removed
  public void setAirportRemoveListener(RemoveListener<Airport> airportRemoveListener) {
    this.airportRemoveListener = airportRemoveListener;
  }
  public void setRunwayRemoveListener(RemoveListener<Runway> runwayRemoveListener) {
    this.runwayRemoveListener = runwayRemoveListener;
  }
  public void setObstacleRemoveListener(RemoveListener<Obstacle> obstacleRemoveListener) {
    this.obstacleRemoveListener = obstacleRemoveListener;
  }

  /**
   *
   * Getters
   *
   */
  public Airport getCurrentAirport() {
    return currentAirport;
  }
  public Obstacle getCurrentObstacle() {
    return currentObstacle;
  }
  public Runway getCurrentRunway() {
    return currentRunway;
  }
  public ArrayList<Airport> getAirports() {
    return airports;
  }
  public ArrayList<Obstacle> getObstacles() {
    return obstacles;
  }
  public ArrayList<Runway> getRunways() {
    return currentAirport.getRunways();
  }
}