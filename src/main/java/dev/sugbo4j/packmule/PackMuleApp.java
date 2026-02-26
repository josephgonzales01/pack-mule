package dev.sugbo4j.packmule;

import dev.sugbo4j.packmule.model.ProjectConfig;
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

    @Override
    protected void onStart() {
        projectInfoScreen = new ProjectInfoScreen(config);
    }

    @Override
    protected Element render() {
        if (projectInfoScreen == null) {
            return text("Loading...").fg(Theme.AMBER.primary());
        }

        return column(projectInfoScreen.render())
                .id("root")
                .focusable()
                .onKeyEvent(this::handleKeyEvent);
    }

    /**
     * Handle keyboard events for the current screen.
     */
    private EventResult handleKeyEvent(KeyEvent event) {
        if (event.isQuit()) {
            quit();
            return EventResult.HANDLED;
        }

        // Q - Quit/Cancel
        if (event.isCharIgnoreCase('q') && !projectInfoScreen.isTextFieldFocused()) {
            quit();
            return EventResult.HANDLED;
        }

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
            projectInfoScreen.focusPrevious();
            return EventResult.HANDLED;
        }
        if (event.isDown()) {
            projectInfoScreen.focusNext();
            return EventResult.HANDLED;
        }
        if (event.isLeft()) {
            projectInfoScreen.cycleOption(-1);
            return EventResult.HANDLED;
        }
        if (event.isRight()) {
            projectInfoScreen.cycleOption(1);
            return EventResult.HANDLED;
        }

        // c — Clear current text field (when not in a text field, do nothing)
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
     * Main entry point.
     */
    public static void main(String[] args) throws Exception {
        var app = new PackMuleApp();
        app.run();
    }
}
