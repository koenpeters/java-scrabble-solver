package nl.cubix.scrabble.boardimporter.extracter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import nl.cubix.scrabble.solver.datastructures.Board;

public class ExtractedImage {
	private Board board;
	private String tray;
	
	public ExtractedImage(Board board, String tray) {
		super();
		this.board = board;
		this.tray = tray;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public String getTray() {
		return tray;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExtractedImage == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		
		ExtractedImage extractedImage = (ExtractedImage) obj;
		return new EqualsBuilder()
					.append(board, extractedImage.board)
					.append(tray, extractedImage.tray)
					.isEquals();
	}
	
	@Override
	public int hashCode() {
	     return new HashCodeBuilder(47, 13)
	     	.append(board)
	     	.append(tray)
	       .toHashCode();
	}
}