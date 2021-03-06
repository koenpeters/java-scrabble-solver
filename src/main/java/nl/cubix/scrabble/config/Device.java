package nl.cubix.scrabble.config;

import org.apache.commons.configuration.AbstractConfiguration;

public class Device {
	private String gameType;
	private String deviceType;
	private int screenWidth;
	private int screenHeight;
	private float boardBoxWidth;
	private float trayBoxWidth;
	private CropRectangle boardCrop; 
	private CropRectangle boardBoxLetterCrop; 
	private CropRectangle boardBoxTileCrop; 
	private CropRectangle boardBoxNumberCrop; 
	private CropRectangle trayCrop; 
	private CropRectangle trayBoxCrop; 
	
	public Device(String prefix, AbstractConfiguration config) {
		this.gameType = config.getString(prefix + "game-type");
		this.deviceType = config.getString(prefix + "device-type");
		this.screenWidth = config.getInt(prefix + "screen-width");
		this.screenHeight = config.getInt(prefix + "screen-height");
		this.boardBoxWidth = config.getFloat(prefix + "board-box-width");
		this.trayBoxWidth = config.getFloat(prefix + "tray-box-width");
		this.boardCrop = new CropRectangle(
				config.getInt(prefix + "board-crop.x")
				,config.getInt(prefix + "board-crop.y")
				,config.getInt(prefix + "board-crop.width")
				,config.getInt(prefix + "board-crop.height"));
		this.boardBoxLetterCrop = new CropRectangle(
				config.getInt(prefix + "board-box-letter-crop.x")
				,config.getInt(prefix + "board-box-letter-crop.y")
				,config.getInt(prefix + "board-box-letter-crop.width")
				,config.getInt(prefix + "board-box-letter-crop.height"));
		this.boardBoxTileCrop = new CropRectangle(
				config.getInt(prefix + "board-box-tile-crop.x")
				,config.getInt(prefix + "board-box-tile-crop.y")
				,config.getInt(prefix + "board-box-tile-crop.width")
				,config.getInt(prefix + "board-box-tile-crop.height"));
		this.boardBoxNumberCrop = new CropRectangle(
				config.getInt(prefix + "board-box-number-crop.x")
				,config.getInt(prefix + "board-box-number-crop.y")
				,config.getInt(prefix + "board-box-number-crop.width")
				,config.getInt(prefix + "board-box-number-crop.height"));
		this.trayCrop = new CropRectangle(
				config.getInt(prefix + "tray-crop.x")
				,config.getInt(prefix + "tray-crop.y")
				,config.getInt(prefix + "tray-crop.width")
				,config.getInt(prefix + "tray-crop.height"));
		this.trayBoxCrop = new CropRectangle(
				config.getInt(prefix + "tray-box-crop.x")
				,config.getInt(prefix + "tray-box-crop.y")
				,config.getInt(prefix + "tray-box-crop.width")
				,config.getInt(prefix + "tray-box-crop.height"));
	}
	
	public String getGameType() {
		return gameType;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}
	
	public float getBoardBoxWidth() {
		return boardBoxWidth;
	}

	public float getTrayBoxWidth() {
		return trayBoxWidth;
	}

	public CropRectangle getBoardCrop() {
		return boardCrop;
	}

	public CropRectangle getBoardBoxLetterCrop() {
		return boardBoxLetterCrop;
	}
	
	public CropRectangle getBoardBoxTileCrop() {
		return boardBoxTileCrop;
	}

	public CropRectangle getTrayCrop() {
		return trayCrop;
	}

	public CropRectangle getTrayBoxCrop() {
		return trayBoxCrop;
	}
	
	public CropRectangle getBoardBoxNumberCrop() {
		return boardBoxNumberCrop;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result
			.append("\tgameType: ").append(gameType).append("\n")
			.append("\tdeviceType: ").append(deviceType).append("\n")
			.append("\tscreenWidth: ").append(screenWidth).append("\n")
			.append("\tscreenHeight: ").append(screenHeight).append("\n")
			.append("\tboardBoxWidth: ").append(boardBoxWidth).append("\n")
			.append("\tboardCrop: ").append(boardCrop).append("\n")
			.append("\tboardBoxLetterCrop: ").append(boardBoxLetterCrop).append("\n")
			.append("\tboardBoxTileCrop: ").append(boardBoxTileCrop).append("\n")
			.append("\tboardBoxCNumberrop: ").append(boardBoxNumberCrop).append("\n")
			.append("\ttrayCrop: ").append(trayCrop).append("\n")
			.append("\ttrayBoxCrop: ").append(trayBoxCrop).append("\n");
		
		return result.toString();
	}
}
