package org.solvr.scrabble.scoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.solvr.scrabble.config.ConfigListener;
import org.solvr.scrabble.datastructures.Board;
import org.solvr.scrabble.datastructures.Box;
import org.solvr.scrabble.datastructures.BoxTypeEnum;
import org.solvr.scrabble.util.TimingSingleton;

/**
 * @author Koen Peters, Cubix Concepts
 */
public class ScoringSingleton  {
	private Map<String, Scoring> scoringSystems = new HashMap<String, Scoring>();
	private static Logger log = Logger.getLogger(ScoringSingleton.class);
	
	// Private constructor prevents instantiation from other classes
	private ScoringSingleton() {
		readAllScoringSystemsFromDisk();
	}
	
	/**
	* SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	* or the first access to SingletonHolder.INSTANCE, not before.
	*/
	private static class SingletonHolder { 
	  public static final ScoringSingleton instance = new ScoringSingleton();
	}
 
	public static ScoringSingleton getInstance() {
	  return SingletonHolder.instance;
	}
	
	public static void freeUpSpace() {
		log.info("destroying scoringSystems");
		SingletonHolder.instance.scoringSystems = null;
	}
	
	public Set<String> getScoringSystems() {
		return scoringSystems.keySet();
	}
	
	public Scoring getScoringSystem(String scoringSystem) {
		if (!scoringSystems.keySet().contains(scoringSystem)) {
			throw new IllegalArgumentException("Unknown scoring system " + scoringSystem + ". Possible scoring systems are: " + getAllScoringSystemNames());
		}
		return scoringSystems.get(scoringSystem);
	}
	
	/* ************************ */
	/*		 PRIVATE METHODS	 */
	/* ************************ */
	
	
	private void readAllScoringSystemsFromDisk() {
		String scoringFolderStr = ConfigListener.getConfiguration().getScoringFolder();
		
		final File scoringFolder = new File(scoringFolderStr);
			
		File[] scoringFiles = scoringFolder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		});
		
		for (File scoringFile: scoringFiles) {
			Scoring scoring = readOneDictionaryFileFromDisk(scoringFile);
			String language = FilenameUtils.removeExtension(scoringFile.getName());
			scoringSystems.put(language, scoring);
		}
	}
	
	private Scoring readOneDictionaryFileFromDisk(File scoringFile) {
		if (!scoringFile.exists() || !scoringFile.canRead()) {
			throw new RuntimeException("Scoring file " + scoringFile.getAbsolutePath() + " cannot be read.");
		}

		int timingId = 0;
		String timingGroup = "scoring";
		TimingSingleton timing = TimingSingleton.getInstance();
		timing.reset(timingGroup, timingId);
		timing.start(timingGroup, timingId);
		
		Scoring scoring = new Scoring();
		
		InputStream is;
		try {
			is = new FileInputStream(scoringFile);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				try {
					String line = "";
					while (line != null) {
						line = br.readLine();
						if (line != null) {
							ParseLine(scoringFile, line, scoring);
						}
					}
					
				} finally {
					br.close();
				}
				
			} finally {
				is.close();
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		timing.stop(timingGroup, timingId);
		log.info("Read " + scoringFile.getName() + " in " + timing.getTime(timingGroup, timingId)+ " msec | " + scoring.toString());
		
		return scoring;
	}
	
	private void ParseLine(File scoringFile, String line, Scoring scoring) {
		line = line.replaceAll("\\s+", "");
		line = line.toLowerCase();
		String[] parts = line.split("=");
		
		if (parts.length != 2) {
			throw new RuntimeException("Error: line has not exacly one =. " +
					" At line " + line + " of " + scoringFile.getAbsolutePath()); 
		
		} else {
			
			String key = parts[0];
			if (key.length() == 0) {
				throw new RuntimeException("Error: has no value before the =. " +
						" At line " + line + " of " + scoringFile.getAbsolutePath()); 
			}
			
			String value = parts[1];
			
			if (key.equals("bonus")) {
				scoring.setBonus(parseInteger(value, line, scoringFile));
				
			} else if (key.equals("traysize")) {
				scoring.setTraySize(parseInteger(value, line, scoringFile));

			} else if (key.equals("board")) {
				scoring.setBoard(parseBoard(value, line, scoringFile));
				
			} else  if (key.length() == 1) {
				
				char letter = key.charAt(0);
				if ((int)letter < (int)'a' || (int)letter > (int)'z') {
					throw new RuntimeException("Error: value before the = is a one letter word, but not a-z. " +
							"At line " + line + " of " + scoringFile.getAbsolutePath()); 
				}
				scoring.setPoints(letter, parseInteger(value, line, scoringFile));
				
			} else {
				throw new RuntimeException("Error: value before the = is neither 'board', 'bonus', 'traysize', or a one letter word." +
						" At line" + line + " of " + scoringFile.getAbsolutePath());
			}
		}
	}

	private int parseInteger(String value, String line, File scoringFile) {
		int number;
		try {
			number = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Error: has no number after the =." +
					" At line " + line + " of " + scoringFile.getAbsolutePath());
		}
		return number;
	}
	
	private Board parseBoard(String value, String line, File scoringFile) {
		
		String[] rows = value.split("\\|");
		Board board = new Board(rows.length);
		
		for (int row=0; row<rows.length; row++) {
			String[] cells = rows[row].split(",");
			
			if (cells.length != rows.length) {
				throw new RuntimeException("Error: row " + row + " is of different size (" + cells.length + ") than the columns (" + rows.length + ")." +
						" At line " + line + " of " + scoringFile.getAbsolutePath());
			}
			
			for (int col=0; col<cells.length; col++) {
				String cell = cells[col];
				Box box;
				if (cell.equals("_")) { box = new Box(BoxTypeEnum.EMPTY); }
				else if (cell.equals("s")) { box = new Box(BoxTypeEnum.STARTING_POSITION); }
				else if (cell.equals("dl")) { box = new Box(BoxTypeEnum.DOUBLE_LETTER); }
				else if (cell.equals("tl")) { box = new Box(BoxTypeEnum.TRIPLE_LETTER); }
				else if (cell.equals("dw")) { box = new Box(BoxTypeEnum.DOUBLE_WORD); }
				else if (cell.equals("tw")) { box = new Box(BoxTypeEnum.TRIPLE_WORD); }
				else {
					throw new RuntimeException("Error: unknown character at position (" + row + "," + col + "): " + cell +
							" At line " + line + " of " + scoringFile.getAbsolutePath()); 
				}
				board.setBox(row, col, box);
			}
		}
		return board;
	}
	
	private String getAllScoringSystemNames() {
		StringBuilder result = new StringBuilder();
		for (String key: scoringSystems.keySet()) {
			result
				.append(result.length() == 0? "": ", ")
				.append(key);
		}
		return result.toString();
	}
}