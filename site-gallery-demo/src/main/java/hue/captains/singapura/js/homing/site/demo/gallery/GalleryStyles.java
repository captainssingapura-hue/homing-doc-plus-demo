package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;
import java.util.Set;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Box.Container;
import static hue.captains.singapura.js.homing.design.Box.Control;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Emphasis.Primary;
import static hue.captains.singapura.js.homing.design.Feedback.Danger;
import static hue.captains.singapura.js.homing.design.Feedback.Success;
import static hue.captains.singapura.js.homing.design.Feedback.Warning;
import static hue.captains.singapura.js.homing.design.Interaction.Interactive;
import static hue.captains.singapura.js.homing.design.Layer.Inverted;
import static hue.captains.singapura.js.homing.design.Interaction.Current;
import static hue.captains.singapura.js.homing.design.Interaction.Focus;
import static hue.captains.singapura.js.homing.design.Layer.Raised;
import static hue.captains.singapura.js.homing.design.Layer.Recessed;
import static hue.captains.singapura.js.homing.design.Target.Affordance;
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
            flex: 1 1 auto;
            min-width: 0;
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

    /** The row of cells on the context menus page: wrapping, with air between. */
    public record ga_menu_cells() implements CssClass<GalleryStyles> {
        @Override public String body() { return """
            display: flex;
            flex-wrap: wrap;
            gap: 16px;
            margin: 8px 0 16px;
            """;
        }
    }

    /** A cell: a raised box with a control's rule and ring, focusable, its face in the middle and its caption under. */
    public record ga_cell() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Surface.class), of(Raised.class, Color.Edge.class), of(Control.class, Shape.Rule.class), of(Control.class, Shape.Corner.class), of(Control.class, Color.Edge.class)); }
        @Override public String body() { return """
            inline-size: 200px;
            block-size: 168px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 8px;
            box-sizing: border-box;
            user-select: none;
            """;
        }
    }

    /** The cell's face: an emoji, a number or a swatch, turned and mirrored by the cell's own numbers, eased as the design eases a thing that moves. */
    public record ga_cell_face() implements CssClass<GalleryStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--ga-rotate"), new CssVar("--ga-flip")); }
        @Override public List<? extends Wearable> wears() { return List.of(of(Interactive.class, Motion.Ease.class), of(Numeral.class, Type.Face.class), of(Numeral.class, Type.Weight.class), of(Display.class, Color.Ink.class)); }
        @Override public String body() { return """
            inline-size: 88px;
            block-size: 88px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 64px;
            line-height: 1;
            border-radius: 12px;
            transform: rotate(var(--ga-rotate, 0deg)) scaleX(var(--ga-flip, 1));
            """;
        }
    }

    /** What the cell says of itself, in a muted caption. */
    public record ga_cell_caption() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    /** The swatch's colours: the words, on the face. */
    public record ga_swatch_primary() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Primary.class, Color.Surface.class)); }
        @Override public String body() { return ""; }
    }
    public record ga_swatch_success() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Success.class, Color.Surface.class)); }
        @Override public String body() { return ""; }
    }
    public record ga_swatch_warning() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Warning.class, Color.Surface.class)); }
        @Override public String body() { return ""; }
    }
    public record ga_swatch_danger() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Danger.class, Color.Surface.class)); }
        @Override public String body() { return ""; }
    }
    /** The swatch inverted: the design's inverted surface, over whatever colour it wore. Last of the swatches, so it wins. */
    public record ga_swatch_inverted() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Inverted.class, Color.Surface.class)); }
        @Override public String body() { return ""; }
    }

    /** The box the strip alone sits in: a raised edge, the strip on top, the floor under it; nothing clipped, so the lifted chip's shadow is seen whole. */
    public record ga_strip_box() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Edge.class), of(Raised.class, Shape.Rule.class), of(Raised.class, Shape.Corner.class)); }
        @Override public String body() { return """
            height: 200px;
            display: flex;
            flex-direction: column;
            """;
        }
    }

    /** The floor under the strip: the row as the page holds it, in a caption. */
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

    /** The specimens on the context menus page: one per kind, stacked, each the whole tree open beside its rows. */
    public record ga_specimens() implements CssClass<GalleryStyles> {
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            gap: 20px;
            margin: 24px 0 16px;
            """;
        }
    }

    /** A specimen's name over it: the kind, in a caption. */
    public record ga_specimen_name() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class), of(Caption.class, Type.Scale.class)); }
        @Override public String body() { return "margin-bottom: 6px;"; }
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

    // ── The focus page ────────────────────────────────────────────────────────

    /** The scene and the monitor side by side: the scene takes the room, the monitor its own column. */
    public record ga_focus() implements CssClass<GalleryStyles> {
        @Override public String body() { return """
            display: flex;
            align-items: flex-start;
            gap: 24px;
            margin: 16px 0;
            """;
        }
    }

    /** The scene: the panels and the loose leaf, wrapping. */
    public record ga_focus_scene() implements CssClass<GalleryStyles> {
        @Override public String body() { return """
            flex: 1 1 auto;
            min-width: 0;
            display: flex;
            flex-wrap: wrap;
            align-items: flex-start;
            gap: 16px;
            """;
        }
    }

    /** The monitor's column: raised, its own box. */
    public record ga_focus_monitor() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Surface.class), of(Raised.class, Color.Edge.class), of(Raised.class, Shape.Rule.class), of(Raised.class, Shape.Corner.class)); }
        @Override public String body() { return """
            flex: 0 0 300px;
            min-width: 0;
            padding: 10px 6px;
            display: flex;
            flex-direction: column;
            gap: 14px;
            """;
        }
    }

    /** The scene's column: the tools row above the scene. */
    public record ga_focus_column() implements CssClass<GalleryStyles> {
        @Override public String body() { return "flex: 1 1 auto; min-width: 0; display: flex; flex-direction: column; gap: 12px;"; }
    }

    /** The tools row: the native world outside every container — a search field, a notes field, a button. */
    public record ga_focus_tools() implements CssClass<GalleryStyles> {
        @Override public String body() { return "display: flex; flex-wrap: wrap; gap: 8px; align-items: center;"; }
    }

    /** A native text field, in the tools or inside a panel. */
    public record ga_field() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Control.class, Shape.Rule.class), of(Control.class, Shape.Corner.class), of(Control.class, Color.Edge.class), of(Recessed.class, Color.Surface.class), of(Body.class, Color.Ink.class), of(Caption.class, Type.Scale.class)); }
        @Override public String body() { return "font: inherit; padding: 4px 8px; min-width: 0; flex: 1 1 12em;"; }
    }

    /** A native button, in the tools or inside a panel. */
    public record ga_button() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Control.class, Shape.Rule.class), of(Control.class, Shape.Corner.class), of(Control.class, Color.Edge.class), of(Raised.class, Color.Surface.class), of(Body.class, Color.Ink.class), of(Caption.class, Type.Scale.class), of(Interactive.class, Affordance.Cursor.class)); }
        @Override public String body() { return "font: inherit; padding: 4px 10px; align-self: flex-start;"; }
    }

    /** The relations page: the unwrapped tree and grid, grouped under a header, outside every container. */
    public record ga_rel_group() implements CssClass<GalleryStyles> {
        @Override public String body() { return "flex: 1 1 100%; min-width: 0; display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-start;"; }
    }

    /** A host for a grid or a tree, in the unwrapped group or inside a panel: a box the widget fills, scrolling within. */
    public record ga_rel_host() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Edge.class), of(Raised.class, Shape.Rule.class), of(Raised.class, Shape.Corner.class)); }
        @Override public String body() { return "flex: 1 1 300px; min-width: 0; min-height: 160px; max-height: 40vh; overflow: auto;"; }
    }

    /** A panel: a container that holds a focus branch; a press on its header claims for it. */
    public record ga_panel() implements CssClass<GalleryStyles> {
        /** A pane: so a design says what a pane looks like while the keys are on it, and data-keys says which state it is in. */
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Surface.class), of(Container.Pane.class, Shape.Rule.class), of(Container.Pane.class, Shape.Corner.class), of(Container.Pane.class, Color.Edge.class), of(Interactive.class, Motion.Ease.class)); }
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            gap: 10px;
            padding: 10px;
            min-width: 220px;
            outline: none;
            """;
        }
    }

    /** The panel's header: the name; a press claims for the panel. */
    public record ga_panel_header() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Kicker.class, Type.Scale.class), of(Kicker.class, Type.Weight.class), of(Kicker.class, Type.Treatment.class), of(Muted.class, Color.Ink.class), of(Interactive.class, Affordance.Cursor.class)); }
        @Override public String body() { return "user-select: none;"; }
    }

    /** A leaf: a focusable box that claims on a press and counts the arrows it takes. */
    public record ga_leaf() implements CssClass<GalleryStyles> {
        /** A card: a bounded thing on a surface, marked by the design when the keys are on it. */
        @Override public List<? extends Wearable> wears() { return List.of(of(Recessed.class, Color.Surface.class), of(Container.Card.Base.class, Shape.Rule.class), of(Container.Card.Base.class, Shape.Corner.class), of(Container.Card.Base.class, Color.Edge.class), of(Body.class, Color.Ink.class), of(Interactive.class, Motion.Ease.class), of(Interactive.class, Affordance.Cursor.class)); }
        @Override public String body() { return """
            display: flex;
            align-items: baseline;
            gap: 10px;
            padding: 8px 12px;
            min-width: 160px;
            outline: none;
            """;
        }
    }

    /** The count a leaf keeps: the arrows it took. */
    public record ga_leaf_count() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Numeral.class, Type.Face.class), of(Numeral.class, Type.Weight.class)); }
        @Override public String body() { return "margin-inline-start: auto;"; }
    }

    /** The leaf's yield button: a small control after the count. */
    public record ga_leaf_yield() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Control.class, Shape.Rule.class), of(Control.class, Shape.Corner.class), of(Control.class, Color.Edge.class), of(Raised.class, Color.Surface.class), of(Muted.class, Color.Ink.class), of(Caption.class, Type.Scale.class), of(Interactive.class, Affordance.Cursor.class)); }
        @Override public String body() { return "font: inherit; padding: 1px 8px;"; }
    }

    /** The note beside a panel's name: what it does with a yield. */
    public record ga_panel_note() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class)); }
        @Override public String body() { return "margin-inline-start: 10px; text-transform: none; letter-spacing: normal; font-weight: normal;"; }
    }

    /** Panel C's native list: which leaf holds the keys. */
    public record ga_panel_list() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Control.class, Shape.Rule.class), of(Control.class, Shape.Corner.class), of(Control.class, Color.Edge.class), of(Recessed.class, Color.Surface.class), of(Body.class, Color.Ink.class), of(Code.class, Type.Face.class), of(Caption.class, Type.Scale.class)); }
        @Override public String body() { return "font: inherit; padding: 4px;"; }
    }

    // ── The keyboard page ─────────────────────────────────────────────────────

    /** The strip that shows who has the keys: one chip per member of the page's party, in a row. */
    public record ga_holders() implements CssClass<GalleryStyles> {
        @Override public String body() { return """
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            gap: 8px;
            margin: 16px 0;
            """;
        }
    }

    /** A member's chip: a control at rest, in a code face. */
    public record ga_holder() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Raised.class, Color.Surface.class), of(Control.class, Shape.Rule.class), of(Control.class, Shape.Corner.class), of(Control.class, Color.Edge.class),
                           of(Code.class, Type.Face.class), of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class), of(Interactive.class, Motion.Ease.class));
        }
        @Override public String body() { return "padding: 4px 10px; white-space: nowrap;"; }
    }

    /** The chip of the one that holds the keys: the current surface, the ring drawn now. */
    public record ga_holder_on() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Current.class, Color.Surface.class), of(Current.class, Color.Edge.class), of(Focus.class, Shape.Rule.class), of(Focus.class, Color.Edge.class), of(Body.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    /** The platformer's stage: a sunk box the animal runs in; a press in it claims the keys. */
    public record ga_stage() implements CssClass<GalleryStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Recessed.class, Color.Surface.class), of(Control.class, Shape.Rule.class), of(Control.class, Shape.Corner.class), of(Control.class, Color.Edge.class)); }
        @Override public String body() { return """
            position: relative;
            inline-size: 100%;
            max-inline-size: 560px;
            block-size: 160px;
            overflow: hidden;
            outline: none;
            """;
        }
    }

    /** The animal on the stage: placed by the two runtime variables the game sets, facing where it last ran. */
    public record ga_sprite() implements CssClass<GalleryStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--ga-x"), new CssVar("--ga-y"), new CssVar("--ga-face")); }
        @Override public List<? extends Wearable> wears() { return List.of(of(Numeral.class, Type.Face.class), of(Display.class, Color.Ink.class)); }
        @Override public String body() { return """
            position: absolute;
            left: 0;
            bottom: 0;
            font-size: 40px;
            line-height: 1;
            user-select: none;
            translate: var(--ga-x, 0px) calc(-1 * var(--ga-y, 0px));
            scale: var(--ga-face, 1) 1;
            """;
        }
    }

    @Override
    public List<CssClass<GalleryStyles>> cssClasses() {
        return List.of(new ga_kicker(), new ga_title(), new ga_lede(), new ga_cards(), new ga_card_list(), new ga_count(), new ga_buttons(),
                       new ga_host(), new ga_pane_host(), new ga_dock_box(), new ga_strip_box(), new ga_shelf(), new ga_menu_cells(), new ga_cell(), new ga_cell_face(), new ga_cell_caption(),
                       new ga_swatch_primary(), new ga_swatch_success(), new ga_swatch_warning(), new ga_swatch_danger(), new ga_swatch_inverted(), new ga_grid_cell(), new ga_grid_cell_current(), new ga_specimens(), new ga_specimen_name(), new ga_log(), new ga_status(),
                       new ga_holders(), new ga_holder(), new ga_holder_on(), new ga_stage(), new ga_sprite(),
                       new ga_focus(), new ga_focus_scene(), new ga_focus_monitor(), new ga_focus_column(), new ga_focus_tools(), new ga_field(), new ga_button(), new ga_rel_group(), new ga_rel_host(), new ga_panel(), new ga_panel_header(), new ga_leaf(), new ga_leaf_count(), new ga_leaf_yield(), new ga_panel_note(), new ga_panel_list(),
                       new ga_shell(), new ga_shell_nav(), new ga_shell_demo(), new ga_shell_explain(), new ga_explain_text(), new ga_explain_link(),
                       new ga_control(), new ga_control_label(), new ga_control_readout());
    }
}
