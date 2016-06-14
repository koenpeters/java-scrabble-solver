package nl.cubix.scrabble.solver.dict;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This class is a listener that creates and destroys all the dictionaries.
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class DictionaryListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent arg0) {
		DictionarySingleton.freeUpSpace();
	}

	public void contextInitialized(ServletContextEvent arg0) {
		DictionarySingleton.getInstance();
	}

}
