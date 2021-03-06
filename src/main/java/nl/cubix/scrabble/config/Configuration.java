package nl.cubix.scrabble.config;

import nl.cubix.scrabble.solver.dict.node.DictionaryNodeType;
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

	private Boolean showMemoryFootprint;
	private DictionaryNodeType dictionaryNodeType;
	private String dataFolder;
	private String dictionariesFolder;
	private String scoringFolder;
	private String trainingImagesFolder;
	private String testDataFolder;
	private String testImageDumpFolder;
	private String uploadFolder;
	private DeviceConfig deviceConfig;
	
	public Configuration(AbstractConfiguration config) {
		logger.info("starting reading ApplicationConfiguration");

		this.showMemoryFootprint = config.getBoolean("show-memory-footprint");
		logger.info("Setting showMemoryFootprint to " + this.showMemoryFootprint);

		this.dictionaryNodeType = DictionaryNodeType.valueOf(config.getString("dictionary-node-type"));
		logger.info("Setting dictionaryNodeType to " + this.dictionaryNodeType);

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
		
		this.uploadFolder = config.getString("upload-folder");
		ParamValidationUtil.validateAsADirectory("", getTrainingImagesFolder(), false);
		logger.info("Setting uploadFolder to " + this.uploadFolder);
		
		this.deviceConfig = new DeviceConfig(config);
		logger.info("Setting deviceConfig to " + this.deviceConfig.toString());

		logger.info("finished reading ApplicationConfiguration");
	}
	
	
	public DictionaryNodeType getDictionaryNodeType() {
		return dictionaryNodeType;
	}
	
	public Boolean getShowMemoryFootprint() {
		return showMemoryFootprint;
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
	
	public String getUploadFolder() {
		return getDataFolder() + uploadFolder;
	}
	
	public DeviceConfig getDeviceConfig() {
		return deviceConfig;
	}
	
}