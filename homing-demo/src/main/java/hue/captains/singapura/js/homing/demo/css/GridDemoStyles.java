package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

/**
 * RFC 0050 — typed CSS for the two Relation Grid companion demos (Dish List
 * + Minesweeper). Theme tokens only; the grid primitive itself carries its
 * own {@code hgr-*} stylesheet, so these classes style only the demo chrome
 * around it.
 */
public record GridDemoStyles() implements CssGroup<GridDemoStyles> {
    public static final GridDemoStyles INSTANCE = new GridDemoStyles();

    public record gd_wrap() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                display: flex;
                flex-direction: column;
                flex: 1;
                width: 100%;
                min-height: 0;
                padding: 14px;
                box-sizing: border-box;
                gap: 10px;
                """;
        }
    }
    public record gd_hint() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                font: 12px sans-serif;
                color: var(--color-text-muted);
                """;
        }
    }
    public record gd_bar() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                display: flex;
                gap: 8px;
                flex-wrap: wrap;
                """;
        }
    }
    public record gd_btn() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                padding: 4px 10px;
                cursor: pointer;
                font: 12px sans-serif;
                background: var(--color-surface-raised);
                color: var(--color-text-primary);
                border: 1px solid var(--color-border);
                border-radius: 4px;
                """;
        }
    }
    public record gd_host() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                overflow: auto;
                max-height: 62vh;
                border: 1px solid var(--color-border);
                border-radius: 6px;
                """;
        }
    }
    public record gd_status() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                font: 12px monospace;
                color: var(--color-text-muted);
                white-space: pre-line;
                max-height: 90px;
                overflow: auto;
                """;
        }
    }
    public record ms_wrap() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                display: flex;
                flex-direction: column;
                flex: 1;
                width: 100%;
                min-height: 0;
                padding: 14px;
                box-sizing: border-box;
                gap: 10px;
                align-items: flex-start;
                """;
        }
    }
    /** The board host — a game board has no header band (nested rule). */
    public record ms_host() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                display: inline-block;
                border: 1px solid var(--color-border);
                border-radius: 6px;
                overflow: hidden;

                thead {
                    display: none;
                }
                """;
        }
    }
    public record ms_status() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                font: 600 14px sans-serif;
                min-height: 18px;
                """;
        }
    }
    public record ms_progress() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                font: 12px monospace;
                color: var(--color-text-muted);
                min-height: 16px;
                """;
        }
    }
    /** One mine tile — fixed square, centered glyph. */
    public record ms_tile() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                width: 30px;
                height: 30px;
                display: flex;
                align-items: center;
                justify-content: center;
                font: 15px monospace;
                user-select: none;
                """;
        }
    }

    @Override
    public List<CssClass<GridDemoStyles>> cssClasses() {
        return List.of(new gd_wrap(), new gd_hint(), new gd_bar(), new gd_btn(),
                new gd_host(), new gd_status(), new ms_wrap(), new ms_host(),
                new ms_status(), new ms_progress(), new ms_tile());
    }

    @Override
    public CssImportsFor<GridDemoStyles> cssImports() {
        return CssImportsFor.none(this);
    }
}
