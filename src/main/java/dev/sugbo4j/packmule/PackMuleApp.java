package dev.sugbo4j.packmule;

import dev.sugbo4j.packmule.generator.GenerationService;
import dev.sugbo4j.packmule.model.GenerationResult;
import dev.sugbo4j.packmule.model.ProjectConfig;
import dev.sugbo4j.packmule.model.config.ConfigurationLoader;
import dev.sugbo4j.packmule.model.config.PackMuleConfig;
import dev.sugbo4j.packmule.tui.CapabilitiesScreen;
import dev.sugbo4j.packmule.tui.ProjectInfoScreen;
import dev.sugbo4j.packmule.tui.Theme;
import dev.sugbo4j.packmule.validator.ConfigValidator;
import java.nio.file.Paths;
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
     * Generate the project by delegating to GenerationService.
     * Presentation (stdout) lives here; the engine returns a structured result.
     */
    private void generateProject() {
        System.out.println("\n=== Generating Project ===");
        try {
            GenerationService service = new GenerationService();
            GenerationResult result = service.generate(config, Paths.get(config.getOutputDirectory()));

            System.out.println("\nGenerated " + result.generatedFiles().size() + " file(s):");
            for (String file : result.generatedFiles()) {
                System.out.println("  -> " + file);
            }
            System.out.println("\nSuccessfully generated project at: " + result.projectDir().toAbsolutePath());
        } catch (Exception e) {
            System.err.println("\nFailed to generate project: " + e.getMessage());
            e.printStackTrace();
        }

        quit();
    }

    /**
     * Main entry point.
     */
    public static void main(String[] args) throws Exception {
        PackMuleConfig config = ConfigurationLoader.loadFromClasspath("/pack-mule.yaml");
        ConfigValidator.Result validation = ConfigValidator.validate(config);
        if (!validation.isValid()) {
            System.err.println("Configuration validation failed — fix the following before running Pack Mule:");
            for (String error : validation.errors()) {
                System.err.println("  ERROR: " + error);
            }
            for (String warning : validation.warnings()) {
                System.err.println("  WARN:  " + warning);
            }
            System.exit(1);
        }
        for (String warning : validation.warnings()) {
            System.out.println("WARN: " + warning);
        }

        var app = new PackMuleApp();
        app.run();
    }
}
