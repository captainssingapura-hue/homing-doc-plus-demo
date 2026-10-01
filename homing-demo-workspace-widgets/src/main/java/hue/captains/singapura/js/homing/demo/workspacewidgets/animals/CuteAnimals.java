package hue.captains.singapura.js.homing.demo.workspacewidgets.animals;

import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgGroup;

import java.util.List;

/**
 * The six cute animals' pictures, each an SVG served as text - the demo's
 * artwork, carried over from the old playground as it was. A widget never
 * reads them itself: {@link AnimalsModule} names each animal and draws it.
 */
public record CuteAnimals() implements SvgGroup<CuteAnimals> {

    public record turtle()    implements SvgBeing<CuteAnimals> {}
    public record ghost()     implements SvgBeing<CuteAnimals> {}
    public record broom()     implements SvgBeing<CuteAnimals> {}
    public record penguin()   implements SvgBeing<CuteAnimals> {}
    public record crocodile() implements SvgBeing<CuteAnimals> {}
    public record whale()     implements SvgBeing<CuteAnimals> {}

    public static final CuteAnimals INSTANCE = new CuteAnimals();

    @Override
    public List<SvgBeing<CuteAnimals>> svgBeings() {
        return List.of(new turtle(), new ghost(), new broom(), new penguin(), new crocodile(), new whale());
    }

    @Override
    public ExportsOf<CuteAnimals> exports() { return new ExportsOf<>(this, List.copyOf(svgBeings())); }
}
