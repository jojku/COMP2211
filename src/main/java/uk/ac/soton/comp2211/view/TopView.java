package uk.ac.soton.comp2211.view;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import uk.ac.soton.comp2211.model.Obstacle;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

class TopView extends Visualisations {
  private final Text pleasePressCalc;
  private Rotator rotator;

  /**
   * Constructor that builds the visual
   * Passes in the current scene for listener purposes and for sizing
   *
   * @param scene main scene
   */
  public TopView(MainScene scene) {
    super(scene);
    pleasePressCalc = new Text("Please press the calculate button to update the view");
    pleasePressCalc.setStyle("-fx-font-size: " + getHeight()/30);
    pleasePressCalc.getStyleClass().add("runwayOverlay");
    setArrowVisibility("dir", false);
  }

  /**
   * Handle drawing the base of the pane (the runway / sky)
   *
   * @param tora     the original length of the runway (aka size of the rectangle, needs scaling)
   * @param stopWay  the size of the stopway (needs scaling)
   * @param clearWay the size of the clearway (needs scaling)
   */
  @Override
  protected void drawBase(int tora, int stopWay, int clearWay) {
    this.getChildren().removeAll(this.getChildren());
    double height = this.getHeight();
    double width = this.getWidth();
    Double[] points = new Double[] {
            0.0, 0.75*height,
            0.1*width, 0.75*height,
            0.25*width, 0.85*height,
            0.75*width, 0.85*height,
            0.9*width, 0.75*height,
            width, 0.75*height,
            width, 0.25*height,
            0.9*width, 0.25*height,
            0.75*width, 0.15*height,
            0.25*width, 0.15*height,
            0.1*width, 0.25*height,
            0.0, 0.25*height,
            0.0, 0.75*height
    };
    Polygon clearedNGraded = new Polygon();
    StackPane.setAlignment(clearedNGraded, Pos.CENTER);
    clearedNGraded.getPoints().addAll(points);
    clearedNGraded.setFill(Color.LIGHTBLUE);
    clearedNGraded.getStyleClass().add("clearedNGraded");

    if (currentLogical == null) {
      Label lbl = new Label("No runway set");
      lbl.setStyle("-fx-text-fill: white; -fx-font-size: " + height/15);
      if (isReversed) {
        StackPane.setAlignment(lbl, Pos.BOTTOM_CENTER);
        lbl.setRotate(180);
      } else {
        StackPane.setAlignment(lbl, Pos.TOP_CENTER);
        lbl.setRotate(0);
      }
      Pane runway = createRunway(0.9*width, 0.3*height, null);
      runway.getChildren().add(lbl);
      this.getChildren().addAll(clearedNGraded, runway);
      return;
    }

    Rectangle threshold = new Rectangle(currentLogical.getThresholdDisplacement() * scale, 0, 0.01*width, 0.3*height);
    Text thresholdText = new Text("Threshold: " + currentLogical.getThresholdDisplacement() + "m");
    thresholdText.setX(currentLogical.getThresholdDisplacement() * scale - thresholdText.getBoundsInLocal().getWidth()/2 + thresholdText.getBoundsInLocal().getHeight());
    thresholdText.setY(height*0.15);
    thresholdText.setRotate(90);
    Pane runway = createRunway(0.9 * width, 0.3 * height, currentLogical.getHeading() + currentLogical.getDesignator());
    runway.getChildren().addAll(threshold, thresholdText);
    runwayOffset = 0.05*width;
    this.getChildren().addAll(clearedNGraded, runway);

    if (!isReversed) {
      changeArrow("Stopway", currentLogical.getOldTORA() + runwayOffset/scale, -runwayOffset/scale, getHeight()*0.45, "Stopway: " + currentLogical.getNewStopway() + "m");
      changeArrow("Clearway", currentLogical.getOldTORA() + runwayOffset/scale, -runwayOffset/scale, getHeight()*0.55, "Clearway: " + currentLogical.getNewClearway() + "m");
    } else  {
      changeArrow("Stopway", -runwayOffset/scale, runwayOffset/scale, getHeight()*0.45, "Stopway: " + currentLogical.getNewStopway() + "m");
      changeArrow("Clearway", -runwayOffset/scale, runwayOffset/scale, getHeight()*0.55, "Clearway: " + currentLogical.getNewClearway() + "m");
    }
  }

  /**
   * Create a runway image for the base of the view
   * @param width width of the runway
   * @param height height of the runway
   * @param designator designator of the logical runway
   * @return a pane which represents the runway
   */
  private Pane createRunway(double width, double height, String designator) {
    Rectangle runway = new Rectangle(width, height, Color.LIGHTGRAY);
    runway.setX(0);
    runway.setY(0);
    runway.getStyleClass().add("runway");

    HBox markers = new HBox(width / 20);
    markers.setMaxSize(width, height);
    markers.setMinSize(width, height);
    markers.setLayoutX(0);
    markers.setLayoutY(0);
    markers.setAlignment(Pos.CENTER);

    if (designator != null) {
      if (designator.length() == 2) designator = "0" + designator;
      Text designatorText = new Text(designator);
      designatorText.setRotate(90);
      designatorText.setStyle("-fx-font-size: " + height/2);
      designatorText.getStyleClass().add("runwayOverlay");
      markers.getChildren().add(designatorText);
    }

    for (int i = 0; i < 5; i++) {
      Rectangle marker = new Rectangle(width/10, height/10, Color.WHITE);
      marker.getStyleClass().add("runwayOverlay");
      markers.getChildren().add(marker);
    }
    Pane rPane = new Pane(runway, markers);
    rPane.setMaxSize(width, height);
    rPane.setMinSize(width, height);
    if (isReversed) rPane.setRotate(180);

    HBox calcLabel = new HBox();
    calcLabel.setMinSize(width, height);
    calcLabel.setAlignment(Pos.TOP_CENTER);
    if (pleasePressCalc != null) {
      calcLabel.getChildren().add(pleasePressCalc);
      rPane.getChildren().add(calcLabel);
      pleasePressCalc.setVisible(false);
      if (isReversed) {
        calcLabel.setAlignment(Pos.BOTTOM_CENTER);
        calcLabel.setRotate(180);
      }
    }
    return rPane;
  }

  /**
   * this.runway will either be null or a runway, if null make the arrows all invisible, else do the arrows to the old values
   */
  @Override
  protected void setEmptyArrows() {
    if (currentLogical == null) return;
    if (!getChildren().contains(arrowsPane)) getChildren().add(arrowsPane);

    double oldTora = currentLogical.getOldTORA();

    for (String key : new String[] {"RESA", "TOCS", "ALS", "BlastDistance", "StripEnd"}) {
      setArrowVisibility(key, false);
    }

    if (!isReversed) {
      changeArrow("TORA", 0, oldTora, getHeight() * 0.7, "TORA: " + currentLogical.getOldTORA() + "m");
      changeArrow("TODA", 0, currentLogical.getOldTODA(), getHeight() * 0.75, "TODA: " + currentLogical.getOldTODA() + "m");
      changeArrow("ASDA", 0, currentLogical.getOldASDA(), getHeight() * 0.8, "ASDA: " + currentLogical.getOldASDA() + "m");
      changeArrow("LDA", currentLogical.getThresholdDisplacement(), currentLogical.getOldLDA(), getHeight() * 0.3, "LDA: " + currentLogical.getOldLDA() + "m");
    } else {
      changeArrow("TORA", oldTora, -oldTora, getHeight() * 0.7, "TORA: " + currentLogical.getOldTORA() + "m");
      changeArrow("TODA", oldTora, -currentLogical.getOldTODA(), getHeight() * 0.75, "TODA: " + currentLogical.getOldTODA() + "m");
      changeArrow("ASDA", oldTora, -currentLogical.getOldASDA(), getHeight() * 0.8, "ASDA: " + currentLogical.getOldASDA() + "m");
      changeArrow("LDA", oldTora - currentLogical.getThresholdDisplacement(), -currentLogical.getOldLDA(), getHeight() * 0.3, "LDA: " + currentLogical.getOldLDA() + "m");
    }
  }

  /**
   * Draw an obstacle onto the runway (no arrows)
   *
   * @param obstacle Obstacle to draw, distToThreshold is in the logicalRunway
   */
  @Override
  protected void drawObstacle(Obstacle obstacle) {
    if (obstacle == null) return;

    obstaclePane.getChildren().removeAll(obstaclePane.getChildren());
    if (!getChildren().contains(obstaclePane)) getChildren().add(obstaclePane);

    double yScale = getHeight()/200; //Runway width is getHeight*0.3, assuming 60m wide runway

    double xPos, yPos;
    if (!isReversed) xPos = runwayOffset + scale * (obstacle.getdThreshold1() + currentLogical.getThresholdDisplacement());
    else xPos = runwayOffset + scale * (currentLogical.getOldTORA() - obstacle.getdThreshold2() - currentLogical.getThresholdDisplacement());
    if (obstacle.getdCenter() == 0) yPos = getHeight()/2 - (obstacle.getLength()*yScale)/2;
    else yPos = getHeight()/2 + (obstacle.getdCenter()*yScale);

    Rectangle obstacleRect = new Rectangle(xPos, yPos, obstacle.getLength() * scale, obstacle.getWidth());
    obstacleRect.setFill(Color.PURPLE);
    obstaclePane.getChildren().add(obstacleRect);

    //If obstacle too small, draw a circle around it
    if (obstacle.getWidth() * obstacle.getLength() < 200) {
      double circleWidth = getWidth() * getHeight() / 20000;
      Circle outside = new Circle(xPos + obstacle.getLength()*scale, yPos + obstacle.getWidth()*yScale/2, circleWidth);
      Circle inside = new Circle(xPos + obstacle.getLength()*scale, yPos + obstacle.getWidth()*yScale/2, circleWidth*0.8);
      Shape dougnut = Shape.subtract(outside, inside);
      dougnut.setFill(Color.PURPLE);
      obstaclePane.getChildren().add(dougnut);
    }
  }

  /**
   * Set the arrows for the runway / obstacle. LogicalRunway is set as a class variable currLogical
   *
   * @param obstacle Obstacle to draw on the runway
   */
  @Override
  protected void setArrows(Obstacle obstacle) {
    if (!currentLogical.isRecalculated()) {
      pleasePressCalc.setVisible(true);
      return;
    }
    pleasePressCalc.setVisible(false);

    double oldTORA = currentLogical.getOldTORA();
    double obstacleXPos = obstacle.getdThreshold1() + currentLogical.getThresholdDisplacement();

    for (String key : new String[] {"RESA", "TOCS", "ALS", "BlastDistance", "StripEnd"}) {
      setArrowVisibility(key, true);
    }
    //TODO: handle 2 strip ends for takeoff/land on either side of obst
    //Atm the landing has the stripend

    HashMap<String, Double> startXMap = new HashMap<>();
    HashMap<String, Integer> changeXMap = new HashMap<>();

    HashMap<String, Double> yPos = new HashMap<>();
    yPos.put("LDA", getHeight()*0.1);
    yPos.put("StripEnd", getHeight()*0.15);
    yPos.put("ALS", getHeight()*0.2);
    yPos.put("BlastDistance", getHeight()*0.25);
    yPos.put("RESA", getHeight()*0.3);

    yPos.put("TOCS", getHeight()*0.7);
    yPos.put("TORA", getHeight()*0.75);
    yPos.put("TODA", getHeight()*0.8);
    yPos.put("ASDA", getHeight()*0.85);

    //Handle landing
    changeXMap.put("LDA", currentLogical.getNewLDA());
    changeXMap.put("StripEnd", runway.getStripEnd());
    changeXMap.put("RESA", currentLogical.getRESA());
    changeXMap.put("BlastDistance", runway.getBlastDistance());
    changeXMap.put("ALS", currentLogical.getNewALS());
    if (currentLogical.getLandToward()) {
      startXMap.put("LDA", (double)currentLogical.getThresholdDisplacement());
      startXMap.put("StripEnd", (double)currentLogical.getThresholdDisplacement() + currentLogical.getNewLDA());
      startXMap.put("RESA", (double)currentLogical.getThresholdDisplacement() + currentLogical.getNewLDA() + runway.getStripEnd());
      startXMap.put("BlastDistance", (double)currentLogical.getThresholdDisplacement() + currentLogical.getNewLDA() + runway.getStripEnd());
      setArrowVisibility("ALS", false);
    } else {
      startXMap.put("LDA", oldTORA - currentLogical.getNewLDA());
      startXMap.put("StripEnd", oldTORA-currentLogical.getNewLDA()-runway.getStripEnd());
      startXMap.put("RESA", obstacleXPos);
      startXMap.put("BlastDistance", obstacleXPos);
      startXMap.put("ALS", obstacleXPos);
    }

    changeXMap.put("TORA", currentLogical.getNewTORA());
    changeXMap.put("TODA", currentLogical.getNewTODA());
    changeXMap.put("ASDA", currentLogical.getNewASDA());
    changeXMap.put("TOCS", currentLogical.getNewTOCS());
    if (currentLogical.getTakeoffToward()) {
      startXMap.put("TORA", 0.0);
      startXMap.put("TODA", 0.0);
      startXMap.put("ASDA", 0.0);
      startXMap.put("TOCS", (double)currentLogical.getNewTORA());
    } else {
      startXMap.put("TORA", oldTORA-currentLogical.getNewTORA());
      startXMap.put("TODA", oldTORA-currentLogical.getNewTORA());
      startXMap.put("ASDA", oldTORA-currentLogical.getNewTORA());
      setArrowVisibility("TOCS", false);
    }

    for (String s : startXMap.keySet()) {
      if (!isReversed) changeArrow(s, startXMap.get(s), changeXMap.get(s), yPos.get(s), s + ": " + changeXMap.get(s).toString() + "m");
      else  changeArrow(s, oldTORA-startXMap.get(s), -changeXMap.get(s), yPos.get(s), s + ": " + changeXMap.get(s).toString() + "m");
    }

    if (!isReversed) {
      changeArrow("Stopway", oldTORA + runwayOffset/scale, -runwayOffset/scale, getHeight()*0.45, "Stopway: " + currentLogical.getNewStopway() + "m");
      changeArrow("Clearway", oldTORA + runwayOffset/scale, -runwayOffset/scale, getHeight()*0.55, "Clearway: " + currentLogical.getNewClearway() + "m");
    } else  {
      changeArrow("Stopway", -runwayOffset/scale, runwayOffset/scale, getHeight()*0.45, "Stopway: " + currentLogical.getNewStopway() + "m");
      changeArrow("Clearway", -runwayOffset/scale, runwayOffset/scale, getHeight()*0.55, "Clearway: " + currentLogical.getNewClearway() + "m");
    }
  }

  /**
   * Initialise the zoomer, panner, etc.
   */
  public void initializeTransformers(){
    rotator = new Rotator(this, (float) (this.getWidth()/2), (float) (this.getHeight()/2));
    panner = new Panner(this);
    zoomer = new Zoomer(this, (float) (this.getWidth()/2), (float) (this.getHeight()/2));
  }

  public Rotator getRotator() {
    return rotator;
  }

  public void rotated(boolean isRotated) {
    if (isRotated && currentLogical.getHeading() > 9) {
      for (Text t : arrowLabels.values()) t.setRotate(180);
    } else {
      for (Text t : arrowLabels.values()) t.setRotate(0);
    }
  }
}