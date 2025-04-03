package uk.ac.soton.comp2211.controller.listeners;

import uk.ac.soton.comp2211.model.Airport;

import java.io.File;

/**
 * Handles the event where an airport is imported
 * Passes the file to the XMLHandler
 */
public interface AirportExportListener { void exportAirport(Airport airport, File file) throws Exception; }
