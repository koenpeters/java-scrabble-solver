package nl.cubix.scrabble.solver.dict.node;


/**
 * Enum that defines the different types of DictionaryNode implementations. It also acts as
 * a factory for new instances of each of these types
 * 
 * @author Koen Peters, Cubix Concepts
 *
 */
public enum DictionaryNodeType {
	FIXED_ARRAY
	,FLEX_LIST;
	
	public DictionaryNode getInstance(DictionaryNode parent, Character letter) {
		switch (this) {
		case FIXED_ARRAY: return new DictionaryNodeArray(parent, letter);
		case FLEX_LIST: return new DictionaryNodeMap(parent, letter);
		default: throw new RuntimeException("Unknown dictionaryNodeType: " + this.name() + ", please add to this switch statement.");
		}
	}

	public DictionaryNode getRootInstance() {
		switch (this) {
		case FIXED_ARRAY: return new DictionaryNodeArray();
		case FLEX_LIST: return new DictionaryNodeMap();
		default: throw new RuntimeException("Unknown dictionaryNodeType: " + this.name() + ", please add to this switch statement.");
		}
	}
}
