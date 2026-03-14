package dev.sugbo4j.packmule;

import dev.sugbo4j.packmule.generator.ProjectScaffolder;
import dev.sugbo4j.packmule.generator.DependencyResolver;
import dev.sugbo4j.packmule.model.ProjectConfig;
import dev.sugbo4j.packmule.tui.CapabilitiesScreen;
import dev.sugbo4j.packmule.tui.ProjectInfoScreen;
import dev.sugbo4j.packmule.tui.Theme;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.event.EventResult;

import static dev.tamboui.toolkit.Toolkit.*;

/**
 * Pack Mule — MuleSoft Project Initializer TUI.
 *
 * A terminal user interface for scaffolding MuleSoft Anypoint projects,
 * inspired by Spring Initializr and powered by TamboUI.
 */
public class PackMuleApp extends ToolkitApp {

    private final ProjectConfig config = new ProjectConfig();
    private ProjectInfoScreen projectInfoScreen;
    private CapabilitiesScreen capabilitiesScreen;
    private int currentScreen = 1; // 1 = ProjectInfo, 2 = Capabilities

    @Override
    protected void onStart() {
        projectInfoScreen = new ProjectInfoScreen(config);
        capabilitiesScreen = new CapabilitiesScreen(config);
    }

    @Override
    protected Element render() {
        if (projectInfoScreen == null) {
            return text("Loading...").fg(Theme.AMBER.primary());
        }

        if (currentScreen == 1) {
            return column(projectInfoScreen.render())
                    .id("root")
                    .focusable()
                    .onKeyEvent(this::handleKeyEvent);
        } else {
            return column(capabilitiesScreen.render())
                    .id("root")
                    .focusable()
                    .onKeyEvent(this::handleKeyEvent);
        }
    }

    /**
     * Handle keyboard events for the current screen.
     */
    private EventResult handleKeyEvent(KeyEvent event) {
        if (event.isQuit()) {
            quit();
            return EventResult.HANDLED;
        }

        if (event.code() == KeyCode.ESCAPE) {
            quit();
            return EventResult.HANDLED;
        }

        // Screen 1 specific event handling
        if (currentScreen == 1) {
            return handleScreen1KeyEvent(event);
        }

        // Screen 2 specific event handling
        if (currentScreen == 2) {
            return handleScreen2KeyEvent(event);
        }

        return EventResult.UNHANDLED;
    }

    /**
     * Handle keyboard events for Screen 1 (Project Info).
     */
    private EventResult handleScreen1KeyEvent(KeyEvent event) {
        // Arrow keys — Up/Down navigate between fields, Left/Right cycle radio options
        if (event.isUp()) {
            if (projectInfoScreen.isVerticalListFocused()) {
                projectInfoScreen.cycleOption(-1);
            } else {
                projectInfoScreen.focusPrevious();
            }
            return EventResult.HANDLED;
        }
        if (event.isDown()) {
            if (projectInfoScreen.isVerticalListFocused()) {
                projectInfoScreen.cycleOption(1);
            } else {
                projectInfoScreen.focusNext();
            }
            return EventResult.HANDLED;
        }
        if (event.isLeft()) {
            if (!projectInfoScreen.isVerticalListFocused()) {
                projectInfoScreen.cycleOption(-1);
            }
            return EventResult.HANDLED;
        }
        if (event.isRight()) {
            if (!projectInfoScreen.isVerticalListFocused()) {
                projectInfoScreen.cycleOption(1);
            }
            return EventResult.HANDLED;
        }

        // Space - toggle selection in vertical lists
        if (event.isChar(' ') && projectInfoScreen.isVerticalListFocused()) {
            projectInfoScreen.toggleTrigger();
            return EventResult.HANDLED;
        }

        // Page Down - Next (if trigger selected)
        if (event.code() == KeyCode.PAGE_DOWN) {
            if (config.getTriggerIndex() != -1) {
                currentScreen = 2;
                capabilitiesScreen.resetFocus();
                return EventResult.HANDLED;
            }
        }

        // Backspace — Delete char in text fields
        if (event.code() == KeyCode.BACKSPACE) {
            projectInfoScreen.handleBackspace();
            return EventResult.HANDLED;
        }

        // Character input for text fields
        if (projectInfoScreen.isTextFieldFocused()) {
            char c = event.character();
            // Prevent terminal artifacts like ^H (0x08 == 8) from rendering when backspace
            // is passed through to character block.
            // 8 == backspace and 127 == DEL in ASCII
            if (c >= 32 && c < 127 && event.code() != KeyCode.BACKSPACE) {
                projectInfoScreen.handleChar(c);
                return EventResult.HANDLED;
            }
        }

        return EventResult.UNHANDLED;
    }

    /**
     * Handle keyboard events for Screen 2 (Capabilities).
     */
    private EventResult handleScreen2KeyEvent(KeyEvent event) {
        // Arrow keys — Up/Down navigate within lists, Left/Right swap focus sections
        if (event.isUp()) {
            capabilitiesScreen.moveUp();
            return EventResult.HANDLED;
        }
        if (event.isDown()) {
            capabilitiesScreen.moveDown();
            return EventResult.HANDLED;
        }
        if (event.isLeft()) {
            capabilitiesScreen.focusPrevious();
            return EventResult.HANDLED;
        }
        if (event.isRight()) {
            capabilitiesScreen.focusNext();
            return EventResult.HANDLED;
        }

        // Space - toggle capability or select queue type
        if (event.isChar(' ')) {
            if (capabilitiesScreen.isCapabilitiesFocused()) {
                capabilitiesScreen.toggleCurrentCapability();
            }
            // For queue type, selection happens automatically via up/down
            return EventResult.HANDLED;
        }

        // PAGE_UP - Back to screen 1
        if (event.code() == KeyCode.PAGE_UP) {
            currentScreen = 1;
            return EventResult.HANDLED;
        }

        // F9 - Generate project
        if (event.code() == KeyCode.F9) {
            if (capabilitiesScreen.canGenerate()) {
                generateProject();
                return EventResult.HANDLED;
            }
        }

        return EventResult.UNHANDLED;
    }

    /**
     * Generate the project based on current configuration.
     * This is a placeholder for the actual project generation logic.
     */
    private void generateProject() {
        System.out.println("\n=== Generating Project ===");
        try {
            // Build the context map
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

            // Resolve dependencies from pack-mule.yaml dynamically
            DependencyResolver dependencyResolver = new DependencyResolver();
            List<Map<String, String>> deps = dependencyResolver.resolveDependencies(config);
            context.put("selectedDependencies", deps);

            // Hardcode port for sample
            context.put("port", "8081");

            File outputDir = new File(config.getOutputDirectory(), config.getProjectName());

            ProjectScaffolder scaffolder = new ProjectScaffolder();
            scaffolder.scaffold(context, outputDir);

            System.out.println("\nSuccessfully generated project at: " + outputDir.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("\nFailed to generate project: " + e.getMessage());
            e.printStackTrace();
        }

        // Exit the application after generation
        quit();
    }

    /**
     * Main entry point.
     */
    public static void main(String[] args) throws Exception {
        var app = new PackMuleApp();
        app.run();
    }
}
