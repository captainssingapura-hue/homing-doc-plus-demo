package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Interaction.Current;
import static hue.captains.singapura.js.homing.design.Interaction.Focus;
import static hue.captains.singapura.js.homing.design.Layer.Raised;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Motion;
import static hue.captains.singapura.js.homing.design.Target.Shape;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Body;
import static hue.captains.singapura.js.homing.design.Text.Caption;
import static hue.captains.singapura.js.homing.design.Text.Code;
import static hue.captains.singapura.js.homing.design.Text.Display;
import static hue.captains.singapura.js.homing.design.Text.Kicker;
import static hue.captains.singapura.js.homing.design.Text.Lede;
import static hue.captains.singapura.js.homing.design.Text.Link;
import static hue.captains.singapura.js.homing.design.Text.Numeral;

/** The pages' own classes — kicker, title, lede, count, two grids, the host boxes, the log and the status line. Cards and buttons are the shared elements'. */
public record GalleryStyles() implements CssGroup<GalleryStyles> {

    public static final GalleryStyles INSTANCE = new GalleryStyles();

    public record ga_kicker() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Kicker.class, Type.Scale.class), of(Kicker.class, Type.Weight.class), of(Kicker.class, Type.Treatment.class), of(Kicker.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0 0 6px;"; }
    }

    public record ga_title() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Display.class, Type.Face.class), of(Display.class, Type.Decoration.class), of(Display.class, Type.Scale.class), of(Display.class, Type.Weight.class), of(Display.class, Type.Treatment.class), of(Display.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0 0 8px;"; }
    }

    public record ga_lede() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Lede.class, Color.Ink.class), of(Lede.class, Type.Scale.class), of(Lede.class, Type.Treatment.class)); }
        @Override public String body() { return "margin: 0 0 28px; max-width: 46rem;"; }
    }

    /** A list a caller mints in a card's body: the card bounds it. */
    public record ga_card_list() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class)); }
        @Override public String body() { return "margin: 0; padding-left: 18px;"; }
    }

    public record ga_cards() implements CssClass<GalleryStyles> {
        @Override public String body() { return """
            display: flex;
            flex-wrap: wrap;
            align-items: flex-start;
            gap: 16px;
            """;
        }
    }

    public record ga_count() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Numeral.class, Type.Face.class), of(Numeral.class, Type.Scale.class), of(Numeral.class, Type.Weight.class), of(Display.class, Color.Ink.class)); }
        @Override public String body() { return """
            font-size: 96px;
            line-height: 1;
            margin: 24px 0;
            """;
        }
    }

    public record ga_buttons() implements CssClass<GalleryStyles> {
        @Override public String body() { return "display: flex; flex-wrap: wrap; align-items: center; gap: 10px;"; }
    }

    /** The box a grid or a tree is given: a raised edge, its own scroll. */
    public record ga_host() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Edge.class), of(Raised.class, Shape.Rule.class), of(Raised.class, Shape.Corner.class)); }
        @Override public String body() { return """
            min-height: 240px;
            max-height: 60vh;
            overflow: auto;
            """;
        }
    }

    /** The box a pane is given: a raised edge, a fixed height, a flex column the pane fills as its item. */
    public record ga_pane_host() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Edge.class), of(Raised.class, Shape.Rule.class), of(Raised.class, Shape.Corner.class)); }
        @Override public String body() { return """
            height: 360px;
            display: flex;
            flex-direction: column;
            overflow: hidden;
            """;
        }
    }

    /** What the split grid page puts in a cell: a raised card filling the cell, its name and its buttons. */
    public record ga_grid_cell() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Surface.class), of(Raised.class, Color.Edge.class), of(Raised.class, Shape.Rule.class)); }
        @Override public String body() { return """
            flex: 1 1 auto;
            min-width: 0;
            min-height: 0;
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            gap: 6px;
            padding: 10px;
            overflow: hidden;
            """;
        }
    }

    /** The cell card the mirror's cursor is at: the current one. */
    public record ga_grid_cell_current() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Current.class, Color.Surface.class), of(Current.class, Color.Edge.class)); }
        @Override public String body() { return ""; }
    }

    /** The box the dock fills and the desk lies over: positioned, so the desk can be a layer; a flex column for the pane. */
    public record ga_dock_box() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Edge.class), of(Raised.class, Shape.Rule.class), of(Raised.class, Shape.Corner.class)); }
        @Override public String body() { return """
            position: relative;
            height: 480px;
            display: flex;
            flex-direction: column;
            overflow: hidden;
            """;
        }
    }

    /** The box the strip alone sits in: a raised edge, the strip on top, the shelf under it; nothing clipped, so a chip pulled off the strip is seen leaving. */
    public record ga_strip_box() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Edge.class), of(Raised.class, Shape.Rule.class), of(Raised.class, Shape.Corner.class)); }
        @Override public String body() { return """
            height: 200px;
            display: flex;
            flex-direction: column;
            """;
        }
    }

    /** The shelf under the strip: what the chips pulled off it say, in a caption. */
    public record ga_shelf() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class), of(Caption.class, Type.Scale.class)); }
        @Override public String body() { return """
            flex: 1 1 auto;
            display: flex;
            align-items: flex-end;
            padding: 12px 16px;
            """;
        }
    }

    /** The log under the pane: one line per mutation, newest last, scrolling. */
    public record ga_log() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Code.class, Type.Face.class), of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return """
            margin-top: 10px;
            max-height: 160px;
            overflow: auto;
            white-space: pre;
            """;
        }
    }

    // ── The shell ─────────────────────────────────────────────────────────────

    /** The shell: fills the full-bleed slot; the splitter fills it. */
    public record ga_shell() implements CssClass<GalleryStyles> {
        @Override public String body() { return """
            flex: 1 1 auto;
            min-height: 0;
            display: flex;
            """;
        }
    }

    /** The navigator's box: the tree, scrolling on its own. */
    public record ga_shell_nav() implements CssClass<GalleryStyles> {
        @Override public String body() { return """
            flex: 1 1 auto;
            min-height: 0;
            overflow: auto;
            padding: 12px 8px;
            """;
        }
    }

    /** The demo's box: a reading column inside the pane, scrolling on its own. */
    public record ga_shell_demo() implements CssClass<GalleryStyles> {
        @Override public String body() { return """
            flex: 1 1 auto;
            min-height: 0;
            overflow: auto;
            padding: 24px 28px 32px;
            """;
        }
    }

    /** The explanation's box: raised, scrolling on its own. */
    public record ga_shell_explain() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Surface.class)); }
        @Override public String body() { return """
            flex: 1 1 auto;
            min-height: 0;
            overflow: auto;
            padding: 18px 28px 24px;
            """;
        }
    }

    /** The explanation's paragraph, in body ink. */
    public record ga_explain_text() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Body.class, Color.Ink.class), of(Body.class, Type.Face.class)); }
        @Override public String body() { return """
            margin: 0 0 14px;
            max-width: 72ch;
            line-height: 1.55;
            """;
        }
    }

    /** The link to the page the demo also is. */
    public record ga_explain_link() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Link.class, Color.Ink.class), of(Link.class, Type.Decoration.class), of(Link.class, Motion.Ease.class), of(Focus.class, Shape.Rule.class), of(Focus.class, Color.Edge.class)); }
        @Override public String body() { return ""; }
    }

    /** A control row: a label, a range and a readout, on one line. */
    public record ga_control() implements CssClass<GalleryStyles> {
        @Override public String body() { return """
            display: flex;
            align-items: center;
            gap: 12px;
            margin: 12px 0 20px;
            """;
        }
    }

    public record ga_control_label() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Kicker.class, Type.Scale.class), of(Kicker.class, Type.Weight.class), of(Kicker.class, Type.Treatment.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "min-width: 160px;"; }
    }

    public record ga_control_readout() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Code.class, Type.Face.class), of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "min-width: 180px; white-space: nowrap;"; }
    }

    /** The line under the box: the party's numbers. */
    public record ga_status() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Code.class, Type.Face.class), of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin-top: 10px;"; }
    }

    @Override
    public List<CssClass<GalleryStyles>> cssClasses() {
        return List.of(new ga_kicker(), new ga_title(), new ga_lede(), new ga_cards(), new ga_card_list(), new ga_count(), new ga_buttons(),
                       new ga_host(), new ga_pane_host(), new ga_dock_box(), new ga_strip_box(), new ga_shelf(), new ga_grid_cell(), new ga_grid_cell_current(), new ga_log(), new ga_status(),
                       new ga_shell(), new ga_shell_nav(), new ga_shell_demo(), new ga_shell_explain(), new ga_explain_text(), new ga_explain_link(),
                       new ga_control(), new ga_control_label(), new ga_control_readout());
    }
}
