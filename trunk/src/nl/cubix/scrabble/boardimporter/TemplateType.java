package nl.cubix.scrabble.boardimporter;

import nl.cubix.scrabble.config.ConfigListener;
import nl.cubix.scrabble.config.Device;
import nl.cubix.scrabble.config.DeviceConfig;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TemplateType {

	private String gameType;
	private String deviceType;
	private int screenWidth;
	private int screenHeight;
	
	public TemplateType(String gameType, String deviceType, int screenWidth, int screenHeight) {
		super();
		this.gameType = gameType;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.deviceType = deviceType;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public int getScreenHeight() {
		return screenHeight;
	}
	
	public String getGameType() {
		return gameType;
	}
	
	public String getDeviceType() {
		return deviceType;
	}

	public Device getDevice() {
		DeviceConfig deviceConfig = ConfigListener.getConfiguration().getDeviceConfig(); 
		return deviceConfig.getDevice(gameType, deviceType, screenWidth, screenHeight);
	}
	
	@Override
	public String toString() {
		return "('" + gameType + "', " + screenWidth + ")";
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
					.append(screenWidth, templateType.screenWidth)
					.append(screenHeight, templateType.screenHeight)
					.append(gameType, templateType.gameType)
					.append(deviceType, templateType.deviceType)
					.isEquals();
	}
	
	@Override
	public int hashCode() {
	     return new HashCodeBuilder(47, 13)
	     	.append(screenWidth)
	     	.append(screenHeight)
	     	.append(gameType)
	     	.append(deviceType)
	       .toHashCode();
	}
}
