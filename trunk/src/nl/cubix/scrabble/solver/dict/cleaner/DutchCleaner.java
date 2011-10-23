package nl.cubix.scrabble.solver.dict.cleaner;

public class DutchCleaner extends AbstractCleaner {
	protected static char[][] dutchAccents = 	{	{'ä', 'a'}, {'à', 'a'}, {'á', 'a'}, {'â', 'a'}
													,{'ë', 'e'}, {'è', 'e'}, {'é', 'e'}, {'ê', 'e'}
													,{'ö', 'o'}, {'ò', 'o'}, {'ó', 'o'}, {'ô', 'o'}
													,{'ü', 'u'}, {'ù', 'u'}, {'ú', 'u'}, {'û', 'u'}
													,{'ï', 'i'}, {'ì', 'i'}, {'í', 'i'}, {'î', 'i'}
													,{'ÿ', 'y'}
													,{'ç', 'c'}
												};

	public DutchCleaner() {
		super();
		init(dutchAccents);
	}
}