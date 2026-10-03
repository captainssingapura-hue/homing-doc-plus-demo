package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgGroup;

import java.util.List;

/**
 * The viewer experiment's drawing, an SVG served as text: the experiment's
 * own focus tree - the page's root, the viewer, the viewport - on a grid to
 * pan over, in ink only, so it takes the theme's colour. The page parses it
 * and hands the element to the viewer.
 */
public record ViewerDrawings() implements SvgGroup<ViewerDrawings> {

    public record focusTree() implements SvgBeing<ViewerDrawings> {}

    public static final ViewerDrawings INSTANCE = new ViewerDrawings();

    @Override
    public List<SvgBeing<ViewerDrawings>> svgBeings() { return List.of(new focusTree()); }

    @Override
    public ExportsOf<ViewerDrawings> exports() { return new ExportsOf<>(this, List.copyOf(svgBeings())); }
}
