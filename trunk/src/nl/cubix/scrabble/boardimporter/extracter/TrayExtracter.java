package nl.cubix.scrabble.boardimporter.extracter;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.OCRScanner;
import nl.cubix.scrabble.boardimporter.extracter.ocrtraining.ImageSectionEnum;
import nl.cubix.scrabble.boardimporter.extracter.ocrtraining.OcrTrainingSingleton;
import nl.cubix.scrabble.config.CropRectangle;
import nl.cubix.scrabble.config.Device;
import nl.cubix.scrabble.solver.scoring.Scoring;

class TrayExtracter extends AbstractExtracter {

	private Logger log = Logger.getLogger(TrayExtracter.class);
	
	String extract(BufferedImage greyedImage, Device device, Scoring scoringSystem) {
		
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
}
