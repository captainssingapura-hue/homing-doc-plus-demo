package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssVar;

import java.util.List;
import java.util.Set;

/**
 * The Swatchboard's classes. Every body reads the global palette and declares
 * nothing about it: the palette is the prior, reachable from every class by
 * definition, and the server writes it into this group's subgraph. No other
 * group is leaned on, so no {@code dependsOn()} anywhere — the plainest
 * shape a themed group can have.
 */
public record SwatchboardStyles() implements CssGroup<SwatchboardStyles> {

    public static final SwatchboardStyles INSTANCE = new SwatchboardStyles();

    public record sb_root() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                min-height: 100vh;
                margin: 0;
                padding: var(--space-6) var(--space-7);
                background: var(--color-surface);
                color: var(--color-text-primary);
                font-family: var(--font-body);
                """; }
    }

    public record sb_head() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                display: flex;
                align-items: baseline;
                justify-content: space-between;
                gap: var(--space-4);
                border-bottom: 1px solid var(--color-border);
                padding-bottom: var(--space-3);
                margin-bottom: var(--space-6);
                """; }
    }

    public record sb_title() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                margin: 0;
                font-size: 28px;
                color: var(--color-text-title);
                """; }
    }

    public record sb_sub() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                margin: var(--space-1) 0 0;
                color: var(--color-text-muted);
                """; }
    }

    public record sb_picker() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                display: flex;
                align-items: center;
                gap: var(--space-2);
                color: var(--color-text-muted);
                """; }
    }

    public record sb_select() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                padding: var(--space-1) var(--space-2);
                border: 1px solid var(--color-border-emphasis);
                border-radius: var(--radius-sm);
                background: var(--color-surface-raised);
                color: var(--color-text-primary);
                font: inherit;
                """; }
    }

    public record sb_grid() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                display: grid;
                grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
                gap: var(--space-3);
                """; }
    }

    public record sb_swatch() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                display: flex;
                align-items: center;
                gap: var(--space-3);
                padding: var(--space-2) var(--space-3);
                border: 1px solid var(--color-border);
                border-radius: var(--radius-md);
                background: var(--color-surface-raised);
                """; }
    }

    /** RFC 0066 Law 5 — the one custom property Swatchboard.js writes per chip: the token the chip paints. */
    public static final CssVar CHIP = new CssVar("--sb-chip");

    /** The chip: painted with the token it names, set from JS as a custom property. */
    public record sb_chip() implements CssClass<SwatchboardStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(CHIP); }
        @Override public String body() { return """
                flex: 0 0 auto;
                width: 36px;
                height: 36px;
                border-radius: var(--radius-sm);
                border: 1px solid var(--color-border-emphasis);
                background: var(--sb-chip);
                """; }
    }

    /** A scale token has no colour to paint; the chip shows the size instead. */
    public record sb_chip_scale() implements CssClass<SwatchboardStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(CHIP); }
        @Override public String body() { return """
                background: var(--color-accent);
                width: var(--sb-chip);
                height: var(--sb-chip);
                min-width: 4px;
                min-height: 4px;
                """; }
    }

    public record sb_name() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                font-family: var(--font-mono);
                font-size: 12px;
                color: var(--color-text-primary);
                """; }
    }

    public record sb_value() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                font-family: var(--font-mono);
                font-size: 12px;
                color: var(--color-text-muted);
                """; }
    }

    public record sb_foot() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                margin-top: var(--space-7);
                padding: var(--space-3) var(--space-4);
                border-radius: var(--radius-lg);
                background: var(--color-surface-inverted);
                color: var(--color-text-on-inverted);
                """; }
    }

    public record sb_foot_muted() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                color: var(--color-text-on-inverted-muted);
                """; }
    }

    public record sb_link() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                color: var(--color-text-link);
                """; }
    }

    @Override
    public List<CssClass<SwatchboardStyles>> cssClasses() {
        return List.of(new sb_root(), new sb_head(), new sb_title(), new sb_sub(), new sb_picker(), new sb_select(),
                new sb_grid(), new sb_swatch(), new sb_chip(), new sb_chip_scale(), new sb_name(), new sb_value(),
                new sb_foot(), new sb_foot_muted(), new sb_link());
    }
}
