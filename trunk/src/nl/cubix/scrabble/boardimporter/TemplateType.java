package nl.cubix.scrabble.boardimporter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.sun.org.apache.xpath.internal.operations.Gt;

public class TemplateType {

	private String gameType;
	private String deviceType;
	private int screenWidth;
	
	public TemplateType(String gameType, String deviceType, int screenWidth) {
		super();
		this.gameType = gameType;
		this.screenWidth = screenWidth;
		this.deviceType = deviceType;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public String getGameType() {
		return gameType;
	}
	
	public String getDeviceType() {
		return deviceType;
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
					.append(gameType, templateType.gameType)
					.append(deviceType, templateType.deviceType)
					.isEquals();
	}
	
	@Override
	public int hashCode() {
	     return new HashCodeBuilder(47, 13)
	     	.append(screenWidth)
	     	.append(gameType)
	     	.append(deviceType)
	       .toHashCode();
	}
}
