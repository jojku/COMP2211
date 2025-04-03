package uk.ac.soton.comp2211.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XMLHandler {

    static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();


    /**
     * Calls write method to save runway to File fileName
     * @param file fileName
     * @param runway runway
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public static void saveRunway(File file, Runway runway) throws TransformerException, ParserConfigurationException, IOException, NullPointerException {
        write(file, writeRunway(file, runway));
    }

    /**
     * Writes runway to a Document doc and returns it
     * @param file fileName
     * @param runway runway
     * @return
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    private static Document writeRunway (File file, Runway runway) throws TransformerException, ParserConfigurationException, IOException {
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.newDocument();

        Element rootElem = doc.createElement("runway");
        doc.appendChild(rootElem);

        saveRunwayFields(doc, rootElem, runway);

        return doc;
    }

    /**
     * Adds the fields of a runway to a Document doc
     * @param doc document
     * @param rootElem root element of document
     * @param runway runway
     */
    private static void saveRunwayFields (Document doc, Element rootElem,  Runway runway){


        Element designator = doc.createElement("Designator");
        rootElem.appendChild(designator);
        designator.appendChild(doc.createTextNode((String.valueOf(runway.getDesignator()))));

        Element heading = doc.createElement("Heading");
        rootElem.appendChild(heading);
        heading.appendChild(doc.createTextNode(String.valueOf(runway.getHeading())));

        Element stripEnd = doc.createElement("StripEnd");
        rootElem.appendChild(stripEnd);
        stripEnd.appendChild(doc.createTextNode(String.valueOf(runway.getStripEnd())));

        Element RESA = doc.createElement("RESA");
        rootElem.appendChild(RESA);
        RESA.appendChild(doc.createTextNode(String.valueOf(runway.getRESA())));

        Element TORA1 = doc.createElement("TORA1");
        rootElem.appendChild(TORA1);
        TORA1.appendChild(doc.createTextNode(String.valueOf(runway.getLogicalRunway1().getNewTORA())));

        Element TODA1 = doc.createElement("TODA1");
        rootElem.appendChild(TODA1);
        TODA1.appendChild(doc.createTextNode(String.valueOf(runway.getLogicalRunway1().getNewTODA())));

        Element ASDA1 = doc.createElement("ASDA1");
        rootElem.appendChild(ASDA1);
        ASDA1.appendChild(doc.createTextNode(String.valueOf(runway.getLogicalRunway1().getNewASDA())));

        Element LDA1 = doc.createElement("LDA1");
        rootElem.appendChild(LDA1);
        LDA1.appendChild(doc.createTextNode(String.valueOf(runway.getLogicalRunway1().getNewLDA())));

        Element thresholdDisplacement1 = doc.createElement("ThresholdDisplay1");
        rootElem.appendChild(thresholdDisplacement1);
        thresholdDisplacement1.appendChild(doc.createTextNode(String.valueOf(runway.getLogicalRunway1().getThresholdDisplacement())));

        Element TORA2 = doc.createElement("TORA2");
        rootElem.appendChild(TORA2);
        TORA2.appendChild(doc.createTextNode(String.valueOf(runway.getLogicalRunway2().getNewTORA())));

        Element TODA2 = doc.createElement("TODA2");
        rootElem.appendChild(TODA2);
        TODA2.appendChild(doc.createTextNode(String.valueOf(runway.getLogicalRunway2().getNewTODA())));

        Element ASDA2 = doc.createElement("ASDA2");
        rootElem.appendChild(ASDA2);
        ASDA2.appendChild(doc.createTextNode(String.valueOf(runway.getLogicalRunway2().getNewASDA())));

        Element LDA2 = doc.createElement("LDA2");
        rootElem.appendChild(LDA2);
        LDA2.appendChild(doc.createTextNode(String.valueOf(runway.getLogicalRunway2().getNewLDA())));

        Element threshDisp2 = doc.createElement("ThresholdDisplay2");
        rootElem.appendChild(threshDisp2);
        threshDisp2.appendChild(doc.createTextNode(String.valueOf(runway.getLogicalRunway2().getThresholdDisplacement())));
    }


    /**
     * Saves a single obstacle to File fileName
     * @param file fileName
     * @param obstacle obstacle
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws IOException
     */
    public static void saveObstacle(File file, Obstacle obstacle) throws ParserConfigurationException, TransformerException, IOException {

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.newDocument();

        Element rootElem = doc.createElement("obstacle");
        doc.appendChild(rootElem);

        saveObstacleFields(doc, rootElem, obstacle);

        write(file, doc);
    }

    /**
     * Adds the fields of an Obstacle to a Document doc
     * @param doc document
     * @param rootElem root element of document
     * @param obstacle obstacle
     */
    private static void saveObstacleFields(Document doc, Element rootElem, Obstacle obstacle) {
        Element name = doc.createElement("ObstacleName");
        rootElem.appendChild(name);
        name.appendChild(doc.createTextNode(obstacle.getName()));

        Element height = doc.createElement("height");
        rootElem.appendChild(height);
        height.appendChild(doc.createTextNode(String.valueOf(obstacle.getHeight())));

        Element width = doc.createElement("width");
        rootElem.appendChild(width);
        width.appendChild(doc.createTextNode(String.valueOf(obstacle.getWidth())));

        Element length = doc.createElement("length");
        rootElem.appendChild(length);
        length.appendChild(doc.createTextNode(String.valueOf(obstacle.getLength())));
    }

    /**
     * Saves a list of Obstacles to a File fileName
     * @param file fileName
     * @param obstacles Obstacles
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws IOException
     */
    public static void saveObstacles (File file, ArrayList<Obstacle> obstacles) throws Exception {
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.newDocument();

        Element rootElem = doc.createElement("Obstacles");
        doc.appendChild(rootElem);

        for(Obstacle obstacle : obstacles) {
            Element obstacleElem = doc.createElement("obstacle");
            rootElem.appendChild(obstacleElem);
            saveObstacleFields(doc, obstacleElem, obstacle);
        }

        write(file, doc);
    }

    /**
     * Writes a Document doc to a File fileName
     * @param file
     * @param doc
     * @throws TransformerException
     * @throws IOException
     */
    private static void write(File file, Document doc) throws TransformerException, IOException, NullPointerException {
        FileOutputStream output =
                new FileOutputStream(file);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Source source = new DOMSource(doc);
        Result result = new StreamResult(output);

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);

        output.close();
    }

    /**
     * Loads a Runway from an XML file
     * @param file
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static Runway loadRunway(File file) throws Exception {
       return getRunway(file, 0);
    }

    /**
     * Returns a Runway with index 'index' from an XML file
     * @param file
     * @param index
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private static Runway getRunway (File file, int index) throws Exception {

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();

        char designator = doc.getDocumentElement().getElementsByTagName("Designator").item(index).getTextContent().charAt(0);
        int heading = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("Heading").item(index).getTextContent());
        int stripEnd = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("StripEnd").item(index).getTextContent());
        int resa = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("RESA").item(index).getTextContent());
        int tora1 = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("TORA1").item(index).getTextContent());
        int toda1 = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("TODA1").item(index).getTextContent());
        int asda1 = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("ASDA1").item(index).getTextContent());
        int lda1 = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("LDA1").item(index).getTextContent());
        int threshold1 = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("ThresholdDisplay1").item(index).getTextContent());
        int tora2 = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("TORA2").item(index).getTextContent());
        int toda2 = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("TODA2").item(index).getTextContent());
        int asda2 = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("ASDA2").item(index).getTextContent());
        int lda2 = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("LDA2").item(index).getTextContent());
        int threshold2 = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("ThresholdDisplay2").item(index).getTextContent());

        return new Runway(heading, designator, stripEnd, resa, tora1, toda1, asda1, lda1, threshold1, tora2, toda2, asda2, lda2, threshold2);
    }

    /**
     * Loads an obstacle from an XML file
     * @param file
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static Obstacle loadObstacle(File file) throws Exception {
       return getObstacle(file, 0);
    }

    /**
     * Loads an obstacle with an index 'index' from an XML file
     * @param file
     * @param index
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private static Obstacle getObstacle (File file, int index) throws Exception {
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();

        String name = doc.getDocumentElement().getElementsByTagName("ObstacleName").item(index).getTextContent();
        Integer height = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("height").item(index).getTextContent());
        Integer width = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("width").item(index).getTextContent());
        Integer length = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("length").item(index).getTextContent());

        return new Obstacle(name, height, width, length);
    }

    /**
     * Loads a list of Obstacles from an XML file
     * @param file
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static ArrayList<Obstacle> loadObstacles(File file) throws Exception {
        ArrayList<Obstacle> obstacles = new ArrayList<>();

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();

        NodeList nodes = doc.getElementsByTagName("obstacle");

        for(int i = 0; i < nodes.getLength(); i++) {
            obstacles.add(getObstacle(file, i));
        }
        return obstacles;
    }

    /**
     * Loads an Airport from an XML file
     * @param file
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Airport loadAirport(File file) throws Exception {
        ArrayList<Runway> runways = new ArrayList<>();

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();

        //if(doc.getDocumentElement().getNodeName() != "Airport") return null;

        NodeList nodes = doc.getElementsByTagName("runway");

        for(int i = 0; i < nodes.getLength(); i++) {
            runways.add(getRunway(file, i));
        }
        String name = doc.getDocumentElement().getElementsByTagName("AirportName").item(0).getTextContent();
        float minAscentRatio = Float.parseFloat(doc.getDocumentElement().getElementsByTagName("MinAscent").item(0).getTextContent());
        Integer blastDistance = Integer.parseInt(doc.getDocumentElement().getElementsByTagName("BlastDistance").item(0).getTextContent());

        return new Airport(name, runways, minAscentRatio, blastDistance);
    }

    /**
     * Saves Airport to an XML file
     * @param file
     * @param airport
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws IOException
     */
    public static void saveAirport(File file, Airport airport) throws ParserConfigurationException, TransformerException, IOException {
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.newDocument();

        Element rootElem = doc.createElement("Airport");
        doc.appendChild(rootElem);

        Element name = doc.createElement("AirportName");
        rootElem.appendChild(name);
        name.appendChild(doc.createTextNode(airport.getName()));

        Element runways = doc.createElement("runways");
        rootElem.appendChild(runways);

        for(Runway runway : airport.getRunways()) {
            Element runElem = doc.createElement("runway");
            runways.appendChild(runElem);
            saveRunwayFields(doc, runElem, runway);
        }


        Element minAscent = doc.createElement("MinAscent");
        rootElem.appendChild(minAscent);
        minAscent.appendChild(doc.createTextNode(String.valueOf(airport.getMinAscentRatio())));

        Element blastDist = doc.createElement("BlastDistance");
        rootElem.appendChild(blastDist);
        blastDist.appendChild(doc.createTextNode(String.valueOf(airport.getBlastDistance())));



        write(file, doc);
    }
}