package nl.cubix.scrabble.solver.dict.node;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

/**
 * An implementation of the DictionaryNode interface that uses a hashmap to store its subnodes. 
 * It is only slightly slower than the DictionaryNodeFixedArray implementation with respect to lookups, 
 * but consumes less memory. The difference in memory consumption is dependent on the dictionary 
 * used.
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class DictionaryNodeMap extends AbstractDictionaryNode{
	
	private Map<String, DictionaryNode> children = null;
	
	DictionaryNodeMap() {
		super();
		// This node is the root node
	}
	
	DictionaryNodeMap(DictionaryNode parentNode, Character letter) {
		super(parentNode, letter);
	}

	@Override
	public DictionaryNode getSubNode(char letter) {
		return children == null? null: children.get(Character.toString(letter));
	}
	
	@Override
	public void setSubNode(char letter, DictionaryNode dictionaryNode) {
		if (children == null) {
			children = new Hashtable<String, DictionaryNode>();
		}
		children.put(Character.toString(letter), dictionaryNode);
	}

	@Override
	public void removeSubNode(char letter) {
		if (children != null) {
			children.remove(Character.toString(letter));
		}
	}
	
	@Override
	public boolean hasNoSubNodes() {
		return children == null || children.isEmpty();
	}
	
	@Override
	public int getNrOfNodes() {
		int result = 1;
		if (children != null) {
			for (DictionaryNode child: children.values()) {
				result += child.getNrOfNodes();
			}
		}
		return result;
	}
	
	@Override
	public int getNrOfWords() {
		int result = isWord()? 1: 0;
		if (children != null) {
			for (DictionaryNode child: children.values()) {
				result += child.getNrOfWords();
			}
		}
		return result; 
	}

	@Override
	public void postFillOptimize() {
		if (children != null) {
			ImmutableSortedMap.Builder<String, DictionaryNode> mapBuilder = new ImmutableSortedMap.Builder<String, DictionaryNode>(Ordering.natural());
			for (Entry<String, DictionaryNode> entry: children.entrySet()) {
				DictionaryNode value = entry.getValue();
				((DictionaryNodeMap)value).postFillOptimize();
				mapBuilder.put(entry.getKey(), value);
			}
			children = mapBuilder.build();
		}
	}
	
	@Override
	public List<StringBuilder> getSubWords() {
		List<StringBuilder> result = new ArrayList<StringBuilder>();
		
		if (isWord()) {
			result.add( new StringBuilder());
		}
		
		if (children != null) {
			for (Entry<String, DictionaryNode> entry: children.entrySet()) {
				List<StringBuilder> tails = entry.getValue().getSubWords();
				for (StringBuilder tail: tails) {
					tail.insert(0, entry.getKey().charAt(0));
					result.add(tail);
				}
			}
		}
		
		return result;
	}
}