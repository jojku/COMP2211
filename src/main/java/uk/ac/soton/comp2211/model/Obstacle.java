package uk.ac.soton.comp2211.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Obstacle {

    private static final Logger logger = LogManager.getLogger(Obstacle.class);

    private int height;
    private int length;
    private int width;
    private String name;

    private int dThreshold1 = 0;
    private int dThreshold2 = 0;
    private int dCenter = 0;


    public Obstacle() {
        this.name = "default";
        this.height = 0;
        this.width = 0;
        this.length = 0;
    }

    /**
     * Constructor
     * @param name name
     * @param height height
     * @param width width
     * @param length length
     */
    public Obstacle(String name, int height, int width, int length) throws Exception {
        if(height < 0 || width < 0 || length < 0) {
            logger.error("Input error. " + name + " was not created.");
            throw new Exception("An obstacle parameter was negative");
        }

        this.name = name;
        this.height = height;
        this.width = width;
        this.length = length;

        logger.info(name + " created");
    }

    /**
     * Constructor
     * @param name name
     * @param height height
     * @param width width
     * @param length length
     */
    public Obstacle(String name, int height, int width, int length, int dThreshold1, int dThreshold2, int dCenter) throws Exception {
        if(height < 0 || width < 0 || length < 0 || dCenter > 100) {
            logger.error("Input error. " + name + " was not created.");
            throw new Exception("An obstacle parameter was out of range");
        }

        this.name = name;
        this.height = height;
        this.width = width;
        this.length = length;
        setdThreshold1(dThreshold1);
        setdThreshold2(dThreshold2);
        setdCenter(dCenter);

        logger.info(name + " created");
    }

    /**
     * Overridden for use in the view
     * @return Name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Getters and Setters
     * @return height
     */
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String type) {
        this.name = type;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public int getdThreshold1() {
        return dThreshold1;
    }

    public int getdThreshold2() {
        return dThreshold2;
    }

    public int getdCenter() {
        return dCenter;
    }

    public void setdCenter(int dCenter) {
        this.dCenter = dCenter;
    }

    public void setdThreshold1(int dThreshold1) {
        this.dThreshold1 = dThreshold1;
    }

    public void setdThreshold2(int dThreshold2) {
        this.dThreshold2 = dThreshold2;
    }
}