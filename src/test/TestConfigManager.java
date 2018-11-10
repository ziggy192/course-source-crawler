package test;

import config.ConfigManager;

import java.util.logging.Logger;

public class TestConfigManager {

	private static Logger logger = Logger.getLogger(TestConfigManager.class.toString());

	public static void main(String[] args) {
		testReadConfig();


	}

	public static void testReadConfig() {
		ConfigManager.getInstance().readConfigFile();

	}
}
