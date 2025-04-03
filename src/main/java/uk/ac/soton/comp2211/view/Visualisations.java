package uk.ac.soton.comp2211.view;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp2211.model.LogicalRunway;
import uk.ac.soton.comp2211.model.Obstacle;
import uk.ac.soton.comp2211.model.Runway;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.HashMap;

/**
 * Generic Runway View class
 * Displays the parameters for both directions
 * Child of Pane -> A view will be a container
 * Author: Thomas Cutts
 */
public abstract class Visualisations extends StackPane implements Export {

    private static final Logger logger = LogManager.getLogger(Visualisations.class);

    protected MainScene scene;
    protected Obstacle obstacle;
    protected Runway runway;
    protected LogicalRunway currentLogical;
    protected boolean isReversed = false;

    /**
     * Transformers
     */
    protected Zoomer zoomer;
    protected Panner panner;

    /** Groupings for objects */
    protected Pane graphics;
    protected Pane obstaclePane = new Pane();
    protected Pane arrowsPane = new Pane();

    protected double scale;
    protected double runwayOffset;
    protected int blastDistance;

    protected final HashMap<String, Text> arrowLabels;
    protected final HashMap<String, Arrow> arrowsMap;

    /**
     * Constructor that builds the visual
     * Passes in the current scene for listener purposes and for sizing
     * @param scene parent scene
     */
    public Visualisations(MainScene scene) {
        this.setWidth(scene.getWidth()-420);
        this.setHeight(scene.getHeight() * 0.6);
        this.setMaxSize(scene.getWidth()-420, scene.getHeight() * 0.6);
        this.scene = scene;
        arrowLabels = new HashMap<>();
        arrowsMap = new HashMap<>();

        System.out.println("Building view");

        putArrows();
        drawRunway(null);
    }

    private void putArrows() {
        String[] params = new String[] {"dir", "TODA", "TORA", "ASDA", "LDA", "RESA", "TOCS", "ALS", "StripEnd", "Stopway", "Clearway", "BlastDistance"};
        for (String s : params) {
            arrowLabels.put(s, new Text(s));
            arrowsMap.put(s, new Arrow());
        }
        arrowsPane.getChildren().addAll(arrowsMap.values());
        arrowsPane.getChildren().addAll(arrowLabels.values());
        arrowsPane.setMinSize(this.getWidth(), this.getHeight());
        arrowsPane.setMaxSize(this.getWidth(), this.getHeight());
    }
    protected void setArrowVisibility(String key, boolean visible) {
        arrowsMap.get(key).setVisible(visible);
        arrowLabels.get(key).setVisible(visible);
    }
    public void switchDirection() {
        if (runway == null) return;
        if (currentLogical == runway.getLogicalRunway1()) currentLogical = runway.getLogicalRunway2();
        else currentLogical = runway.getLogicalRunway1();
        this.isReversed = !this.isReversed;
        drawRunway(currentLogical);
        drawObstacle(obstacle);
        setArrows(obstacle);
    }

    public void setRunway(Runway runway) {
        this.runway = runway;
        if (runway == null) {
            this.currentLogical = null;
            drawRunway(null);
            return;
        }
        this.blastDistance = runway.getBlastDistance();
        this.currentLogical = runway.getLogicalRunway1();
        drawRunway(currentLogical);
    }
    private void drawRunway(LogicalRunway runway) {
        if (runway == null) {
            this.scale = 0.1;
            drawBase(0, 0, 0);
            setEmptyArrows();
            return;
        }
        this.scale = 0.9 * this.getWidth()/runway.getOldTORA();
        drawBase(runway.getNewTORA(), runway.getNewStopway(), runway.getNewClearway());
        setEmptyArrows();
        drawObstacle(null);
    }
    public void setObstacle(Obstacle obstacle) {
        this.obstacle = obstacle;
        if (runway == null) {
            logger.info("The runway is null but an obstacle is being set, returning");
            return;
        }
        setEmptyArrows();
        drawObstacle(obstacle);
        if (obstacle != null) setArrows(obstacle);
    }


    /**
     * Handle drawing the base of the pane (the runway / sky)
     * @param tora the original length of the runway (aka size of the rectangle, needs scaling)
     * @param stopWay the size of the stopway (needs scaling)
     * @param clearWay the size of the clearway (needs scaling)
     */
    protected abstract void drawBase(int tora, int stopWay, int clearWay); //Draw the base rectange, sky, clear and stopway

    /**
     * this.runway will either be null or a runway, if null make the arrows all invisible, else do the arrows to the old values
     */
    protected abstract void setEmptyArrows();

    /**
     * Draw an obstacle onto the runway (no arrows)
     * @param obstacle Obstacle to draw, distToThreshold is in the logicalRunway
     */
    protected abstract void drawObstacle(Obstacle obstacle);

    /**
     * Set the arrows for the runway / obstacle. LogicalRunway is set as a class variable currLogical
     * @param obstacle Obstacle to draw on the runway
     */
    protected abstract void setArrows(Obstacle obstacle);

    public Zoomer getZoomer() {
        return zoomer;
    }

    public Panner getPanner() {
    return panner;
    }

    /**
     * Take an arrow, and it's text, and make the relevant changes to both of them
     * @param key the string that is the key for the arrow / text in the hashmaps
     * @param startX x
     * @param changeX change in x
     * @param yValue y
     * @param newText text to change the Text to
     */
    protected void changeArrow(String key, double startX, double changeX, double yValue, String newText) {
        double stX = runwayOffset + startX * scale;
        double endX = stX + (changeX * scale);
        Arrow arrow = arrowsMap.get(key);
        Text arrowText = arrowLabels.get(key);

        arrow.setStartX(stX);
        arrow.setEndX(endX);
        arrow.setStartY(yValue);
        arrow.setEndY(yValue);

        arrowText.setText(newText);
        arrowText.setY(yValue - 5);
        if (changeX < 0) arrowText.setX(stX - arrowText.getBoundsInLocal().getWidth());
        else arrowText.setX(stX + 2);
    }

    /**
     * Exports the current view to PNG with FileChooser
     * @throws Exception when FileChooser is cancelled
     */
    @Override
    public void export() throws Exception {
        Pane clonePane = new Pane();
        clonePane.getChildren().addAll(this.getChildren());
        Scene imageScene = new Scene(clonePane);
        WritableImage image = new WritableImage((int)this.getWidth(),(int)this.getHeight());
        image = imageScene.snapshot(image);
        this.getChildren().addAll(clonePane.getChildren());
        FileChooser dirChooser = new FileChooser();
        dirChooser.setTitle("Select a location and enter filename to save image to");
        dirChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter ("PNG File", "*.png"));
        File selected = dirChooser.showSaveDialog(scene.getWindow());
        if (selected == null) {
            throw new Exception("File Explorer closed");
        } else {
            int ind = selected.getName().lastIndexOf('.');
            if (ind == -1) {  // No file extension
                selected = new File(selected.getParent(), selected.getName()+".png");
            } else if (!selected.getName().substring(ind).equals("png")) {  // Extension exists but not lowercase "png"
                selected = new File(selected.getParent(), selected.getName().substring(0,ind)+".png");
            }
        }
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "PNG", selected);
    }

    /**
     * Adapted from:
     * Fabian, (2016), 'How to draw arrow JavaFX? (pane)', StackOverflow,
     * Available at: https://stackoverflow.com/questions/41353685/how-to-draw-arrow-javafx-pane,
     * (Accessed: 15 March 2022)
     *
     * Arrows correspond to the parameters in the view
     */
    protected static class Arrow extends Group {

        private final Line line;

        public Arrow() {
            this(new Line(), new Line(), new Line());
        }

        private static final double arrowLength = 20;
        private static final double arrowWidth = 7;

        private Arrow(Line line, Line arrow1, Line arrow2) {
            super(line, arrow1, arrow2);
            this.line = line;
            InvalidationListener updater = o -> {
                double ex = getEndX();
                double ey = getEndY();
                double sx = getStartX();
                double sy = getStartY();

                arrow1.setEndX(ex);
                arrow1.setEndY(ey);
                arrow2.setEndX(ex);
                arrow2.setEndY(ey);

                if (ex == sx && ey == sy) {
                    // arrow parts of length 0
                    arrow1.setStartX(ex);
                    arrow1.setStartY(ey);
                    arrow2.setStartX(ex);
                    arrow2.setStartY(ey);
                } else {
                    double factor = 0.5*arrowLength / Math.hypot(sx-ex, sy-ey);
                    double factorO = 0.5*arrowWidth / Math.hypot(sx-ex, sy-ey);

                    // part in direction of main line
                    double dx = (sx - ex) * factor;
                    double dy = (sy - ey) * factor;

                    // part ortogonal to main line
                    double ox = (sx - ex) * factorO;
                    double oy = (sy - ey) * factorO;

                    arrow1.setStartX(ex + dx - oy);
                    arrow1.setStartY(ey + dy + ox);
                    arrow2.setStartX(ex + dx + oy);
                    arrow2.setStartY(ey + dy - ox);
                }
            };

            // add updater to properties
            startXProperty().addListener(updater);
            startYProperty().addListener(updater);
            endXProperty().addListener(updater);
            endYProperty().addListener(updater);
            updater.invalidated(null);
        }

        // start/end properties

        public final void setStartX(double value) {
            line.setStartX(value);
        }

        public final double getStartX() {
            return line.getStartX();
        }

        public final DoubleProperty startXProperty() {
            return line.startXProperty();
        }

        public final void setStartY(double value) {
            line.setStartY(value);
        }

        public final double getStartY() {
            return line.getStartY();
        }

        public final DoubleProperty startYProperty() {
            return line.startYProperty();
        }

        public final void setEndX(double value) {
            line.setEndX(value);
        }

        public final double getEndX() {
            return line.getEndX();
        }

        public final DoubleProperty endXProperty() {
            return line.endXProperty();
        }

        public final void setEndY(double value) {
            line.setEndY(value);
        }

        public final double getEndY() {
            return line.getEndY();
        }

        public final DoubleProperty endYProperty() {
            return line.endYProperty();
        }

    }

}