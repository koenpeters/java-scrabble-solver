package nl.cubix.scrabble.boardimporter;

import ij.IJ;
import ij.ImagePlus;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import nl.cubix.scrabble.boardimporter.template.TemplateSet;
import nl.cubix.scrabble.boardimporter.template.TemplateSingleton;
import nl.cubix.scrabble.boardimporter.template.TemplateType;
import nl.cubix.scrabble.solver.datastructures.Board;
import nl.cubix.scrabble.solver.datastructures.Coordinate;
import nl.cubix.scrabble.solver.scoring.Scoring;
import nl.cubix.scrabble.solver.scoring.ScoringSingleton;
import nl.cubix.scrabble.solver.util.TimingSingleton;

/**
 * This class provides the main entry to the functionality needed to extract a board from an image
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class BoardExtracter {

	private Logger log = Logger.getLogger(BoardExtracter.class);
	
	public Board extract(File boardImage) {
		TemplateType templateType = getTemplateType(boardImage);
		
		ImagePlus boardImagePlus = IJ.openImage(boardImage.getAbsolutePath());
		
		TimingSingleton ts = TimingSingleton.getInstance();
		ts.resetAll(this);
		ts.start(this, 1);
		
		Board board = extractBoard(boardImagePlus, templateType);
		ts.stop(this, 1);
		
		log.info(ts.toString(this));
		return board;
	}

	private TemplateType getTemplateType(File boardImage) {
		return new TemplateType("wordfeud", 640);
	}
	
	private Board extractBoard(ImagePlus boardImage, TemplateType templateType) {
		// Create a board object with nothing on it.
		ScoringSingleton scoringSingleton = ScoringSingleton.getInstance();
		Scoring scoringSystem = scoringSingleton.getScoringSystem(templateType.getGameType());
		Board board = Board.createEmptyBoard(scoringSystem);
		
		// Get all the templates for the letters on the board
		TemplateSingleton templateSingleton = TemplateSingleton.getInstance();
		TemplateSet templateSet = templateSingleton.getTemplateSet(templateType);
		Map<Character, ImagePlus> boardLetters = templateSet.getBoardLetters();

		// First we make sure we collect all the coordinates of all the possible characters on the board
		Map<Character, Set<Coordinate>> coordinatesPerCharacter = new HashMap<Character, Set<Coordinate>>();

		Iterator<Character> iterator = boardLetters.keySet().iterator();
		Extremes extremes = new Extremes();
		while (iterator.hasNext()) {
			Character letter = iterator.next();
			ImagePlus template = boardLetters.get(letter);
			Set<Coordinate> coordinates = TemplateMatcher.getCoordinates(boardImage, template);
			coordinatesPerCharacter.put(letter, coordinates);
			extremes.add(coordinates);
		}
		
		// Now we convert those imagecoordinates into coordinates in a board object 
		// and add the characters to the board
		log.info(extremes.topMost);
		log.info(extremes.bottomMost);
		log.info(extremes.leftMost);
		log.info(extremes.rightMost);
		
		return board;
		
	}
	
	private class Extremes {
		int topMost = Integer.MAX_VALUE;
		int bottomMost = 0;
		int leftMost = Integer.MAX_VALUE;
		int rightMost = 0;
		
		void add(Set<Coordinate> coordinates) {
			for (Coordinate coordinate: coordinates) {
				topMost = Math.min(topMost, coordinate.getRow());
				bottomMost = Math.max(bottomMost, coordinate.getRow());
				leftMost = Math.min(leftMost, coordinate.getCol());
				rightMost = Math.max(rightMost, coordinate.getCol());
			}
		}
		
	}
}