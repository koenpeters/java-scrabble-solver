package org.solvr.scrabble.config;

import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

/**
 * This class makes sure the apache commons configuration is read.
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class ConfigListener implements ServletContextListener {

	private static final String CONFIG_LOCATION = "config.xml";
	private static volatile Configuration configuration;

	public void contextDestroyed(ServletContextEvent event) {
		ConfigListener.configuration = null;
	}

	public void contextInitialized(ServletContextEvent event) {
		ConfigListener.configuration = instantiateConfiguration();
	}

	public static Configuration getConfiguration() {
		if (ConfigListener.configuration == null) {
			throw new RuntimeException(
					"Configuration is null, probably not yet loaded");
		}
		return ConfigListener.configuration;
	}

	public Configuration instantiateConfiguration() {
		try {
			Logger logger = Logger.getLogger(ConfigListener.class);
			URL url = getClass().getClassLoader().getResource(CONFIG_LOCATION);
			logger.info("url: " + url);

			CustomConfigurationBuilder configurationBuilder = new CustomConfigurationBuilder(url);
			CombinedConfiguration config = configurationBuilder.getConfiguration(true);

			config.setThrowExceptionOnMissing(true);

			Configuration configuration = new Configuration(config);

			return configuration;
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
}
