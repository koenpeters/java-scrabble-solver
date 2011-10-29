package nl.cubix.scrabble.boardimporter.extracter;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.OCRScanner;
import nl.cubix.scrabble.boardimporter.extracter.ocrtraining.ImageSectionEnum;
import nl.cubix.scrabble.boardimporter.extracter.ocrtraining.OcrTrainingSingleton;
import nl.cubix.scrabble.config.CropRectangle;
import nl.cubix.scrabble.config.Device;
import nl.cubix.scrabble.solver.datastructures.Board;
import nl.cubix.scrabble.solver.datastructures.Box;
import nl.cubix.scrabble.solver.datastructures.BoxTypeEnum;
import nl.cubix.scrabble.solver.scoring.Scoring;

class BoardExtracter extends AbstractExtracter {

	private Logger log = Logger.getLogger(BoardExtracter.class);
	
	Board extract(BufferedImage greyedImage, Device device, Scoring scoringSystem) {
		
		// Create an empty board
		Board result = Board.createEmptyBoard(scoringSystem);
		
		// Crop the image so only the board is in the image
		BufferedImage croppedBlackAndWhiteImage = cropAndBlackAndWhite(greyedImage, device.getBoardCrop());
		//writeImage(croppedBlackAndWhiteImage, "board");
		
		// Get the letter and tile scanners from the singelton
		OcrTrainingSingleton ocrTrainingSingleton = OcrTrainingSingleton.getInstance();
		OCRScanner ocrScannerLetters = new OCRScanner();
		ocrScannerLetters.addTrainingImages(ocrTrainingSingleton.getTrainingImages(ImageSectionEnum.BOARD, 'a', 'z'));
		OCRScanner ocrScannerTiles = new OCRScanner();
		ocrScannerTiles.addTrainingImages(ocrTrainingSingleton.getTrainingImages(ImageSectionEnum.BOARD, '1', '8'));
		
		// Determine the board's dimension for this game type 
		int boardDimension = scoringSystem.getBoard().getDimension();
		
		// Loop over each box of the board and scan the board, resulting in a Box object
		for (int row=0; row < boardDimension; row++) {
			for (int col=0; col < boardDimension; col++) {
				
				Box box = performOcrOnASingleBoardBox(croppedBlackAndWhiteImage, device, ocrScannerLetters, ocrScannerTiles, row, col);
				if (box != null) {
					
					// We actually found a letter of a special box meaning (dl, tw, etc). 
					// Add it to the result.
					result.setBox(row, col, box);
				}
			}			
		}
		return result;
	}
	
	private Box performOcrOnASingleBoardBox(
			BufferedImage bAndWimage
			,Device device
			,OCRScanner ocrScannerLetters
			,OCRScanner ocrScannerTiles
			,int row
			,int col) {
		
		int xOffset = Math.round(col * device.getBoardBoxWidth());
		int yOffset = Math.round(row * device.getBoardBoxWidth());
		
		boolean isLetter = bAndWimage.getRGB(xOffset + device.getBoardBoxLetterCrop().getX(), yOffset + device.getBoardBoxLetterCrop().getY()) == BLACK;

		if (isLetter) {
			CropRectangle boxCrop = device.getBoardBoxLetterCrop();

			// Crop the whole image to the box we want to scan in this iteration
			BufferedImage box = bAndWimage.getSubimage(xOffset + boxCrop.getX(), yOffset + boxCrop.getY()
														,boxCrop.getWidth(), boxCrop.getHeight());

			// We need a black letter on a white background. The letter tiles need to
			// be negated to get this effect.
			applyPhotographicNegative(box);
			//writeImage(box, "board-letter-(" + row + ", " + col + ")");
			
			//writeImage(box, "board-letter-(" + col + ")");
			// Read the letter in this box
			String text = ocrScannerLetters.scan(box, 0, 0, 0, 0, null);
			text = text.replaceAll(" ", "");
			
			if (text.length() > 0) {
				// Check if this box is a joker or a normal letter
				boolean isJoker = isJoker(bAndWimage, xOffset, yOffset, device);
				return new Box(text.charAt(text.length() - 1), isJoker);
			}
		} else {
			CropRectangle boxCrop = device.getBoardBoxTileCrop();

			// Crop the whole image to the box we want to scan in this iteration
			BufferedImage box = bAndWimage.getSubimage(xOffset + boxCrop.getX(), yOffset + boxCrop.getY()
					,boxCrop.getWidth(), boxCrop.getHeight());

			// Read the tiles in this box
			String text = ocrScannerTiles.scan(box, 0, 0, 0, 0, null);
			text = text.replaceAll(" ", "");
			//writeImage(box, "board-tile-(" + row + ", " + col + ")");
			
			BoxTypeEnum boxType = parseToBoxType(text);
			if (boxType != null) {
				return new Box(boxType);
			}
		}
		
		return null;
	}
	
	private boolean isJoker(BufferedImage croppedBlackAndWhiteImage, int xOffset, int yOffset, Device device) {
		
		CropRectangle nbrCrop = device.getBoardBoxNumberCrop();
		BufferedImage scoringBox = croppedBlackAndWhiteImage.getSubimage(
				xOffset + nbrCrop.getX(), yOffset + nbrCrop.getY(), nbrCrop.getWidth(), nbrCrop.getHeight());
		//writeImage(scoringBox, "numbers");
		return isUniformColor(scoringBox);
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