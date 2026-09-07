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
 *
 * <p><b>Why the rail is under the stage and not beside it.</b> A rail beside a
 * 16:9 player takes its width from the video, and a pane is already as narrow as
 * the user's divider left it. Under the stage the rail costs one row of height
 * and never competes: the player keeps the whole width, and five takes that do
 * not fit scroll sideways instead of squeezing the thing they are about.</p>
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

    /** Dish on the left, position in the list on the right, on one baseline. */
    public record vid_head() implements CssClass<VideoStyles> {
        @Override public String body() { return """
            display: flex;
            align-items: baseline;
            justify-content: space-between;
            gap: var(--space-3);
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

    /**
     * "3 / 5". Tabular figures on purpose — a counter that changes width as it
     * counts drags the heading beside it around.
     */
    public record vid_count() implements CssClass<VideoStyles> {
        @Override public String body() { return """
            flex: 0 0 auto;
            font-size: 12px;
            font-variant-numeric: tabular-nums;
            color: var(--color-text-muted);
            """; }
    }

    /** Says which take is playing. Reactive — it is the selection, in words. */
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

    /**
     * The strip of takes. Fixed height at the foot of the column, scrolling
     * sideways when the takes outrun the pane — the house rule for wide content,
     * and the reason the player never has to give up width for it.
     */
    public record vid_rail() implements CssClass<VideoStyles> {
        @Override public String body() { return """
            flex: 0 0 auto;
            display: flex;
            align-items: center;
            gap: var(--space-2);
            overflow-x: auto;
            padding-bottom: var(--space-1);
            """; }
    }

    /** One take. Quiet by default; one line, clipped rather than wrapped. */
    public record vid_take() implements CssClass<VideoStyles> {
        @Override public String body() { return """
            flex: 0 0 auto;
            max-width: 180px;
            font: inherit;
            font-size: 12px;
            padding: var(--space-1) var(--space-3);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-sm);
            background: var(--color-surface);
            color: var(--color-text-primary);
            cursor: pointer;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            """; }
    }

    /**
     * The take on the stage. A filled shape, like the dialog's primary action —
     * the one place in the strip that reads as solid, which is what makes it
     * findable without a second size or weight.
     */
    public record vid_take_on() implements CssClass<VideoStyles> {
        @Override public String body() { return """
            background: var(--color-accent);
            border-color: var(--color-accent);
            color: var(--color-accent-on);
            font-weight: 600;
            """; }
    }

    /**
     * {@code :focus-visible} rather than {@code :focus}, which is what the rest
     * of the studio uses. The strip activates on click as well as on Enter, and
     * a ring left behind by the pointer would mark a take the keyboard is not
     * on — the ring here means "arrows move from here", so it must appear only
     * when arrows are how you arrived.
     */
    public record vid_take_focus() implements CssClass<VideoStyles> {
        @Override public String pseudoState() { return ":focus-visible"; }
        @Override public String body() { return """
            outline: 2px solid var(--color-accent-emphasis);
            outline-offset: 1px;
            """; }
    }

    @Override
    public CssImportsFor<VideoStyles> cssImports() {
        return CssImportsFor.none(this);
    }

    @Override
    public List<CssClass<VideoStyles>> cssClasses() {
        return List.of(new vid_root(), new vid_head(), new vid_title(), new vid_count(),
                       new vid_note(), new vid_stage(), new vid_frame(),
                       new vid_rail(), new vid_take(), new vid_take_on(), new vid_take_focus());
    }
}
