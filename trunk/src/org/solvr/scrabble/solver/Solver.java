package org.solvr.scrabble.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.solvr.scrabble.datastructures.Board;
import org.solvr.scrabble.datastructures.Word;
import org.solvr.scrabble.dict.DictionaryNode;
import org.solvr.scrabble.dict.DictionarySingleton;
import org.solvr.scrabble.scoring.Scoring;
import org.solvr.scrabble.scoring.ScoringSingleton;
import org.solvr.scrabble.util.ParamValidationUtil;


/**
 * This class provides the main entry to the functionality needed to solve a scrabble puzzle.
 * 
 * @author Koen Peters, Cubix Concepts
 */
final public class Solver {
	
	Logger log = Logger.getLogger(Solver.class);
	
	/**
	 * Solves the given scrabble puzzle 
	 * 
	 * @param board				The board that contains the values of the boxes and the already existing words
	 * @param tray				The letters that need to be put on the board. Use Word.JOKER for jokers.
	 * @param dictionaryName	The name of the dictionary to use. This is the name of one of the dictionary files 
	 * 							(without the extension) that are in the dictionary folder defined in the configuration.
	 * @param scoringSystemName	The name of the scoring system to use. This is the name of one of the scoring files 
	 * 							(without the extension) that are in the scoring folder defined in the configuration.
	 * @return	A list of all possible solutions to the given scrabble puzzle (the combination of the board, the letters
	 * 			and the dictionary), sorted from best solution to worst.
	 */
	public List<Word> solve(Board board, String tray, String dictionaryName, String scoringSystemName) {
		ParamValidationUtil.validateParamNotNull(board, "board");
		ParamValidationUtil.validateParamNotNull(tray, "letters");
		ParamValidationUtil.validateParamNotNull(dictionaryName, "dictionaryName");
		ParamValidationUtil.validateParamNotNull(scoringSystemName, "scoringSystemName");
		
		// Generate all unsorted possible solutions using the given dictionary.
		DictionaryNode wholeDictionary = DictionarySingleton.getInstance().getDictionary(dictionaryName);
		PermutationCreator permutationCreator = new PermutationCreator(wholeDictionary);
		Set<Word> possibilities = permutationCreator.listAll(board, tray);

		// Sort the possibilities in order of score using the given scoring system.
		Scoring scoringSystem = ScoringSingleton.getInstance().getScoringSystem(scoringSystemName);
		List<Word> possibilitiesAsList = new ArrayList<Word>(possibilities);
		Collections.sort(possibilitiesAsList, new HighestValueComparator(scoringSystem));
		
		return possibilitiesAsList;
	}
}