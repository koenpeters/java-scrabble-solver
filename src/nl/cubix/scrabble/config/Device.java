package nl.cubix.scrabble.config;

import org.apache.commons.configuration.AbstractConfiguration;

public class Device {
	private String gameType;
	private String deviceType;
	private int screenWidth;
	private int boardCropX;
	private int boardCropY;
	private int boardCropWidth;
	private int boardCropHeight;
	private float boardBoxWidth;
	private int trayCropX;
	private int trayCropY;
	private int trayCropWidth;
	private int trayCropHeight;
	private float trayBoxWidth;
	
	public Device(String prefix, AbstractConfiguration config) {
		this.gameType = config.getString(prefix + "game-type");
		this.deviceType = config.getString(prefix + "device-type");
		this.screenWidth = config.getInt(prefix + "screen-width");
		this.boardCropX = config.getInt(prefix + "board-crop-x");
		this.boardCropY = config.getInt(prefix + "board-crop-y");
		this.boardCropWidth = config.getInt(prefix + "board-crop-width");
		this.boardCropHeight = config.getInt(prefix + "board-crop-height");
		this.boardBoxWidth = config.getFloat(prefix + "board-box-width");
		this.trayCropX = config.getInt(prefix + "tray-crop-x");
		this.trayCropY = config.getInt(prefix + "tray-crop-y");
		this.trayCropWidth = config.getInt(prefix + "tray-crop-width");
		this.trayCropHeight = config.getInt(prefix + "tray-crop-height");
		this.trayBoxWidth = config.getFloat(prefix + "tray-box-width");
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getBoardCropX() {
		return boardCropX;
	}

	public int getBoardCropY() {
		return boardCropY;
	}

	public float getBoardBoxWidth() {
		return boardBoxWidth;
	}

	public int getTrayCropX() {
		return trayCropX;
	}

	public int getTrayCropY() {
		return trayCropY;
	}

	public float getTrayBoxWidth() {
		return trayBoxWidth;
	}

	public String getGameType() {
		return gameType;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public int getBoardCropWidth() {
		return boardCropWidth;
	}

	public int getBoardCropHeight() {
		return boardCropHeight;
	}

	public int getTrayCropWidth() {
		return trayCropWidth;
	}

	public int getTrayCropHeight() {
		return trayCropHeight;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result
			.append("\tgameType: ").append(gameType).append("\n")
			.append("\tdeviceType: ").append(deviceType).append("\n")
			.append("\tscreenWidth: ").append(screenWidth).append("\n")
			.append("\tboardCropX: ").append(boardCropX).append("\n")
			.append("\tboardCropY: ").append(boardCropY).append("\n")
			.append("\tboardCropWidth: ").append(boardCropWidth).append("\n")
			.append("\tboardCropHeight: ").append(boardCropHeight).append("\n")
			.append("\tboardBoxWidth: ").append(boardBoxWidth).append("\n")
			.append("\ttrayCropX: ").append(trayCropX).append("\n")
			.append("\ttrayCropY: ").append(trayCropY).append("\n")
			.append("\ttrayCropWidth: ").append(trayCropWidth).append("\n")
			.append("\ttrayCropHeight: ").append(trayCropHeight).append("\n")
			.append("\ttrayBoxWidth: ").append(trayBoxWidth);
		
		return result.toString();
	}
}
