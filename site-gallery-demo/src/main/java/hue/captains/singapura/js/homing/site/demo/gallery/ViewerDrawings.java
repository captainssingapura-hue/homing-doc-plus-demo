package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgGroup;

import java.util.List;

/**
 * The viewer experiment's drawings, SVGs served as text, in ink only so they
 * take the theme's colour: the chain - the page's root, the viewer, the
 * viewport - drawn at the head of the board, and a specimen for each viewer
 * on it. The page parses them.
 */
public record ViewerDrawings() implements SvgGroup<ViewerDrawings> {

    public record focusTree() implements SvgBeing<ViewerDrawings> {}
    /** A small drawing for a viewer on the board: shapes and a line of small print to zoom in on. */
    public record specimen() implements SvgBeing<ViewerDrawings> {}

    public static final ViewerDrawings INSTANCE = new ViewerDrawings();

    @Override
    public List<SvgBeing<ViewerDrawings>> svgBeings() { return List.of(new focusTree(), new specimen()); }

    @Override
    public ExportsOf<ViewerDrawings> exports() { return new ExportsOf<>(this, List.copyOf(svgBeings())); }
}
