package dev.sugbo4j.packmule.model.config;

import java.util.List;
import java.util.Map;

/**
 * Root configuration object mapped to pack-mule.yaml.
 */
public record PackMuleConfig(
    DefaultsConfig defaults,
    List<VersionConfig> runtime,
    List<VersionConfig> jdk,
    List<TriggerConfig> triggers,
    List<CapabilityConfig> capabilities,
    Map<String, DependencyConfig> dependencies,
    Map<String, List<HookConfig>> hooks
) {
    public PackMuleConfig {
        runtime = runtime != null ? List.copyOf(runtime) : List.of();
        jdk = jdk != null ? List.copyOf(jdk) : List.of();
        triggers = triggers != null ? List.copyOf(triggers) : List.of();
        capabilities = capabilities != null ? List.copyOf(capabilities) : List.of();
        dependencies = dependencies != null ? Map.copyOf(dependencies) : Map.of();
        hooks = hooks != null ? Map.copyOf(hooks) : Map.of();
    }
}
