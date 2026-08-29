package hue.captains.singapura.js.homing.demo.es.svg;
import hue.captains.singapura.js.homing.demo.es.animation.CuteAnimal;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.libs.ThreeJs;

import java.util.List;

@LegacyAppMain(reason = "Three.js coin viewer; migration follows RFC 0024 P3's canonical pattern.")
public record ExtrudedTurtleDemo() implements AppModule<ExtrudedTurtleDemo.Params, ExtrudedTurtleDemo> {

    record appMain() implements AppModule._AppMain<Params, ExtrudedTurtleDemo> {}

    public record link() implements AppLink<ExtrudedTurtleDemo> {}

    /**
     * @param animal optional initial animal name; {@code null} → fall back to URL
     *               param, then default {@code "turtle"}.
     */
    public record Params(String animal) implements AppModule._Param {}

    public static final ExtrudedTurtleDemo INSTANCE = new ExtrudedTurtleDemo();

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
        return "SVG Coin";
    }

    @Override
    public ImportsFor<ExtrudedTurtleDemo> imports() {
        return ImportsFor.<ExtrudedTurtleDemo>builder()
                .add(new ModuleImports<>(List.of(
                        new ThreeJs.Scene(),
                        new ThreeJs.PerspectiveCamera(),
                        new ThreeJs.WebGLRenderer(),
                        new ThreeJs.AmbientLight(),
                        new ThreeJs.DirectionalLight(),
                        new ThreeJs.Color(),
                        new ThreeJs.Box3(),
                        new ThreeJs.Vector3(),
                        new ThreeJs.CylinderGeometry(),
                        new ThreeJs.MeshStandardMaterial(),
                        new ThreeJs.Mesh(),
                        new ThreeJs.Group()
                ), ThreeJs.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new SvgExtruder.extrudeSvg()
                ), SvgExtruder.INSTANCE))
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
    public ExportsOf<ExtrudedTurtleDemo> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
