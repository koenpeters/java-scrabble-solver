package nl.cubix.scrabble.solver.datastructures;

/**
 * All possible types of boxes that can exist on a board
 * 
 * @author Koen Peters, Cubix Concepts
 */
public enum BoxTypeEnum {
	
	/** The box is taken by an existing letter. It is unknown if the box has any special meaning. */
	TAKEN
	
	/** The box has no existing letter in it and has no special meaning */
	,EMPTY
	
	/** The box has no existing letter in it and has a double letter value */
	,DOUBLE_LETTER
	
	/** The box has no existing letter in it and has a triple letter value */
	,TRIPLE_LETTER
	
	/** The box has no existing letter in it and has a double word value */
	,DOUBLE_WORD
	
	/** The box has no existing letter in it and has a triple word value */
	,TRIPLE_WORD
	
	/** The box has no existing letter in it and is the starting position of the board */
	,STARTING_POSITION
}
