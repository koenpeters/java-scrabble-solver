package nl.cubix.scrabble.solver.dict.cleaner;

public class CleanerFactory {

	public static Cleanable getInstance(String languageCode) {
		if (languageCode.equalsIgnoreCase("en")) {
			return new EnglishCleaner();

		} else if (languageCode.equalsIgnoreCase("nl")) {
			return new DutchCleaner();
		
		} else {
			throw new RuntimeException("Unknown language code: " + languageCode);
		}
	}
}
