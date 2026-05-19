package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.demo.es.CuteAnimal;
import hue.captains.singapura.js.homing.demo.es.DecomposedSvgDemo;
import hue.captains.singapura.js.homing.demo.es.ExtrudedSvgDemo;
import hue.captains.singapura.js.homing.demo.es.ExtrudedTurtleDemo;
import hue.captains.singapura.js.homing.studio.base.SvgDoc;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.composed.DocumentaryWidget;
import hue.captains.singapura.js.homing.studio.base.composed.SvgSegment;
import hue.captains.singapura.js.homing.studio.base.composed.TextSegment;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Per-animal {@link DocumentaryWidget} demo — a {@code ComposedDoc} that
 * interleaves the animal's canonical SVG with three typed interactive 3D
 * widgets, each parameterised with the same animal name. The widgets are
 * isolated islands; chrome composes once around the whole page.
 *
 * <p>Demonstrates the doctrinal claim that {@code DocumentaryWidget} is
 * <i>function-like</i>: one EsModule URL per widget <b>type</b>, reused
 * across all per-animal instances; the per-call {@code Params} record
 * carries the per-instance variation (which animal to preselect). The
 * browser caches the module once; the user-visible variation is data,
 * not code.</p>
 *
 * <p>Reached via the polymorphic doc viewer:
 * {@code /app?app=composed-viewer&id=<uuid>}.</p>
 */
public final class InteractiveAnimalDoc {

    private InteractiveAnimalDoc() {}

    /** All six per-animal interactive docs, in catalogue order. */
    public static final List<ComposedDoc> ALL = List.of(
            forAnimal(new CuteAnimal.turtle(),    "Turtle",    "Slow, steady, ancient."),
            forAnimal(new CuteAnimal.penguin(),   "Penguin",   "Antarctic in a tuxedo."),
            forAnimal(new CuteAnimal.crocodile(), "Crocodile", "Older than the dinosaurs."),
            forAnimal(new CuteAnimal.whale(),     "Whale",     "A skyscraper at sea."),
            forAnimal(new CuteAnimal.ghost(),     "Ghost",     "Halloween regular."),
            forAnimal(new CuteAnimal.broom(),     "Broom",     "Halloween regular."));

    /** Backwards-compatible alias for the turtle variant. */
    public static final ComposedDoc TURTLE = ALL.get(0);

    /**
     * Build a per-animal interactive doc. The same three widget EsModules
     * are reused across animals — only the typed {@code Params(animal)}
     * record varies per instance.
     *
     * @param being       the SvgBeing record (eg. {@code new CuteAnimal.turtle()})
     * @param displayName human-facing name, eg. {@code "Turtle"}
     * @param oneLiner    short one-line summary shown on the SVG tile
     */
    public static ComposedDoc forAnimal(SvgBeing<CuteAnimal> being, String displayName, String oneLiner) {
        var ref = new SvgRef<>(CuteAnimal.INSTANCE, being);
        var doc = new SvgDoc<>(ref, displayName, oneLiner);

        // SvgBeing record's simpleName is the animal key (eg "turtle").
        String animalKey = being.getClass().getSimpleName().toLowerCase(Locale.ROOT);

        var intro = new TextSegment(
                ("""
                 One page, four typed segments — a single `SvgSegment` carrying the canonical %s artwork, then three `DocumentaryWidget` segments embedding the framework's interactive 3D demos, each parameterised so the **%s** is pre-selected inside the widget.

                 The widgets aren't separate pages with their own chrome; they're sections of *this* page. The chrome composes once at the top level. Each widget is a typed `AppModule` loaded via dynamic ES module import. The same three EsModule URLs serve every per-animal doc — only the typed `Params(animal)` record varies per call, per the *Thin HTML, Typed JS* doctrine.
                 """).formatted(displayName.toLowerCase(Locale.ROOT), displayName.toLowerCase(Locale.ROOT)),
                Optional.of("Four typed segments, one page"));

        var theSvg = new SvgSegment(
                doc,
                Optional.of("Figure 1 — the canonical %s SVG, themed via currentColor (RFC 0017). The same `SvgDoc` instance that powers the leaf in the Animals tree."
                        .formatted(displayName.toLowerCase(Locale.ROOT))));

        var coinIntro = new TextSegment(
                """
                The coin gallery extrudes each animal's SVG silhouette into a 3D coin: front face, back face, edge. Three.js draws the meshes; the framework's typed-JS pipeline emits the module; the widget runs inside this segment without escaping its bounds.
                """,
                Optional.of("3D coin gallery"));

        var coinWidget = new DocumentaryWidget<>(
                ExtrudedTurtleDemo.INSTANCE,
                new ExtrudedTurtleDemo.Params(animalKey),
                Optional.of("Figure 2 — `DocumentaryWidget` embedding `ExtrudedTurtleDemo` with `Params(animal=\"%s\")`. Pick a different animal in the in-widget selector to swap the silhouette."
                        .formatted(animalKey)));

        var extruderIntro = new TextSegment(
                """
                The pure SVG-to-3D extruder runs the same `SvgExtruder` primitive without the coin frame — each closed path becomes its own extruded mesh, free-floating in the 3D scene.
                """,
                Optional.of("3D SVG extruder"));

        var extruderWidget = new DocumentaryWidget<>(
                ExtrudedSvgDemo.INSTANCE,
                new ExtrudedSvgDemo.Params(animalKey),
                Optional.of("Figure 3 — `DocumentaryWidget` embedding `ExtrudedSvgDemo` with `Params(animal=\"%s\")`."
                        .formatted(animalKey)));

        var decomposerIntro = new TextSegment(
                """
                The decomposer goes the other direction: SVG paths become individually positioned 3D meshes — useful for visualising layered or grouped artwork by spreading the layers across the Z axis.
                """,
                Optional.of("3D SVG decomposer"));

        var decomposerWidget = new DocumentaryWidget<>(
                DecomposedSvgDemo.INSTANCE,
                new DecomposedSvgDemo.Params(animalKey),
                Optional.of("Figure 4 — `DocumentaryWidget` embedding `DecomposedSvgDemo` with `Params(animal=\"%s\")`. Each subpath becomes its own positioned mesh; orbit and scrub the depth-spread."
                        .formatted(animalKey)));

        var closing = new TextSegment(
                """
                Four typed segments, one chrome. The widgets share nothing — no event bus, no shared client state, no cross-widget reference — but they share the page's typed wrapper, and they all read the same per-animal `Params` at mount time. *The bottom line is that it's still a Doc, not a full-fledged application.*
                """,
                Optional.of("What you're looking at"));

        return ComposedDoc.of(
                ComposedDoc.deterministicUuid("demo:interactive-animal:" + animalKey),
                displayName + ", Interactive",
                ("DocumentaryWidget demo (%s) — one ComposedDoc with the canonical %s SVG + three typed interactive 3D widgets (coin, extruder, decomposer) pre-selected to the %s. Chrome wraps the whole page once.")
                        .formatted(displayName.toLowerCase(Locale.ROOT), animalKey, animalKey),
                "DEMO",
                List.of(intro,
                        theSvg,
                        coinIntro, coinWidget,
                        extruderIntro, extruderWidget,
                        decomposerIntro, decomposerWidget,
                        closing));
    }
}
