package nl.cubix.scrabble.solver.dict;

import java.util.ArrayList;
import java.util.List;

import nl.cubix.scrabble.util.CharUtil;

/**
 * @author Koen Peters, Cubix Concepts
 */
public class DictionaryNode {
	private DictionaryNode[] children = new DictionaryNode[26];
	private boolean isWord = false;
	
	public DictionaryNode() {
		super();
	}

	public DictionaryNode getChild(char letter) {
		return children[CharUtil.toOridinal(letter)];
	}
	
	public boolean isWord() {
		return isWord;
	}

	public void markAsWord() {
		this.isWord = true;
	}
	
	public void setDictionaryNode(char letter, DictionaryNode dictionaryNode) {
		children[CharUtil.toOridinal(letter)] = dictionaryNode;
	}
	
	private List<StringBuilder> getChildrenAsString() {
		List<StringBuilder> result = new ArrayList<StringBuilder>();
		if (isWord) {
			result.add( new StringBuilder());
		}
		for (int i=0; i < children.length; i++) {
			DictionaryNode node = children[i];
			if (node != null) {
				List<StringBuilder> tails = node.getChildrenAsString();
				for (StringBuilder tail: tails) {
					tail.insert(0, CharUtil.toChar(i));
					result.add(tail);
				}
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		List<StringBuilder> childrenAsString = getChildrenAsString();
		for (StringBuilder word: childrenAsString) {
			result.append(word).append("\n");
		}
		return result.toString();
		
	}

	public String toString(String prefix) {
		StringBuilder result = new StringBuilder();
		List<StringBuilder> childrenAsString = getChildrenAsString();
		for (StringBuilder word: childrenAsString) {
			word.insert(0, prefix);
			result.append(word).append("\n");
		}
		return result.toString();
	}
}
