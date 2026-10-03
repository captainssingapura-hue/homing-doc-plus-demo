package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.design.Design;
import hue.captains.singapura.js.homing.design.DesignClass;
import hue.captains.singapura.js.homing.design.DesignId;
import hue.captains.singapura.js.homing.design.Impl;
import hue.captains.singapura.js.homing.design.Palette;
import hue.captains.singapura.js.homing.design.server.DesignRegistry;
import hue.captains.singapura.js.homing.designs.HomingEditorial;
import hue.captains.singapura.js.homing.designs.SeedPalette;
import hue.captains.singapura.js.homing.designs.SeedPalette.Seeds;

import java.util.List;

/**
 * RFC 0066 — the bare app's design, its two palettes, and the registry that
 * lists them. Neither knows the studio exists: a theme is a design — a
 * function from the words a page wears to their fulfilment — and that is all
 * the framework asks of it.
 *
 * <p>One physique, two palettes: one palette proves the binding, two prove the
 * switch. The physique is Editorial's, borrowed whole; the colours are the
 * app's own, written as seeds. Chalk is light and warm; Slate is dark and cool.
 * Each is the same in both modes: here the picker is the switch, not the
 * operating system.</p>
 */
public final class BareThemes {

    private BareThemes() {}

    /** Light — chalk on a warm board. The bare design's own colours. */
    public static final SeedPalette CHALK = new SeedPalette("chalk", "Chalk",
            "Warm paper, graphite text, one terracotta accent.", Bare.ID,
            chalk(), chalk());

    /** Dark — slate with a cold blue accent. Offered to the bare design beside its own. */
    public static final SeedPalette SLATE = new SeedPalette("slate", "Slate",
            "Wet slate, pale chalk lines, a cold blue accent.", Bare.ID,
            slate(), slate());

    private static Seeds chalk() {
        return new Seeds("#FBF7F0", "#FFFFFF", "#F1EBE0", "#2B2622", "#2B2622", "#7A6F66", "#FBF7F0", "#C9BFB4",
                         "#2B2622", "#C8552F", "#A2401F", "#FFFFFF", "#E2D8CB");
    }

    private static Seeds slate() {
        return new Seeds("#1E2328", "#272D33", "#171B1F", "#E8ECEF", "#E8ECEF", "#9AA5AE", "#1E2328", "#4E5860",
                         "#FFFFFF", "#4A90D9", "#7FB7E8", "#0F1418", "#38414A");
    }

    /**
     * The bare design: Editorial's physique, worn in Chalk by default — an
     * identity record, the way {@link HomingEditorial} is one over its own words.
     */
    public record Bare() implements Design {
        public static final DesignId ID = new DesignId("bare");
        public static final Bare INSTANCE = new Bare();

        @Override public Impl impl(DesignClass<?> pair) {
            return pair.onColourPlane() ? CHALK.impl(pair) : HomingEditorial.INSTANCE.impl(pair);
        }
        @Override public Palette palette() { return CHALK; }
        @Override public DesignId id() { return ID; }
        @Override public String label() { return "Bare"; }
        @Override public String group() { return "Bare"; }
        @Override public String inspiration() { return "Editorial's physique in the bare app's own colours."; }
    }

    /**
     * The registry: one base, its own colours and Slate's. The base is the
     * default the page is served under — {@code bare}, in Chalk; Slate is the
     * cross {@code bare_slate}.
     */
    public static final DesignRegistry REGISTRY = new DesignRegistry(List.of(Bare.INSTANCE), List.of(SLATE), List.of());
}
