package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

/**
 * RFC 0050 — typed CSS for the Relation Grid demos: the two companions (Dish
 * List + Minesweeper) and the Table Workbench specimens. Theme tokens only;
 * the grid primitive itself carries its own {@code hgr-*} stylesheet, so
 * these classes style only the demo chrome around it.
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
    /** The board host. The header band is gone via header.show=false on the
     *  grid itself — this used to need a nested thead{display:none} rule. */
    public record ms_host() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                display: inline-block;
                border: 1px solid var(--color-border);
                border-radius: 6px;
                overflow: hidden;
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

    // ── the Table Workbench ──────────────────────────────────────────────
    // A workbench specimen fills its pane rather than capping at a fraction
    // of the page: the scrollport IS the thing under test, so it has to be
    // the pane's own height, not an arbitrary 62vh slice of the document.

    /** height:100%, NOT flex:1 — the pane's content area is display:block
     *  (hmtp-tab-content), so a flex basis there resolves to nothing and the
     *  root grows to its content instead. It then never constrains tw_host,
     *  the host never scrolls, and the PANE scrolls in its place — which
     *  drags the toolbar and the frozen header out of view and hides the very
     *  behaviour the bench exists to show. The pg_doc_root precedent. */
    public record tw_root() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                display: flex;
                flex-direction: column;
                height: 100%;
                width: 100%;
                min-height: 0;
                padding: 10px;
                box-sizing: border-box;
                gap: 8px;
                """;
        }
    }
    /** The scrollport under test — flex:1 + min-height:0 so it takes the
     *  pane's leftover height exactly, and scrolls rather than growing. */
    public record tw_host() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                flex: 1;
                min-height: 0;
                overflow: auto;
                border: 1px solid var(--color-border);
                border-radius: 6px;
                """;
        }
    }
    /** The live position readout: cursor, scroll offsets, visible band. The
     *  point of the workbench is being able to READ where the viewport is. */
    public record tw_readout() implements CssClass<GridDemoStyles> {
        @Override public String body() { return """
                font: 11px monospace;
                color: var(--color-text-muted);
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                """;
        }
    }

    @Override
    public List<CssClass<GridDemoStyles>> cssClasses() {
        return List.of(new gd_wrap(), new gd_hint(), new gd_bar(), new gd_btn(),
                new gd_host(), new gd_status(), new ms_wrap(), new ms_host(),
                new ms_status(), new ms_progress(), new ms_tile(),
                new tw_root(), new tw_host(), new tw_readout());
    }

    @Override
    public CssImportsFor<GridDemoStyles> cssImports() {
        return CssImportsFor.none(this);
    }
}
