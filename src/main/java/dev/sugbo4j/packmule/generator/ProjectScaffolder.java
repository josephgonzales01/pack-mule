package dev.sugbo4j.packmule.generator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Orchestrates scanning template directories and delegating to
 * TemplateRenderer.
 *
 * Returns the list of generated file paths (relative to the output root) so
 * that callers (TUI, CLI, future MCP server) can report progress or return a
 * manifest without parsing stdout. No System.out/err is written here: stdout is
 * the transport for the future MCP server and must remain clean.
 */
public class ProjectScaffolder {

    private final TemplateRenderer renderer = new TemplateRenderer();

    public List<String> scaffold(Map<String, Object> context, File outputDir) throws Exception {
        List<String> generatedFiles = new ArrayList<>();

        generatedFiles.addAll(processDirectory("base", context, outputDir));

        String triggerId = (String) context.get("flowTrigger");
        if (triggerId != null && !triggerId.isEmpty()) {
            generatedFiles.addAll(processDirectory("triggers/" + triggerId, context, outputDir));
        }

        @SuppressWarnings("unchecked")
        List<String> capabilities = (List<String>) context.getOrDefault("capabilities", Collections.emptyList());
        for (String capId : capabilities) {
            if (capId != null && !capId.isEmpty()) {
                generatedFiles.addAll(processDirectory("capabilities/" + capId, context, outputDir));
            }
        }

        return generatedFiles;
    }

    private List<String> processDirectory(String templateSubDir, Map<String, Object> context, File targetRootDir)
            throws Exception {

        File externalDir = new File("templates/" + templateSubDir);
        if (externalDir.exists() && externalDir.isDirectory()) {
            return processExternalDirectory(externalDir, templateSubDir, context, targetRootDir);
        }

        URL resourceUrl = getClass().getResource("/templates/" + templateSubDir);
        if (resourceUrl == null) {
            return Collections.emptyList();
        }

        List<String> generated = new ArrayList<>();
        Path sourceDir;
        FileSystem fs = null;
        try {
            URI uri = resourceUrl.toURI();
            if ("jar".equals(uri.getScheme())) {
                fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
                sourceDir = fs.getPath("/templates/" + templateSubDir);
            } else {
                sourceDir = Paths.get(uri);
            }

            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        Path relativePath = sourceDir.relativize(file);
                        String relativePathStr = relativePath.toString().replace('\\', '/');

                        String evaluatedRelativePath = renderer.evaluateExpression(relativePathStr, context);

                        File outputFile = new File(targetRootDir, evaluatedRelativePath);
                        outputFile.getParentFile().mkdirs();

                        String templatePath = templateSubDir + "/" + relativePathStr;
                        String content = renderer.renderClasspathTemplate(templatePath, context);

                        Files.writeString(outputFile.toPath(), content);
                        generated.add(evaluatedRelativePath);

                    } catch (Exception e) {
                        throw new IOException("Failed to process file: " + file, e);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } finally {
            if (fs != null) {
                fs.close();
            }
        }
        return generated;
    }

    private List<String> processExternalDirectory(File externalDir, String templateSubDir, Map<String, Object> context, File targetRootDir) throws Exception {
        Path sourceDir = externalDir.toPath();
        List<String> generated = new ArrayList<>();
        Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    Path relativePath = sourceDir.relativize(file);
                    String relativePathStr = relativePath.toString().replace('\\', '/');

                    String evaluatedRelativePath = renderer.evaluateExpression(relativePathStr, context);

                    File outputFile = new File(targetRootDir, evaluatedRelativePath);
                    outputFile.getParentFile().mkdirs();

                    String content = renderer.renderFileTemplate(file, context);

                    Files.writeString(outputFile.toPath(), content);
                    generated.add(evaluatedRelativePath);

                } catch (Exception e) {
                    throw new IOException("Failed to process external file: " + file, e);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return generated;
    }
}
