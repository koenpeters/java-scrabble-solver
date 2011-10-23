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
import nl.cubix.scrabble.boardimporter.ocrtraining.ImageSectionEnum;
import nl.cubix.scrabble.boardimporter.ocrtraining.OcrTrainingSingleton;
import nl.cubix.scrabble.config.CropRectangle;
import nl.cubix.scrabble.config.Device;
import nl.cubix.scrabble.solver.datastructures.Board;
import nl.cubix.scrabble.solver.datastructures.Box;
import nl.cubix.scrabble.solver.datastructures.BoxTypeEnum;
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
		Board board = extractBoard(greyedImage, device, scoringSystem);

		// Get the tray
		String tray = extractTray(greyedImage, device, scoringSystem);
		
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
	
	private Board extractBoard(BufferedImage greyedImage, Device device, Scoring scoringSystem) {
		
		// Create an empty board
		Board result = Board.createEmptyBoard(scoringSystem);
		
		// Crop the image so only the board is in the image
		BufferedImage croppedBlackAndWhiteImage = cropAndBlackAndWhite(greyedImage, device.getBoardCrop());
		writeImage(croppedBlackAndWhiteImage, "board");
		
		// Prefill the two types of scanner to be used multiple times
		OcrTrainingSingleton ocrTrainingSingleton = OcrTrainingSingleton.getInstance();
		OCRScanner ocrScannerLetters = new OCRScanner();
		ocrScannerLetters.addTrainingImages(ocrTrainingSingleton.getTrainingImages(ImageSectionEnum.BOARD, 'a', 'z'));
		OCRScanner ocrScannerTiles = new OCRScanner();
		ocrScannerTiles.addTrainingImages(ocrTrainingSingleton.getTrainingImages(ImageSectionEnum.BOARD, '1', '8'));
		
		// Determine the board's dimension for this game type 
		int boardDimension = scoringSystem.getBoard().getDimension();
		for (int row=0; row < boardDimension; row++) {
			for (int col=0; col < boardDimension; col++) {
				
				Box box = performOcrOnASingleBoardBox(croppedBlackAndWhiteImage, device, ocrScannerLetters, ocrScannerTiles, row, col);
				if (box != null) {
					result.setBox(row, col, box);
				}
			}			
		}
		//log.info(result.toStringTilesOnly());
		//log.info(result.toString());
		return result;
	}
	
	private Box performOcrOnASingleBoardBox(
			BufferedImage croppedBlackAndWhiteImage
			,Device device
			,OCRScanner ocrScannerLetters
			,OCRScanner ocrScannerTiles
			,int row
			,int col) {
		
		CropRectangle boardBoxCrop = device.getBoardBoxCrop();
		float boardBoxWidth = device.getBoardBoxWidth();
		
		int x = Math.round(col * boardBoxWidth + boardBoxCrop.getX());
		int y = Math.round(row * boardBoxWidth + boardBoxCrop.getY());
		
		boolean isLetter = croppedBlackAndWhiteImage.getRGB(x, y) != -1;
		if (isLetter) {
			BufferedImage box = croppedBlackAndWhiteImage.getSubimage(x, y, boardBoxCrop.getWidth(), boardBoxCrop.getHeight());
			applyPhotographicNegative(box);
			String text = ocrScannerLetters.scan(box, 0, 0, 0, 0, null);
			text = text.replaceAll(" ", "");
			//writeImage(box, "board-(" + row + ", " + col + ", " + text + ")");
			if (text.length() > 0) {
				/* FIXME joker detection needed */
				return new Box(text.charAt(text.length() - 1), false); 
			}
		} else {
			BufferedImage box = croppedBlackAndWhiteImage.getSubimage(x, y, boardBoxCrop.getWidth(), boardBoxCrop.getHeight());
			String text = ocrScannerTiles.scan(box, 0, 0, 0, 0, null);
			text = text.replaceAll(" ", "");
			//writeImage(box, "baord-(" + row + ", " + col + ", " + text + ")");
			BoxTypeEnum boxType = parseToBoxType(text);
			if (boxType != null) {
				return new Box(boxType);
			}
		}
		return null;
	}
	private String extractTray(BufferedImage greyedImage, Device device, Scoring scoringSystem) {
		
		// Create an empty board
		StringBuilder result = new StringBuilder();
		
		// Crop the image so only the board is in the image
		BufferedImage croppedBlackAndWhiteImage = cropAndBlackAndWhite(greyedImage, device.getTrayCrop());
		//writeImage(negative(croppedBlackAndWhiteImage), "tray");
		
		// Prefill the two types of scanner to be used multiple times
		OcrTrainingSingleton ocrTrainingSingleton = OcrTrainingSingleton.getInstance();
		OCRScanner ocrScannerLettersTray = new OCRScanner();
		ocrScannerLettersTray.addTrainingImages(ocrTrainingSingleton.getTrainingImages(ImageSectionEnum.TRAY, 'a', 'z'));
		
		// Determine the board's dimension for this game type 
		int traySize = scoringSystem.getTraySize();
		for (int col=0; col < traySize; col++) {
				
			Character character = performOcrOnASingleTrayBox(croppedBlackAndWhiteImage, device, ocrScannerLettersTray, col);
			if (character != null) {
				result.append(character);
			}			
		}
		//log.info(result);
		return result.toString();
	}
	
	private Character performOcrOnASingleTrayBox(
			BufferedImage croppedBlackAndWhiteImage
			,Device device
			,OCRScanner ocrScannerLettersTray
			,int col) {
		
		CropRectangle trayBoxCrop = device.getTrayBoxCrop();
		
		int x = Math.round(col * device.getTrayBoxWidth() + trayBoxCrop.getX());
		int y = trayBoxCrop.getY();

		boolean isLetter = croppedBlackAndWhiteImage.getRGB(x, y) != -1;
		if (isLetter) {
			BufferedImage box = croppedBlackAndWhiteImage.getSubimage(x, y, trayBoxCrop.getWidth(), trayBoxCrop.getHeight());
			applyPhotographicNegative(box);
			String text = ocrScannerLettersTray.scan(box, 0, 0, 0, 0, null);
			text = text.replaceAll(" ", "");
			//writeImage(box, "tray-(" + col + ")");
			if (text.length() > 0) {
				return text.charAt(0);
			} else {
				return ' ';
			}
		}
		return null;
	}
	
	private BufferedImage cropAndBlackAndWhite(BufferedImage image, CropRectangle cropRectangle) {

		// Set threshold to turn the image into a black and white image to increase the contrast
		ByteProcessor byteProcessor = new ByteProcessor(image);
		byteProcessor.setThreshold(142, 255, ByteProcessor.BLACK_AND_WHITE_LUT);

		// Crop the image to the given ROI
		return byteProcessor.getBufferedImage().getSubimage(cropRectangle.getX(), cropRectangle.getY(), cropRectangle.getWidth(), cropRectangle.getHeight());
	}
	
	private void applyPhotographicNegative(BufferedImage img) {
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
    }
	
	private void writeImage(BufferedImage image, String namePrefix) {
		File outputfile = new File("c:\\temp\\dump\\" + namePrefix + "_" + System.currentTimeMillis() + ".png");
		try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}	
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
	

}