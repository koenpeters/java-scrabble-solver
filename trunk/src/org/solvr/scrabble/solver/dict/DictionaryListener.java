package org.solvr.scrabble.solver.dict;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This class is a listener that creates and destroys all the dictionaries.
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class DictionaryListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		DictionarySingleton.freeUpSpace();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		DictionarySingleton.getInstance();
	}

}
