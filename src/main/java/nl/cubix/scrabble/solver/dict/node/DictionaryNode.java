package nl.cubix.scrabble.solver.dict.node;

import java.util.List;

/**
 * An Interface describing a node inside a Tree based dictionary.
 *  
 * @author Koen Peters, Cubix Concepts
 */
public interface DictionaryNode {
	
	/**
	 * @return True if this node is the last letter of a word in the dictionary. False otherwise
	 */
	public abstract boolean isWord();
	
	/**
	 * Sets the flag that indicates if this node is the last letter of a word in the dictionary 
	 * @param isWord True if this node is the last letter of a word in the dictionary. False otherwise
	 */
	public abstract void isWord(boolean isWord);

	/**
	 * @param letter One of the letters (lower case a-z).
	 * @return	The sub DictionaryNode that is mapped to the given letter. 
	 * 			If no such node exists null will be returned.
	 */
	public abstract DictionaryNode getSubNode(char letter);
	
	/**
	 * @param letter The letter (lower case a-z) that should be set as a child node.
	 * @param dictionaryNode The Dictionary node that should be added as a sub node mapped to the given letter
	 */
	public abstract void setSubNode(char letter, DictionaryNode dictionaryNode);	

	/**
	 * Removes the child node that is mapped to the given letter.
	 * @param letter The letter (lower case a-z) that should be removed as a child node.
	 */
	public abstract void removeSubNode(char letter);

	/**
	 * @return	True if this node has no sub nodes. False otherwise
	 */
	public abstract boolean hasNoSubNodes();

	/**
	 * @return	The number of sub nodes that are reachable through this node including this node itself.
	 */
	public abstract int getNrOfNodes();

	/**
	 * @return	The number of words that are reachable through sub nodes of this node including 
	 * 			the word in this node itself (if this node is a word itself).
	 */
	public abstract int getNrOfWords();
	
	/**
	 * @return	The string that precedes this node inside the whole dictionary including the letter of this node.
	 */
	public abstract String getPrefix();
		
	/**
	 * @return	If this node is a word then the string that precedes this node inside the whole dictionary 
	 * 			including the letter of this node. Null otherwise
	 */
	public abstract String getWord();

	/**
	 * 
	 * @return	A list of StringBuilder objects of which each StringBuilder contains one word that is
	 * 			reachable through sub nodes of this node preceded by the prefix of this node (See Prefix). 
	 * 			This includes the word in this node itself (if this node is a word itself). This list might 
	 * 			be alphabetically sorted, but this is not guaranteed.
	 */
	public List<StringBuilder> getWords();
	
	/**
	 * @return	A list of StringBuilder objects of which each StringBuilder contains one word that is
	 * 			reachable through sub nodes of this node. This includes the word in this node itself 
	 * 			(if this node is a word itself). This list might be alphabetically sorted, but this 
	 * 			is not guaranteed.
	 */
	public abstract List<StringBuilder> getSubWords();
	
	/**
	 * After this node and all its sub nodes are completely filled it has enough information to optimize its 
	 * internal data structure with respect to memory usages. After calling this method the Node and all its 
	 * sub nodes will be optimized, but might also no longer accept any changed to its internal dictionary 
	 * sub nodes state.
	 */
	public abstract void postFillOptimize();

	/**
	 * @return	An String containing all words that are reachable through sub nodes of this node preceded
	 * 			by the precix of this node (See Prefix). The words inside this string are alphabetically 
	 * 			ordered. This includes the word in this node itself (if this node is a word itself). 
	 * 			A new line id added after each word.
	 */
	public abstract String toString();

}