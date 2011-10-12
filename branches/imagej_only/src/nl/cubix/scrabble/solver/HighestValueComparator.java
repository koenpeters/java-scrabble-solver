package nl.cubix.scrabble.solver;

import java.util.Comparator;

import nl.cubix.scrabble.solver.datastructures.Word;
import nl.cubix.scrabble.solver.scoring.Scoring;


/**
 * Comparator class that compares two given Word objects by their score on the board
 * 
 * @author Koen Peters, Cubix Concepts
 */
final class HighestValueComparator implements Comparator<Word> {

	// The scoring system that the comparing will use
	private Scoring scoringSystem;
	
	public HighestValueComparator(Scoring scoringSystem) {
		this.scoringSystem = scoringSystem;
	}
	
	@Override
	public int compare(Word word1, Word word2) {
		if (word1==word2) {
			return 0;
		}
		if (word1 == null) {
			return -1;
		}
		if (word2 == null) {
			return 1;
		}
		Integer scoreO1 = word1.getScore(scoringSystem);
		Integer scoreO2 = word2.getScore(scoringSystem);
		return scoreO2.compareTo(scoreO1);
	}
	
}
