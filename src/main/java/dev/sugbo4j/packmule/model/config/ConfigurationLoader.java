package dev.sugbo4j.packmule.model.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.InputStream;

/**
 * Utility class to load configuration from the YAML file.
 */
public final class ConfigurationLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    private ConfigurationLoader() {
        // Prevent instantiation
    }

    /**
     * Loads the PackMuleConfig from the specified classpath location.
     *
     * @param configPath The path on the classpath (e.g. "/pack-mule.yaml")
     * @return The parsed PackMuleConfig
     */
    public static PackMuleConfig loadFromClasspath(String configPath) {
        try (InputStream is = ConfigurationLoader.class.getResourceAsStream(configPath)) {
            if (is == null) {
                throw new IllegalArgumentException("Configuration not found at classpath path: " + configPath);
            }
            return MAPPER.readValue(is, PackMuleConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration from " + configPath, e);
        }
    }
}
