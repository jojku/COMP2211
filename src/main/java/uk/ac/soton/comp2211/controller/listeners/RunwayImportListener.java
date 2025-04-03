package uk.ac.soton.comp2211.controller.listeners;

import java.io.File;

/**
 * Listens for importing a runway
 * Passes file to XMLHandler
 */
public interface RunwayImportListener { void importRunway(File file) throws Exception; }
