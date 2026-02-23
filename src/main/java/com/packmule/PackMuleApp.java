package com.packmule;

import java.io.IOException;
import java.io.UncheckedIOException;

import dev.tamboui.css.engine.StyleEngine;
import dev.tamboui.toolkit.app.ToolkitRunner;
import dev.tamboui.tui.TuiConfig;
import com.packmule.tui.screen.ProjectInfoScreen;

public class PackMuleApp {

    public static void main(String[] args) throws Exception {
        StyleEngine styleEngine = StyleEngine.create();
        try {
            styleEngine.loadStylesheet("pack-mule", "/pack-mule.tcss");
            styleEngine.setActiveStylesheet("pack-mule");
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load CSS themes", e);
        }

        TuiConfig config = TuiConfig.builder().build();

        try (ToolkitRunner runner = ToolkitRunner.create(config)) {
            runner.styleEngine(styleEngine);
            runner.run(ProjectInfoScreen::render);
        }
    }
}
