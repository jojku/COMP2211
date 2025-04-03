package uk.ac.soton.comp2211.controller.listeners;

import uk.ac.soton.comp2211.model.Obstacle;

import java.io.File;

/**
 * Handles the event where an obstacle is exported
 * Passes the file and the obstacle to the XMLHandler
 */
public interface ObstacleExportListener { void exportObstacle(Obstacle obstacle, File file) throws Exception; }
