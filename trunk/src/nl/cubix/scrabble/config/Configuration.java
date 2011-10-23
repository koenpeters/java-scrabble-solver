package nl.cubix.scrabble.config;

import nl.cubix.scrabble.util.ParamValidationUtil;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.log4j.Logger;

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
	private String trainingImagesFolder;
	private String testDataFolder;
	private String testImageDumpFolder;
	private DeviceConfig deviceConfig;
	
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
		
		this.trainingImagesFolder = config.getString("training-images-folder");
		ParamValidationUtil.validateAsADirectory("", getTrainingImagesFolder(), false);
		logger.info("Setting trainingImagesFolder to " + this.trainingImagesFolder);
		
		this.testDataFolder = config.getString("test-data-folder");
		ParamValidationUtil.validateAsADirectory("", getTrainingImagesFolder(), false);
		logger.info("Setting testDataFolder to " + this.testDataFolder);
		
		this.testImageDumpFolder = config.getString("test-image-dump-folder");
		ParamValidationUtil.validateAsADirectory("", getTrainingImagesFolder(), false);
		logger.info("Setting testImageDumpFolder to " + this.testImageDumpFolder);
		
		this.deviceConfig = new DeviceConfig(config);
		logger.info("Setting deviceConfig to " + this.deviceConfig.toString());

		logger.info("finished reading ApplicationConfiguration");
	}
	
	public String getDataFolder() {
		return dataFolder;
	}

	public String getDictionariesFolder() {
		return getDataFolder() + dictionariesFolder;
	}
	
	public String getScoringFolder() {
		return getDataFolder() + scoringFolder;
	}
	
	public String getTrainingImagesFolder() {
		return getDataFolder() + trainingImagesFolder;
	}
	
	public String getTestDataFolder() {
		return getDataFolder() + testDataFolder;
	}
	
	public String getTestImageDumpFolder() {
		return getDataFolder() + testImageDumpFolder;
	}
	
	public DeviceConfig getDeviceConfig() {
		return deviceConfig;
	}
	
}