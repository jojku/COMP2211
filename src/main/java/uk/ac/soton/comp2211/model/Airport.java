package uk.ac.soton.comp2211.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class Airport {

    private static final Logger logger = LogManager.getLogger(Airport.class);

    private String name;

    private final ArrayList<Runway> runways;

    private float minAscentRatio;

    private int blastDistance;


    /**
     * Builds a default airport (doesn't throw an exception)
     */
    public Airport() {
        this.name = "default";
        this.runways = new ArrayList<>();
        this.minAscentRatio = 0;
        this.blastDistance = 0;

    }

    /**
     * builds airport with desired parameters
     * @param name name
     * @param runways runways
     * @param minAscentRatio minimum ascent ratio
     * @param blastDistance blast distance
     */
    public Airport(String name, ArrayList<Runway> runways, float minAscentRatio, int blastDistance) throws Exception {

        if(minAscentRatio <= 0 || blastDistance < 0 || runways == null) {
            logger.error("Input error. " + name + " was not created.");
            throw new Exception("Airport initialised with negative parameters or had a null runways list");
        }
        logger.info("Building Airport");

        this.name = name;
        this.runways = runways;
        this.minAscentRatio = minAscentRatio;
        this.blastDistance = blastDistance;

    }



    public void addRunway(Runway runway) { runways.add(runway); }
    public void removeRunway(Runway runway) { runways.remove(runway); }

    public ArrayList<Runway> getRunways() { return runways; }

    @Override
    public String toString() {
        return name;
    }

    public String getName() { return name; }

    public float getMinAscentRatio() { return minAscentRatio; }

    public int getBlastDistance() { return blastDistance; }

    public void setName(String name) {
        this.name = name;
    }

    public void setBlastDistance(int blastDistance) {
        this.blastDistance = blastDistance;
    }

    public void setMinAscentRatio(float minAscentRatio) {
        this.minAscentRatio = minAscentRatio;
    }
}