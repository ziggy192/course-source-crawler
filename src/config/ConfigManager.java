package config;

import config.model.ParserConfig;
import constant.AppConstants;
import listerner.ContextHolder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.validation.ValidatorFactory;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConfigManager {

	private static Logger logger = Logger.getLogger(ConfigManager.class.toString());

	private static ConfigManager instance;
	private static final Object LOCK = new Object();

	private ParserConfig configModel;
	private List<ConfigChangeListener> configChangeListeners;

	private ConfigManager() {
		configChangeListeners = new ArrayList<>();

		//subscribe listeners
		configChangeListeners.add(CategoryMapper.getInstance());
	}


	private void init() {
		readConfigFile();
	}


	public void readConfigFile() {


		try {
			//read xml config file
			File parserConfigXml;
			if (ContextHolder.getApplicationContext() != null) {
				String realPath = ContextHolder.getApplicationContext().getRealPath("/" + AppConstants.PARSER_CONFIG_XML_PATH);

				parserConfigXml = new File(realPath);

			} else {

				parserConfigXml = new File("web/" + AppConstants.PARSER_CONFIG_XML_PATH);
			}


			File parserConfigSchema;
			if (ContextHolder.getApplicationContext() != null) {
				String realPath = ContextHolder.getApplicationContext().getRealPath("/" + AppConstants.PARSER_CONFIG_SCHEMA_PATH);

				parserConfigSchema = new File(realPath);

			} else {

				parserConfigSchema = new File("web/" + AppConstants.PARSER_CONFIG_SCHEMA_PATH);
			}


			// validate xml file

			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(parserConfigSchema);
			Validator validator = schema.newValidator();
			Source source = new SAXSource(new InputSource(new FileInputStream(parserConfigXml)));
			validator.validate(source);

			//unmarshal to jaxb
			JAXBContext jaxbContext = JAXBContext.newInstance(ParserConfig.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			this.setConfigModel((ParserConfig) unmarshaller.unmarshal(parserConfigXml));
			logger.info("Config Validated | ConfigModel="+configModel);

		} catch (SAXException e) {
			e.printStackTrace();
			logger.info("Config file not validated | Exception="+e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static ConfigManager getInstance() {
		synchronized (LOCK) {
			if (instance == null) {
				instance = new ConfigManager();
				instance.init();
			}
		}
		return instance;

	}

	public ParserConfig getConfigModel() {
		return configModel;
	}

	private void setConfigModel(ParserConfig configModel) {
		this.configModel = configModel;
	}

}
