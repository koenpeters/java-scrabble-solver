package nl.cubix.scrabble.boardimporter.ocrtraining;

import java.awt.Frame;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.CharacterRange;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImageLoader;
import nl.cubix.scrabble.config.ConfigListener;
import nl.cubix.scrabble.util.TimingSingleton;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * @author Koen Peters, Cubix Concepts
 */
public class OcrTrainingSingleton  {
	private Map<String, OcrTraining> trainingImages = new HashMap<String, OcrTraining>();
	
	private static Logger log = Logger.getLogger(OcrTrainingSingleton.class);
	
	// Private constructor prevents instantiation from other classes
	private OcrTrainingSingleton() {
		readAllTrainingImagesFromDisk();
	}
	
	/**
	* SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	* or the first access to SingletonHolder.INSTANCE, not before.
	*/
	private static class SingletonHolder { 
	  public static final OcrTrainingSingleton instance = new OcrTrainingSingleton();
	}
 
	public static OcrTrainingSingleton getInstance() {
	  return SingletonHolder.instance;
	}
	
	public static void freeUpSpace() {
		log.info("destroying OcrTrainingSingleton");
		SingletonHolder.instance.trainingImages = null;
	}
	
	public HashMap<Character, ArrayList<TrainingImage>> getTrainingImages(String charRange) {
		OcrTraining ocrTraining = trainingImages.get(charRange);
		if (ocrTraining != null) {
			return ocrTraining.getTrainingImages();
		} else {
			StringBuffer availableRanges = new StringBuffer();
			for (String key: trainingImages.keySet()) {
				if (availableRanges.length() != 0) {
					availableRanges.append(", ");
				}
				availableRanges.append(key);
			}
			throw new RuntimeException("Unknown characterRange: " + charRange 
					+ ". Available character ranges: " + availableRanges.toString());
		}
	}
	
	/* ************************ */
	/*		 PRIVATE METHODS	 */
	/* ************************ */
	
	
	private void readAllTrainingImagesFromDisk() {
		String trainingImagesFolderStr = ConfigListener.getConfiguration().getTrainingImagesFolder();
		
		final File trainingImagesFolder = new File(trainingImagesFolderStr);
			
		File[] trainingImages = trainingImagesFolder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		});
		
		TimingSingleton timingSingleton = TimingSingleton.getInstance();
		timingSingleton.resetAll(this);
		for (File trainingImage: trainingImages) {
			timingSingleton.start(this, 1);
			
			addOneTrainingImage(trainingImage);
			
			timingSingleton.stop(this, 1);
			log.info("Successfully read training image " + trainingImage.getName() + " in " + timingSingleton.getTime(this, 1) + " msec" );
		}
	}
	
	private void addOneTrainingImage(File trainingImage) {
		
		// Determine the character range in the training image
		String fileName = FilenameUtils.removeExtension(trainingImage.getName());
		String[] parts = fileName.split("\\-");
		if (parts.length < 2) {
			throw new RuntimeException("The filename of the trainigimage must contain at least two parts seperated by an underscore.");
		}
		if (parts[0].length() != 1) {
			throw new RuntimeException("The first part of the traininimage filename may only be one character long,");
		}
		if (parts[1].length() != 1) {
			throw new RuntimeException("The second part of the traininimage filename may only be one character long,");
		}
		CharacterRange characterRange = new CharacterRange(parts[0].charAt(0), parts[1].charAt(0));
		
		OcrTraining ocrTraining = new OcrTraining();
		ocrTraining.addTrainingImage(trainingImage.getAbsolutePath(), characterRange);
		trainingImages.put(fileName, ocrTraining);
	}
}