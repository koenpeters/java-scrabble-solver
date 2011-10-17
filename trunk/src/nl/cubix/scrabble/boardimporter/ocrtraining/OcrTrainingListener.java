package nl.cubix.scrabble.boardimporter.ocrtraining;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This class is a listener that creates and destroys all the CR trainging images
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class OcrTrainingListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		OcrTrainingSingleton.freeUpSpace();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		OcrTrainingSingleton.getInstance();
	}

}
