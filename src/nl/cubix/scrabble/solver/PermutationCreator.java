package nl.cubix.scrabble.solver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.cubix.scrabble.solver.datastructures.Board;
import nl.cubix.scrabble.solver.datastructures.Coordinate;
import nl.cubix.scrabble.solver.datastructures.Word;
import nl.cubix.scrabble.solver.datastructures.Word.DirectionEnum;
import nl.cubix.scrabble.solver.dict.DictionaryNode;
import nl.cubix.scrabble.solver.dict.DictionarySingleton;
import nl.cubix.scrabble.util.ParamValidationUtil;

import org.apache.log4j.Logger;


/**
 * This class provides functionality for looking for all possible solutions to a given scrabble puzzle
 * 
 * @author Koen Peters, Cubix Concepts
 */
final class PermutationCreator {

	private DictionarySingleton dictionary = DictionarySingleton.getInstance();
	private DictionaryNode wholeDictionary;
		
	// A list of all letters in the alphabet used when testing all possible substitutions for a joker
	private static final char[] A_TO_Z = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k'
			, 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	
	private Logger log = Logger.getLogger(PermutationCreator.class);

	public PermutationCreator(DictionaryNode wholeDictionary) {
		ParamValidationUtil.validateParamNotNull(wholeDictionary, "wholeDictionary");
		this.wholeDictionary = wholeDictionary;
	}
	
	/**
	 * Creates a set of all possible solutions to the given scrabble puzzle.
	 * 
	 * @param board				A valid scrabble board that contains the values of the boxes and the already existing words
	 * @param tray				The letters that need to be put on the board. Use Word.JOKER for jokers.
	 * @param wholeDictionary	The starting node of a dictionary. This method will return all possible solutions based on 
	 * 							this dictionary.
	 * @return	A list of all possible solutions to the given scrabble puzzle (the combination of the board, the letters
	 * 			and the dictionary)
	 */
	public Set<Word> listAll(Board board, String tray) {
		ParamValidationUtil.validateParamNotNull(board, "board");
		ParamValidationUtil.validateParamNotNull(tray, "tray");
		
		Set<Word> result= new HashSet<Word>();

		// Case does not matter within scrabble, so we do all testing in lower case. 
		String trayLowerCase = tray.toLowerCase();
		
		// We loop over every box on the board. We try all possible permutations for this box of the board 
		// of every possible length and for both the vertical and the horizontal orientation.
		for (int row=0; row < board.getDimension(); row++) {
			for (int col=0; col < board.getDimension(); col++) {
				Coordinate startCoordinates = new Coordinate(row, col);
				
				// We do this for both vertical and horizontal placement of the letters in the tray
				for (DirectionEnum orientation: DirectionEnum.values()) {

					// We do not have to consider this starting coordinate if we know that none of the possible permutations
					// we be connected to the the existing structure of the board.
					if (board.isPossibleFit(startCoordinates, orientation, trayLowerCase)) {
						
						Word startingWord = new Word("", null, board, startCoordinates, orientation);
						permutate(startingWord, trayLowerCase, wholeDictionary, result);
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Recursive method that fills 'possibilities' with all the possible solutions to the scrabble puzzle using
	 * the tray of letters in unusedLetters and wordInLastRecursion as a prefix to those possible words.
	 * 
	 * @param wordInLastRecursion	The Word object that was created in the previous recursion. 
	 * @param unusedLetters			All letters on the tray that have been not been used in previous recursive calls. 
	 * 								Use only lower case. Use Word.JOKER for jokers.
	 * @param oldNode				The position in the dictionary that would result from calling 
	 * 								dictionary.getDictionarySubsetOfPrefix(wordInLastRecursion.getPrimaryWord()). This
	 * 								indicated the position in the dictionary from where we can start checking for 
	 * 								the existence of the word that we will create in this recursion.
	 * @param possibilities			The set containing all the possible solutions to this scrabble puzzle found so far.
	 */
	private void permutate(Word wordInLastRecursion, String unusedLetters, DictionaryNode oldNode, Set<Word> possibilities) {

		for (int i = 0; i < unusedLetters.length(); i++) {

			char letterFromUnusedLetters = unusedLetters.charAt(i);
			char[] lettersToTestInThisRecursion = (letterFromUnusedLetters == Word.JOKER? A_TO_Z: new char[] {letterFromUnusedLetters});
			
			for (char letterToTest: lettersToTestInThisRecursion) {
			
				// We construct a new Word Object that is the same as the word in the last recursion, but
				// with a letter added to it: letterFromUnusedLetters. If the letter is a joker we also 
				// provide the substitution for the joker in this test: letterToTest
				Word newWord = new Word(wordInLastRecursion, letterFromUnusedLetters, letterToTest); 

				// We determine the part of the new word that has not been checked against the dictionary
				String uncheckedPart;
				if (wordInLastRecursion.getLetters().length() == 0) { 
					// This is the first recursive call, so wordInLastRecursion has not been checked. We need to
					// check the whole word.
					uncheckedPart = newWord.getPrimaryWord(); 
				} else {
					// If this is the second or later recursive call then the word in wordInLastRecursion has been
					// checked in the previous recursion , so we only need to check the unchecked (new) part
					uncheckedPart = newWord.getPrimaryWord().substring(wordInLastRecursion.getPrimaryWord().length());
				}
				
				
				// All the letters in the primary word of 'wordInLastRecursion' have been checked against the dictionary
				// in the previous iteration, resulting in the 'oldNode' starting position in the dictionary. Now we check 
				// the part of the word that we added in this iteration against the dictionary starting at the dictionary 
				// position where we left off the last time. 
				DictionaryNode newNode = dictionary.getDictionarySubsetOfPrefix(uncheckedPart, oldNode);
				
				// We must only continue if the check went well meaning that the word exists or that is is 
				// a prefix of some longer words.
				if (newNode != null) {
					
					// As we increase the number of letters we also create possible new words on the other axis.
					// We must check if these words exist. If they don't we can stop this recursion as it is not a 
					// possible solution to the scrabble puzzle. We already tested the secondary words of
					// wordInLastRecursion, so we only need to test the ones created by the addition of the new letter.
					// We do this by providing the position where we left of.
					List<Word> secondaryWords = newWord.getSecundaryWords(wordInLastRecursion.getPrimaryWord().length());
					boolean allSecondaryWordsExist = true;
					for (Word secundaryWord: secondaryWords) {
						if (!dictionary.exists(secundaryWord.getPrimaryWord(), wholeDictionary)) {
							allSecondaryWordsExist = false;
							break; 
						}
					}
					
					if (allSecondaryWordsExist) {
						// It could be that the word we have created in this iteration is a valid word itself even
						// without the other letters in 'newUnusedLetters'. If so and if the word is actually connected to
						// the existing structure, we add it to our set of results.
						if (newNode.isWord() && newWord.isConnectedToExistingStructure()) {
							possibilities.add(newWord); 
						}
						
						// We used up one of the letter in 'unusedLetters', so we remove that from the string of unused letters.
						String newUnusedLetters = unusedLetters.substring(0,i) + unusedLetters.substring(i + 1);
						
						// We only continue the recursion if there are letters left that we did not use yet, and 
						// if there is room left on the board. Otherwise the recursion bottoms out.
						if (!newUnusedLetters.isEmpty() && !newWord.bordersAtEnd()) {
							permutate(newWord, newUnusedLetters, newNode, possibilities);
						}
					}
				}
			}
		}
	}
}
