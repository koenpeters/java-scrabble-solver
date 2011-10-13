package nl.cubix.scrabble.solver.scoring;

import java.util.Arrays;

import nl.cubix.scrabble.solver.datastructures.Board;
import nl.cubix.scrabble.util.CharUtil;

import org.apache.log4j.Logger;

/**
 * @author Koen Peters, Cubix Concepts
 */
public class Scoring {

	Logger log = Logger.getLogger(Scoring.class);
	
	private static final int IS_NOT_SET = -1;
	
	int[] pointsPerTile = new int[26];
	int bonus = IS_NOT_SET;
	int traySize = IS_NOT_SET;
	Board board = null;
	
	public Scoring() {
		Arrays.fill(pointsPerTile, IS_NOT_SET);
	}
	
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

	public boolean areAllValuesSet() {
		boolean result = true;
		result = result && bonus != IS_NOT_SET;
		result = result && traySize != IS_NOT_SET;
		result = result && board != null;
		for (int p: pointsPerTile) {
			result = result && p != IS_NOT_SET;
		}
		return result;
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
