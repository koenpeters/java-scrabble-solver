package nl.cubix.scrabble.boardimporter;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.OCRScanner;
import nl.cubix.scrabble.boardimporter.ocrtraining.OcrTrainingSingleton;
import nl.cubix.scrabble.config.CropRectangle;
import nl.cubix.scrabble.config.Device;
import nl.cubix.scrabble.solver.datastructures.Board;
import nl.cubix.scrabble.solver.datastructures.Box;
import nl.cubix.scrabble.solver.datastructures.BoxTypeEnum;
import nl.cubix.scrabble.solver.scoring.Scoring;
import nl.cubix.scrabble.solver.scoring.ScoringSingleton;
import nl.cubix.scrabble.util.ParamValidationUtil;
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
		ParamValidationUtil.validateParamNotNull(imageOfBoard, "imageOfBoard");
		
		TimingSingleton ts = TimingSingleton.getInstance();
		ts.resetAll(this);
		
		ts.start(this, 1);
		// We determine what kind of image this is, so we know how to handle it
		TemplateType templateType = getTemplateType(imageOfBoard);
		Device device = templateType.getDevice();
		ScoringSingleton scoringSingleton = ScoringSingleton.getInstance();
		Scoring scoringSystem = scoringSingleton.getScoringSystem(device.getGameType());
		
		if (device == null) {
			throw new RuntimeException("Cannot determine device for " + imageOfBoard.getAbsolutePath());
		}
		ts.stop(this, 1);
		
		ts.start(this, 2);
		// Create the greyed out version if the image
		BufferedImage greyedImage = getGreyedImage(imageOfBoard);
		ts.stop(this, 2);
		
		ts.start(this, 4);
		Board board = extractBoard(greyedImage, device, scoringSystem);
		ts.stop(this, 4);

		ts.start(this, 5);
		String tray = extractTray(greyedImage, device, scoringSystem);
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
	
	private Board extractBoard(BufferedImage greyedImage, Device device, Scoring scoringSystem) {
		
		// Create an empty board
		Board result = Board.createEmptyBoard(scoringSystem);
		
		// Crop the image so only the board is in the image
		BufferedImage croppedBlackAndWhiteImage = cropAndBlackAndWhite(greyedImage, device.getBoardCrop());
		writeImage(croppedBlackAndWhiteImage, "board");
		
		// Prefill the two types of scanner to be used multiple times
		OcrTrainingSingleton ocrTrainingSingleton = OcrTrainingSingleton.getInstance();
		OCRScanner ocrScannerLetters = new OCRScanner();
		ocrScannerLetters.addTrainingImages(ocrTrainingSingleton.getTrainingImages("a-z"));
		OCRScanner ocrScannerTiles = new OCRScanner();
		ocrScannerTiles.addTrainingImages(ocrTrainingSingleton.getTrainingImages("1-8"));
		
		// Determine the board's dimension for this game type 
		int boardDimension = scoringSystem.getBoard().getDimension();
		for (int row=0; row < boardDimension; row++) {
			for (int col=0; col < boardDimension; col++) {
				
				Box box = performOcrOnASingleBox(croppedBlackAndWhiteImage, device.getBoardBoxCrop(), ocrScannerLetters
						,ocrScannerTiles, device.getBoardBoxWidth(), row, col);
				if (box != null) {
					result.setBox(row, col, box);
				}
			}			
		}
		log.info(result.toStringTilesOnly());
		log.info(result.toString());
		return result;
	}
	
	private Box performOcrOnASingleBox(
			BufferedImage croppedBlackAndWhiteImage
			,CropRectangle boardBoxCrop
			,OCRScanner ocrScannerLetters
			,OCRScanner ocrScannerTiles
			,float boardBoxWidth
			,int row
			,int col) {
		int x = Math.round(col * boardBoxWidth + boardBoxCrop.getX());
		int y = Math.round(row * boardBoxWidth + boardBoxCrop.getY());
		boolean isLetter = croppedBlackAndWhiteImage.getRGB(x, y) != -1;
		
		BufferedImage box;
		if (isLetter) {
			box = croppedBlackAndWhiteImage.getSubimage(x, y, boardBoxCrop.getWidth() - 10, boardBoxCrop.getHeight());
			box = negative(box);
			String text = ocrScannerLetters.scan(box, 0, 0, 0, 0, null);
			text = text.replaceAll(" ", "");
			//writeImage(box, "(" + row + ", " + col + ", " + text + ")");
			if (text.length() > 0) {
				/* FIXME joker detection needed */
				return new Box(text.charAt(text.length() - 1), false); 
			}
		} else {
			box = croppedBlackAndWhiteImage.getSubimage(x, y, boardBoxCrop.getWidth(), boardBoxCrop.getHeight());
			String text = ocrScannerTiles.scan(box, 0, 0, 0, 0, null);
			text = text.replaceAll(" ", "");
			//writeImage(box, "(" + row + ", " + col + ", " + text + ")");
			BoxTypeEnum boxType = parseToBoxType(text);
			if (boxType != null) {
				return new Box(boxType);
			}
		}
		return null;
	}

	private BoxTypeEnum parseToBoxType(String ocrResult) {
		try {
			int number = Integer.parseInt(ocrResult);

			switch (number) {
			case 5: return BoxTypeEnum.TRIPLE_WORD;
			case 6: return BoxTypeEnum.TRIPLE_LETTER;
			case 7: return BoxTypeEnum.DOUBLE_LETTER;
			case 8: return BoxTypeEnum.DOUBLE_WORD;
			case 12: return BoxTypeEnum.TRIPLE_LETTER; 
			case 13: return BoxTypeEnum.TRIPLE_WORD; 
			case 42: return BoxTypeEnum.DOUBLE_LETTER;
			case 43: return BoxTypeEnum.DOUBLE_WORD;
			}
		} catch (NumberFormatException e) {
			// Cannot parse the number. It most likely is an empty box
			log.debug("fout!", e);
		}
		return null;
	}
	
	private BufferedImage negative(BufferedImage img) {
        Color col;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int RGBA = img.getRGB(x, y);
                col = new Color(RGBA, true); //get the color data of the specific pixel
                col = new Color(
                		Math.abs(col.getRed() - 255)
                        ,Math.abs(col.getGreen() - 255)
                        ,Math.abs(col.getBlue() - 255)); //Swaps values
                img.setRGB(x, y, col.getRGB());
            }
        }
        return img;
    }
	
	private String extractTray(BufferedImage greyedImage, Device device, Scoring scoringSystem) {
		
		//BufferedImage pureBoardImage = getRegionOfInterest(greyedImage, device.getTrayCrop());
		//writeImage(pureBoardImage);
		
		return null;
	}
	
	private BufferedImage cropAndBlackAndWhite(BufferedImage image, CropRectangle cropRectangle) {

		// Set threshold to turn the image into a black and white image to increase the contrast
		ByteProcessor byteProcessor = new ByteProcessor(image);
		byteProcessor.setThreshold(142, 255, ByteProcessor.BLACK_AND_WHITE_LUT);

		// Crop the image to the given ROI
		return byteProcessor.getBufferedImage().getSubimage(cropRectangle.getX(), cropRectangle.getY(), cropRectangle.getWidth(), cropRectangle.getHeight());
	}
	
	private void writeImage(BufferedImage image, String namePrefix) {
		File outputfile = new File("c:\\temp\\dump\\" + namePrefix + "_" + System.currentTimeMillis() + ".png");
		try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}