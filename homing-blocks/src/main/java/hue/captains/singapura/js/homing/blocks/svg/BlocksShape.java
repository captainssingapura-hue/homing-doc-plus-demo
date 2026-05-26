package hue.captains.singapura.js.homing.blocks.svg;

import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgGroup;

import java.util.List;

/**
 * A minimal SvgGroup with one SvgBeing — used by the SvgDoc block's
 * demo. Demonstrates the SvgGroup + SvgBeing pattern in its smallest
 * shape. The {@code star.svg} classpath resource provides the rendered
 * markup; the framework's {@code SvgGroupContentProvider} resolves it
 * via the typed reference at runtime.
 */
public record BlocksShape() implements SvgGroup<BlocksShape> {

    public record star() implements SvgBeing<BlocksShape> {}

    public static final BlocksShape INSTANCE = new BlocksShape();

    @Override
    public List<SvgBeing<BlocksShape>> svgBeings() {
        return List.of(new star());
    }

    @Override
    public ExportsOf<BlocksShape> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
