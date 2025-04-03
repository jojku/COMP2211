package uk.ac.soton.comp2211.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import uk.ac.soton.comp2211.model.Obstacle;

import java.util.HashMap;

class SideView extends Visualisations {

    private Rectangle obstacleImage;

    /**
     * Constructor that builds the visual
     * Passes in the current scene for listener purposes and for sizing
     *
     * @param scene main scene
     */
    public SideView(MainScene scene) {
        super(scene);
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
        // want a vertical stack for SideView
        graphics = new VBox();

        var sky = new Rectangle(this.getWidth(), scene.getHeight() * 0.9);
        sky.getStyleClass().add("clearedNGraded");

        if (currentLogical == null) {
            System.out.println("Drawing base in Sideview");

            // refresh view
            graphics.getChildren().clear();
            this.getChildren().clear();

            Label noneSet = new Label("No runway set");
            noneSet.setStyle("-fx-text-fill: white; -fx-font-size: " + this.getHeight() / 15);

            var grass = new Rectangle(sky.getWidth(), this.getHeight() * 0.1);
            grass.getStyleClass().add("lawn");

            graphics.getChildren().addAll(sky, grass, noneSet);
            this.getChildren().add(graphics);
        } else {
            System.out.println("Drawing runway in Sideview");

            // refresh view
            graphics.getChildren().clear();
            this.getChildren().clear();

            var runwayImage = new Rectangle(sky.getWidth(), scene.getHeight() * 0.05);
            runwayImage.getStyleClass().add("runway");

            var des = currentLogical.getHeading() + currentLogical.getDesignator();
            if (des.length() == 2) {
                des = "0" + des;
            }
            var designatorText = new Text(des);
            designatorText.setStyle("-fx-fill: white; -fx-font-size: " + scene.getHeight() / 30);

            graphics.getChildren().addAll(sky, runwayImage);
            this.getChildren().addAll(graphics, designatorText);
            designatorText.setTranslateX(-200);
            designatorText.setTranslateY(315);
        }

    }

    /**
     * this.runway will either be null or a runway, if null make the arrows all invisible, else do the arrows to the old values
     */
    @Override
    protected void setEmptyArrows() {
        System.out.println("Setting empty arrows in SideView");

        // only apply for obstacles
        for (String key : new String[]{"RESA", "StripEnd", "TOCS", "ALS", "BlastDistance"}) {
            setArrowVisibility(key, false);
        }

        if (currentLogical != null) {
            // refresh view
            if (!this.getChildren().contains(arrowsPane)) {
                this.getChildren().add(arrowsPane);
            }

            // only display stopway and clearway when they hold values
            if (!isReversed) {
                if (currentLogical.getNewStopway() != 0) {
                    setArrowVisibility("Stopway", true);
                    changeArrow("Stopway", 100, 100 - currentLogical.getNewStopway(), getHeight()*0.2, "Stopway: " + currentLogical.getNewStopway());
                }
                if (currentLogical.getNewClearway() != 0) {
                    setArrowVisibility("Clearway", true);
                    changeArrow("Clearway", getWidth() - 100, getWidth() - 100 + currentLogical.getNewStopway(), getHeight()*0.2, "Clearway: " + currentLogical.getNewClearway());
                }
            } else {
                if (currentLogical.getNewStopway() != 0) {
                    setArrowVisibility("Stopway", true);
                    changeArrow("Stopway", getWidth() - 100, getWidth() - 100 + currentLogical.getNewStopway(), getHeight()*0.2, "Stopway: " + currentLogical.getNewStopway());
                }
                if (currentLogical.getNewClearway() != 0) {
                    setArrowVisibility("Clearway", true);
                    changeArrow("Clearway", 100, 100 - currentLogical.getNewStopway(), getHeight()*0.2, "Clearway: " + currentLogical.getNewClearway());
                }
            }

            // as I understand it the new values are only calculated upon clicking the 'Calculate' button
            var tora = currentLogical.getOldTORA();
            var toda = currentLogical.getOldTODA();
            var asda = currentLogical.getOldASDA();
            var lda = currentLogical.getOldLDA();
            var displacement = currentLogical.getThresholdDisplacement();

            if (!isReversed) {
                changeArrow("TORA", 0, tora, scene.getHeight() * 0.5, "TORA: " + tora);
                changeArrow("TODA", 0, toda, scene.getHeight() * 0.55, "TODA: " + toda);
                changeArrow("ASDA", 0, asda, scene.getHeight() * 0.6, "ASDA: " + asda);
                changeArrow("LDA", displacement, lda, scene.getHeight() * 0.65, "LDA: " + lda);
                changeArrow("dir", 50, 650, scene.getHeight() * 0.1, "Direction");
            } else {
                changeArrow("TORA", tora, -tora, scene.getHeight() * 0.5, "TORA: " + tora);
                changeArrow("TODA", tora, -toda, scene.getHeight() * 0.55, "TODA: " + toda);
                changeArrow("ASDA", tora, -asda, scene.getHeight() * 0.6, "ASDA: " + asda);
                changeArrow("LDA", tora - displacement, -lda, scene.getHeight() * 0.65, "LDA: " + lda);
                changeArrow("dir", tora, -650, scene.getHeight() * 0.1, "Direction");
            }

        }

    }

    /**
     * Draw an obstacle onto the runway (no arrows)
     *
     * @param obstacle Obstacle to draw, distToThreshold is in the logicalRunway
     */
    @Override
    protected void drawObstacle(Obstacle obstacle) {

        System.out.println("Drawing obstacle in SideView");

        if (obstacle != null) {
            if (this.getChildren().contains(obstacleImage)) { this.getChildren().remove(obstacleImage); }

            String[] params = new String[] {"dir", "TODA", "TORA", "ASDA", "LDA", "RESA", "TOCS", "ALS", "StripEnd", "Stopway", "Clearway", "BlastDistance"};
            for (String s : params) {
                setArrowVisibility(s, false);
            }

            double xPos, yPos;
            if (!isReversed) {
                xPos = runwayOffset + scale * (obstacle.getdThreshold1() + currentLogical.getThresholdDisplacement());
            } else {
                xPos = runwayOffset + scale * (currentLogical.getOldTORA() - obstacle.getdThreshold2() - currentLogical.getThresholdDisplacement());
            }
            yPos = 630 - (obstacle.getHeight() * 5);
            System.out.println(xPos + " " + yPos);

            // multiplied by 10 to improve readability
            Rectangle obstacleRect = new Rectangle(obstacle.getLength() * 5 * scale, obstacle.getHeight() * 5);
            obstacleRect.setTranslateX(xPos);
            obstacleRect.setTranslateY(yPos);
            System.out.println(obstacleRect.getHeight());
            System.out.println("Runway height should be " + (obstacleRect.getHeight() + yPos));
            obstacleRect.setFill(Color.PURPLE);
            obstacleImage = obstacleRect;


            this.getChildren().add(obstacleImage);
            StackPane.setAlignment(obstacleImage, Pos.TOP_LEFT);
        }

    }

    /**
     * Set the arrows for the runway / obstacle. LogicalRunway is set as a class variable currLogical
     *
     * @param obstacle Obstacle to draw on the runway
     */
    @Override
    protected void setArrows(Obstacle obstacle) {
        System.out.println("Setting arrows in SideView");
        System.out.println("Is reversed " + isReversed);

        if (!currentLogical.isRecalculated()) {
            return;
        }

        String[] params = new String[] {"dir", "TODA", "TORA", "ASDA", "LDA", "RESA", "StripEnd", "BlastDistance"};
        for (String s : params) {
            setArrowVisibility(s, true);
        }

        double oldTORA = currentLogical.getOldTORA();

        // only display stopway and clearway when they hold values
        if (!isReversed) {
            if (currentLogical.getNewStopway() != 0) {
                setArrowVisibility("Stopway", true);
                changeArrow("Stopway", 100, 100 - currentLogical.getNewStopway(), getHeight()*0.2, "Stopway: " + currentLogical.getNewStopway());
            }
            if (currentLogical.getNewClearway() != 0) {
                setArrowVisibility("Clearway", true);
                changeArrow("Clearway", getWidth() - 100, getWidth() - 100 + currentLogical.getNewStopway(), getHeight()*0.2, "Clearway: " + currentLogical.getNewClearway());
            }
        } else {
            if (currentLogical.getNewStopway() != 0) {
                setArrowVisibility("Stopway", true);
                changeArrow("Stopway", getWidth() - 100, getWidth() - 100 + currentLogical.getNewStopway(), getHeight()*0.2, "Stopway: " + currentLogical.getNewStopway());
            }
            if (currentLogical.getNewClearway() != 0) {
                setArrowVisibility("Clearway", true);
                changeArrow("Clearway", 100, 100 - currentLogical.getNewStopway(), getHeight()*0.2, "Clearway: " + currentLogical.getNewClearway());
            }
        }


        HashMap<String, Double> startXMap = new HashMap<>();
        HashMap<String, Integer> changeXMap = new HashMap<>();

        HashMap<String, Double> yPos = new HashMap<>();
        yPos.put("LDA", getHeight()*0.45);
        yPos.put("RESA", getHeight()*0.6);
        yPos.put("StripEnd", getHeight()*0.5);
        yPos.put("TORA", getHeight()*0.35);
        yPos.put("TODA", getHeight()*0.3);
        yPos.put("ASDA", getHeight()*0.25);

        //Handle landing
        changeXMap.put("LDA", currentLogical.getNewLDA());
        changeXMap.put("StripEnd", runway.getStripEnd());
        changeXMap.put("RESA", currentLogical.getRESA());

        System.out.println(currentLogical.getLandToward());
        if (currentLogical.getLandToward()) {
            startXMap.put("LDA", (double)currentLogical.getThresholdDisplacement());
            startXMap.put("StripEnd", (double)currentLogical.getThresholdDisplacement() + currentLogical.getNewLDA());
            startXMap.put("RESA", (double)currentLogical.getThresholdDisplacement() + currentLogical.getNewLDA() + runway.getStripEnd());
        } else {
            setArrowVisibility("ALS", true);
            yPos.put("ALS", getHeight() * 0.55);
            startXMap.put("ALS", oldTORA-currentLogical.getNewLDA()-runway.getStripEnd()-currentLogical.getNewALS());
            changeXMap.put("ALS", currentLogical.getNewALS());
            startXMap.put("BlastDistance", oldTORA-currentLogical.getNewLDA()-runway.getBlastDistance());
            yPos.put("BlastDistance", getHeight()*0.4);
            changeXMap.put("BlastDistance", runway.getBlastDistance());
            startXMap.put("LDA", oldTORA - currentLogical.getNewLDA());
            startXMap.put("StripEnd", oldTORA-currentLogical.getNewLDA()-runway.getStripEnd());
            startXMap.put("RESA", oldTORA-currentLogical.getNewLDA()-runway.getStripEnd()-currentLogical.getRESA());
        }

        changeXMap.put("TORA", currentLogical.getNewTORA());
        changeXMap.put("TODA", currentLogical.getNewTODA());
        changeXMap.put("ASDA", currentLogical.getNewASDA());
        // handle takeoff
        if (currentLogical.getTakeoffToward()) {
            setArrowVisibility("TOCS", true);
            setArrowVisibility("BlastDistance", false);
            yPos.put("TOCS", getHeight() * 0.55);
            startXMap.put("TOCS", (double)currentLogical.getThresholdDisplacement() + currentLogical.getNewLDA() + runway.getStripEnd());
            changeXMap.put("TOCS", currentLogical.getNewTOCS());
            startXMap.put("TORA", 0.0);
            startXMap.put("TODA", 0.0);
            startXMap.put("ASDA", 0.0);
        } else {
            startXMap.put("TORA", oldTORA-currentLogical.getNewTORA());
            startXMap.put("TODA", oldTORA-currentLogical.getNewTORA());
            startXMap.put("ASDA", oldTORA-currentLogical.getNewTORA());
        }

        for (String s : startXMap.keySet()) {
            if (!isReversed) changeArrow(s, startXMap.get(s), changeXMap.get(s), yPos.get(s), s + ": " + changeXMap.get(s).toString() + "m");
            else  changeArrow(s, oldTORA-startXMap.get(s), -changeXMap.get(s), yPos.get(s), s + ": " + changeXMap.get(s).toString() + "m");
        }

    }
}