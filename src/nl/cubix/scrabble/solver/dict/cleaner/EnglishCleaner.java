package nl.cubix.scrabble.solver.dict.cleaner;

public class EnglishCleaner extends AbstractCleaner {
	protected char[][] englishAccents = {	{'�', 'a'}, {'�', 'a'}, {'�', 'a'}, {'�', 'a'}
											,{'�', 'e'}, {'�', 'e'}, {'�', 'e'}, {'�', 'e'}
											,{'�', 'o'}, {'�', 'o'}, {'�', 'o'}, {'�', 'o'}
											,{'�', 'u'}, {'�', 'u'}, {'�', 'u'}, {'�', 'u'}
											,{'�', 'i'}, {'�', 'i'}, {'�', 'i'}, {'�', 'i'}
											,{'�', 'y'}
											,{'�', 'c'}
										};

	public EnglishCleaner() {
		super();
		init(englishAccents);
	}
}