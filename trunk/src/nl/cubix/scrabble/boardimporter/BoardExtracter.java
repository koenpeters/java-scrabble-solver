package nl.cubix.scrabble.boardimporter;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import nl.cubix.scrabble.config.Device;
import nl.cubix.scrabble.solver.datastructures.Board;
import nl.cubix.scrabble.util.TimingSingleton;

import org.apache.log4j.Logger;

/**
 * This class provides the main entry to the functionality needed to extract a board from an image
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class BoardExtracter {

	private Logger log = Logger.getLogger(BoardExtracter.class);
	
	public ExtractedImage extract(File imageOfBoard) {
		TimingSingleton ts = TimingSingleton.getInstance();
		ts.resetAll(this);
		
		ts.start(this, 1);
		// We determine what kind of image this is, so we know how to handle it
		TemplateType templateType = getTemplateType(imageOfBoard);
		Device device = templateType.getDevice();
		ts.stop(this, 1);
		
		if (device == null) {
			throw new RuntimeException("Cannot determine device for " + imageOfBoard.getAbsolutePath());
		}
		
		ts.start(this, 2);
		// Create the greyed out version if the image
		BufferedImage greyedImage = getGreyedImage(imageOfBoard);
		ts.stop(this, 2);
		
		ts.start(this, 4);
		Board board = extractBoard(greyedImage, device);
		ts.stop(this, 4);

		ts.start(this, 5);
		String tray = extractTray(greyedImage, device);
		ts.stop(this, 5);
		
		log.info(ts.toString(this));

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
		// FIXME
		return new TemplateType("nl-wordfeud", "iphone", 640);
	}
	
	private Board extractBoard(BufferedImage greyedImage, Device device) {
		
		// Crop the image to the board only
		
		BufferedImage pureBoardImage = getRegionOfInterest(greyedImage, device.getBoardCropX(), device.getBoardCropY()
				,device.getBoardCropWidth(), device.getBoardCropHeight());
		
		writeImage(pureBoardImage);
		return null;
	}
	
	private String extractTray(BufferedImage greyedImage, Device device) {
		
		BufferedImage pureBoardImage = getRegionOfInterest(greyedImage, device.getTrayCropX(), device.getTrayCropY()
				,device.getTrayCropWidth(), device.getTrayCropHeight());
		
		writeImage(pureBoardImage);
		return null;
	}
	
	private BufferedImage getRegionOfInterest(BufferedImage image, int x, int y, int width, int height) {
		// Crop the image to the given ROI
		ByteProcessor byteProcessor = new ByteProcessor(image);
		byteProcessor.setRoi(x, y, width, height);
		byteProcessor = (ByteProcessor)byteProcessor.crop();
		
		// Set threshold to turn the image into a black and white image to increase the contrast
		byteProcessor.setThreshold(142, 255, ByteProcessor.BLACK_AND_WHITE_LUT);
		return byteProcessor.getBufferedImage();
	}
	
	private void writeImage(BufferedImage image) {
		File outputfile = new File("c:\\temp\\saved" + System.currentTimeMillis() + ".png");
		try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}