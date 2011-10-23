package nl.cubix.scrabble.boardimporter.ocrtraining;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.CharacterRange;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import nl.cubix.scrabble.config.ConfigListener;
import nl.cubix.scrabble.util.TimingSingleton;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

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
	
	public HashMap<Character, ArrayList<TrainingImage>> getTrainingImages(ImageSectionEnum imageSection, char minCharInRange, char maxCharInRange) {
		String hashKey = createKey(imageSection, minCharInRange, maxCharInRange);
		OcrTraining ocrTraining = trainingImages.get(hashKey);
		if (ocrTraining != null) {
			return ocrTraining.getTrainingImages();
		} else {
			// chrarange not found. Throw error 
			StringBuffer availableRanges = new StringBuffer();
			for (String key: trainingImages.keySet()) {
				if (availableRanges.length() != 0) {
					availableRanges.append(", ");
				}
				availableRanges.append(key);
			}
			throw new RuntimeException("Unknown characterRange: " + imageSection + "," + minCharInRange + ", " + maxCharInRange 
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
		if (parts.length < 3) {
			throw new RuntimeException("The filename of the trainigimage must contain at least two parts seperated by an underscore.");
		}
		if (parts[1].length() != 1) {
			throw new RuntimeException("The first part of the traininimage filename may only be one character long,");
		}
		if (parts[2].length() != 1) {
			throw new RuntimeException("The second part of the traininimage filename may only be one character long,");
		}
		ImageSectionEnum imageSection;
		try {
			imageSection = ImageSectionEnum.valueOf(parts[0].toUpperCase());
		} catch (ParseException e) {
			throw new RuntimeException("Unknown boardSection: " + parts[0]);
		}
		char min = Character.toLowerCase(parts[1].charAt(0));
		char max = Character.toLowerCase(parts[2].charAt(0));
		
		CharacterRange characterRange = new CharacterRange(min, max);
		
		OcrTraining ocrTraining = new OcrTraining();
		ocrTraining.addTrainingImage(trainingImage.getAbsolutePath(), characterRange, imageSection);
		String hashKey = createKey(imageSection, min, max);
		trainingImages.put(hashKey, ocrTraining);
	}
	
	private String createKey(ImageSectionEnum imageSection, char min, char max) {
		StringBuilder result  = new StringBuilder();
		result.append("(").append(imageSection).append(", ").append(min).append(", ").append(max).append(")");
		return result.toString();
	}
}