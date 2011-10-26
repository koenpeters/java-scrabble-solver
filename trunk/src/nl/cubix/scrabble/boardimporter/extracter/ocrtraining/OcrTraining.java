package nl.cubix.scrabble.boardimporter.extracter.ocrtraining;

import java.awt.Frame;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.CharacterRange;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImageLoader;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

/**
 * @author Koen Peters, Cubix Concepts
 */
class OcrTraining {

	Logger log = Logger.getLogger(OcrTraining.class);
	
	private ImageSectionEnum imageSection;
	private CharacterRange characterRange;
	private String fileLocation;
	private TrainingImageLoader loader = new TrainingImageLoader();
	private HashMap<Character, ArrayList<TrainingImage>> trainingImages = new HashMap<Character, ArrayList<TrainingImage>>();
	private Frame frame = new Frame();
	 
	void addTrainingImage(String fileLocation, CharacterRange characterRange, ImageSectionEnum imageSection) {
		this.fileLocation = fileLocation;
		this.characterRange = characterRange;
		this.imageSection = imageSection;
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
	
	public CharacterRange getCharacterRange() {
		return characterRange;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OcrTraining == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		
		OcrTraining ocrTraining = (OcrTraining) obj;
		return new EqualsBuilder()
					.append(imageSection, ocrTraining.imageSection)
					.append(characterRange.min, ocrTraining.characterRange.min)
					.append(characterRange.max, ocrTraining.characterRange.max)
					.append(fileLocation, ocrTraining.fileLocation)
					.isEquals();
	}
	
	@Override
	public int hashCode() {
		  return new HashCodeBuilder(53, 31).
	       append(imageSection).
	       append(characterRange.min).
	       append(characterRange.max).
	       append(fileLocation).
	       toHashCode();
	}
}
