package hue.captains.singapura.js.homing.blocks.svg;

import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.studio.base.SvgDoc;

/**
 * Live demo SvgDoc — a simple themed star using {@link BlocksShape}.
 * The doc is a constant {@code SvgDoc&lt;BlocksShape&gt;} the catalogue
 * registers directly.
 */
public final class SvgDocDemoDoc {

    private SvgDocDemoDoc() {}

    public static final SvgDoc<BlocksShape> INSTANCE = new SvgDoc<>(
            new SvgRef<>(BlocksShape.INSTANCE, new BlocksShape.star()),
            "SvgDoc — Demo (a themed star)",
            "A live SvgDoc rendering a simple star via currentColor. "
          + "Java source: SvgDocDemoDoc.java + BlocksShape.java + star.svg.");
}
