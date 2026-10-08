package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Layer.Raised;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Shape;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Body;

/**
 * The demos' layout: the stage a demo stands its component on, a row of what sits side by side, a
 * framed box the component fills or floats in, words, a layer over the page, frames side by side,
 * a box that scrolls, what fills its box. Layout and the design's words only: a demo paints
 * nothing of its own - what is shown is the component's.
 */
public record DemoStyles() implements CssGroup<DemoStyles> {

    public static final DemoStyles INSTANCE = new DemoStyles();

    /** The stage: the component and what is around it, top to bottom, with air - scrolling on its own whatever the component grows to. */
    public record dm_stage() implements CssClass<DemoStyles> {
        @Override public String body() { return "display: flex;\nflex-direction: column;\nalign-items: stretch;\ngap: 12px;\nheight: 100%;\nmin-width: 0;\noverflow: auto;\npadding: 10px 12px;\nbox-sizing: border-box;\n"; }
    }

    /** What sits side by side, wrapping. */
    public record dm_row() implements CssClass<DemoStyles> {
        @Override public String body() { return "display: flex;\nflex-wrap: wrap;\nalign-items: center;\ngap: 8px;\n"; }
    }

    /** A framed box of its own height, for a component that fills what holds it or floats in it. */
    public record dm_host() implements CssClass<DemoStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Edge.class), of(Raised.class, Shape.Rule.class), of(Raised.class, Shape.Corner.class)); }
        @Override public String body() { return "position: relative;\ndisplay: flex;\nflex-direction: column;\nflex: none;\nheight: 180px;\noverflow: hidden;\n"; }
    }

    /** Words in a demo: what a box holds, what a mark is called. */
    public record dm_text() implements CssClass<DemoStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Body.class, Type.Face.class), of(Body.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\npadding: 8px 12px;\n"; }
    }

    /** A layer over the page, for what a demo shows at a point: fixed, the hand passing through it except on what it shows. */
    public record dm_layer() implements CssClass<DemoStyles> {
        @Override public String body() { return "position: fixed;\ninset: 0;\nz-index: 10021;\npointer-events: none;\n"; }
    }

    /** Frames side by side, from the first, wrapping when the room runs out: a menu shown still. */
    public record dm_frames() implements CssClass<DemoStyles> {
        @Override public String body() { return "display: flex;\nflex-wrap: wrap;\nalign-items: flex-start;\ngap: 24px;\n"; }
    }

    /** A framed box of its own height that scrolls what it holds: a tree too long for it. */
    public record dm_scroll() implements CssClass<DemoStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Edge.class), of(Raised.class, Shape.Rule.class), of(Raised.class, Shape.Corner.class)); }
        @Override public String body() { return "flex: none;\nheight: 220px;\noverflow: auto;\npadding: 8px 12px;\n"; }
    }

    /** What fills the box it is in: a viewport its host sizes. */
    public record dm_fill() implements CssClass<DemoStyles> {
        @Override public String body() { return "flex: 1;\nmin-height: 0;\nmin-width: 0;\n"; }
    }

    @Override
    public List<CssClass<DemoStyles>> cssClasses() {
        return List.of(new dm_stage(), new dm_row(), new dm_host(), new dm_text(), new dm_layer(), new dm_frames(), new dm_scroll(), new dm_fill());
    }
}
