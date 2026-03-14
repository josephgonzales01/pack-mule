package dev.sugbo4j.packmule.model.config;

/**
 * Hook configuration model.
 */
public record HookConfig(
    String type,
    String command,
    Boolean enabled
) {}
