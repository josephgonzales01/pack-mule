package dev.sugbo4j.packmule;

import dev.sugbo4j.packmule.model.ProjectConfig;
import dev.sugbo4j.packmule.tui.CapabilitiesScreen;
import dev.sugbo4j.packmule.tui.ProjectInfoScreen;
import dev.sugbo4j.packmule.tui.Theme;
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

        // Q - Quit/Cancel (works on both screens)
        if (event.isCharIgnoreCase('q')) {
            if (currentScreen == 1 && projectInfoScreen.isTextFieldFocused()) {
                // Don't quit if in text field on screen 1
            } else {
                quit();
                return EventResult.HANDLED;
            }
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
        // Tab / Shift+Tab — Navigate focus
        if (event.isFocusNext() || event.isKey(KeyCode.TAB)) {
            if (event.hasShift()) {
                projectInfoScreen.focusPrevious();
            } else {
                projectInfoScreen.focusNext();
            }
            return EventResult.HANDLED;
        }
        if (event.isFocusPrevious()) {
            projectInfoScreen.focusPrevious();
            return EventResult.HANDLED;
        }

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

        // n / N - Next (if trigger selected)
        if (event.isCharIgnoreCase('n')) {
            if (config.getTriggerIndex() != -1) {
                currentScreen = 2;
                return EventResult.HANDLED;
            }
        }

        // c — Clear current text field
        if (event.isCharIgnoreCase('c') && projectInfoScreen.isTextFieldFocused()) {
            projectInfoScreen.clearCurrentField();
            return EventResult.HANDLED;
        }

        // Backspace — Delete char in text fields
        if (event.code() == KeyCode.BACKSPACE) {
            projectInfoScreen.handleBackspace();
            return EventResult.HANDLED;
        }

        // Character input for text fields
        if (projectInfoScreen.isTextFieldFocused()) {
            char c = event.character();
            if (c >= 32 && c < 127) {
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
        // Tab / Shift+Tab — Navigate between sections
        if (event.isFocusNext() || event.isKey(KeyCode.TAB)) {
            if (event.hasShift()) {
                capabilitiesScreen.focusPrevious();
            } else {
                capabilitiesScreen.focusNext();
            }
            return EventResult.HANDLED;
        }
        if (event.isFocusPrevious()) {
            capabilitiesScreen.focusPrevious();
            return EventResult.HANDLED;
        }

        // Arrow keys — Up/Down navigate within lists
        if (event.isUp()) {
            capabilitiesScreen.moveUp();
            return EventResult.HANDLED;
        }
        if (event.isDown()) {
            capabilitiesScreen.moveDown();
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

        // b / B - Back to screen 1
        if (event.isCharIgnoreCase('b')) {
            currentScreen = 1;
            return EventResult.HANDLED;
        }

        // g / G - Generate project
        if (event.isCharIgnoreCase('g')) {
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
        // TODO: Implement actual project generation logic
        System.out.println("\n=== Project Configuration ===");
        System.out.println("Project Name: " + config.getProjectName());
        System.out.println("Group ID: " + config.getGroupId());
        System.out.println("Output Directory: " + config.getOutputDirectory());
        System.out.println("Mule Runtime: " + config.getMuleRuntime());
        System.out.println("Java Version: " + config.getJavaVersion());
        System.out.println("Trigger: " + config.getTrigger());

        if (config.getQueueType() != null) {
            System.out.println("Queue Type: " + config.getQueueType());
        }

        System.out.println("Capabilities: " + String.join(", ", config.getCapabilities()));
        System.out.println("=============================\n");

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
