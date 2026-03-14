package dev.sugbo4j.packmule.model.config;

import java.util.List;

/**
 * Trigger configuration model.
 */
public record TriggerConfig(
    String id,
    String label,
    List<String> dependencies
) {
    public TriggerConfig {
        dependencies = dependencies != null ? List.copyOf(dependencies) : List.of();
    }
}
