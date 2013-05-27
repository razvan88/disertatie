package analytics.utils;

import java.io.File;

import org.ini4j.Ini;

public class ConfigurationSettings {
	private static final String configFileName = "config.ini";
	private static ConfigurationSettings config = null;
	private static Ini iniFile;
	
	static{
		config = new ConfigurationSettings();
		loadConfigFile();
	}
	
	private ConfigurationSettings() {}
	
	public static ConfigurationSettings getInstance() {
		return config;
	}
	
	private static void loadConfigFile() {
		iniFile = new Ini();
		try {
			iniFile.load(new File(configFileName));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getValue(String section, String key) {
		Ini.Section sec = iniFile.get(section);
		return sec.get(key);
	}
}
