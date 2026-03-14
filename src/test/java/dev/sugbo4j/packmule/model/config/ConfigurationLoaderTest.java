package dev.sugbo4j.packmule.model.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConfigurationLoaderTest {

    @Test
    void testLoadFromClasspath() {
        PackMuleConfig config = ConfigurationLoader.loadFromClasspath("/pack-mule.yaml");
        
        assertNotNull(config, "Config should not be null");
        assertNotNull(config.defaults(), "Defaults should not be null");
        assertEquals("com.mycompany", config.defaults().groupId());
        
        assertFalse(config.triggers().isEmpty(), "Triggers must not be empty");
        assertEquals("HTTP_LISTENER", config.triggers().get(0).id());
        
        assertFalse(config.capabilities().isEmpty(), "Capabilities must not be empty");
        assertEquals("DATABASE", config.capabilities().get(0).id());
        
        assertFalse(config.dependencies().isEmpty(), "Dependencies must not be empty");
        assertNotNull(config.dependencies().get("apikit"));
        assertEquals("org.mule.modules", config.dependencies().get("apikit").groupId());
    }
}
