package nl.cubix.scrabble.boardimporter.template;

import ij.IJ;
import ij.ImagePlus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import nl.cubix.scrabble.solver.datastructures.BoxTypeEnum;

class TemplateSet {
	
	private TemplateType templateType;
	private Map<Character, ImagePlus> boardLetters = new HashMap<Character, ImagePlus>();
	private Map<Character, ImagePlus> trayletters = new HashMap<Character, ImagePlus>();
	private Map<BoxTypeEnum, ImagePlus> boardTiles = new HashMap<BoxTypeEnum, ImagePlus>();

	private Logger logger = Logger.getLogger(TemplateSet.class);
	
	
	/* ************************* */
	/*  PACKAGE PRIVATE METHODS  */
	/* ************************* */
	
	TemplateSet(TemplateType templateType) {
		super();
		this.templateType = templateType;
	}
	
	TemplateType getTemplateType() {
		return templateType;
	}

	Map<Character, ImagePlus> getBoardLetters() {
		return boardLetters;
	}
	
	Map<Character, ImagePlus> getTrayletters() {
		return trayletters;
	}
	
	Map<BoxTypeEnum, ImagePlus> getBoardTiles() {
		return boardTiles;
	}

	void addBoardLetter(File templateFile) {
		String fileName = FilenameUtils.removeExtension(templateFile.getName());
		if (fileName.length() != 1) {
			throw new RuntimeException("filename of board letter can only be a to z, not " + fileName);
		}
		boardLetters.put(Character.valueOf(fileName.charAt(0)), parseTemplate(templateFile));
	}
	
	void addTrayLetter(File templateFile) {
		String fileName = FilenameUtils.removeExtension(templateFile.getName());
		if (fileName.length() != 1) {
			throw new RuntimeException("filename of tray letter can only be a to z, not " + fileName);
		}
		trayletters.put(Character.valueOf(fileName.charAt(0)), parseTemplate(templateFile));
	}
	
	void addBoardTile(File templateFile) {
		String fileName = FilenameUtils.removeExtension(templateFile.getName()).toLowerCase();
		if (fileName.length() != 2) {
			throw new RuntimeException("filename of templatefile must be of size two, not " + fileName);
		}
		BoxTypeEnum key;
		if ("tw".equals(fileName)) {
			key = BoxTypeEnum.TRIPLE_WORD;
		} else if ("dw".equals(fileName)) {
			key = BoxTypeEnum.DOUBLE_WORD;
		} else if ("tl".equals(fileName)) {
			key = BoxTypeEnum.TRIPLE_LETTER;
		} else if ("dl".equals(fileName)) {
			key = BoxTypeEnum.DOUBLE_LETTER;
		} else {
			throw new RuntimeException("The filename of " + fileName + " cannot be parsed into a BoxTypeEnum. Only tw, dw, tl and dl are valid filenames.");
		}
		boardTiles.put(key, parseTemplate(templateFile));
	}
	
	boolean areAllValuesSet() {
		boolean result = boardLetters.keySet().size() == 26
		&& boardTiles.keySet().size() == 4;
		//&& trayletters.keySet().size() == 26
		
		if (!result) {
			logger.error("boardLetters size:\t" + boardLetters.keySet().size());
			logger.error("boardTiles size:\t" + boardTiles.keySet().size());
			logger.error("trayletters size:\t" + trayletters.keySet().size());
		}
		
		return result;
	}

	/* ************************ */
	/*		 PRIVATE METHODS	 */
	/* ************************ */

	
	private ImagePlus parseTemplate(File templateFile) {
		return IJ.openImage(templateFile.getAbsolutePath());
	}
}
