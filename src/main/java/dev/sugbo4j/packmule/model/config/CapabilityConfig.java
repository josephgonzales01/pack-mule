package dev.sugbo4j.packmule.model.config;

import java.util.List;

/**
 * Capability configuration model.
 */
public record CapabilityConfig(
    String id,
    String label,
    String category,
    List<String> dependencies
) {
    public CapabilityConfig {
        dependencies = dependencies != null ? List.copyOf(dependencies) : List.of();
    }
}
