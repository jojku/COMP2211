module uk.ac.soton.comp2211 {
    requires javafx.controls;
    requires org.apache.logging.log4j;
    requires java.xml;
    requires org.apache.pdfbox;
	requires javafx.swing;
	exports uk.ac.soton.comp2211.controller;
    exports uk.ac.soton.comp2211.model;
    exports uk.ac.soton.comp2211.view;
    exports uk.ac.soton.comp2211.controller.listeners;
}
