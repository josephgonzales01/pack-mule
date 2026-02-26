package dev.sugbo4j.packmule.tui;

import dev.tamboui.style.Color;

/**
 * Semantic color theme for Pack Mule TUI.
 * Uses the Amber / Warm Phosphor color palette.
 * 
 * Color hierarchy:
 * - #f5b942 (brightest) - Header, section titles, selected items, active focus
 * - #c8962a (mid) - Labels, secondary text
 * - #f0d080 (dim) - Input text, subtle elements
 * - #4a3000 / #150f00 (darkest) - Borders, backgrounds
 */
public record Theme(
        Color primary, // Brightest accent - #f5b942
        Color primaryDim, // Mid tone - #c8962a
        Color text, // Input text - #f0d080
        Color textDim, // Subtle text - #4a3000
        Color background, // Screen background - #0d0900
        Color inputBg, // Input field background - #150f00
        Color inputBorder, // Input field border - #4a3000
        Color keyBadgeBg, // Footer key badge background - #1a1100
        Color keyBadgeBorder // Footer key badge border - #4a3000
) {

    /**
     * The default Amber / Warm Phosphor theme for Pack Mule.
     */
    public static final Theme AMBER = new Theme(
            Color.rgb(0xf5, 0xb9, 0x42), // primary - brightest accent
            Color.rgb(0xc8, 0x96, 0x2a), // primaryDim - mid/body
            Color.rgb(0xf0, 0xd0, 0x80), // text - input text, subtle
            Color.rgb(0x4a, 0x30, 0x00), // textDim - darkest subtle
            Color.rgb(0x0d, 0x09, 0x00), // background - screen bg
            Color.rgb(0x15, 0x0f, 0x00), // inputBg - input field bg
            Color.rgb(0x4a, 0x30, 0x00), // inputBorder - input border
            Color.rgb(0x1a, 0x11, 0x00), // keyBadgeBg - footer key bg
            Color.rgb(0x4a, 0x30, 0x00) // keyBadgeBorder - footer key border
    );
}
