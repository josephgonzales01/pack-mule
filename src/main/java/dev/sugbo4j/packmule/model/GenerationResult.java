package dev.sugbo4j.packmule.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Immutable result of a project generation run.
 *
 * Returned by GenerationService so that any frontend (TUI, CLI, future MCP server)
 * can inspect what was produced without parsing stdout. This decouples the engine's
 * output from how it is presented to the user or agent.
 */
public record GenerationResult(
        Path projectDir,
        List<String> generatedFiles,
        List<Map<String, String>> resolvedDependencies
) {
}
