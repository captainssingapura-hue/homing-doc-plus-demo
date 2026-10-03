package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;
import java.util.Set;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Emphasis.Primary;
import static hue.captains.singapura.js.homing.design.Emphasis.Secondary;
import static hue.captains.singapura.js.homing.design.Feedback.Success;
import static hue.captains.singapura.js.homing.design.Feedback.Warning;
import static hue.captains.singapura.js.homing.design.Interaction.Inert;
import static hue.captains.singapura.js.homing.design.Layer.Raised;
import static hue.captains.singapura.js.homing.design.Layer.Recessed;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Effect;
import static hue.captains.singapura.js.homing.design.Target.Shape;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Caption;
import static hue.captains.singapura.js.homing.design.Text.Code;

/**
 * The tear-off lab's sheet: a sunken stage the strip sits in, the escape's and the capture's
 * edges, the hand, its trail and the breach and settle marks, a chip torn
 * off the strip and placed by the lab, and the speed chart under the stage.
 * The phases are three colours the design already has — the rail secondary,
 * the flight a warning, the settled window a success — and every place is a
 * runtime variable, never a number written here.
 */
public record TearLabStyles() implements CssGroup<TearLabStyles> {

    public static final TearLabStyles INSTANCE = new TearLabStyles();

    /** The stage: sunken, framed, and the only thing that scrolls nothing; the hand is the lab's here, so nothing is selected. */
    public record tl_stage() implements CssClass<TearLabStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Recessed.class, Color.Surface.class), of(Raised.class, Color.Edge.class), of(Raised.class, Shape.Rule.class), of(Raised.class, Shape.Corner.class));
        }
        @Override public String body() { return """
            position: relative;
            height: 420px;
            overflow: hidden;
            user-select: none;
            touch-action: none;
            """;
        }
    }

    /** The strip's place on the stage: room above it for the band's upper edge. */
    public record tl_strip() implements CssClass<TearLabStyles> {
        @Override public String body() { return """
            position: absolute;
            left: 16px;
            right: 16px;
            top: 64px;
            padding-inline: 8px;
            """;
        }
    }

    /** One edge of the band, dashed across the stage at the height the lab gives it. */
    public record tl_band() implements CssClass<TearLabStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--tl-y")); }
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Edge.class)); }
        @Override public String body() { return """
            position: absolute;
            left: 0;
            right: 0;
            top: var(--tl-y, 0px);
            border-top-width: 1px;
            border-top-style: dashed;
            pointer-events: none;
            """;
        }
    }

    /** The capture's edge, inside the escape's: dotted where the escape's is dashed. */
    public record tl_band_inner() implements CssClass<TearLabStyles> {
        @Override public String body() { return "border-top-style: dotted;"; }
    }

    /** The chip's centre, drawn as a ring: what both bands measure, wherever the chip was taken. */
    public record tl_centre() implements CssClass<TearLabStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--tl-x"), new CssVar("--tl-y")); }
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Edge.class)); }
        @Override public String body() { return """
            position: absolute;
            left: var(--tl-x, 0px);
            top: var(--tl-y, 0px);
            width: 14px;
            height: 14px;
            margin: -7px 0 0 -7px;
            box-sizing: border-box;
            border-width: 2px;
            border-style: solid;
            border-radius: 50%;
            z-index: 3;
            pointer-events: none;
            """;
        }
    }

    /** A chip off the strip, where the lab placed it: the window it stands for. */
    public record tl_free() implements CssClass<TearLabStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--tl-x"), new CssVar("--tl-y")); }
        @Override public String body() { return """
            position: absolute;
            left: var(--tl-x, 0px);
            top: var(--tl-y, 0px);
            z-index: 2;
            """;
        }
    }

    /** The window made and waiting at the breach while the hand flies on. */
    public record tl_waiting() implements CssClass<TearLabStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Inert.class, Effect.Opacity.class)); }
        @Override public String body() { return ""; }
    }

    /** The hand, drawn: where the pointer is, which in flight is not where the chip is. */
    public record tl_hand() implements CssClass<TearLabStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--tl-x"), new CssVar("--tl-y")); }
        @Override public List<? extends Wearable> wears() { return List.of(of(Primary.class, Color.Surface.class)); }
        @Override public String body() { return """
            position: absolute;
            left: var(--tl-x, 0px);
            top: var(--tl-y, 0px);
            width: 12px;
            height: 12px;
            margin: -6px 0 0 -6px;
            border-radius: 50%;
            z-index: 3;
            pointer-events: none;
            """;
        }
    }

    /** A point of the hand's path. */
    public record tl_dot() implements CssClass<TearLabStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--tl-x"), new CssVar("--tl-y")); }
        @Override public String body() { return """
            position: absolute;
            left: var(--tl-x, 0px);
            top: var(--tl-y, 0px);
            width: 4px;
            height: 4px;
            margin: -2px 0 0 -2px;
            border-radius: 50%;
            pointer-events: none;
            """;
        }
    }

    /** The breach or the settle, named where it happened. */
    public record tl_mark() implements CssClass<TearLabStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--tl-x"), new CssVar("--tl-y")); }
        @Override public List<? extends Wearable> wears() { return List.of(of(Code.class, Type.Face.class), of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return """
            position: absolute;
            left: var(--tl-x, 0px);
            top: var(--tl-y, 0px);
            margin: 6px 0 0 8px;
            white-space: nowrap;
            pointer-events: none;
            """;
        }
    }

    /** On the rail. */
    public record tl_rail() implements CssClass<TearLabStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Secondary.class, Color.Surface.class)); }
        @Override public String body() { return ""; }
    }

    /** In flight: torn, the window waiting. */
    public record tl_flight() implements CssClass<TearLabStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Warning.class, Color.Surface.class)); }
        @Override public String body() { return ""; }
    }

    /** Settled: the window at the hand, following it. */
    public record tl_follow() implements CssClass<TearLabStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Success.class, Color.Surface.class)); }
        @Override public String body() { return ""; }
    }

    /** The speed over the gesture: a bar a frame, the newest on the right. */
    public record tl_chart() implements CssClass<TearLabStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Recessed.class, Color.Surface.class), of(Raised.class, Color.Edge.class), of(Raised.class, Shape.Rule.class), of(Raised.class, Shape.Corner.class));
        }
        @Override public String body() { return """
            position: relative;
            height: 96px;
            margin-top: 10px;
            display: flex;
            align-items: flex-end;
            gap: 1px;
            overflow: hidden;
            """;
        }
    }

    /** One frame's speed. */
    public record tl_bar() implements CssClass<TearLabStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--tl-h")); }
        @Override public String body() { return """
            flex: 1 1 0;
            min-width: 1px;
            height: var(--tl-h, 0px);
            """;
        }
    }

    /** A level across the chart: the breach's speed, and the change either side of it. */
    public record tl_level() implements CssClass<TearLabStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--tl-y")); }
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Edge.class)); }
        @Override public String body() { return """
            position: absolute;
            left: 0;
            right: 0;
            bottom: var(--tl-y, 0px);
            border-top-width: 1px;
            border-top-style: dashed;
            pointer-events: none;
            """;
        }
    }

    /** What the lab says of the gesture: its phase and its numbers. */
    public record tl_readout() implements CssClass<TearLabStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Code.class, Type.Face.class), of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin-top: 8px; white-space: pre-wrap;"; }
    }

    @Override
    public List<CssClass<TearLabStyles>> cssClasses() {
        return List.of(new tl_stage(), new tl_strip(), new tl_band(), new tl_band_inner(), new tl_centre(), new tl_free(), new tl_waiting(), new tl_hand(), new tl_dot(), new tl_mark(),
                       new tl_rail(), new tl_flight(), new tl_follow(), new tl_chart(), new tl_bar(), new tl_level(), new tl_readout());
    }
}
