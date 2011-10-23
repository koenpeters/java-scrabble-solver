package nl.cubix.scrabble.boardimporter;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageConverter;

import java.awt.image.BufferedImage;
import java.io.File;

import nl.cubix.scrabble.config.Device;
import nl.cubix.scrabble.solver.datastructures.Board;
import nl.cubix.scrabble.solver.scoring.Scoring;
import nl.cubix.scrabble.solver.scoring.ScoringSingleton;
import nl.cubix.scrabble.util.ParamValidationUtil;

import org.apache.log4j.Logger;

/**
 * This class provides the main entry to the functionality needed to extract a board from an image
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class Extracter {

	private Logger log = Logger.getLogger(Extracter.class);
	
	public ExtractedImage extract(File imageOfBoard) {
		ParamValidationUtil.validateParamNotNull(imageOfBoard, "imageOfBoard");
		
		// We determine what kind of image this is, so we know how to handle it
		TemplateType templateType = getTemplateType(imageOfBoard);
		Device device = templateType.getDevice();
		ScoringSingleton scoringSingleton = ScoringSingleton.getInstance();
		Scoring scoringSystem = scoringSingleton.getScoringSystem(device.getGameType());
		
		if (device == null) {
			throw new RuntimeException("Cannot determine device for " + imageOfBoard.getAbsolutePath());
		}
		
		// Create the greyed out version if the image
		BufferedImage greyedImage = getGreyedImage(imageOfBoard);
		
		// Get the board
		BoardExtracter boardExtracter = new BoardExtracter();
		Board board = boardExtracter.extract(greyedImage, device, scoringSystem);

		// Get the tray
		TrayExtracter trayExtracter = new TrayExtracter();
		String tray = trayExtracter.extract(greyedImage, device, scoringSystem);
		
		return new ExtractedImage(board, tray);
	}

	private BufferedImage getGreyedImage(File imageOfBoard) {
		
		// Open the image from the file 
		ImagePlus result = IJ.openImage(imageOfBoard.getAbsolutePath());
		
		// Turn into 8bit greyscale
		ImageConverter imageConverter = new ImageConverter(result);
		imageConverter.convertToGray8();
		
		return result.getBufferedImage();
	}
	
	
	private TemplateType getTemplateType(File boardImage) {
		// FIXME build detection
		return new TemplateType("nl-wordfeud", "iphone", 640, 960);
	}
	




	

}