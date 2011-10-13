package nl.cubix.scrabble.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.log4j.Logger;

public class DeviceConfig {
	private Logger log = Logger.getLogger(DeviceConfig.class);
	private List<Device> devices = new ArrayList<Device>();
	private Map<String, Device> devicesMap = new HashMap<String, Device>();
	
	public DeviceConfig(AbstractConfiguration config) {
		List<String> devices = config.getList("device-config.device.game-type");
		for (int i=0; i<devices.size(); i++) {
			Device device = new Device("device-config.device(" + i + ").", config);
			this.devices.add(device);
			devicesMap.put(toKey(device.getGameType(), device.getDeviceType(), device.getScreenWidth()), device);
		}
	}

	public Device getDevice(String gameType, String deviceType, int screenWidth) {
		return devicesMap.get(toKey(gameType, deviceType, screenWidth));
	}
	
	private String toKey(String gameType, String deviceType, int screenWidth) {
		return gameType + "/" + deviceType + "/" + screenWidth;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		for (int i=0; i < devices.size(); i++) {
			result.append("\ndevice ").append((i+1)).append(":\n").append(devices.get(i).toString());
		}
		return result.toString();
	}
}
