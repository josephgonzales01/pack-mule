package dev.sugbo4j.packmule.model.config;

/**
 * Dependency component configuration model.
 */
public record DependencyConfig(
    String groupId,
    String artifactId,
    String version,
    String classifier
) {}
