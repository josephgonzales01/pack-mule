package dev.sugbo4j.packmule.generator;

import dev.sugbo4j.packmule.model.ProjectConfig;
import dev.sugbo4j.packmule.model.config.TriggerConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DependencyResolverTest {

    @Test
    void testResolveDependencies() {
        ProjectConfig config = new ProjectConfig();
        
        // Setup state: HTTP Trigger + DATABASE + SALESFORCE
        config.setTrigger(config.getAvailableTriggers().get(0)); // HTTP_LISTENER
        config.toggleCapability("DATABASE");
        config.toggleCapability("SALESFORCE");

        DependencyResolver resolver = new DependencyResolver();
        List<Map<String, String>> deps = resolver.resolveDependencies(config);

        assertEquals(4, deps.size(), "Should resolve 4 dependencies: apikit, http, db, salesforce");
        
        // Assert correct shape of map
        Map<String, String> firstDep = deps.get(0);
        assertTrue(firstDep.containsKey("groupId"));
        assertTrue(firstDep.containsKey("artifactId"));
        assertTrue(firstDep.containsKey("version"));
        assertTrue(firstDep.containsKey("classifier"));
    }
}
