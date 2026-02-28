package dev.sugbo4j.packmule.generator;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Renders templates and template filenames using FreeMarker.
 */
public class TemplateRenderer {

    private final Mustache.Compiler compiler;

    public TemplateRenderer() {
        // We configure Mustache to not escape HTML/XML characters by default
        compiler = Mustache.compiler().escapeHTML(false);
    }

    /**
     * Replaces expressions in a string based on the context.
     * e.g., "{{projectName}}-main.xml" -> "customer-order-api-main.xml"
     */
    public String evaluateExpression(String expression, Map<String, Object> context) {
        Template template = compiler.compile(expression);
        return template.execute(context);
    }

    /**
     * Renders a template from the classpath with the given model map.
     */
    public String renderClasspathTemplate(String templatePath, Map<String, Object> context) throws Exception {
        String fullPath = templatePath.startsWith("/") ? templatePath : "/templates/" + templatePath;
        // Read file from classpath
        try (InputStream is = getClass().getResourceAsStream(fullPath)) {
            if (is == null) {
                throw new IllegalArgumentException("Template not found: " + fullPath);
            }
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                Template template = compiler.compile(reader);
                return template.execute(context);
            }
        }
    }
}
