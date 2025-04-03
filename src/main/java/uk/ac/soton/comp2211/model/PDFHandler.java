package uk.ac.soton.comp2211.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PDFHandler {

	Airport airport;
	Runway runway;
	Obstacle obstacle;

	public PDFHandler() {
		airport = null;
		runway = null;
		obstacle = null;
	}

	public void setAirport(Airport airport) { this.airport = airport; }

	public void setRunway(Runway runway) { this.runway = runway; }

	public void setObstacle(Obstacle obstacle) { this.obstacle = obstacle; }

	public void export(File location) throws Exception{
		if (airport == null || runway == null || obstacle == null) throw new Exception("Parameter null, unable to export");

		var lr1 = runway.getLogicalRunway1();
		var lr2 = runway.getLogicalRunway2();
		LocalDateTime time = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");

		String allText = "HAirport\n" +
					"TName: "+airport.getName()+"\n" +
					"TMinimum Ascent Ratio: 1:"+airport.getMinAscentRatio()+"m\n" +
					"TBlast Distance: "+airport.getBlastDistance()+"m\nX\n" +
				"HRunway\n" +
					"TStrip End: "+runway.getStripEnd()+"m\n" +
					"TRESA: "+runway.getRESA()+"m\n" +
					"SRunway: "+lr1.getHeading()+lr1.getDesignator()+"\n" +
						"TThreshold Displacement: "+lr1.getThresholdDisplacement()+"m\n" +
						"TTORA: "+lr1.getOldTORA()+"m\n" +
						"TTODA: "+lr1.getOldTODA()+"m\n" +
						"TASDA: "+lr1.getOldASDA()+"m\n" +
						"T LDA: "+lr1.getOldLDA()+"m\n" +
					"SRunway: "+lr2.getHeading()+lr2.getDesignator()+"\n" +
						"TThreshold Displacement: "+lr2.getThresholdDisplacement()+"m\n" +
						"TTORA: "+lr2.getOldTORA()+"m\n" +
						"TTODA: "+lr2.getOldTODA()+"m\n" +
						"TASDA: "+lr2.getOldASDA()+"m\n" +
						"T LDA: "+lr2.getOldLDA()+"m\nX\n" +
				"HObstacle\n" +
					"T  Name: "+obstacle.getName()+"\n" +
					"THeight: "+obstacle.getHeight()+"m\n" +
					"T Width: "+obstacle.getWidth()+"m\n" +
					"TLength: "+obstacle.getLength()+"m\n" +
					"TDistance from "+lr1.getHeading()+lr1.getDesignator()+" threshold: "+obstacle.getdThreshold1()+"m\n" +
					"TDistance from "+lr2.getHeading()+lr2.getDesignator()+" threshold: "+obstacle.getdThreshold2()+"m\n" +
					"TDistance from centre line: "+obstacle.getdCenter()+"m\nX\n" +
				"HRedeclared Values\n" +
					"SRunway: "+lr1.getHeading()+lr1.getDesignator()+"\n" +
						"TTORA: "+lr1.getNewTORA()+"m\n" +
						"TTODA: "+lr1.getNewTODA()+"m\n" +
						"TASDA: "+lr1.getNewASDA()+"m\n" +
						"T LDA: "+lr1.getNewLDA()+"m\n" +
					"SRunway: "+lr2.getHeading()+lr2.getDesignator()+"\n" +
						"TTORA: "+lr2.getNewTORA()+"m\n" +
						"TTODA: "+lr2.getNewTODA()+"m\n" +
						"TASDA: "+lr2.getNewASDA()+"m\n" +
						"T LDA: "+lr2.getNewLDA()+"m\nX\n" +
				"ICreated "+formatter.format(time);
		String[] splitText = allText.split("\n");

		PDDocument pdf = new PDDocument();
		PDPage page = new PDPage(PDRectangle.A4);
		pdf.addPage(page);
		PDPageContentStream stream = new PDPageContentStream(pdf, page);
		stream.setLeading(18);
		stream.beginText();
		stream.newLineAtOffset(30, 800);

		for (String text : splitText) {
			switch (text.substring(0,1)) {
				case "X": stream.newLine(); continue;
				case "T": stream.setFont(PDType1Font.COURIER, 12); break;
				case "S": stream.setFont(PDType1Font.COURIER_BOLD, 12); break;
				case "H": stream.setFont(PDType1Font.COURIER_BOLD, 15); break;
				case "I": stream.setFont(PDType1Font.COURIER_OBLIQUE, 12); break;
			}
			stream.showText(text.substring(1));
			stream.newLine();
		}

		stream.endText();
		stream.close();
		pdf.save(location);
		pdf.close();
	}
}