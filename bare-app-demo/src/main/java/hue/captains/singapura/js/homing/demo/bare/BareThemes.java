package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.PaletteProvision;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.server.ThemeRegistry;
import hue.captains.singapura.js.homing.theme.color.GlobalColorPalette;
import hue.captains.singapura.js.homing.theme.color.HomingVars;
import hue.captains.singapura.js.homing.theme.type.GlobalTypePalette;
import hue.captains.singapura.js.homing.theme.type.HomingFonts;

import java.util.List;
import java.util.Map;

/**
 * RFC 0066 — the bare app's two themes, and the registry that lists them.
 * Neither knows the studio exists: a theme is an identity and a
 * {@link GlobalColorPalette.Provision} — a value for every token the palette
 * declares — and that is all the framework asks of it.
 *
 * <p>Two on purpose: one palette proves the binding, two prove the switch.
 * Chalk is light and warm; Slate is dark and cool. Same spacing and radius,
 * because the scales are the same job under any skin — which is why Episode 2
 * takes them out of the colour palette.</p>
 */
public final class BareThemes {

    private BareThemes() {}

    /** Light — chalk on a warm board. */
    public record Chalk() implements Theme {
        public static final Chalk INSTANCE = new Chalk();
        @Override public String slug()  { return "chalk"; }
        @Override public String label() { return "Chalk"; }
        @Override public String inspiration() { return "Warm paper, graphite text, one terracotta accent."; }

        public record Palette() implements GlobalColorPalette.Provision<Chalk> {
            public static final Palette INSTANCE = new Palette();
            @Override public Chalk theme() { return Chalk.INSTANCE; }
            @Override public Map<CssVar, String> values() { return VALUES; }

            private static final Map<CssVar, String> VALUES = Map.ofEntries(
                    Map.entry(HomingVars.COLOR_SURFACE,          "#FBF7F0"),
                    Map.entry(HomingVars.COLOR_SURFACE_RAISED,   "#FFFFFF"),
                    Map.entry(HomingVars.COLOR_SURFACE_RECESSED, "#F1EBE0"),
                    Map.entry(HomingVars.COLOR_SURFACE_INVERTED, "#2B2622"),

                    Map.entry(HomingVars.COLOR_TEXT_PRIMARY,           "#2B2622"),
                    Map.entry(HomingVars.COLOR_TEXT_MUTED,             "#7A6F66"),
                    Map.entry(HomingVars.COLOR_TEXT_ON_INVERTED,       "#FBF7F0"),
                    Map.entry(HomingVars.COLOR_TEXT_ON_INVERTED_MUTED, "#C9BFB4"),
                    Map.entry(HomingVars.COLOR_TEXT_TITLE,             "#2B2622"),
                    Map.entry(HomingVars.COLOR_TEXT_LINK,              "#B5482B"),
                    Map.entry(HomingVars.COLOR_TEXT_LINK_HOVER,        "#8E3620"),

                    Map.entry(HomingVars.COLOR_BORDER,          "#E2D8CB"),
                    Map.entry(HomingVars.COLOR_BORDER_EMPHASIS, "#B5482B"),

                    Map.entry(HomingVars.COLOR_ACCENT,          "#C8552F"),
                    Map.entry(HomingVars.COLOR_ACCENT_EMPHASIS, "#A2401F"),
                    Map.entry(HomingVars.COLOR_ACCENT_ON,       "#FFFFFF"),

                    Map.entry(HomingVars.SPACE_1, "4px"),
                    Map.entry(HomingVars.SPACE_2, "8px"),
                    Map.entry(HomingVars.SPACE_3, "12px"),
                    Map.entry(HomingVars.SPACE_4, "16px"),
                    Map.entry(HomingVars.SPACE_5, "20px"),
                    Map.entry(HomingVars.SPACE_6, "24px"),
                    Map.entry(HomingVars.SPACE_7, "32px"),
                    Map.entry(HomingVars.SPACE_8, "40px"),

                    Map.entry(HomingVars.RADIUS_SM, "3px"),
                    Map.entry(HomingVars.RADIUS_MD, "6px"),
                    Map.entry(HomingVars.RADIUS_LG, "12px")
            );
        }

        /** System faces — the bare app has no typographic identity beyond the platform's. */
        public record Fonts() implements GlobalTypePalette.Provision<Chalk> {
            public static final Fonts INSTANCE = new Fonts();
            @Override public Chalk theme() { return Chalk.INSTANCE; }
            @Override public Map<CssVar, String> values() {
                return Map.of(HomingFonts.FONT_BODY, "system-ui, sans-serif", HomingFonts.FONT_DISPLAY, "system-ui, sans-serif", HomingFonts.FONT_MONO, "ui-monospace, monospace");
            }
        }
    }

    /** Dark — slate with a cold blue accent. */
    public record Slate() implements Theme {
        public static final Slate INSTANCE = new Slate();
        @Override public String slug()  { return "slate"; }
        @Override public String label() { return "Slate"; }
        @Override public String inspiration() { return "Wet slate, pale chalk lines, a cold blue accent."; }

        public record Palette() implements GlobalColorPalette.Provision<Slate> {
            public static final Palette INSTANCE = new Palette();
            @Override public Slate theme() { return Slate.INSTANCE; }
            @Override public Map<CssVar, String> values() { return VALUES; }

            private static final Map<CssVar, String> VALUES = Map.ofEntries(
                    Map.entry(HomingVars.COLOR_SURFACE,          "#1E2328"),
                    Map.entry(HomingVars.COLOR_SURFACE_RAISED,   "#272D33"),
                    Map.entry(HomingVars.COLOR_SURFACE_RECESSED, "#171B1F"),
                    Map.entry(HomingVars.COLOR_SURFACE_INVERTED, "#E8ECEF"),

                    Map.entry(HomingVars.COLOR_TEXT_PRIMARY,           "#E8ECEF"),
                    Map.entry(HomingVars.COLOR_TEXT_MUTED,             "#9AA5AE"),
                    Map.entry(HomingVars.COLOR_TEXT_ON_INVERTED,       "#1E2328"),
                    Map.entry(HomingVars.COLOR_TEXT_ON_INVERTED_MUTED, "#4E5860"),
                    Map.entry(HomingVars.COLOR_TEXT_TITLE,             "#FFFFFF"),
                    Map.entry(HomingVars.COLOR_TEXT_LINK,              "#7FB7E8"),
                    Map.entry(HomingVars.COLOR_TEXT_LINK_HOVER,        "#A9D0F2"),

                    Map.entry(HomingVars.COLOR_BORDER,          "#38414A"),
                    Map.entry(HomingVars.COLOR_BORDER_EMPHASIS, "#7FB7E8"),

                    Map.entry(HomingVars.COLOR_ACCENT,          "#4A90D9"),
                    Map.entry(HomingVars.COLOR_ACCENT_EMPHASIS, "#7FB7E8"),
                    Map.entry(HomingVars.COLOR_ACCENT_ON,       "#0F1418"),

                    Map.entry(HomingVars.SPACE_1, "4px"),
                    Map.entry(HomingVars.SPACE_2, "8px"),
                    Map.entry(HomingVars.SPACE_3, "12px"),
                    Map.entry(HomingVars.SPACE_4, "16px"),
                    Map.entry(HomingVars.SPACE_5, "20px"),
                    Map.entry(HomingVars.SPACE_6, "24px"),
                    Map.entry(HomingVars.SPACE_7, "32px"),
                    Map.entry(HomingVars.SPACE_8, "40px"),

                    Map.entry(HomingVars.RADIUS_SM, "3px"),
                    Map.entry(HomingVars.RADIUS_MD, "6px"),
                    Map.entry(HomingVars.RADIUS_LG, "12px")
            );
        }
/** The same system faces as Chalk — a shared provider would be the studio's answer. */        public record Fonts() implements GlobalTypePalette.Provision<Slate> {            public static final Fonts INSTANCE = new Fonts();            @Override public Slate theme() { return Slate.INSTANCE; }            @Override public Map<CssVar, String> values() { return Chalk.Fonts.INSTANCE.values(); }        }
    }

    /**
     * The registry: two themes, a colour and a type provision each. The first theme
     * listed is the default the page is served under. {@code priors()} is
     * derived from the provisions — colour, then type — and those are
     * the priors the server writes into every subgraph.
     */
    public static final class Registry implements ThemeRegistry {
        public static final Registry INSTANCE = new Registry();
        private Registry() {}

        @Override public List<Theme> themes() {
            return List.of(Chalk.INSTANCE, Slate.INSTANCE);
        }
        /** Colour and type, per theme — the two priors this app reaches. */
        @Override public List<PaletteProvision<?, ?>> palettes() {
            return List.of(Chalk.Palette.INSTANCE, Slate.Palette.INSTANCE,
                           Chalk.Fonts.INSTANCE,   Slate.Fonts.INSTANCE);
        }
    }
}
