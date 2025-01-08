package selenium.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PropertiesUtility {

    private PropertiesUtility() {
        // Private constructor to prevent instantiation
    }

    public static String getValue(String key) {
        Properties properties = new Properties();
        String value = null;
        try (FileInputStream fileInputStream = new FileInputStream("config/config.properties")) {
            properties.load(fileInputStream);
            value = properties.getProperty(key);
            log.trace("Found Properties file");
            log.trace("Properties Load Success");
        } catch (IOException e) {
            log.error("Error loading properties: {}", e.getMessage());
        }
        return value;
    }
}