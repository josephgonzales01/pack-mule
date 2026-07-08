package dev.sugbo4j.packmule.generator;

import dev.sugbo4j.packmule.model.GenerationResult;
import dev.sugbo4j.packmule.model.ProjectConfig;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UI-agnostic orchestration of project generation.
 *
 * This is the single entry point that any frontend (TUI, CLI, future MCP
 * server) should call. It builds the template context map, resolves
 * dependencies via the catalog in pack-mule.yaml, delegates to
 * ProjectScaffolder, and returns a structured GenerationResult.
 *
 * Keep this class free of any I/O side effects (no System.out/err, no
 * framework imports). The caller decides how to present the result.
 */
public class GenerationService {

    private final ProjectScaffolder scaffolder = new ProjectScaffolder();
    private final DependencyResolver dependencyResolver = new DependencyResolver();

    public GenerationResult generate(ProjectConfig config, Path outputBaseDir) throws Exception {
        Map<String, Object> context = buildContext(config);

        List<Map<String, String>> deps = dependencyResolver.resolveDependencies(config);
        context.put("selectedDependencies", deps);

        File outputDir = outputBaseDir.resolve(config.getProjectName()).toFile();
        List<String> generatedFiles = scaffolder.scaffold(context, outputDir);

        return new GenerationResult(outputDir.toPath(), generatedFiles, deps);
    }

    private Map<String, Object> buildContext(ProjectConfig config) {
        Map<String, Object> context = new HashMap<>();
        context.put("projectName", config.getProjectName());
        context.put("groupId", config.getGroupId());
        context.put("muleVersion", config.getMuleRuntime());
        context.put("javaVersion", config.getJavaVersion());
        context.put("flowTrigger", config.getTrigger());

        if (config.getQueueType() != null) {
            context.put("queueType", config.getQueueType());
        }

        context.put("capabilities", config.getCapabilities());

        context.put("port", "8081");
        return context;
    }
}
