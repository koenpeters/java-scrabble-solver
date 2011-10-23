package nl.cubix.scrabble.solver.dict.cleaner;

public class DutchCleaner extends AbstractCleaner {
	protected static char[][] dutchAccents = 	{	{'�', 'a'}, {'�', 'a'}, {'�', 'a'}, {'�', 'a'}
													,{'�', 'e'}, {'�', 'e'}, {'�', 'e'}, {'�', 'e'}
													,{'�', 'o'}, {'�', 'o'}, {'�', 'o'}, {'�', 'o'}
													,{'�', 'u'}, {'�', 'u'}, {'�', 'u'}, {'�', 'u'}
													,{'�', 'i'}, {'�', 'i'}, {'�', 'i'}, {'�', 'i'}
													,{'�', 'y'}
													,{'�', 'c'}
												};

	public DutchCleaner() {
		super();
		init(dutchAccents);
	}
}