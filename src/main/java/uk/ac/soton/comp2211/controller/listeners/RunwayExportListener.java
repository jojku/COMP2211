package uk.ac.soton.comp2211.controller.listeners;

import uk.ac.soton.comp2211.model.Runway;

import java.io.File;

/**
 * Handles the event where an airport is imported
 * Passes the filename to the XMLHandler
 */
public interface RunwayExportListener { void exportRunway(Runway runway, File file) throws Exception; }
