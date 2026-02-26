package dev.sugbo4j.packmule.tui;

import dev.sugbo4j.packmule.model.ProjectConfig;
import dev.tamboui.toolkit.element.Element;

import java.util.ArrayList;
import java.util.List;

import static dev.tamboui.toolkit.Toolkit.*;

/**
 * Project Information Screen for Pack Mule TUI.
 * First screen containing project name, group ID, output directory,
 * and runtime/JDK version selection via radio buttons.
 */
public class ProjectInfoScreen {

    /**
     * Focus areas for keyboard navigation.
     */
    public enum FocusArea {
        PROJECT_NAME, GROUP_ID, OUTPUT_DIRECTORY,
        MULE_RUNTIME, JAVA_VERSION
    }

    private final ProjectConfig config;
    private FocusArea focusArea = FocusArea.PROJECT_NAME;

    // ASCII art banner for PACK MULE (4x scale effect)
    private static final String[] BANNER_LINES = {
            "  ██████╗  █████╗  ██████╗██╗  ██╗      ███╗   ███╗██╗   ██╗██╗     ███████╗",
            "  ██╔══██╗██╔══██╗██╔════╝██║ ██╔╝      ████╗ ████║██║   ██║██║     ██╔════╝",
            "  ██████╔╝███████║██║     █████╔╝ ████╗ ██╔████╔██║██║   ██║██║     █████╗  ",
            "  ██╔═══╝ ██╔══██║██║     ██╔═██╗ ╚════╝██║╚██╔╝██║██║   ██║██║     ██╔══╝  ",
            "  ██║     ██║  ██║╚██████╗██║  ██╗      ██║ ╚═╝ ██║╚██████╔╝███████╗███████╗",
            "  ╚═╝     ╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝      ╚═╝     ╚═╝ ╚═════╝ ╚══════╝╚══════╝"
    };

    public ProjectInfoScreen(ProjectConfig config) {
        this.config = config;
    }

    public FocusArea getFocusArea() {
        return focusArea;
    }

    /**
     * Move focus to the next field.
     */
    public void focusNext() {
        var areas = FocusArea.values();
        int next = (focusArea.ordinal() + 1) % areas.length;
        focusArea = areas[next];
    }

    /**
     * Move focus to the previous field.
     */
    public void focusPrevious() {
        var areas = FocusArea.values();
        int prev = (focusArea.ordinal() - 1 + areas.length) % areas.length;
        focusArea = areas[prev];
    }

    /**
     * Handle character input for text fields.
     */
    public void handleChar(char c) {
        switch (focusArea) {
            case PROJECT_NAME -> config.setProjectName(config.getProjectName() + c);
            case GROUP_ID -> config.setGroupId(config.getGroupId() + c);
            case OUTPUT_DIRECTORY -> config.setOutputDirectory(config.getOutputDirectory() + c);
            default -> {
            }
        }
    }

    /**
     * Handle backspace for text fields.
     */
    public void handleBackspace() {
        switch (focusArea) {
            case PROJECT_NAME -> {
                String v = config.getProjectName();
                if (!v.isEmpty())
                    config.setProjectName(v.substring(0, v.length() - 1));
            }
            case GROUP_ID -> {
                String v = config.getGroupId();
                if (!v.isEmpty())
                    config.setGroupId(v.substring(0, v.length() - 1));
            }
            case OUTPUT_DIRECTORY -> {
                String v = config.getOutputDirectory();
                if (!v.isEmpty())
                    config.setOutputDirectory(v.substring(0, v.length() - 1));
            }
            default -> {
            }
        }
    }

    /**
     * Clear the current text field.
     */
    public void clearCurrentField() {
        switch (focusArea) {
            case PROJECT_NAME -> config.setProjectName("");
            case GROUP_ID -> config.setGroupId("");
            case OUTPUT_DIRECTORY -> config.setOutputDirectory("");
            default -> {
            }
        }
    }

    /**
     * Cycle radio button selection left (-1) or right (+1).
     */
    public void cycleOption(int direction) {
        switch (focusArea) {
            case MULE_RUNTIME -> {
                int idx = config.getMuleRuntimeIndex();
                int newIdx = (idx + direction + ProjectConfig.MULE_RUNTIMES.length)
                        % ProjectConfig.MULE_RUNTIMES.length;
                config.setMuleRuntimeByIndex(newIdx);
            }
            case JAVA_VERSION -> {
                int idx = config.getJavaVersionIndex();
                int newIdx = (idx + direction + ProjectConfig.JAVA_VERSIONS.length)
                        % ProjectConfig.JAVA_VERSIONS.length;
                config.setJavaVersionByIndex(newIdx);
            }
            default -> {
            }
        }
    }

    /**
     * Check if a text input field is currently focused.
     */
    public boolean isTextFieldFocused() {
        return focusArea == FocusArea.PROJECT_NAME
                || focusArea == FocusArea.GROUP_ID
                || focusArea == FocusArea.OUTPUT_DIRECTORY;
    }

    /**
     * Render the complete screen.
     */
    public Element render() {
        var t = Theme.AMBER;

        return column(
                renderHeader(t),
                text(""),
                renderProjectInfoSection(t),
                text(""),
                renderRuntimeSection(t),
                text(""),
                renderFooter(t)).id("project-info-screen");
    }

    /**
     * Render the PACK MULE header banner.
     */
    private Element renderHeader(Theme t) {
        List<Element> bannerElements = new ArrayList<>();
        for (String line : BANNER_LINES) {
            bannerElements.add(text(line).fg(t.primary()).bold().length(1));
        }
        return column(bannerElements.toArray(Element[]::new)).id("header");
    }

    /**
     * Render Section 1: Project Information with input fields.
     */
    private Element renderProjectInfoSection(Theme t) {
        return column(
                renderSectionTitle("     Project Information         ", t),
                text(""),
                renderInputRow("Project Name", config.getProjectName(), focusArea == FocusArea.PROJECT_NAME, t),
                renderInputRow("Group Id", config.getGroupId(), focusArea == FocusArea.GROUP_ID, t),
                renderInputRow("Output Directory", config.getOutputDirectory(), focusArea == FocusArea.OUTPUT_DIRECTORY,
                        t),
                text(""),
                renderDivider(t));
    }

    /**
     * Render Section 2: Runtime and JDK Version with radio buttons.
     */
    private Element renderRuntimeSection(Theme t) {
        return column(
                renderSectionTitle("      Runtime and JDK Version ", t),
                text(""),
                renderRadioGroup("Mule Runtime", ProjectConfig.MULE_RUNTIMES,
                        config.getMuleRuntimeIndex(), focusArea == FocusArea.MULE_RUNTIME, t),
                text(""),
                renderRadioGroup("Java Version", formatJavaVersions(),
                        config.getJavaVersionIndex(), focusArea == FocusArea.JAVA_VERSION, t),
                text(""),
                renderDivider(t));
    }

    /**
     * Format Java versions with "Java " prefix.
     */
    private String[] formatJavaVersions() {
        String[] versions = new String[ProjectConfig.JAVA_VERSIONS.length];
        for (int i = 0; i < ProjectConfig.JAVA_VERSIONS.length; i++) {
            versions[i] = "Java " + ProjectConfig.JAVA_VERSIONS[i];
        }
        return versions;
    }

    /**
     * Render a section title.
     */
    private Element renderSectionTitle(String title, Theme t) {
        return text(title).fg(t.primary()).bold();
    }

    /**
     * Render a horizontal divider line.
     */
    private Element renderDivider(Theme t) {
        return text("────────────────────────────────────────────────────────────────────")
                .fg(t.textDim());
    }

    /**
     * Render a labeled input field row.
     */
    private Element renderInputRow(String label, String value, boolean focused, Theme t) {
        String paddedLabel = String.format("  %-18s:", label);
        String displayValue = focused ? "[ " + value + "█ ]" : "[ " + value + " ]";

        return row(
                text(paddedLabel).fg(focused ? t.primaryDim() : t.textDim()).bold(),
                text(displayValue).fg(focused ? t.primary() : t.text()));
    }

    /**
     * Render a horizontal radio button group.
     */
    private Element renderRadioGroup(String label, String[] options, int selectedIndex, boolean focused, Theme t) {
        List<Element> parts = new ArrayList<>();

        // Label
        String paddedLabel = String.format("  %-18s", label);
        parts.add(text(paddedLabel).fg(t.primaryDim()).bold());

        // Radio options
        for (int i = 0; i < options.length; i++) {
            boolean selected = (i == selectedIndex);
            String marker = selected ? "(●) " : "( ) ";
            String optText = marker + options[i] + "   ";

            if (selected && focused) {
                parts.add(text(optText).fg(t.primary()).bold());
            } else if (selected) {
                parts.add(text(optText).fg(t.primary()));
            } else {
                parts.add(text(optText).fg(t.text()));
            }
        }

        // Navigation hint when focused
        if (focused) {
            parts.add(text("◀ ▶").fg(t.textDim()));
        }

        return row(parts.toArray(Element[]::new));
    }

    /**
     * Render the footer navigation help bar.
     */
    private Element renderFooter(Theme t) {
        return row(
                text("  "),
                renderKeyBadge("↑↓←→", t),
                text(" Move  ").fg(t.textDim()),
                renderKeyBadge("Tab", t),
                text(" Next Field  ").fg(t.textDim()),
                renderKeyBadge("c", t),
                text(" Clear  ").fg(t.textDim()),
                renderKeyBadge("Q", t),
                text(" Cancel").fg(t.textDim()),
                spacer()).length(1);
    }

    /**
     * Render a key badge for the footer.
     */
    private Element renderKeyBadge(String key, Theme t) {
        return text("[" + key + "]").fg(t.primaryDim());
    }
}
