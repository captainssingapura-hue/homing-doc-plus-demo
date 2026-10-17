package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssVar;

import java.util.List;
import java.util.Set;


/**
 * The platformer's sheet: where everything goes, and nothing of how it looks.
 * The bodies lay out; how each shape of the scene looks - its colours, its
 * edge, its corner - is the scene's design ({@link HousePlatformerScene}),
 * whose leaves the stage wears beside these classes. The stage is the game's
 * own size, 700 by 500 of its units, scaled to the box it is lent by
 * {@code --pf-scale}; everything in it is placed by the game's numbers, each a
 * custom property its class reads - the stage never writes a style of its own.
 */
public record PlatformerStyles() implements CssGroup<PlatformerStyles> {

    public static final PlatformerStyles INSTANCE = new PlatformerStyles();

    /** The widget: its head, its controls, then the stage - one column, filling its root. */
    public record pf_root() implements CssClass<PlatformerStyles> {
        @Override public String body() { return """
            gap: 8px;
            padding: 12px 16px;
            """;
        }
    }

    /** The title on the left and the score on the right, on one baseline. */
    public record pf_head() implements CssClass<PlatformerStyles> {
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

    /** The game's name, in the design's heading voice. */
    public record pf_title() implements CssClass<PlatformerStyles> {
        @Override public String body() { return """
            margin: 0;
            min-width: 0;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            """;
        }
    }

    /** How far the run has come: a numeral, its figures of one width so the head never moves as it counts. */
    public record pf_score() implements CssClass<PlatformerStyles> {
        @Override public String body() { return """
            flex: 0 0 auto;
            font-variant-numeric: tabular-nums;
            """;
        }
    }

    /** The keys and what they do, quietly. */
    public record pf_hint() implements CssClass<PlatformerStyles> {
        @Override public String body() { return """
            flex: 0 0 auto;
            margin: 0;
            """;
        }
    }

    /** The sliders, side by side while the width holds them. */
    public record pf_controls() implements CssClass<PlatformerStyles> {
        @Override public String body() { return """
            flex: 0 0 auto;
            display: flex;
            flex-wrap: wrap;
            gap: 8px 24px;
            """;
        }
    }

    /** The rest of the height: the stage centred in it, scaled to it. */
    public record pf_view() implements CssClass<PlatformerStyles> {
        @Override public String body() { return """
            flex: 1 1 auto;
            min-width: 0;
            min-height: 0;
            overflow: hidden;
            display: flex;
            align-items: center;
            justify-content: center;
            """;
        }
    }

    /** The stage: the game's own size, scaled by {@code --pf-scale} to fit; its ground the scene's backdrop. */
    public record pf_stage() implements CssClass<PlatformerStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--pf-scale")); }
        @Override public String body() { return """
            flex: 0 0 auto;
            position: relative;
            width: 700px;
            height: 500px;
            overflow: hidden;
            transform: scale(var(--pf-scale, 1));
            transform-origin: center;
            cursor: pointer;
            """;
        }
    }

    /** The sky: a band along the top. */
    public record pf_sky() implements CssClass<PlatformerStyles> {
        @Override public String body() { return """
            position: absolute;
            left: 0;
            right: 0;
            top: 0;
            height: 120px;
            """;
        }
    }

    /** The world, slid left as the camera follows: {@code --pf-camera}, in the game's units. */
    public record pf_world() implements CssClass<PlatformerStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--pf-camera")); }
        @Override public String body() { return """
            position: absolute;
            inset: 0;
            transform: translateX(calc(var(--pf-camera, 0) * -1px));
            will-change: transform;
            """;
        }
    }

    /** A platform: at {@code --pf-x}, {@code --pf-y}, {@code --pf-w} wide. */
    public record pf_platform() implements CssClass<PlatformerStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--pf-x"), new CssVar("--pf-y"), new CssVar("--pf-w")); }
        @Override public String body() { return """
            position: absolute;
            left: calc(var(--pf-x, 0) * 1px);
            top: calc(var(--pf-y, 0) * 1px);
            width: calc(var(--pf-w, 0) * 1px);
            height: 16px;
            box-sizing: border-box;
            """;
        }
    }

    /** Gone for now: a place for a platform, empty; the card, while the run goes on. Last in the sheet, so it wins. */
    public record pf_gone() implements CssClass<PlatformerStyles> {
        @Override public String body() { return "display: none;"; }
    }

    /** The animal: its picture, {@code --pf-art}, at {@code --pf-x}, {@code --pf-y}. */
    public record pf_animal() implements CssClass<PlatformerStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--pf-x"), new CssVar("--pf-y"), new CssVar("--pf-art")); }
        @Override public String body() { return """
            position: absolute;
            left: calc(var(--pf-x, 0) * 1px);
            top: calc(var(--pf-y, 0) * 1px);
            width: 50px;
            height: 50px;
            background: var(--pf-art) center / contain no-repeat;
            """;
        }
    }

    /** The animal facing left: its picture turned. */
    public record pf_left() implements CssClass<PlatformerStyles> {
        @Override public String body() { return "transform: scaleX(-1);"; }
    }

    /** The lava: along the foot. */
    public record pf_lava() implements CssClass<PlatformerStyles> {
        @Override public String body() { return """
            position: absolute;
            left: 0;
            right: 0;
            bottom: 0;
            height: 40px;
            box-sizing: border-box;
            """;
        }
    }

    /** The run over: a card in the middle of the stage. */
    public record pf_over() implements CssClass<PlatformerStyles> {
        @Override public String body() { return """
            position: absolute;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 10px;
            padding: 20px 32px;
            """;
        }
    }

    /** "Game over", in the heading voice. */
    public record pf_over_title() implements CssClass<PlatformerStyles> {
        @Override public String body() { return "margin: 0;"; }
    }

    @Override
    public List<CssClass<PlatformerStyles>> cssClasses() {
        // pf_gone last: what is gone stays gone, whatever display the thing it hides has
        return List.of(new pf_root(), new pf_head(), new pf_title(), new pf_score(), new pf_hint(), new pf_controls(), new pf_view(), new pf_stage(), new pf_sky(),
                       new pf_world(), new pf_platform(), new pf_animal(), new pf_left(), new pf_lava(),
                       new pf_over(), new pf_over_title(), new pf_gone());
    }
}
