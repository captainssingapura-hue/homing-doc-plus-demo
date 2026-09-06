package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

/**
 * The embedded-video widget's look. Every value is a theme token, so the frame
 * sits on the pane's own surface under any theme rather than on a colour of its
 * own.
 *
 * <p>The one interesting rule is the frame's: it keeps 16:9 and shrinks to what
 * the pane gives it, on whichever axis runs out first, by measuring the stage
 * with container-query units. A workspace pane is resized by dragging a
 * divider, so a video that only fitted one axis would be letterboxed on the
 * other for most of its life.</p>
 */
public record VideoStyles() implements CssGroup<VideoStyles> {

    public static final VideoStyles INSTANCE = new VideoStyles();

    /** The widget body: a column that fills the pane. */
    public record vid_root() implements CssClass<VideoStyles> {
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            gap: var(--space-2);
            height: 100%;
            min-height: 0;
            padding: var(--space-4);
            box-sizing: border-box;
            """; }
    }

    public record vid_title() implements CssClass<VideoStyles> {
        @Override public String body() { return """
            margin: 0;
            font-size: 18px;
            font-weight: 600;
            color: var(--color-text-title);
            """; }
    }

    public record vid_note() implements CssClass<VideoStyles> {
        @Override public String body() { return """
            margin: 0;
            font-size: 12px;
            color: var(--color-text-muted);
            """; }
    }

    /** Takes the remaining height and centres the player in it. */
    public record vid_stage() implements CssClass<VideoStyles> {
        @Override public String body() { return """
            flex: 1 1 auto;
            min-height: 0;
            container-type: size;
            display: flex;
            align-items: center;
            justify-content: center;
            """; }
    }

    /**
     * 16:9, fitted on whichever axis runs out first — see the rule for why
     * the stage is measured rather than the width simply set to 100%.
     */
    public record vid_frame() implements CssClass<VideoStyles> {
        @Override public String body() { return """
            aspect-ratio: 16 / 9;
            /* Fit on whichever axis runs out first. width:100% alone lets
               aspect-ratio compute a height the pane cannot give, and max-height
               then clips it - the ratio breaks rather than the box shrinking.
               Measuring the stage (cqh) makes the width the smaller of "all of it"
               and "as wide as this height allows at 16:9". */
            width: min(100%, calc(100cqh * 16 / 9));
            height: auto;
            border: 0;
            border-radius: var(--radius-md);
            background: var(--color-surface-recessed);
            """; }
    }

    @Override
    public CssImportsFor<VideoStyles> cssImports() {
        return CssImportsFor.none(this);
    }

    @Override
    public List<CssClass<VideoStyles>> cssClasses() {
        return List.of(new vid_root(), new vid_title(), new vid_note(),
                       new vid_stage(), new vid_frame());
    }
}
