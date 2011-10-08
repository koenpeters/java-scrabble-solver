package org.solvr.scrabble.solver.scoring;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This class is a listener that creates and destroys all the scoring systems.
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class ScoringListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		ScoringSingleton.freeUpSpace();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		ScoringSingleton.getInstance();
	}

}
