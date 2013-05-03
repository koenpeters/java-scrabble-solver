package nl.cubix.scrabble.boardimporter.GameDetector;

import nl.cubix.scrabble.config.ConfigListener;
import nl.cubix.scrabble.config.Device;
import nl.cubix.scrabble.config.DeviceConfig;
import nl.cubix.scrabble.solver.scoring.Scoring;
import nl.cubix.scrabble.solver.scoring.ScoringSingleton;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TemplateType {

	private String language;
	private Device device;
	private Scoring scoringSystem;
	
	public TemplateType(String language, String gameType, String deviceType, int screenWidth, int screenHeight) {
		super();
		this.language = language;
		DeviceConfig deviceConfig = ConfigListener.getConfiguration().getDeviceConfig(); 
		this.device = deviceConfig.getDevice(gameType, deviceType, screenWidth, screenHeight);
		
		ScoringSingleton scoringSingleton = ScoringSingleton.getInstance();
		this.scoringSystem = scoringSingleton.getScoringSystem(device.getGameType(), language);
	}
	
	
	public String getLanguage() {
		return language;
	}
	
	public Device getDevice() {
		return device;
	}
	
	public Scoring getScoringSystem() {
		return scoringSystem;
	}
	
	@Override
	public String toString() {
		return "('" + language + "', '" + device.toString() + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TemplateType == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		
		TemplateType templateType = (TemplateType) obj;
		return new EqualsBuilder()
					.append(language, templateType.language)
					.append(device, templateType.device)
					.isEquals();
	}
	
	@Override
	public int hashCode() {
	     return new HashCodeBuilder(47, 13)
	     	.append(language)
	       .toHashCode();
	}
}