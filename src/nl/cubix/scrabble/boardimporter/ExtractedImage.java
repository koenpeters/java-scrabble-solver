package nl.cubix.scrabble.boardimporter;

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
}
