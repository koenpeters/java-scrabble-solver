package nl.cubix.scrabble.boardimporter.extracter;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageConverter;

import java.awt.image.BufferedImage;
import java.io.File;

import nl.cubix.scrabble.boardimporter.GameDetector.TemplateType;
import nl.cubix.scrabble.solver.datastructures.Board;
import nl.cubix.scrabble.util.ParamValidationUtil;

import org.apache.log4j.Logger;

/**
 * This class provides the main entry to the functionality needed to extract a board from an image
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class Extracter {

	private Logger log = Logger.getLogger(Extracter.class);
	
	public ExtractedImage extract(File imageOfBoard, TemplateType templateType) {
		ParamValidationUtil.validateParamNotNull(imageOfBoard, "imageOfBoard");
		
		// Create the greyed out version if the image
		BufferedImage greyedImage = convertToGrey(imageOfBoard);
		
		// Get the board
		BoardExtracter boardExtracter = new BoardExtracter();
		Board board = boardExtracter.extract(greyedImage, templateType.getDevice(), templateType.getScoringSystem());

		// Get the tray
		TrayExtracter trayExtracter = new TrayExtracter();
		String tray = trayExtracter.extract(greyedImage, templateType.getDevice(), templateType.getScoringSystem());
		
		return new ExtractedImage(board, tray);
	}

	private BufferedImage convertToGrey(File imageOfBoard) {
		
		ImagePlus imagePlus = IJ.openImage(imageOfBoard.getAbsolutePath());
		
		// Turn into 8bit greyscale
		ImageConverter imageConverter = new ImageConverter(imagePlus);
		imageConverter.convertToGray8();
		
		return imagePlus.getBufferedImage();
	}
}
