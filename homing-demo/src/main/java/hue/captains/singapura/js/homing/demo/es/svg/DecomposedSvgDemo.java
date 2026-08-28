package hue.captains.singapura.js.homing.demo.es.svg;
import hue.captains.singapura.js.homing.demo.es.animation.CuteAnimal;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.libs.ThreeJs;

import java.util.List;

@LegacyAppMain(reason = "Three.js decomposition demo; opportunistic migration after RFC 0024 P3.")
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

    /**
     * RFC 0051 — {@code animal} is OPTIONAL: absent means the app's own
     * default, so this reports Ok with a null rather than Missing.
     *
     * <p>Declaring a codec is what stops {@code ParamsWriter} emitting a
     * module-level {@code params} const derived from
     * {@code window.location.search}. This app used to carry both — the
     * generated const and the {@code appMain} argument — and picked between
     * them at runtime, which is two answers in one module agreeing only for
     * as long as no default, redirect or coercion differed.</p>
     */
    public static final ParamCodec<Params> CODEC = new ParamCodec<>() {
        @Override public Decoded<Params> from(java.util.Map<String, java.util.List<String>> query) {
            return Decoded.ok(new Params(QueryString.first(query, "animal")));
        }
        @Override public java.util.Map<String, java.util.List<String>> to(Params params) {
            var out = QueryString.params();
            QueryString.put(out, "animal", params.animal());
            return out;
        }
    };

    @Override public ParamCodec<Params> paramCodec() { return CODEC; }

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
