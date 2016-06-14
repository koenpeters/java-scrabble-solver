package nl.cubix.scrabble.solver.dict.cleaner;

public interface Cleanable {

	public static final char SKIP_THIS_CHARACTER = '@';
	public static final char SKIP_THIS_WORD = '#';
	
	public char cleanup(char letter);
}
