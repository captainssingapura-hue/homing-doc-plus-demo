package hue.captains.singapura.js.homing.demo.workspacewidgets.video;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;

import static hue.captains.singapura.js.homing.design.Box.Control;
import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Interaction.Selectable;
import static hue.captains.singapura.js.homing.design.Layer.Recessed;
import static hue.captains.singapura.js.homing.design.Target.Affordance;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Motion;
import static hue.captains.singapura.js.homing.design.Target.Shape;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Caption;
import static hue.captains.singapura.js.homing.design.Text.Heading;

/**
 * The video playlist's sheet, in the design's words and nothing of its own: the
 * bodies lay out, the designs colour, set and shape. The player keeps 16:9 and
 * shrinks to what it is lent on whichever axis runs out first, by measuring its
 * stage; the takes are a strip of tabs under it, which never costs the player
 * its width.
 */
public record EmbeddedVideoStyles() implements CssGroup<EmbeddedVideoStyles> {

    public static final EmbeddedVideoStyles INSTANCE = new EmbeddedVideoStyles();

    /** The playlist: its head, the line under it, the stage, the takes - one column, filling its root. */
    public record vd_root() implements CssClass<EmbeddedVideoStyles> {
        @Override public String body() { return """
            gap: 8px;
            padding: 12px 16px;
            """;
        }
    }

    /** The dish on the left and where the list is on the right, on one baseline. */
    public record vd_head() implements CssClass<EmbeddedVideoStyles> {
        @Override public String body() { return """
            flex: 0 0 auto;
            display: flex;
            align-items: baseline;
            justify-content: space-between;
            gap: 12px;
            min-width: 0;
            """;
        }
    }

    /** The dish, in the design's heading voice. */
    public record vd_title() implements CssClass<EmbeddedVideoStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Heading.class, Type.Face.class), of(Heading.class, Type.Scale.class), of(Heading.class, Type.Weight.class), of(Heading.class, Color.Ink.class)); }
        @Override public String body() { return """
            margin: 0;
            min-width: 0;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            """;
        }
    }

    /** "3 / 5", quietly; its figures of one width, so the heading beside it never moves as it counts. */
    public record vd_count() implements CssClass<EmbeddedVideoStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return """
            flex: 0 0 auto;
            font-variant-numeric: tabular-nums;
            """;
        }
    }

    /** Which take is on the stage, in words, quietly. */
    public record vd_note() implements CssClass<EmbeddedVideoStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return """
            flex: 0 0 auto;
            margin: 0;
            min-width: 0;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            """;
        }
    }

    /** The rest of the height, measured, the player centred in it. */
    public record vd_stage() implements CssClass<EmbeddedVideoStyles> {
        @Override public String body() { return """
            flex: 1 1 auto;
            min-width: 0;
            min-height: 0;
            container-type: size;
            display: flex;
            align-items: center;
            justify-content: center;
            """;
        }
    }

    /**
     * The player: 16:9, as wide as the stage or as its height allows, whichever
     * is less - so the ratio holds and the box shrinks, rather than the height
     * clipping it. Set a layer back until the player paints over it.
     */
    public record vd_frame() implements CssClass<EmbeddedVideoStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Recessed.class, Color.Surface.class), of(Recessed.class, Shape.Corner.class)); }
        @Override public String body() { return """
            aspect-ratio: 16 / 9;
            width: min(100%, calc(100cqh * 16 / 9));
            height: auto;
            border: 0;
            """;
        }
    }

    /** The takes: a strip at the foot, one row, scrolling sideways when they outrun the width. */
    public record vd_rail() implements CssClass<EmbeddedVideoStyles> {
        @Override public String body() { return """
            flex: 0 0 auto;
            display: flex;
            align-items: center;
            gap: 6px;
            min-width: 0;
            overflow-x: auto;
            padding-bottom: 2px;
            """;
        }
    }

    /**
     * A take: a tab, as the design draws one - shaped as a tab, coloured as a
     * selectable seen at rest, its ring a control's. The take on the stage says so
     * by aria-selected, and the design's selected state draws it; the ring shows on
     * the keys' focus alone. One line, clipped rather than wrapped.
     */
    public record vd_take() implements CssClass<EmbeddedVideoStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Control.Tab.class, Shape.Rule.class), of(Control.Tab.class, Shape.Corner.class), of(Control.class, Color.Edge.class),
                           of(Selectable.Tab.class, Color.Surface.class), of(Selectable.Tab.class, Color.Ink.class), of(Selectable.Tab.class, Color.Edge.class),
                           of(Selectable.class, Motion.Ease.class), of(Selectable.class, Affordance.Cursor.class),
                           of(Caption.class, Type.Scale.class));
        }
        @Override public String body() { return """
            flex: 0 0 auto;
            max-width: 180px;
            padding: 4px 12px;
            font-family: inherit;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            """;
        }
    }

    @Override
    public List<CssClass<EmbeddedVideoStyles>> cssClasses() {
        return List.of(new vd_root(), new vd_head(), new vd_title(), new vd_count(), new vd_note(), new vd_stage(), new vd_frame(), new vd_rail(), new vd_take());
    }
}
