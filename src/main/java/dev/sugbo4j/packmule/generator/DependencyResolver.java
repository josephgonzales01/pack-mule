package dev.sugbo4j.packmule.generator;

import dev.sugbo4j.packmule.model.ProjectConfig;
import dev.sugbo4j.packmule.model.config.CapabilityConfig;
import dev.sugbo4j.packmule.model.config.DependencyConfig;
import dev.sugbo4j.packmule.model.config.TriggerConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves Maven dependencies from pack-mule.yaml based on selected capabilities and triggers.
 */
public class DependencyResolver {

    /**
     * Resolves all required dependencies for the given project configuration.
     * 
     * @param config the current project configuration
     * @return a list of maps representing dependency coordinates for JMustache
     */
    public List<Map<String, String>> resolveDependencies(ProjectConfig config) {
        List<Map<String, String>> resolvedDeps = new ArrayList<>();
        Map<String, DependencyConfig> catalog = config.getPackMuleConfig().dependencies();

        // 1. Resolve Trigger Dependencies
        TriggerConfig trigger = config.getTriggerConfig();
        if (trigger != null && trigger.dependencies() != null) {
            for (String depKey : trigger.dependencies()) {
                DependencyConfig dep = catalog.get(depKey);
                if (dep != null) {
                    resolvedDeps.add(mapDependency(dep));
                }
            }
        }

        // 2. Resolve Capability Dependencies
        List<String> selectedCapabilityIds = config.getCapabilities();
        for (CapabilityConfig cap : config.getAvailableCapabilities()) {
            if (selectedCapabilityIds.contains(cap.id()) && cap.dependencies() != null) {
                for (String depKey : cap.dependencies()) {
                    DependencyConfig dep = catalog.get(depKey);
                    if (dep != null) {
                        resolvedDeps.add(mapDependency(dep));
                    }
                }
            }
        }

        return resolvedDeps;
    }

    /**
     * Converts a DependencyConfig into a Map suitable for JMustache traversal.
     */
    private Map<String, String> mapDependency(DependencyConfig dep) {
        Map<String, String> map = new HashMap<>();
        map.put("groupId", dep.groupId());
        map.put("artifactId", dep.artifactId());
        
        if (dep.version() != null && !dep.version().isEmpty()) {
            map.put("version", dep.version());
        }
        
        if (dep.classifier() != null && !dep.classifier().isEmpty()) {
            map.put("classifier", dep.classifier());
        }
        
        return map;
    }
}
