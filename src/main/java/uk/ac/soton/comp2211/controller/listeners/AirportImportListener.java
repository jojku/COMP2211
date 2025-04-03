package uk.ac.soton.comp2211.controller.listeners;

import java.io.File;

/**
 * Handles the event where an airport is imported
 * Passes the filename to the XMLHandler
 */
public interface AirportImportListener { void importAirport(File file) throws Exception; }
