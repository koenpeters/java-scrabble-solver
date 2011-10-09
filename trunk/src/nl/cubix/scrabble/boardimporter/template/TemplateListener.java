package nl.cubix.scrabble.boardimporter.template;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This class is a listener that creates and destroys all the templates for the boardimporter.
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class TemplateListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		TemplateSingleton.freeUpSpace();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		TemplateSingleton.getInstance();
	}

}
