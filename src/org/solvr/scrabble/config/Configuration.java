package org.solvr.scrabble.config;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.log4j.Logger;

import org.solvr.scrabble.util.ParamValidationUtil;

/**
 * This class contains all the configuration of this application
 * 
 * @author Koen Peters, Cubix Concepts
 */
public class Configuration {
	private static Logger logger = Logger.getLogger(Configuration.class.getName());

	private String dataFolder;
	private String dictionariesFolder;
	private String scoringFolder;
	
	public Configuration(AbstractConfiguration config) {
		logger.info("starting reading ApplicationConfiguration");

		this.dataFolder = config.getString("data-folder");
		ParamValidationUtil.validateAsADirectory("", getDataFolder(), false);
		logger.info("Setting dataFolder to " + this.dataFolder);
		
		this.dictionariesFolder = config.getString("dictionaries-folder");
		ParamValidationUtil.validateAsADirectory("", getDictionariesFolder(), false);
		logger.info("Setting dictionariesFolder to " + this.dictionariesFolder);

		this.scoringFolder = config.getString("scoring-folder");
		ParamValidationUtil.validateAsADirectory("", getScoringFolder(), false);
		logger.info("Setting scoringFolder to " + this.scoringFolder);

		
		logger.info("finished reading ApplicationConfiguration");
	}
	
	public String getDataFolder() {
		return dataFolder;
	}

	public String getDictionariesFolder() {
		return getDataFolder() + dictionariesFolder;
	}
	
	public String getScoringFolder() {
		return getDataFolder() +scoringFolder;
	}

}