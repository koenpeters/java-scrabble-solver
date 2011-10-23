package nl.cubix.scrabble.solver.dict.cleaner;

public abstract class AbstractCleaner implements Cleanable {
	
	// You must initialize these two fields in the inheriting class
	protected char[][] accents;
	protected LookupMap lookupMap;
	
	public AbstractCleaner() {
	}
	
	@Override
	public char cleanup(char letter) {
		
		// If a word contains periods it is am abbreviation and we will not add it to the dictionary
		if (letter == '.' || letter == '-') {
			return SKIP_THIS_WORD;
		}
		
		letter = Character.toLowerCase(letter);
		
		// Some letters with extra accents will be transformed into the letter without the accent
		letter = lookupMap.removeAccent(letter);
		
		// Some characters will not result in a bad word, but will just be skipped. 
		// e.g. spaces, -, ', ", etc  
		if (letter < 'a' || letter > 'z') {
			return SKIP_THIS_CHARACTER;
		}
		
		return letter;
	}
	
	protected void init(char[][] accents) {
		this.accents = accents;
		lookupMap = new LookupMap();
	}
	
	protected class LookupMap {
		private char[] map;
		
		LookupMap() {
			
			// We create a map from the ascii value of the character in the first col in 'accents'
			// to the character in the second col. We first need to find the maximum ascii value of
			// the character in the first col to initialize the array with. 
			int max = Integer.MIN_VALUE;
			for (int i=0; i < accents.length; i++) {
				max = Math.max(max, accents[i][0]);
			}
			map = new char[max + 1];
			
			// Now we fill the array
			for (int i=0; i < accents.length; i++) {
				map[accents[i][0]] = accents[i][1];
			}
		}
		
		char removeAccent(char character) {
			if (character > map.length) { 
				return character;
			}
			char noAccent = map[character];
			if (noAccent == 0) {
				return character;
			}
			return noAccent;
		}
	}
}