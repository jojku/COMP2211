package uk.ac.soton.comp2211.controller.listeners;

import java.io.File;

/**
 * Listens for redeclared results exporting
 */
public interface PDFExportListener { void export(File location) throws Exception; }
