package analytics.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	
	public List<String> getSectionValues(String sectionName) {
		Ini.Section section = iniFile.get(sectionName);
		return new ArrayList<String>(section.values());
	}
	
	public String getValue(String sectionName, String key) {
		Ini.Section section = iniFile.get(sectionName);
		return section.get(key);
	}
}
