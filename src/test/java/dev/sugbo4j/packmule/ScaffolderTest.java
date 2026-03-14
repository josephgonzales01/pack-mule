package dev.sugbo4j.packmule;

import dev.sugbo4j.packmule.generator.ProjectScaffolder;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        context.put("flowTrigger", "HTTP_LISTENER");
        context.put("capabilities", Arrays.asList("DATABASE"));

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
        
        // Assert that base templates were created
        File mainXml = new File(outputDir, "src/main/mule/test-project-main.xml");
        assertTrue(mainXml.exists(), "Base template main XML should be created");
        
        System.out.println("Test generation finished at: " + outputDir.getAbsolutePath());
    }

    @Test
    public void testExternalTemplates() throws Exception {
        // Create an external template directory
        File extBaseDir = new File("templates/base");
        extBaseDir.mkdirs();
        File extTemplate = new File(extBaseDir, "hello.txt");
        try (FileWriter fw = new FileWriter(extTemplate)) {
            fw.write("Hello {{projectName}} from external template!");
        }

        try {
            ProjectScaffolder scaffolder = new ProjectScaffolder();
            Map<String, Object> context = new HashMap<>();
            context.put("projectName", "ext-project");

            File outputDir = new File("c:/tmp/ext-test-output");
            if (outputDir.exists()) {
                deleteDirectory(outputDir);
            }
            outputDir.mkdirs();

            scaffolder.scaffold(context, outputDir);

            // Assert that external template was used
            File generatedFile = new File(outputDir, "hello.txt");
            assertTrue(generatedFile.exists(), "External template file should be rendered");

        } finally {
            // Clean up external templates
            deleteDirectory(new File("templates"));
        }
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
