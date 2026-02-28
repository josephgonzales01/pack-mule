package dev.sugbo4j.packmule.generator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Orchestrates scanning template directories and delegating to
 * TemplateRenderer.
 */
public class ProjectScaffolder {

    private final TemplateRenderer renderer = new TemplateRenderer();

    public void scaffold(Map<String, Object> context, File outputDir) throws Exception {
        System.out.println("Starting generation in: " + outputDir.getAbsolutePath());

        // 1. Process /templates/base
        processDirectory("base", context, outputDir);

        // 2. Process Trigger
        String triggerStr = (String) context.get("flowTrigger");
        if (triggerStr != null && !triggerStr.isEmpty()) {
            // Find trigger id matching label (simulating DB lookup, normally ID is passed
            // directly)
            String triggerId = triggerStr.contains("HTTP") ? "HTTP_LISTENER"
                    : triggerStr.contains("Schedule") ? "SCHEDULER" : "ANYPOINT_MQ";
            processDirectory("triggers/" + triggerId, context, outputDir);
        }

        // 3. Process Capabilities
        @SuppressWarnings("unchecked")
        List<String> capabilities = (List<String>) context.getOrDefault("capabilities", Collections.emptyList());
        for (String capability : capabilities) {
            String capId = capability.contains("Database") ? "DATABASE"
                    : capability.contains("Salesforce") ? "SALESFORCE" : null;
            if (capId != null) {
                processDirectory("capabilities/" + capId, context, outputDir);
            }
        }

        System.out.println("Generation complete!");
    }

    private void processDirectory(String templateSubDir, Map<String, Object> context, File targetRootDir)
            throws Exception {
        URL resourceUrl = getClass().getResource("/templates/" + templateSubDir);
        if (resourceUrl == null) {
            System.out.println("Directory not found (skipping): /templates/" + templateSubDir);
            return;
        }

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
                        // The relative path within the template sub dir
                        Path relativePath = sourceDir.relativize(file);
                        String relativePathStr = relativePath.toString().replace('\\', '/');

                        // Evaluate filename and directory path via Mustache
                        String evaluatedRelativePath = renderer.evaluateExpression(relativePathStr, context);

                        File outputFile = new File(targetRootDir, evaluatedRelativePath);
                        outputFile.getParentFile().mkdirs();

                        // Render the content
                        String templatePath = templateSubDir + "/" + relativePathStr;
                        String content = renderer.renderClasspathTemplate(templatePath, context);

                        Files.writeString(outputFile.toPath(), content);
                        System.out.println("  -> Created: " + evaluatedRelativePath);

                    } catch (Exception e) {
                        e.printStackTrace();
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
    }
}
