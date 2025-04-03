package uk.ac.soton.comp2211.controller.listeners;

import java.io.File;

/**
 * Handles importing obstacles
 * Passes the file to XMLHandler
 */
public interface ObstacleImportListener { void importObstacle(File file) throws Exception; }
