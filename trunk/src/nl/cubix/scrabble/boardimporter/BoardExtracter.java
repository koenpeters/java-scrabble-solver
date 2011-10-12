package nl.cubix.scrabble.boardimporter;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import nl.cubix.scrabble.boardimporter.template.DeviceInfo;
import nl.cubix.scrabble.boardimporter.template.TemplateSet;
import nl.cubix.scrabble.boardimporter.template.TemplateSingleton;
import nl.cubix.scrabble.boardimporter.template.TemplateType;
import nl.cubix.scrabble.solver.datastructures.Board;
import nl.cubix.scrabble.solver.datastructures.Box;
import nl.cubix.scrabble.solver.datastructures.Coordinate;
import nl.cubix.scrabble.solver.datastructures.Word;
import nl.cubix.scrabble.solver.scoring.Scoring;
import nl.cubix.scrabble.solver.scoring.ScoringSingleton;
import nl.cubix.scrabble.solver.util.TimingSingleton;

import org.apache.log4j.Logger;

/**
 * This class provides the main entry to the functionality needed to extract a board from an image
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class BoardExtracter {

	private Logger log = Logger.getLogger(BoardExtracter.class);
	
	public ExtractedImage extract(File imageOfBoard) {
		TimingSingleton ts = TimingSingleton.getInstance();
		ts.resetAll(this);
		
		// We determine what kind of image this is, so we know how to handle it
		ts.start(this, 1);
		TemplateType templateType = getTemplateType(imageOfBoard);
		ts.stop(this, 1);
		
		ts.start(this, 2);
		BufferedImage boardImagePlus = prepareImageOfBoard(imageOfBoard, templateType);
		ts.stop(this, 2);
		
		
		ts.start(this, 3);
		Board board = extractBoard(boardImagePlus, templateType);
		ts.stop(this, 3);

		ts.start(this, 4);
		String tray = extractTray(boardImagePlus, templateType);
		ts.stop(this, 4);
		
		log.info(ts.toString(this));

		File outputfile = new File("c:\\temp\\saved" + System.currentTimeMillis() + ".png");
		try {
			ImageIO.write(boardImagePlus, "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return new ExtractedImage(board, tray);
	}

	private BufferedImage prepareImageOfBoard(File imageOfBoard, TemplateType templateType) {
		
		// Open the image from the file 
		ImagePlus result = IJ.openImage(imageOfBoard.getAbsolutePath());
		
		// 1) turn into 8bit greyscale
		ImageConverter imageConverter = new ImageConverter(result);
		imageConverter.convertToGray8();
		
		// 2) Crop to only the board and the tray
		ByteProcessor byteProcessor = new ByteProcessor(result.getBufferedImage());
		byteProcessor.setRoi(0, 0, 100, 100);
		byteProcessor.crop();
		
		// 3) Use treshholding to turn it into a black and white image to increase the contrast
		byteProcessor.setThreshold(142, 255, ByteProcessor.BLACK_AND_WHITE_LUT);
		return byteProcessor.getBufferedImage();
	}
	
	
	
	
	
	
	
	
	
	private TemplateType getTemplateType(File boardImage) {
		return new TemplateType("nl-wordfeud", "iphone", 640);
	}
	
	private Board extractBoard(BufferedImage boardImage, TemplateType templateType) {
		/*
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
		while (iterator.hasNext()) {
			Character letter = iterator.next();
			ImagePlus template = boardLetters.get(letter);
			Set<Coordinate> coordinates = TemplateMatcher.getCoordinates(boardImage, template);
			coordinatesPerCharacter.put(letter, coordinates);
		}
		
		// Now we convert those imagecoordinates into coordinates in a board object 
		// and add the characters to the board
		DeviceInfo deviceInfo = templateSet.getDeviceInfo();
		iterator = coordinatesPerCharacter.keySet().iterator();
		float boxSize = new Float(deviceInfo.getScreenWidth()) / new Float(board.getDimension()); 
		while (iterator.hasNext()) {
			Character character = iterator.next();
			for (Coordinate coordinate: coordinatesPerCharacter.get(character)) {
				int row = Math.round((new Float(coordinate.getRow()) - new Float(deviceInfo.getBoardOffSet())) / boxSize);
				int col  = Math.round(new Float(coordinate.getCol()) / boxSize);
				Box box = new Box(character.charValue(), false); // FIXME: bepalen of dit een joker is... argh
				
				log.info("deviceInfo.getBoardOffSet(): " + deviceInfo.getBoardOffSet());
				log.info("coordinate.getRow(): " + coordinate.getRow());
				log.info("coordinate.getCol(): " + coordinate.getCol());
				log.info("board.getDimension(): " + board.getDimension());
				log.info("row: " + row);
				log.info("col: " + col);
				log.info("box: " + box);
				board.setBox(row, col, box);
			}
		}
		log.info(board);
		return board;
		*/
		return null;
	}
	
	private String extractTray(BufferedImage boardImage, TemplateType templateType) {
		// TODO
		return null;
	}
}