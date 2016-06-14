package nl.cubix.scrabble.solver.datastructures;

import java.util.List;

import nl.cubix.scrabble.solver.scoring.Scoring;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * This class models a word that is placed on a scrabble board.
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class Word {
	
	// The letters that are not already on the board in the order of which they must be placed on the
	// board.
	private String letters;
	
	// The letters that need to be used as a replacement for any jokers in 'letters'. The order of 
	// the jokers in 'letters' is the same as the order of the replacements in 'jokerMask'
	private String jokerReplacements;
	
	// The board the letters in 'letters' will be placed on. Board may not change after the construction
	// of this Word object. 
	private Board board;
	
	// The coordinates of the placement of the first letter in 'letters' on the board. Zero based.
	private Coordinate startCoordinates;
	
	// The direction of which the letters must be placed.
	private DirectionEnum direction;
	
	// Derived field. The letters in 'letters' with the jokers replaced bu their corresponding letters in 
	// 'jokerMask'
	private String lettersThroughMask;
	
	// Derived field. The word that results from placing the 'lettersThroughMask' on the board in the 
	// given direction starting at the given startCoordinates, taken the already existing letters on the 
	// board into account.
	private String primaryWord;
	
	// Derived field. All words beside the 'primaryWord' that are created after placement of the letters.
	// These are the words that are created in the other direction.
	private List<Word> secondaryWords;
	
	/** 
	 * The character that represents a joker 
	 */
	public static final char JOKER = ' ';
	
	/**
	 * The different directions that a word can be placed on on the board.
	 * 
	 * @author Koen Peters, Cubix Concepts
	 */
	public enum DirectionEnum {
		HORIZONTAL
		,VERTICAL;
		
		/**
		 * @return	The direction orthogonal to this one.
		 */
		public DirectionEnum inverse() {
			return this == HORIZONTAL? VERTICAL: HORIZONTAL;
		}
	}

	/**
	 * Constructor
	 * 
	 * @param letters	A list of letters that need to be placed on the board in this order. Use Word.JOKER
	 * 					for jokers.
	 * @param jokerReplacements	A list of characters that need to be used as substitutions for the jokers in 'letters'
	 * 					in this order. If there are no jokers in 'letters', this must be null
	 * @param board		The board to use. The board may not be changed hereafter
	 * @param startCoordinates	The coordinates of the placement of the first letter in 'letters' on the board. Zero based.
	 * @param direction	The direction the word must be placed on the board
	 */
	public Word(String letters, String jokerReplacements, Board board, Coordinate startCoordinates, DirectionEnum direction) {
		super();
		this.letters = letters;
		this.jokerReplacements = jokerReplacements;
		this.board = board;
		this.startCoordinates = startCoordinates;
		this.direction = direction;
	}

	/**
	 * Copy constructor that adds an extra letter to the letters in the given word  
	 * 
	 * @param word				The Word object to copy.
	 * @param extraLetter		The extra letter that needs to be added to the existing letters. Use Word.JOKER
	 * 							for jokers.
	 * @param extraJokerReplacements	The character that need to be used as substitutions in the case 'extraLetter' is a
	 * 							joker. If 'extraLetter' is not a joker this will be ignored. 
	 */
	public Word(Word word, char extraLetter, char extraJokerReplacements) {
		super();
		this.board = word.board;
		this.startCoordinates = word.startCoordinates;
		this.direction = word.direction;
		this.letters = word.letters + extraLetter;
		if (extraLetter == Word.JOKER) {
			this.jokerReplacements = word.jokerReplacements == null? Character.toString(extraJokerReplacements): (word.jokerReplacements + extraJokerReplacements);
		} else {
			this.jokerReplacements = word.jokerReplacements;
		}
	}

	public String getLetters() {
		return letters;
	}

	public Board getBoard() {
		return board;
	}
	
	public Coordinate getStartingPosition() {
		return startCoordinates;
	}
	
	public DirectionEnum getDirection() {
		return direction;
	}
	
	/**
	 * @return	The letter with the jokers replaced by their placeholders
	 */
	public String getLettersThroughMask() {
		if (lettersThroughMask == null) {
			applyMask();
		}
		return lettersThroughMask;
	}
	
	/**
	 * @see Board#isConnectedToExistingStructure  
	 */
	public boolean isConnectedToExistingStructure() {
		return board.isConnectedToExistingStructure(this);
	}
	
	/**
	 * @see Board#bordersAtEnd  
	 */
	public boolean bordersAtEnd() {
		return board.bordersAtEnd(this);
	}
	
	/**
	 * @see Board#getScore  
	 */
	public int getScore(Scoring scoring) {
		return board.getScore(this, scoring);
	}
	
	/**
	 * @see Board#getPrimaryWord  
	 */
	public String getPrimaryWord() {
		if (primaryWord == null) {
			primaryWord = board.getPrimaryWord(this);
		}
		return primaryWord;
	}
	
	/**
	 * @see Board#getSecundaryWords  
	 */
	public List<Word> getSecundaryWords(int partOfWordToIgnore) {
		if (secondaryWords == null) {
			secondaryWords = board.getSecundaryWords(this, partOfWordToIgnore);
		}
		return secondaryWords;
	}
	
	@Override
	/**
	 * @see Board#toString  
	 */
	public String toString() {
		return board.toString(this, false);
	}
	
	
	@Override
	/**
	 * This equals method compares two Word objects by the result of their toString() method. 
	 * This means that even though two object could have a different board or different starting 
	 * positions on the board this method could return true, because the end result is the same. 
	 * This is done this way, because 
	 * <ul>
	 * 	<li>It is faster than testing for the equality of all non derived private fields.</li>
	 * 	<li>In practice only words with the same board will be tested for equality. It does not make
	 * 		sense to test two word for equality if the boards themselves are different.</li>
	 * 	<li>The equals method was added to test for equality when placing words in a Set. If two 
	 * 		words give the same end result even though they differ in their private fields we still
	 * 		only want one of the two to show up in the results.</li>
	 * </ul> 
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Word == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		Word word = (Word) obj;
		return  new EqualsBuilder()
	                 .append(toString(), word.toString())
	                 .isEquals();
	}
	
	@Override
	public int hashCode() {
	     return new HashCodeBuilder(23, 43)
	       .append(toString())
	       .toHashCode();
	}

	/* ************************ */
	/*       PRIVATE METHODS    */
	/* ************************ */

	/**
	 * Fills this.lettersThroughMask with all jokers replaced by the substitutions in jokerMask,
	 */
	private void applyMask() {
		StringBuilder result = new StringBuilder(letters.length());
		int jokerReplacementsPointer = 0;
		
		for (int i = 0; i < letters.length(); i++) {
			char letter = letters.charAt(i);

			if (letter == JOKER) {
				if (jokerReplacements == null || jokerReplacementsPointer > jokerReplacements.length() - 1) {
					throw new RuntimeException("Mask and letters do not match. At position " + i + " there exists a " +
							"joker without a mask entry. Letters: >" + letters + "<, mask: >" + jokerReplacements + "<.");
				}
				result.append(jokerReplacements.charAt(jokerReplacementsPointer));
				jokerReplacementsPointer++;
			} else {
				result.append(letter);
			}
		}
		
		lettersThroughMask = result.toString();
	}
}