package dev.sugbo4j.packmule.validator;

import dev.sugbo4j.packmule.model.config.CapabilityConfig;
import dev.sugbo4j.packmule.model.config.PackMuleConfig;
import dev.sugbo4j.packmule.model.config.TriggerConfig;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates that pack-mule.yaml is internally consistent with the template
 * directories and the dependency catalog.
 *
 * Checks performed:
 *   1. Every trigger ID has a matching templates/triggers/{id}/ directory
 *      (external filesystem override or bundled classpath resource).
 *   2. Every capability ID has a matching templates/capabilities/{id}/ directory.
 *   3. Every dependency key referenced by a trigger or capability exists in the
 *      dependencies catalog.
 *   4. (warning) Dependency catalog entries never referenced by any trigger or
 *      capability.
 *
 * Mismatched IDs silently skip generation at runtime, so this validator is run
 * at startup to fail fast with an actionable message instead.
 */
public final class ConfigValidator {

    private ConfigValidator() {
    }

    /**
     * Result of a validation run. Errors are fatal; warnings are informational.
     */
    public static final class Result {
        private final List<String> errors = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();

        public List<String> errors() {
            return List.copyOf(errors);
        }

        public List<String> warnings() {
            return List.copyOf(warnings);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        void addError(String message) {
            errors.add(message);
        }

        void addWarning(String message) {
            warnings.add(message);
        }
    }

    /**
     * Validate the given configuration against its templates and catalog.
     *
     * @param config the loaded PackMuleConfig
     * @return a Result containing any errors and warnings
     */
    public static Result validate(PackMuleConfig config) {
        Result result = new Result();

        Set<String> catalogKeys = config.dependencies() != null
                ? new HashSet<>(config.dependencies().keySet())
                : new HashSet<>();

        Set<String> referencedKeys = new HashSet<>();

        for (TriggerConfig trigger : config.triggers()) {
            String id = trigger.id();
            if (id == null || id.isEmpty()) {
                result.addError("Trigger with missing id: " + trigger);
                continue;
            }
            if (!templateDirectoryExists("triggers/" + id)) {
                result.addError(
                        "Trigger '" + id + "' has no template directory: templates/triggers/" + id + "/");
            }
            for (String depKey : trigger.dependencies()) {
                referencedKeys.add(depKey);
                if (!catalogKeys.contains(depKey)) {
                    result.addError("Trigger '" + id + "' references unknown dependency key '" + depKey
                            + "' (not declared in the dependencies catalog)");
                }
            }
        }

        for (CapabilityConfig cap : config.capabilities()) {
            String id = cap.id();
            if (id == null || id.isEmpty()) {
                result.addError("Capability with missing id: " + cap);
                continue;
            }
            if (!templateDirectoryExists("capabilities/" + id)) {
                result.addError("Capability '" + id
                        + "' has no template directory: templates/capabilities/" + id + "/");
            }
            for (String depKey : cap.dependencies()) {
                referencedKeys.add(depKey);
                if (!catalogKeys.contains(depKey)) {
                    result.addError("Capability '" + id + "' references unknown dependency key '" + depKey
                            + "' (not declared in the dependencies catalog)");
                }
            }
        }

        for (String catalogKey : catalogKeys) {
            if (!referencedKeys.contains(catalogKey)) {
                result.addWarning("Dependency catalog entry '" + catalogKey
                        + "' is not referenced by any trigger or capability");
            }
        }

        return result;
    }

    /**
     * Check whether a template subdirectory exists, mirroring the resolution
     * priority used by ProjectScaffolder: external filesystem first, then the
     * bundled classpath.
     */
    private static boolean templateDirectoryExists(String templateSubDir) {
        File externalDir = new File("templates/" + templateSubDir);
        if (externalDir.exists() && externalDir.isDirectory()) {
            return true;
        }

        URL resourceUrl = ConfigValidator.class.getResource("/templates/" + templateSubDir);
        return resourceUrl != null;
    }
}
