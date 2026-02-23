package com.packmule.tui.screen;

import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.widgets.form.FormState;

import static dev.tamboui.toolkit.Toolkit.*;

public final class ProjectInfoScreen {

    // Form State
    private static final FormState FORM = FormState.builder()
            .textField("projectName", "my-mule-app")
            .textField("groupId", "com.example")
            .textField("outputDirectory", "/home/user/projects")
            .build();

    private static final String[] RUNTIMES = { "4.4.0", "4.5.0", "4.6.0" };
    private static int selectedRuntime = 2; // Default 4.6.0

    private static final String[] JAVA_VERSIONS = { "Java 8", "Java 11", "Java 17" };
    private static int selectedJava = 1; // Default Java 11

    private ProjectInfoScreen() {
    }

    public static Element render() {
        return column(
                header(),
                spacer(1),
                section1(),
                divider(),
                section2(),
                divider(),
                footer()).spacing(1).fill().addClass("root");
    }

    private static Element header() {
        String logo = "              ██████╗  █████╗  ██████╗██╗  ██╗\n" +
                "              ██╔══██╗██╔══██╗██╔════╝██║ ██╔╝\n" +
                "              ██████╔╝███████║██║     █████╔╝\n" +
                "              ██╔═══╝ ██╔══██║██║     ██╔═██╗\n" +
                "              ██║     ██║  ██║╚██████╗██║  ██╗\n" +
                "              ╚═╝     ╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝  MULE\n";

        return text(logo).addClass("header-title").fill();
    }

    private static Element section1() {
        return column(
                text("── Project Information ──────────────────────────────────────────").addClass("section-title"),
                spacer(1),
                column(
                        formField("Project Name", FORM.textField("projectName")).id("project-name").labelWidth(18),
                        formField("Group Id", FORM.textField("groupId")).id("group-id").labelWidth(18),
                        formField("Output Directory", FORM.textField("outputDirectory")).id("output-dir")
                                .labelWidth(18))
                        .spacing(0).fill())
                .spacing(0).fill();
    }

    private static Element section2() {
        return column(
                text("── Runtime and JDK Version ──────────────────────────────────────").addClass("section-title"),
                spacer(1),

                text("  Mule Runtime").addClass("radio-group-title"),
                row(() -> renderRadioGroup(RUNTIMES, selectedRuntime))
                        .focusable()
                        .onKeyEvent(event -> {
                            if (event.isLeft()) {
                                selectedRuntime = Math.max(0, selectedRuntime - 1);
                                return EventResult.HANDLED;
                            }
                            if (event.isRight()) {
                                selectedRuntime = Math.min(RUNTIMES.length - 1, selectedRuntime + 1);
                                return EventResult.HANDLED;
                            }
                            return EventResult.UNHANDLED;
                        }),

                spacer(1),

                text("  Java Version").addClass("radio-group-title"),
                row(() -> renderRadioGroup(JAVA_VERSIONS, selectedJava))
                        .focusable()
                        .onKeyEvent(event -> {
                            if (event.isLeft()) {
                                selectedJava = Math.max(0, selectedJava - 1);
                                return EventResult.HANDLED;
                            }
                            if (event.isRight()) {
                                selectedJava = Math.min(JAVA_VERSIONS.length - 1, selectedJava + 1);
                                return EventResult.HANDLED;
                            }
                            return EventResult.UNHANDLED;
                        }))
                .spacing(0).fill();
    }

    private static Element renderRadioGroup(String[] options, int selectedIndex) {
        Element[] elements = new Element[options.length * 2 + 1];
        elements[0] = spacer(2);
        for (int i = 0; i < options.length; i++) {
            boolean isSelected = (i == selectedIndex);
            String symbol = isSelected ? "(●) " : "( ) ";
            String clazz = isSelected ? "radio-selected" : "radio-unselected";
            elements[i * 2 + 1] = text(symbol + options[i]).addClass(clazz);
            elements[i * 2 + 2] = spacer(3);
        }
        return row(elements);
    }

    private static Element divider() {
        return text("──────────────────────────────────────────────────────────────────").addClass("divider");
    }

    private static Element footer() {
        return row(
                spacer(2),
                badge("[↑↓←→]", " Move "),
                spacer(1),
                badge("[Tab]", " Next Field "),
                spacer(1),
                badge("[Space]", " Toggle "),
                spacer(1),
                badge("[N]", " Next "),
                spacer(1),
                badge("[Q]", " Quit "));
    }

    private static Element badge(String key, String desc) {
        return row(
                text(key).addClass("footer-badge"),
                text(desc).addClass("footer-dim"));
    }
}
