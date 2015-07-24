package org.lfundaro.followermaze.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;
import org.lfundaro.followermaze.App;

/**
 *
 * @author Lorenzo
 */
public class PropertiesReader {
    
    private static final String CONFIG_FILENAME = "config.properties";
    private static final Logger logger = Logger.getLogger(PropertiesReader.class.getName());

    public static Properties getProperties() {
        InputStream input = null;
        try {
            logger.info("Reading properties file");
            Properties prop = new Properties();
            input = App.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME);
            if (input == null) {
                logger.severe("Unable to find file: "+CONFIG_FILENAME);
            }
            //load a properties file from class path, inside static method
            prop.load(input);
            return prop;

        } catch (IOException ex) {
            logger.severe("Error reading configuration file");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
