package org.solvr.scrabble.datastructures;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * This class models one of the boxes of a scrabble board
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class Box {
	private BoxTypeEnum boxTypeEnum;
	private char character;
	private boolean isJoker;
	
	private Box() { /* used for the clone method */	}
	
	public Box(BoxTypeEnum boxTypeEnum) {
		super();
		if (boxTypeEnum == BoxTypeEnum.TAKEN) {
			throw new IllegalArgumentException("BoxTypeEnum cannot be TAKEN. Use the other constructor for that purpose.");
		}
		this.boxTypeEnum = boxTypeEnum;
	}

	public Box(char character, boolean isJoker) {
		super();
		this.boxTypeEnum = BoxTypeEnum.TAKEN;
		this.character = character;
		this.isJoker = isJoker;
	}
	
	public char getCharacter() {
		return character;
	}	

	public BoxTypeEnum getBoxType() {
		return boxTypeEnum;
	}
	
	public boolean isJoker() {
		return isJoker;
	}
	
	/**
	 * @return True if this box has not been taken by an existing letter
	 */
	public boolean isEmpty() {
		return boxTypeEnum != BoxTypeEnum.TAKEN;
	}
	
	/**
	 * @return True if this box has been taken by an existing letter
	 */
	public boolean isTaken() {
		return boxTypeEnum == BoxTypeEnum.TAKEN;
	}
	
	/**
	 * @return True if this box is the startingposition of the board
	 */
	public boolean isStartingPosition() {
		return boxTypeEnum == BoxTypeEnum.STARTING_POSITION;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Box == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		Box box = (Box) obj;
		return new EqualsBuilder()
	                 .append(boxTypeEnum, box.boxTypeEnum)
	                 .append(character, box.character)
	                 .append(isJoker, box.isJoker)
	                 .isEquals();
	}
	
	@Override
	public int hashCode() {
	     return new HashCodeBuilder(17, 37).
	       append(boxTypeEnum).
	       append(character).
	       append(isJoker).
	       toHashCode();
	}
	
	@Override
	public Box clone() throws CloneNotSupportedException {
		Box result = new Box();
		result.boxTypeEnum = boxTypeEnum;
		result.character = character;
		result.isJoker = isJoker;
		return result;
	}
}
