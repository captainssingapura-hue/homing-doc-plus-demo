package hue.captains.singapura.js.homing.demo.playground;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Target.*;
import static hue.captains.singapura.js.homing.design.Emphasis.*;
import static hue.captains.singapura.js.homing.design.Interaction.*;
import static hue.captains.singapura.js.homing.design.Text.*;

/**
 * The table demo's own classes — structure only; every element wears what
 * it means. Two routes to a lifted cell or row sit side by side here:
 *
 * <ul>
 *   <li><b>State</b> — {@link tdm_cell} wears the {@code Interactive} pairs and
 *       flips {@code aria-selected}, {@code aria-current} or
 *       {@code data-highlighted}; the design's word at that slot is the look.</li>
 *   <li><b>Class</b> — {@link tdm_cell_lifted} and {@link tdm_row_lifted} wear
 *       the {@code Selected} pairs and are applied beside the base by the
 *       component when it decides a thing is lifted; the sheet's precedence
 *       (a state semantic after a layer's) is what makes "beside" win.</li>
 * </ul>
 *
 * A cell takes a shadow and a transform; a row takes a filter — a browser
 * paints no shadow and no transform on a table row.
 */
public record TableDemoStyles() implements CssGroup<TableDemoStyles> {
    public static final TableDemoStyles INSTANCE = new TableDemoStyles();

    public record tdm_root() implements CssClass<TableDemoStyles> {
        @Override public String body() { return """
                padding: 16px;
                height: 100%;
                box-sizing: border-box;
                overflow: auto;
                display: flex;
                flex-direction: column;
                gap: 8px;
                """; }
    }
    public record tdm_note() implements CssClass<TableDemoStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class), of(Caption.class, Type.Scale.class), of(Body.class, Type.Face.class)); }
        @Override public String body() { return ""; }
    }
    public record tdm_heading() implements CssClass<TableDemoStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Kicker.class, Color.Ink.class), of(Kicker.class, Type.Scale.class), of(Kicker.class, Type.Weight.class), of(Kicker.class, Type.Treatment.class)); }
        @Override public String body() { return """
                margin-top: 12px;
                """; }
    }

    /**
     * An interactive cell: one class, every state a slot — hover, selected,
     * current, highlighted, focus. Stacks above its neighbours while lifted,
     * so its shadow is not painted over by the cell after it.
     */
    public record tdm_cell() implements CssClass<TableDemoStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(
                of(Interactive.class, Color.Surface.class), of(Interactive.class, Color.Ink.class),
                of(Interactive.class, Color.Edge.class), of(Interactive.class, Shape.Rule.class),
                of(Interactive.class, Shape.Shadow.class), of(Interactive.class, Motion.Transform.class),
                of(Interactive.class, Motion.Ease.class), of(Interactive.class, Affordance.Cursor.class)); }
        @Override public String body() { return """
                padding: 10px 14px;
                vertical-align: top;
                position: relative;
                &[aria-selected="true"], &[data-highlighted] { z-index: 2; }
                """; }
    }

    /** The component's own word for a lifted row: the Selected pairs, applied beside st_tr. */
    public record tdm_row_lifted() implements CssClass<TableDemoStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(
                of(Selected.class, Color.Surface.class), of(Selected.class, Color.Ink.class),
                of(Selected.class, Color.Edge.class), of(Selected.class, Effect.Filter.class)); }
        @Override public String body() { return """
                z-index: 1;
                """; }
    }
    /** The component's own word for a lifted cell: the Selected pairs, applied beside tdm_cell. */
    public record tdm_cell_lifted() implements CssClass<TableDemoStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(
                of(Selected.class, Color.Surface.class), of(Selected.class, Color.Ink.class),
                of(Selected.class, Color.Edge.class), of(Selected.class, Shape.Shadow.class),
                of(Selected.class, Motion.Transform.class)); }
        @Override public String body() { return """
                z-index: 2;
                """; }
    }

    @Override
    public List<CssClass<TableDemoStyles>> cssClasses() {
        return List.of(new tdm_root(), new tdm_note(), new tdm_heading(), new tdm_cell(), new tdm_row_lifted(), new tdm_cell_lifted());
    }
}
