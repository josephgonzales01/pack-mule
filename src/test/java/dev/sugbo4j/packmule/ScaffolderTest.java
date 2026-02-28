package dev.sugbo4j.packmule;

import dev.sugbo4j.packmule.generator.ProjectScaffolder;
import dev.sugbo4j.packmule.model.ProjectConfig;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ScaffolderTest {
    @Test
    public void testScaffolding() throws Exception {
        System.out.println("Running test generator...");
        ProjectScaffolder scaffolder = new ProjectScaffolder();

        Map<String, Object> context = new HashMap<>();
        context.put("projectName", "test-project");
        context.put("groupId", "com.test");
        context.put("muleVersion", "4.6.0");
        context.put("port", "8085");
        context.put("flowTrigger", "HTTP Listener");
        context.put("capabilities", Arrays.asList("Database (JDBC)"));

        // Add dummy dependencies
        Map<String, String> dep1 = new HashMap<>();
        dep1.put("groupId", "org.mule.connectors");
        dep1.put("artifactId", "mule-http-connector");
        dep1.put("version", "1.9.0");

        Map<String, String> dep2 = new HashMap<>();
        dep2.put("groupId", "org.mule.connectors");
        dep2.put("artifactId", "mule-db-connector");
        dep2.put("version", "1.14.7");

        context.put("selectedDependencies", Arrays.asList(dep1, dep2));

        File outputDir = new File("c:/tmp/test-output");
        if (outputDir.exists()) {
            deleteDirectory(outputDir);
        }
        outputDir.mkdirs();

        scaffolder.scaffold(context, outputDir);
        System.out.println("Test generation finished at: " + outputDir.getAbsolutePath());
    }

    private static void deleteDirectory(File dir) {
        File[] allContents = dir.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }
}
