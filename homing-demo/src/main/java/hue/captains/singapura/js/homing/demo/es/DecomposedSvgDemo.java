package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.libs.ThreeJs;

import java.util.List;

public record DecomposedSvgDemo() implements AppModule<DecomposedSvgDemo.Params, DecomposedSvgDemo> {

    record appMain() implements AppModule._AppMain<Params, DecomposedSvgDemo> {}

    public record link() implements AppLink<DecomposedSvgDemo> {}

    /**
     * @param animal optional initial animal name; {@code null} → URL fallback,
     *               then default.
     */
    public record Params(String animal) implements AppModule._Param {}

    public static final DecomposedSvgDemo INSTANCE = new DecomposedSvgDemo();

    @Override public Class<Params> paramsType() { return Params.class; }

    @Override
    public String title() {
        return "SVG Decomposer";
    }

    @Override
    public ImportsFor<DecomposedSvgDemo> imports() {
        return ImportsFor.<DecomposedSvgDemo>builder()
                .add(new ModuleImports<>(List.of(
                        new ThreeJs.Scene(),
                        new ThreeJs.PerspectiveCamera(),
                        new ThreeJs.WebGLRenderer(),
                        new ThreeJs.AmbientLight(),
                        new ThreeJs.DirectionalLight(),
                        new ThreeJs.Color(),
                        new ThreeJs.Box3(),
                        new ThreeJs.Vector3()
                ), ThreeJs.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new SvgDecomposer.decomposeSvg()
                ), SvgDecomposer.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new CuteAnimal.turtle(),
                        new CuteAnimal.ghost(),
                        new CuteAnimal.broom(),
                        new CuteAnimal.penguin(),
                        new CuteAnimal.crocodile(),
                        new CuteAnimal.whale()
                ), CuteAnimal.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DecomposedSvgDemo> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
