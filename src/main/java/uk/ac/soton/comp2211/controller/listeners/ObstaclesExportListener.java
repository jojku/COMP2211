package uk.ac.soton.comp2211.controller.listeners;

import uk.ac.soton.comp2211.model.Obstacle;

import java.io.File;
import java.util.ArrayList;


/**
 * Handles the event where obstacles are exported
 * Passes the file and the obstacles to the XMLHandler
 */
public interface ObstaclesExportListener { void exportObstacles(File file, ArrayList<Obstacle> obstacles) throws Exception;
}
