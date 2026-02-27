package dev.sugbo4j.packmule.tui;

import dev.sugbo4j.packmule.model.ProjectConfig;
import dev.sugbo4j.packmule.model.ProjectTriggerAndCapabilities;
import dev.sugbo4j.packmule.model.QueueType;
import dev.tamboui.toolkit.element.Element;

import java.util.ArrayList;
import java.util.List;

import static dev.tamboui.toolkit.Toolkit.*;

/**
 * Capabilities Screen for Pack Mule TUI.
 * Second screen containing Queue Type selection (conditional) and Additional
 * Capabilities.
 */
public class CapabilitiesScreen {

    /**
     * Focus areas for keyboard navigation.
     */
    public enum FocusArea {
        QUEUE_TYPE, // Only active when trigger = Messaging/Queue
        CAPABILITIES // Always active
    }

    private final ProjectConfig config;
    private FocusArea focusArea;
    private int focusedQueueTypeIndex = 0;
    private int focusedCapabilityIndex = 0;

    // ASCII art banner for PACK MULE (4x scale effect) - same as Screen 1
    private static final String[] BANNER_LINES = {
            "  ██████╗  █████╗  ██████╗██╗  ██╗      ███╗   ███╗██╗   ██╗██╗     ███████╗",
            "  ██╔══██╗██╔══██╗██╔════╝██║ ██╔╝      ████╗ ████║██║   ██║██║     ██╔════╝",
            "  ██████╔╝███████║██║     █████╔╝ ████╗ ██╔████╔██║██║   ██║██║     █████╗  ",
            "  ██╔═══╝ ██╔══██║██║     ██╔═██╗ ╚════╝██║╚██╔╝██║██║   ██║██║     ██╔══╝  ",
            "  ██║     ██║  ██║╚██████╗██║  ██╗      ██║ ╚═╝ ██║╚██████╔╝███████╗███████╗",
            "  ╚═╝     ╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝      ╚═╝     ╚═╝ ╚═════╝ ╚══════╝╚══════╝"
    };

    public CapabilitiesScreen(ProjectConfig config) {
        this.config = config;
        // Set initial focus based on whether queue type section is visible
        this.focusArea = isQueueTypeSectionVisible() ? FocusArea.QUEUE_TYPE : FocusArea.CAPABILITIES;
    }

    /**
     * Check if the Queue Type section should be displayed.
     */
    public boolean isQueueTypeSectionVisible() {
        return "Messaging / Queue".equals(config.getTrigger());
    }

    /**
     * Get the current focus area.
     */
    public FocusArea getFocusArea() {
        return focusArea;
    }

    /**
     * Move focus to the next field.
     */
    public void focusNext() {
        if (isQueueTypeSectionVisible()) {
            focusArea = (focusArea == FocusArea.QUEUE_TYPE) ? FocusArea.CAPABILITIES : FocusArea.QUEUE_TYPE;
        }
        // If queue type section is not visible, focus stays on CAPABILITIES
    }

    /**
     * Move focus to the previous field.
     */
    public void focusPrevious() {
        focusNext(); // Same behavior for two-section screen
    }

    /**
     * Move up within the current list.
     */
    public void moveUp() {
        if (focusArea == FocusArea.QUEUE_TYPE) {
            int idx = config.getQueueTypeIndex();
            int queueTypeCount = QueueType.values().length;
            if (idx == -1) {
                focusedQueueTypeIndex = queueTypeCount - 1;
            } else {
                focusedQueueTypeIndex = (idx - 1 + queueTypeCount) % queueTypeCount;
            }
            config.setQueueTypeByIndex(focusedQueueTypeIndex);
        } else {
            // Move up in capabilities list
            focusedCapabilityIndex = (focusedCapabilityIndex - 1
                    + ProjectTriggerAndCapabilities.CAPABILITY_LABELS.length)
                    % ProjectTriggerAndCapabilities.CAPABILITY_LABELS.length;
        }
    }

    /**
     * Move down within the current list.
     */
    public void moveDown() {
        if (focusArea == FocusArea.QUEUE_TYPE) {
            int idx = config.getQueueTypeIndex();
            int queueTypeCount = QueueType.values().length;
            if (idx == -1) {
                focusedQueueTypeIndex = 0;
            } else {
                focusedQueueTypeIndex = (idx + 1) % queueTypeCount;
            }
            config.setQueueTypeByIndex(focusedQueueTypeIndex);
        } else {
            // Move down in capabilities list
            focusedCapabilityIndex = (focusedCapabilityIndex + 1)
                    % ProjectTriggerAndCapabilities.CAPABILITY_LABELS.length;
        }
    }

    /**
     * Toggle the currently focused capability.
     */
    public void toggleCurrentCapability() {
        if (focusArea == FocusArea.CAPABILITIES) {
            String key = ProjectTriggerAndCapabilities.getCapabilityKey(focusedCapabilityIndex);
            if (key != null) {
                config.toggleCapability(key);
            }
        }
    }

    /**
     * Check if generation is allowed.
     * Generation is blocked if Queue Type is required but not selected.
     */
    public boolean canGenerate() {
        if (isQueueTypeSectionVisible()) {
            return config.getQueueTypeIndex() != -1;
        }
        return true;
    }

    /**
     * Check if currently focused on queue type section.
     */
    public boolean isQueueTypeFocused() {
        return focusArea == FocusArea.QUEUE_TYPE;
    }

    /**
     * Check if currently focused on capabilities section.
     */
    public boolean isCapabilitiesFocused() {
        return focusArea == FocusArea.CAPABILITIES;
    }

    /**
     * Render the complete screen.
     */
    public Element render() {
        var t = Theme.AMBER;

        List<Element> elements = new ArrayList<>();
        elements.add(renderHeader(t));
        elements.add(text(""));

        // Conditionally render Queue Type section
        if (isQueueTypeSectionVisible()) {
            elements.add(renderQueueTypeSection(t));
            elements.add(text(""));
        }

        // Always render Capabilities section
        elements.add(renderCapabilitiesSection(t));
        elements.add(text(""));
        elements.add(renderFooter(t));

        return column(elements.toArray(Element[]::new)).id("capabilities-screen");
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
     * Render Section 4: Queue Type (vertical radio list).
     */
    private Element renderQueueTypeSection(Theme t) {
        List<Element> elements = new ArrayList<>();

        elements.add(renderSectionTitle("      Queue Type ", t));
        elements.add(text(""));
        elements.add(text("  Select the messaging technology:").fg(t.primaryDim()));
        elements.add(text(""));

        boolean isListFocused = (focusArea == FocusArea.QUEUE_TYPE);
        int selectedIndex = config.getQueueTypeIndex();

        QueueType[] queueTypes = QueueType.values();
        for (int i = 0; i < queueTypes.length; i++) {
            boolean isSelected = (i == selectedIndex);
            String marker = isSelected ? "(●) " : "( ) ";
            QueueType queueType = queueTypes[i];
            String displayName = queueType.getDisplayName();
            String description = queueType.getDescription();

            String optText;
            if (description != null && !description.isEmpty()) {
                optText = "  " + marker + displayName + "  (" + description + ")";
            } else {
                optText = "  " + marker + displayName;
            }

            if (isListFocused && isSelected) {
                elements.add(text(optText).fg(t.primary()).bold());
            } else if (isSelected) {
                elements.add(text(optText).fg(t.primary()));
            } else {
                elements.add(text(optText).fg(t.text()));
            }
        }

        elements.add(text(""));
        elements.add(renderDivider(t));

        return column(elements.toArray(Element[]::new));
    }

    /**
     * Render Section 5: Additional Capabilities (vertical checkbox list).
     */
    private Element renderCapabilitiesSection(Theme t) {
        List<Element> elements = new ArrayList<>();

        elements.add(renderSectionTitle("      Additional Capabilities ", t));
        elements.add(text(""));
        elements.add(text("  Select connectors to include:").fg(t.primaryDim()));
        elements.add(text(""));

        boolean isListFocused = (focusArea == FocusArea.CAPABILITIES);

        for (int i = 0; i < ProjectTriggerAndCapabilities.CAPABILITY_LABELS.length; i++) {
            String key = ProjectTriggerAndCapabilities.CAPABILITY_KEYS[i];
            String label = ProjectTriggerAndCapabilities.CAPABILITY_LABELS[i];
            String description = ProjectTriggerAndCapabilities.CAPABILITY_DESCRIPTIONS[i];

            boolean isChecked = config.hasCapability(key);
            boolean isFocused = (isListFocused && i == focusedCapabilityIndex);

            // Check if this capability is implied by trigger (Anypoint MQ case)
            boolean isImpliedByTrigger = "anypoint-mq".equals(key) && config.isAnypointMQFromTrigger();

            String marker = isChecked ? "[✓] " : "[ ] ";
            String displayLabel = label;
            String note = "";

            if (isImpliedByTrigger && !config.hasCapability(key)) {
                // Pre-check and add note
                marker = "[✓] ";
                note = "  (added by trigger)";
            } else if (description != null && !description.isEmpty()) {
                note = "  (" + description + ")";
            }

            String optText = "  " + marker + displayLabel + note;

            if (isFocused && isChecked) {
                elements.add(text(optText).fg(t.primary()).bold());
            } else if (isFocused) {
                elements.add(text(optText).fg(t.primary()));
            } else if (isChecked) {
                elements.add(text(optText).fg(t.primary()));
            } else {
                elements.add(text(optText).fg(t.text()));
            }
        }

        elements.add(text(""));
        elements.add(renderDivider(t));

        return column(elements.toArray(Element[]::new));
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
     * Render the footer navigation help bar.
     */
    private Element renderFooter(Theme t) {
        boolean canGen = canGenerate();

        return row(
                text("  "),
                renderKeyBadge("↑↓", t),
                text(" Move  ").fg(t.textDim()),
                renderKeyBadge("Space", t),
                text(" Toggle / Select  ").fg(t.textDim()),
                canGen ? renderKeyBadge("G", t) : text("[G]").fg(t.textDim()),
                text(" Generate  ").fg(t.textDim()),
                renderKeyBadge("B", t),
                text(" Back  ").fg(t.textDim()),
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
