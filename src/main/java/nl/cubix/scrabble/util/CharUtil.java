package nl.cubix.scrabble.util;

/**
 * @author Koen Peters, Cubix Concepts
 */
public final class CharUtil {

	private CharUtil() {}
	
	/**
	 * @param letter The character (a-z only) that needs to be converted. 
	 * @return	A integer representation of the given letter. a = 0, b = 1, etc
	 */
	public static int toOridinal(char letter) {
		return (int)letter - 97;
	}
	
	/**
	 * @param letter The integer (0-25 only) that needs to be converted. 
	 * @return	A char representation of the given integer. 0 = a, 1 = b, etc
	 */
	public static char toChar(int ordinal) {
		return (char)(ordinal + 97);
	}
}