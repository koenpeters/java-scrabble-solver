package nl.cubix.scrabble.solver.dict.node;


import java.util.ArrayList;
import java.util.List;

import nl.cubix.scrabble.util.CharUtil;

/**
 * An implementation of the DictionaryNode interface that uses a fixed size array of length 26 (a-z)
 * to store its sub nodes. Because of the instantaneous access to sub nodes based on the index
 * in the array it is a very fast implementation with respect to lookups. This comes at a cost of 
 * extra memory consumption due to empty spaces in the fixed size array.
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class DictionaryNodeArray extends AbstractDictionaryNode {
	private DictionaryNode[] children = null;
	
	DictionaryNodeArray() {
		super();
		// This is the root instance
	}
	
	DictionaryNodeArray(DictionaryNode parentNode, Character letter) {
		super(parentNode, letter);
	}

	@Override
	public DictionaryNode getSubNode(char letter) {
		return children == null? null: children[CharUtil.toOridinal(letter)];
	}
	
	@Override
	public void setSubNode(char letter, DictionaryNode dictionaryNode) {
		if (children == null) {
			children = new DictionaryNode[26];
		}
		children[CharUtil.toOridinal(letter)] = dictionaryNode;	
	}

	@Override
	public void removeSubNode(char letter) {
		if (children != null) {
			children[CharUtil.toOridinal(letter)] = null;
		}
	}

	@Override
	public boolean hasNoSubNodes() {
		if (children != null) {
			for (DictionaryNode child: children) {
				if (child != null) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public int getNrOfNodes() {
		int result = 1;
		if (children != null) {
			for (DictionaryNode child: children) {
				if (child != null) {
					result += child.getNrOfNodes();
				}
			}
		}
		return result;
	}
	
	@Override
	public int getNrOfWords() {
		int result = isWord()? 1: 0;
		if (children != null) {
			for (DictionaryNode child: children) {
				if (child != null) {
					result += child.getNrOfWords();
				}
			}
		}
		return result;
	}

	@Override
	public List<StringBuilder> getSubWords() {
		List<StringBuilder> result = new ArrayList<StringBuilder>();
		
		if (isWord()) {
			result.add( new StringBuilder());
		}
		
		if (children != null) {
			for (int i=0; i < children.length; i++) {
				DictionaryNode node = children[i];
				if (node != null) {
					List<StringBuilder> tails = node.getWords();
					for (StringBuilder tail: tails) {
						tail.insert(0, CharUtil.toChar(i));
						result.add(tail);
					}
				}
			}
		}
		
		return result;
	}
}