package org.solvr.scrabble.scoring;

import org.apache.log4j.Logger;
import org.solvr.scrabble.datastructures.Board;
import org.solvr.scrabble.util.CharUtil;

/**
 * @author Koen Peters, Cubix Concepts
 */
public class Scoring {

	Logger log = Logger.getLogger(Scoring.class);
	
	int[] pointsPerTile = new int[26];
	int bonus;
	int traySize;
	Board board;
	
	public Integer getPoints(char letter) {
		return pointsPerTile[CharUtil.toOridinal(letter)];
	}
	
	public void setPoints(char letter, int points) {
		pointsPerTile[CharUtil.toOridinal(letter)] = points;
	}

	public int getBonus() {
		return bonus;
	}
	
	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
	
	public int getTraySize() {
		return traySize;
	}
	
	public void setTraySize(int traySize) {
		this.traySize = traySize;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		
		result.append("bonus:" + getBonus() + ", ");
		result.append("tray size:" + getTraySize() + ", ");
		
		for (int i=0; i<pointsPerTile.length; i++) {
			result.append(CharUtil.toChar(i) + ":" + pointsPerTile[i] + ", ");
		}
		return result.toString();
	}
}
