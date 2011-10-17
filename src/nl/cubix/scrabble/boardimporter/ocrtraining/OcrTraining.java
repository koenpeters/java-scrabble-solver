package nl.cubix.scrabble.boardimporter.ocrtraining;

import java.awt.Frame;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.CharacterRange;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImageLoader;

import org.apache.log4j.Logger;

/**
 * @author Koen Peters, Cubix Concepts
 */
class OcrTraining {

	Logger log = Logger.getLogger(OcrTraining.class);
	
	private TrainingImageLoader loader = new TrainingImageLoader();
	private HashMap<Character, ArrayList<TrainingImage>> trainingImages = new HashMap<Character, ArrayList<TrainingImage>>();
	private Frame frame = new Frame();
	 
	void addTrainingImage(String fileLocation, CharacterRange characterRange) {
		try {
			loader.load(
			        frame,
			        fileLocation,
			        characterRange,
			        trainingImages);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot read or open image training file '" + fileLocation + "'", e);
		}
	}
	
	HashMap<Character, ArrayList<TrainingImage>> getTrainingImages() {
		return trainingImages;
	}
}
