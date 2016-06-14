package nl.cubix.scrabble.solver.dict.cleaner;

public enum CleanerType {
	EN
	,NL;
	
	public Cleanable getInstance() {
		switch (this) {
		case EN: return new EnglishCleaner();
		case NL: return new DutchCleaner();
		default: throw new RuntimeException("Unknown cleaner type: " + this.name() + ", please add it.");
		}
	}
}
