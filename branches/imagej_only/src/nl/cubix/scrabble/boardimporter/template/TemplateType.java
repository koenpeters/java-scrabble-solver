package nl.cubix.scrabble.boardimporter.template;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TemplateType {

	private String gameType;
	private int screenWidth;
	
	public TemplateType(String gameType, int screenWidth) {
		super();
		this.gameType = gameType;
		this.screenWidth = screenWidth;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public String getGameType() {
		return gameType;
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
					.isEquals();
	}
	
	@Override
	public int hashCode() {
	     return new HashCodeBuilder(47, 13)
	     	.append(screenWidth)
	     	.append(gameType)
	       .toHashCode();
	}
}
