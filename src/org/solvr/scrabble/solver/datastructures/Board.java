package org.solvr.scrabble.solver.datastructures;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.solvr.scrabble.solver.datastructures.Word.DirectionEnum;
import org.solvr.scrabble.solver.scoring.Scoring;
import org.solvr.scrabble.solver.util.CharUtil;

/**
 * This class models a scrabble board
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class Board {
	
	// The representation of the scrabble board as a matrix of boxes
	private Box[][] matrix;
	
	// equal to 'matrix' but with its rows and columns swapped
	private Box[][] transposedMatrix;
	
	// The number of rows and columns of the scrabble board 
	private int dimension;
	
	// Derived field. True if there are no boxes on the board that have existing letters in them.
	private boolean isEmpty;
	
	// Representation of letters that are placeholders for jokers. a = 1, b = 2, etc. Used only in the toString method
	private char[] blankPlaceholders = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '!', '@', '#', '$', '%', '^', '&', '(', ')', '_', '-', '=', '+', '{', '}', '[' };  

	private Logger log = Logger.getLogger(Board.class); 
	
	
	/**
	 * Constructor
	 * 
	 * @param dimension	The number of rows and columns the board will have.
	 */
	public Board(int dimension) {
		this.matrix = new Box[dimension][dimension];
		this.transposedMatrix = new Box[dimension][dimension];
		this.isEmpty = true;
		this.dimension = dimension;
	}
	
	/**
	 * Overlays the current board with a bunch of letters encoded in the given string. 
	 * 
	 * @param letters	A string representation of a matrix with the same dimensions as this board, 
	 * 					with each row separated by a newline (\n). Each cell in the row corresponds 
	 * 					with the box in the matrix. Three types of characters can occur.
	 *					<ul>
	 *						<li>A-Z: Add a letter that has the normal value</li>
	 *						<li>a-z: Add a letter that has no value (a blank)</li>
	 *						<li>.: Add nothing (skip this one)</li>
	 *					</ul> 
	 */
	public void addLetters(String letters) {
		
		String[] rows = letters.split("\n");
		
		if (rows.length != dimension) {
			throw new RuntimeException("Number of rows in 'letters' not of the same dimension " +
					"as board: " + rows.length + " vs " + dimension );
		}
		
		for (int row = 0; row < rows.length; row++) {
			String rowStr = rows[row];

			if (rowStr.length() != dimension) {
				throw new RuntimeException("Number of colls in row " + row + " in 'letters' not" +
						" of the same dimension as board: " + rowStr.length() + " vs " + dimension );
			}

			for (int col = 0; col < rows.length; col++) {
				char cell = rowStr.charAt(col);
				
				if (cell != '.') {
					boolean isBlank = cell >= 'a' && cell <= 'z';
					
					// We override the existing box. So the old information is lost
					Box box = new Box(Character.toLowerCase(cell), isBlank);
					setBox(row, col, box);
					
					// The board is no longer empty. 
					isEmpty = false;
				}
			}
		}
	}
	
	/**
	 * Sets the values of the different boxes of this board. No existing letters on the board will be deleted.
	 * 
	 * @param row	The row of the board. Zero base
	 * @param col	The column of the board. Zero base
	 * @param box	The box that needs to be placed on the board at the specified coordinates
	 */
	public void setBox(int row, int col, Box box) {
		Box currentBox = matrix[row][col];
		if (currentBox == null || currentBox.isEmpty()) {
			matrix[row][col] = box;
			transposedMatrix[col][row] = box;
		} else {
			log.info("Box " + box + " not set at position " + row + ", " + col + " because the exsting box contains a letter.");
		}
	}
	
	public int getDimension() {
		return dimension;
	}
	
	public Box getBox(int row, int col) {
		return matrix[row][col];
	}
	
	/**
	 * @return	True if there are no existing letters on the board. False otherwise
	 */
	public boolean isEmpty() {
		return isEmpty;
	}
	
	/**
	 * Determines if the preconditions are met for placing the letters on the board. If these are not met
	 * no permutation of the given letter would ever yield a possible solution to the given scrabble puzzle.
	 * The preconditions are:
	 * <ul>
	 * 	<li>The box at the starting position does not already contain a letter. If so the result would be
	 * 		the same as starting one position over.</li>
	 * 	<li>When all letters are placed on the board there must at least be one connection to the existing
	 * 		structure (existing letters or if the board is empty: placed over the board's starting position)</li>
	 * </ul>
	 * 
	 * @param coordinate	Coordinate on the board
	 * @param direction		The direction the letters will be placed
	 * @param letters		All letters. The actual characters do not matter. Only the number of letters are
	 * 						important in this test.
	 * @return	True is the preconditions are met, false otherwise
	 */
	public boolean isPossibleFit(Coordinate coordinate, Word.DirectionEnum direction, String letters) {
		if (!getBox(coordinate.getRow(), coordinate.getCol()).isEmpty()) {
			// The box at the startcoordinate is empty. It is not a possible startingpoint for a new word
			return false;
		} else {
			// We check if when we lay out all the letters there will be a connection to the existing letters
			// if there does not exists such a connection we know it can never be a word.
			Transposition tr = new Transposition(coordinate, direction);
			return isConnectedToExistingStructure(tr, letters);
		}
	}
	
	/**
	 * @return	A string representation of this board. Only the letters will be visible, not the special meaning
	 * 			of the boxes.
	 */
	@Override
	public String toString() {
		return toString(new Word("", null, this, new Coordinate(0, 0), DirectionEnum.VERTICAL));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Board == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		
		Board word = (Board) obj;
		return new EqualsBuilder()
					.append(matrix, word.matrix)
					.isEquals();
	}
	
	@Override
	public int hashCode() {
	     return new HashCodeBuilder(47, 5).
	       append(matrix).
	       toHashCode();
	}
	
	@Override
	public Board clone() throws CloneNotSupportedException {
		Board result = new Board(dimension);
		result.isEmpty = isEmpty;
		for (int row=0; row < dimension; row++) {
			for (int col=0; col < dimension; col++) {
				Box clonedBox = matrix[row][col].clone();
				result.setBox(row, col, clonedBox);
			}
		}
		return result;
	}

	
	/* ************************* */
	/*  PACKAGE PRIVATE METHODS  */
	/* ************************* */
	
	/* These methods are used by the WOrd class to do the calculations. The reason they are here 
	 * instead of in the Word class itself is because the word class should not know the inner
	 * workings of the Board class and we need to do some board matrix manipulation to get this
	 * as fast as possible. Manipulating the matrix is something that the Word class should not
	 * know hpw to do.
	 */
	
	/**
	 * Creates the word that is spelled out if the letters are placed on the board starting at the provided
	 * starting position in the provided direction using the provided replacement for the jokers. 
	 * Letters that touch the placed letters left, right or in the middle are taken into account and
	 * will result in a longer word. 
	 * 
	 * @param word	The word that must be placed on this board.  
	 * @return	The word resulting from placing the letters on this board
	 */
	String getPrimaryWord(Word word) {
		StringBuilder result = new StringBuilder();
		
		Transposition tr = new Transposition(word);

		int charArrayPointer = 0;
		for (int boardPointer = 0; boardPointer < dimension; boardPointer++) {
			Box box = tr.matrixToUse[tr.startRow][boardPointer];
			
			if (!box.isEmpty()) {
				result.append(box.getCharacter());
			
			} else {
				// Now we know the box is empty
				
				if (boardPointer < tr.startCol) {
					// We are before the actual beginning of the word
					result.setLength(0);
					
				} else if (charArrayPointer < word.getLettersThroughMask().length()) {
					// We are in the middle of our word, 
					result.append(word.getLettersThroughMask().charAt(charArrayPointer));
					charArrayPointer++;
					
				} else {
					// We used up all of our letters. The word cannot get any longer, 
					// so we just break the for statement
					break; 
				}	
			}
		}
		return result.toString();
	}

	/**
	 * Creates a list of all new words except the primary word that are created when the letters in the given
	 * word are placed on the board. These are all the newly created words that lie orthogonal to the primary 
	 * word. We only return the secondary words that are connected to the letters of the primary word from the 
	 * letter at position 'partOfWordToIgnore' and further. 
	 * 
	 * @param word	The word that must be placed on this board. 
	 * @param partOfWordToIgnore The number of starting letters of the primary word to ignore when looking for
	 * 				the newly created secondary words. 
	 * @return	The list of secondary words starting from the 'partOfWordToIgnore'nd letter of the primary word.
	 */
	List<Word> getSecundaryWords(Word word, int partOfWordToIgnore) {
		List<Word> result = new ArrayList<Word>();

		Transposition tr = new Transposition(word);
		
		int charArrayPointer = 0;
		int primaryWordLength = 0;
		
		for (int boardPointer = 0; boardPointer < dimension; boardPointer++) {
			Box box = tr.matrixToUse[tr.startRow][boardPointer];
			
			if (!box.isEmpty()) {
				primaryWordLength++;
			
			} else {
				// Now we know the box is empty, so we can add a letter from our tray
				
				if (boardPointer < tr.startCol) {
					// We are before the actual beginning of the word and we encountered 
					// an empty box. We reset the beginning of the word as we are disconnected
					primaryWordLength = 0;
					
				} else if (charArrayPointer < word.getLetters().length()) {
					// We are in the middle of our word
					primaryWordLength++;
					
					// We only need to return the secondary words that are after the part of the 
					// primary word that we can ignore according to the given parameter.
					if (primaryWordLength > partOfWordToIgnore) {
						
						// A secondary word is only a word if it has at least two characters. So we check if
						// the cell above or below contains a letter.
						if ((tr.startRow == 0? false: !tr.matrixToUse[tr.startRow - 1][boardPointer].isEmpty())
							|| (tr.startRow == dimension - 1? false: !tr.matrixToUse[tr.startRow + 1][boardPointer].isEmpty())) {
							
							char letter = word.getLetters().charAt(charArrayPointer);
							String mask = letter != Word.JOKER? null: Character.toString(word.getLettersThroughMask().charAt(charArrayPointer));
							
							Coordinate coordinate = new Coordinate(
									word.getDirection()== DirectionEnum.HORIZONTAL? tr.startRow: boardPointer
									,word.getDirection()== DirectionEnum.HORIZONTAL? boardPointer: tr.startRow);
							DirectionEnum direction = word.getDirection().inverse();
							result.add(new Word(Character.toString(letter), mask, this, coordinate, direction));
						}
					}
					
					charArrayPointer++;
					
				} else {
					// We used up all of our letters and need to check for letters that were on the
					// board to start with that touch our laid out letters on the right side
					break; // the word cannot get any longer, so we just break the for statement
				}	
			}
		}
		
		return result;
	}
	
	/**
	 * Determines if when all letters are placed on the board there is at least one connection to the
	 * existing structure (existing letters or if the board is empty: placed over the board's starting 
	 * position)
	 * 
	 * @param word 	The word that we must use to test for connectivity
	 * @return True the word is connected. False otherwise.
	 */
	boolean isConnectedToExistingStructure(Word word) {
		return isConnectedToExistingStructure(new Transposition(word), word.getLetters());
	}
	
	/**
	 * This method determines if there is no space left between the edge of the board and the last letter
	 * of the given word if placed on the board. Depending on the direction of the word, the right edge 
	 * (horizontal) or the bottom edge (vertical) is used.
	 * 
	 * @return True if the words touches the edge, false otherwise.
	 */
	boolean bordersAtEnd(Word word) {

		Transposition tr = new Transposition(word);
	
		int charArrayPointer = 0;
		
		for (int boardPointer=tr.startCol; boardPointer < dimension; boardPointer++) {
			Box box = tr.matrixToUse[tr.startRow][boardPointer];
			
			if (box.isEmpty()) {
				if (charArrayPointer < word.getLetters().length()) {
					charArrayPointer++;
				} else {
					return false;
				}
			} 
		}
		return true;
	}

	/**
	 * Calculates the total score of this word using the given scoring system.
	 * 
	 * @param word	The word we need to calculate the score for.
	 * @param scoringSystem	The scoring system to be used.
	 * @return	The total score when the given word is is placed on the board (both primary and 
	 * 			secondary words) using the given scoring system.
	 */
	int getScore(Word word, Scoring scoringSystem) {

		List<Word> allWords = new ArrayList<Word>();
		allWords.add(word);
		allWords.addAll(getSecundaryWords(word, 0));

		int result = 0;
		for (Word w: allWords) {
			result += getScoreOfPrimaryWord(w.getLetters(), scoringSystem, new Transposition(w));
		}
		return result;
	}
	
	/**
	 * @param word	The word that needs to be placed on the board
	 * @return	A string representation of given word placed on this board. Only the letters will be visible,
	 * 			not the empty box's special meaning.
	 */
	String toString(Word word) {
		
		Transposition tr = new Transposition(word);

		char[][] tempMatrix = new char[dimension][dimension];
		
		int charArrayPointer = 0;
		
		for (int row=0; row < dimension; row++) {
			
			for (int col= 0; col< dimension; col++) {
				Box box = tr.matrixToUse[row][col];
				
				if (!box.isEmpty()) {
					tempMatrix[row][col] = Character.toUpperCase(box.getCharacter());
				
				} else {
					// Now we know the box is empty
					
					if (row == tr.startRow && col >= tr.startCol && charArrayPointer < word.getLetters().length()) {
						// We are in the middle of our word, we need to either use the letter in the
						// box if one exists or use the first letter of the remaining available letters.
					
						char letter = word.getLettersThroughMask().charAt(charArrayPointer);
						if (word.getLetters().charAt(charArrayPointer) == Word.JOKER) {
							tempMatrix[row][col] = blankPlaceholders[CharUtil.toOridinal(letter)];
						} else {
							tempMatrix[row][col] = letter;
						}
						charArrayPointer++;
						
					} else {
						// We used up all of our letters and need to check for letters that were on the
						// board to start with that touch our laid out letters on the right side
						tempMatrix[row][col] = '.';
					}	
				}
			}
		}
		
		// Depending on which direction the primary word is written we used the normal matrix or the transposed one
		// resulting in a tempMatrix that is normal or transposed as well. For the toString we need normal version
		// so we transpose the tempMatrix back if needed. This is also the only reason we used the tempMatrix: so we
		// can transpose the result before returning it. If we'd written it directly to a StringBuiler we would not
		// be able to transpose it now.
		StringBuilder result = new StringBuilder("\n");
		
		for (int row=0; row < dimension; row++) {
			for (int col=0; col < dimension; col++) {
				if (word.getDirection() == DirectionEnum.HORIZONTAL) {
					result.append(tempMatrix[row][col]);
				} else {
					result.append(tempMatrix[col][row]);
				}
			}
			result.append("\n");
		}
		
		return result.toString();
	}
	
	
	/* ************************ */
	/*       PRIVATE METHODS    */
	/* ************************ */
	
	/**
	 * Calculates the score of the given letters on this board using the given scoring system
	 * 
	 * @param letters		The letters to be placed on the board, possibly containing jokers
	 * @param scoringSystem	The scoring system to be used.
	 * @param tr			The transposition of the word.
	 */
	private int getScoreOfPrimaryWord(String letters, Scoring scoringSystem, Transposition tr) {
		int score = 0;
		int wordMultiplier = 1;
		int charArrayPointer = 0;
		
		for (int boardPointer = 0; boardPointer < dimension; boardPointer++) {
			Box box = tr.matrixToUse[tr.startRow][boardPointer];

			if (!box.isEmpty()) {
				if (!box.isJoker()) {
					score += scoringSystem.getPoints(box.getCharacter());
				}
			
			} else {
				if (boardPointer < tr.startCol) {
					// We are before the actual beginning of the word
					score = 0;
					wordMultiplier = 1;
					
				} else if (charArrayPointer < letters.length()) {
					// We are in the middle of our word

					if (letters.charAt(charArrayPointer) == Word.JOKER) {
						// The letter is a joker which will not add to the score. If this letter is on a 
						// double or triple word value box that will be taken into account.  
						switch (box.getBoxType()) {
						case DOUBLE_WORD: wordMultiplier *= 2; break;
						case TRIPLE_WORD: wordMultiplier *= 3; break; 
						}
						
					} else {
						
						int letterMultiplier = 1;
						
						switch (box.getBoxType()) {
						case DOUBLE_LETTER: letterMultiplier = 2; break;
						case TRIPLE_LETTER: letterMultiplier = 3; break;
						case DOUBLE_WORD: wordMultiplier *= 2; break;
						case TRIPLE_WORD: wordMultiplier *= 3; break; 
						case STARTING_POSITION: break;
						case EMPTY: break;
						default: throw new RuntimeException("Unknown boxtype: " + box.getBoxType()); 
						}
						
						score += scoringSystem.getPoints(letters.charAt(charArrayPointer)) * letterMultiplier;
					}
					charArrayPointer++;
					
				} else {
					// We used up all of our letters and encountered an empty box after the word. There will
					// be no additional points from now on. We can quit.
					break;
				}
			}
		}
		
		// If we spend all letters on the tray we get an extra bonus
		int bonus = letters.length() == scoringSystem.getTraySize()? scoringSystem.getBonus(): 0;
		
		return score * wordMultiplier + bonus;
	}
	
	/**
	 * Determines of the letters are connected to the existing structure. 
	 * Also see the other isConnectedToExistingStructure method.
	 * 
	 * @param scoringSystem	The scoring system to be used.
	 * @param letters		The letters to be placed on the board, possibly containing jokers
	 * @return	True if the structure is connected, false otherwise
	 */
	private boolean isConnectedToExistingStructure(Transposition tr, String letters) {
		
		if (isEmpty) {
			int i=0;
			while (tr.startCol + i < dimension && i < letters.length() ) {
				// Check if the cell itself is the starting position
				if (tr.matrixToUse[tr.startRow][tr.startCol + i].isStartingPosition()) {
					return true;
				}
				i++;
			}
		} else {
			// If the word does not touch the left end of the board then check if 
			// the box left of the word is empty
			if (tr.startCol > 0 && !tr.matrixToUse[tr.startRow][tr.startCol - 1].isEmpty()) {
				return true;
			}
			
			// If the word does not touch the right side of the board then check if
			// the box right of the word is empty
			if ((tr.startCol + letters.length()) < dimension && !tr.matrixToUse[tr.startRow][tr.startCol + letters.length()].isEmpty()) {
				return true;
			}
	
			int i=0;
			while (tr.startCol + i < dimension && i < letters.length() ) {
				// Check if either the cell itself or the ones above or below it is taken
				if (tr.matrixToUse[tr.startRow][tr.startCol + i].isTaken()
						|| (tr.startRow > 0 && tr.matrixToUse[tr.startRow - 1][tr.startCol + i].isTaken())
						|| (tr.startRow < dimension - 1 && tr.matrixToUse[tr.startRow + 1][tr.startCol + i].isTaken())) {
					return true;
				}
				i++;
			}
		}
		
		return false;
	}
	
	/* ************************ */
	/*        INNER CLASSES     */
	/* ************************ */
	

	/** Depending on the direction of the word we need to either go horizontal or vertical while doing our
	 * 	calculations. To make it easier for ourselves we use the normal of the transposed matrix, so we can 
	 * 	treat both cases as the horizontal case. This class calculates which one to use dependng on the given 
	 *  word.
	 */ 
	private class Transposition {
		Box[][] matrixToUse;
		int startRow; 
		int startCol;
		
		public Transposition(Word word) {
			this(word.getStartingPosition(), word.getDirection());
		}
		
		public Transposition(Coordinate coordinate, DirectionEnum direction) {
			matrixToUse = direction == DirectionEnum.HORIZONTAL? matrix: transposedMatrix;
			startRow = direction == DirectionEnum.HORIZONTAL? coordinate.getRow(): coordinate.getCol(); 
			startCol = direction == DirectionEnum.HORIZONTAL? coordinate.getCol(): coordinate.getRow(); 
		}
	}
	
}
