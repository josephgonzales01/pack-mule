package dev.sugbo4j.packmule.validator;

import dev.sugbo4j.packmule.model.config.CapabilityConfig;
import dev.sugbo4j.packmule.model.config.ConfigurationLoader;
import dev.sugbo4j.packmule.model.config.DependencyConfig;
import dev.sugbo4j.packmule.model.config.PackMuleConfig;
import dev.sugbo4j.packmule.model.config.TriggerConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigValidatorTest {

    @Test
    void bundledConfigIsValid() {
        PackMuleConfig config = ConfigurationLoader.loadFromClasspath("/pack-mule.yaml");
        ConfigValidator.Result result = ConfigValidator.validate(config);

        assertTrue(result.isValid(), "Bundled pack-mule.yaml must be valid");
        assertFalse(result.hasErrors(), "Bundled config must report no errors");
    }

    @Test
    void detectsMissingTriggerTemplateDirectory() {
        PackMuleConfig config = new PackMuleConfig(
                null, List.of(), List.of(),
                List.of(new TriggerConfig("DOES_NOT_EXIST", "ghost", List.of())),
                List.of(),
                Map.of(),
                Map.of()
        );

        ConfigValidator.Result result = ConfigValidator.validate(config);

        assertFalse(result.isValid());
        assertTrue(result.errors().stream()
                .anyMatch(e -> e.contains("DOES_NOT_EXIST") && e.contains("templates/triggers/DOES_NOT_EXIST")));
    }

    @Test
    void detectsMissingCapabilityTemplateDirectory() {
        PackMuleConfig config = new PackMuleConfig(
                null, List.of(), List.of(),
                List.of(),
                List.of(new CapabilityConfig("NOPE", "ghost", "Connector", List.of())),
                Map.of(),
                Map.of()
        );

        ConfigValidator.Result result = ConfigValidator.validate(config);

        assertFalse(result.isValid());
        assertTrue(result.errors().stream()
                .anyMatch(e -> e.contains("NOPE") && e.contains("templates/capabilities/NOPE")));
    }

    @Test
    void detectsUnknownDependencyKey() {
        PackMuleConfig config = new PackMuleConfig(
                null, List.of(), List.of(),
                List.of(new TriggerConfig("HTTP_LISTENER", "HTTP", List.of("missing-key"))),
                List.of(),
                Map.of(),
                Map.of()
        );

        ConfigValidator.Result result = ConfigValidator.validate(config);

        assertFalse(result.isValid());
        assertTrue(result.errors().stream()
                .anyMatch(e -> e.contains("missing-key") && e.contains("unknown dependency key")));
    }

    @Test
    void warnsAboutOrphanedCatalogEntry() {
        DependencyConfig dep = new DependencyConfig("g", "a", "1", null);
        PackMuleConfig config = new PackMuleConfig(
                null, List.of(), List.of(),
                List.of(new TriggerConfig("HTTP_LISTENER", "HTTP", List.of())),
                List.of(),
                Map.of("orphan", dep),
                Map.of()
        );

        ConfigValidator.Result result = ConfigValidator.validate(config);

        assertTrue(result.isValid());
        assertFalse(result.warnings().isEmpty());
        assertTrue(result.warnings().stream()
                .anyMatch(w -> w.contains("orphan") && w.contains("not referenced")));
    }

    @Test
    void validatesBundledTriggersAndCapabilities() {
        PackMuleConfig config = ConfigurationLoader.loadFromClasspath("/pack-mule.yaml");

        ConfigValidator.Result result = ConfigValidator.validate(config);

        for (TriggerConfig trigger : config.triggers()) {
            String id = trigger.id();
            assertTrue(result.errors().stream()
                            .noneMatch(e -> e.contains("templates/triggers/" + id)),
                    "Bundled trigger '" + id + "' must have its template directory");
        }
        for (CapabilityConfig cap : config.capabilities()) {
            String id = cap.id();
            assertTrue(result.errors().stream()
                            .noneMatch(e -> e.contains("templates/capabilities/" + id)),
                    "Bundled capability '" + id + "' must have its template directory");
        }
    }
}
