package org.solvr.scrabble.solver.datastructures;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * This class models a coordinate on a scrabble board. Coordinates are zero based
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class Coordinate {
	int row;
	int col;
	
	public Coordinate(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("(").append(row).append(", ").append(col).append(")");
		return result.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Coordinate == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		Coordinate coordinate = (Coordinate) obj;
		return new EqualsBuilder()
	                 .append(row, coordinate.row)
	                 .append(col, coordinate.col)
	                 .isEquals();
	}
	
	@Override
	public int hashCode() {
	     return new HashCodeBuilder(17, 37).
	       append(row).
	       append(col).
	       toHashCode();
	}
}
