package uk.ac.soton.comp2211.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Runway {
    private static final Logger logger = LogManager.getLogger(Runway.class);

    private String name;
    private char designator;
    private final int heading;
    private final int resa;

    private final LogicalRunway logicalRunway1;
    private final LogicalRunway logicalRunway2;

    private final int stripEnd;
    private float minAscentRatio;
    private int blastDistance;

    /**
     * Initialise a new runway as default, 0s everywhere
     */
    public Runway() {
        this.name = "Default";
        this.heading = 0;
        this.stripEnd = 0;
        this.resa = 0;

        logger.info(name + " created");

        this.logicalRunway1 = new LogicalRunway(0, 0, 'C', 0, 0, 0, 0, 0);
        this.logicalRunway2 = new LogicalRunway(0, 0, 'C',0, 0, 0, 0, 0);
        try {
        this.setObstacle(new Obstacle());
        } catch (Exception e) {
            //This should never occur
        }
    }

    /**
     * Constructor
     * @param stripEnd Distance of cleared and graded area after end of runway
     * @param RESA resa
     * @param TORA1 tora of logical runway 1
     * @param TODA1 toda of logical runway 1
     * @param ASDA1 asda of logical runway 1
     * @param LDA1 lda of logical runway 1
     * @param thresholdDisplace1 threshold displacement of logical runway 1
     * @param TORA2 toda of logical runway 2
     * @param TODA2 toda of logical runway 2
     * @param ASDA2 asda of logical runway 2
     * @param LDA2 lda of logical runway 2
     * @param thresholdDisplace2 threshold displacement of logical runway 2
     */
    public Runway(int heading, char designator, int stripEnd, int RESA,
                  int TORA1, int TODA1, int ASDA1, int LDA1, int thresholdDisplace1,
                  int TORA2, int TODA2, int ASDA2, int LDA2, int thresholdDisplace2) throws Exception {

        if(heading < 0 || heading >= 18 || stripEnd < 0 || thresholdDisplace1 < 0 || thresholdDisplace2 < 0 || RESA < 90 ||
                TORA1 < 1000 || TODA1 < 1000 || ASDA1 < 1000 || LDA1 < 1000  ||
                TORA2 < 1000 || TODA2 < 1000 || ASDA2 < 1000 || LDA2 < 1000)   {
            throw new Exception("Runway parameters are invalid");
        }
        this.heading = heading;
        this.designator = designator;
        this.stripEnd = stripEnd;
        this.resa = RESA;

        logger.info(name + " created");
        char oppoDesignator = 'C';
        if (designator == 'L') oppoDesignator = 'R';
        if (designator == 'R') oppoDesignator = 'L';
        this.name = heading + "" + designator + "/" + (heading+18) + "" + oppoDesignator;

        this.logicalRunway1 = new LogicalRunway(RESA, heading, designator, thresholdDisplace1, TORA1, TODA1, ASDA1, LDA1);
        this.logicalRunway2 = new LogicalRunway(RESA, heading + 18, oppoDesignator, thresholdDisplace2, TORA2, TODA2, ASDA2, LDA2);

        this.setObstacle(null);
    }

    /**
     * Redeclare both logical runways
     * @param minAscentRatio min ascent ratio
     * @param blastDistance blast distance
     */
    public void redeclare(float minAscentRatio, int blastDistance) {
        this.blastDistance = blastDistance;
        this.minAscentRatio = minAscentRatio;
        this.logicalRunway1.redeclare(minAscentRatio, blastDistance, this.getStripEnd());
        this.logicalRunway2.redeclare(minAscentRatio, blastDistance, this.getStripEnd());
    }

    public void setName(char lowerHeading, char upperHeading) {
        this.name = heading + lowerHeading + "/" + (heading + 18) + upperHeading;
    }

    /**
     * Set an obstacle keeping the distances to thresholds and center lines the same
     * @param obstacle obstacle to set
     */
    public void setObstacle(Obstacle obstacle) {
        if (obstacle == null) {
            logicalRunway1.setObstacle(null, 0, 0);
            logicalRunway2.setObstacle(null, 0, 0);
            return;
        }
        this.logicalRunway1.setObstacle(obstacle, obstacle.getdThreshold1(), obstacle.getdCenter());
        this.logicalRunway2.setObstacle(obstacle, obstacle.getdThreshold2(), obstacle.getdCenter());
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getStripEnd() {
        return stripEnd;
    }

    public char getDesignator() { return designator;}

    public int getRESA() { return resa; }

    public int getHeading() { return heading; }

    public float getMinAscentRatio() {
        return minAscentRatio;
    }

    public int getBlastDistance() {
        return blastDistance;
    }

    @Override
    public String toString() {
        return name;
    }

    public LogicalRunway getLogicalRunway1() {
        return logicalRunway1;
    }

    public LogicalRunway getLogicalRunway2() {
        return logicalRunway2;
    }
}