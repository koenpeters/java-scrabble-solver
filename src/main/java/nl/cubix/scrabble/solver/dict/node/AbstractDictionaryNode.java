package nl.cubix.scrabble.solver.dict.node;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import nl.cubix.scrabble.util.ParamValidationUtil;

/**
 * A skeleton implementation of the DictionaryNode interface meant to be overridden. This asbtract implementation
 * provides a default implementation for all concepts except the sub nodes. This needs to be implemented by subclasses.
 * 
 * @author Koen Peters, Cubix Concepts
 */
public abstract class AbstractDictionaryNode implements DictionaryNode {

	/*
	 * Stores per occurrence amount all words that contain this letter including all
	 * preceding letters. Using this structure you can ask questions like: "give me all 
	 * words that have at least X time the occurrence of the substring 'abc' in it.". 
	 * A word is represented by the last Node of that word. Using the parentNode we 
	 * can reconstruct the actual word when needed.
	 */
	private Map<Integer, HashSet<DictionaryNode>> WordsPerOccurenceAmount = null;
	
	private DictionaryNode parentNode = null;
	private char letter = '^';  // ^ is used as the root node. 
	private boolean isWord = false;
	
	AbstractDictionaryNode() {
		super();
		
		// This node is the root node of the dictionary.
	}
	
	AbstractDictionaryNode(DictionaryNode parentNode, Character letter) {
		super();

		ParamValidationUtil.validateParamNotNull(parentNode, "parentNode");
		ParamValidationUtil.validateParamNotNull(letter, "letter");
		
		this.parentNode = parentNode;
		this.letter = letter;
	}

	public boolean isWord() {
		return isWord;
	}
	
	public void isWord(boolean isWord) {
		this.isWord = true;
	}
	
	public String getPrefix() {
		return this.parentNode == null? "": this.parentNode.getPrefix() + letter;  
	}
	
	public String getWord() {
		return isWord()? getPrefix(): null;  
	}

	public List<StringBuilder> getWords() {
		List<StringBuilder> result = getSubWords();
		
		String prefix = getPrefix();
		
		for (StringBuilder word: result) {
			word.insert(0, prefix);
		}
		
		return result;
	}

	public void postFillOptimize() {
		// Nothing to optimize. Noop
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		List<StringBuilder> words = getWords();
		
		// Sort the list alphabetically
		Collections.sort(words, new Comparator<StringBuilder>() {
			public int compare(StringBuilder o1, StringBuilder o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		
		for (StringBuilder word: words) {
			result.append(word).append("\n");
		}
		
		return result.toString();	
	}
}