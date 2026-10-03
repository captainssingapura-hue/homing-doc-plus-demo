package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.Wearable;
import hue.captains.singapura.js.homing.design.DesignClass;

import java.util.List;
import java.util.Set;

import static hue.captains.singapura.js.homing.design.Box.Control;
import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Layer.Base;
import static hue.captains.singapura.js.homing.design.Layer.Inverted;
import static hue.captains.singapura.js.homing.design.Layer.Raised;
import static hue.captains.singapura.js.homing.design.Layer.Recessed;
import static hue.captains.singapura.js.homing.design.Pairing.OnInverted;
import static hue.captains.singapura.js.homing.design.Pairing.OnInvertedMuted;
import static hue.captains.singapura.js.homing.design.Structure.Divider;
import static hue.captains.singapura.js.homing.design.Structure.Hairline;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Shape;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Body;
import static hue.captains.singapura.js.homing.design.Text.Code;
import static hue.captains.singapura.js.homing.design.Text.Heading;
import static hue.captains.singapura.js.homing.design.Text.Kicker;
import static hue.captains.singapura.js.homing.design.Text.Lede;
import static hue.captains.singapura.js.homing.design.Text.Link;

/**
 * The Swatchboard's classes. Every look is a design word worn beside the
 * class; the bodies hold layout only. No other group is leaned on, so no
 * {@code dependsOn()} anywhere — the plainest shape a designed group can have.
 */
public record SwatchboardStyles() implements CssGroup<SwatchboardStyles> {

    public static final SwatchboardStyles INSTANCE = new SwatchboardStyles();

    /**
     * The colour words the board draws, one per seed of a palette: the layers,
     * the inks, the accent's two marks and the hairline. The chip reads them,
     * so the deployment binds every one; {@link SwatchboardData} lists them for
     * the page.
     */
    public static final List<DesignClass<?>> BOARD = List.of(
            of(Base.class, Color.Surface.class), of(Raised.class, Color.Surface.class),
            of(Recessed.class, Color.Surface.class), of(Inverted.class, Color.Surface.class),
            of(Body.class, Color.Ink.class), of(Muted.class, Color.Ink.class), of(Heading.class, Color.Ink.class),
            of(OnInverted.class, Color.Ink.class), of(OnInvertedMuted.class, Color.Ink.class), of(Link.class, Color.Ink.class),
            of(Kicker.class, Color.Ink.class), of(Divider.class, Color.Edge.class), of(Hairline.class, Color.Edge.class));

    public record sb_root() implements CssClass<SwatchboardStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Base.class, Color.Surface.class), of(Body.class, Color.Ink.class), of(Body.class, Type.Face.class)); }
        @Override public String body() { return """
                min-height: 100vh;
                margin: 0;
                padding: 24px 32px;
                """; }
    }

    public record sb_head() implements CssClass<SwatchboardStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Hairline.class, Color.Edge.class), of(Hairline.class, Shape.Rule.class)); }
        @Override public String body() { return """
                display: flex;
                align-items: baseline;
                justify-content: space-between;
                gap: 16px;
                border-width: 0 0 1px;
                padding-bottom: 12px;
                margin-bottom: 24px;
                """; }
    }

    public record sb_title() implements CssClass<SwatchboardStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Heading.class, Type.Face.class), of(Heading.class, Type.Weight.class), of(Heading.class, Color.Ink.class)); }
        @Override public String body() { return """
                margin: 0;
                font-size: 28px;
                """; }
    }

    public record sb_sub() implements CssClass<SwatchboardStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Lede.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 4px 0 0;"; }
    }

    public record sb_picker() implements CssClass<SwatchboardStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return """
                display: flex;
                align-items: center;
                gap: 8px;
                """; }
    }

    public record sb_select() implements CssClass<SwatchboardStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Surface.class), of(Raised.class, Color.Edge.class), of(Body.class, Color.Ink.class), of(Control.class, Shape.Rule.class), of(Control.class, Shape.Corner.class)); }
        @Override public String body() { return """
                padding: 4px 8px;
                font: inherit;
                """; }
    }

    public record sb_grid() implements CssClass<SwatchboardStyles> {
        @Override public String body() { return """
                display: grid;
                grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
                gap: 12px;
                """; }
    }

    public record sb_swatch() implements CssClass<SwatchboardStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Surface.class), of(Raised.class, Color.Edge.class), of(Raised.class, Shape.Rule.class), of(Raised.class, Shape.Corner.class)); }
        @Override public String body() { return """
                display: flex;
                align-items: center;
                gap: 12px;
                padding: 8px 12px;
                """; }
    }

    /** RFC 0066 Law 5 — the one custom property Swatchboard.js writes per chip: the word the chip paints. */
    public static final CssVar CHIP = new CssVar("--sb-chip");

    /** The chip: painted with the word it names, set from JS as a custom property; it reads every word on the board. */
    public record sb_chip() implements CssClass<SwatchboardStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(CHIP); }
        @Override public List<? extends Wearable> reads() { return BOARD; }
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Edge.class), of(Hairline.class, Shape.Rule.class), of(Control.class, Shape.Corner.class)); }
        @Override public String body() { return """
                flex: 0 0 auto;
                width: 36px;
                height: 36px;
                background: var(--sb-chip);
                """; }
    }

    public record sb_name() implements CssClass<SwatchboardStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Code.class, Type.Face.class), of(Body.class, Color.Ink.class)); }
        @Override public String body() { return "font-size: 12px;"; }
    }

    public record sb_value() implements CssClass<SwatchboardStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Code.class, Type.Face.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "font-size: 12px;"; }
    }

    public record sb_foot() implements CssClass<SwatchboardStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Inverted.class, Color.Surface.class), of(OnInverted.class, Color.Ink.class), of(Raised.class, Shape.Corner.class)); }
        @Override public String body() { return """
                margin-top: 32px;
                padding: 12px 16px;
                """; }
    }

    public record sb_foot_muted() implements CssClass<SwatchboardStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(OnInvertedMuted.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    public record sb_link() implements CssClass<SwatchboardStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Link.class, Color.Ink.class), of(Link.class, Type.Decoration.class)); }
        @Override public String body() { return ""; }
    }

    @Override
    public List<CssClass<SwatchboardStyles>> cssClasses() {
        return List.of(new sb_root(), new sb_head(), new sb_title(), new sb_sub(), new sb_picker(), new sb_select(),
                new sb_grid(), new sb_swatch(), new sb_chip(), new sb_name(), new sb_value(),
                new sb_foot(), new sb_foot_muted(), new sb_link());
    }
}
