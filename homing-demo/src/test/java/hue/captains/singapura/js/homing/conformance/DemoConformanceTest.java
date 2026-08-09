package hue.captains.singapura.js.homing.conformance;

import hue.captains.singapura.js.homing.conformance.engine.ConformanceEngine;
import hue.captains.singapura.js.homing.conformance.rules.Baseline;
import hue.captains.singapura.js.homing.conformance.rules.Finding;
import hue.captains.singapura.js.homing.conformance.rules.FindingGrader;
import hue.captains.singapura.js.homing.conformance.rules.GradedFinding;
import hue.captains.singapura.js.homing.conformance.rules.Severity;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.demo.es.AnimalCell;
import hue.captains.singapura.js.homing.demo.es.DancingAnimals;
import hue.captains.singapura.js.homing.demo.es.DecomposedSvgDemo;
import hue.captains.singapura.js.homing.demo.es.ExtrudedSvgDemo;
import hue.captains.singapura.js.homing.demo.es.ExtrudedTurtleDemo;
import hue.captains.singapura.js.homing.demo.es.JumpPhysics;
import hue.captains.singapura.js.homing.demo.es.MovingAnimal;
import hue.captains.singapura.js.homing.demo.es.MovingAnimalGame;
import hue.captains.singapura.js.homing.demo.es.PlatformEngine;
import hue.captains.singapura.js.homing.demo.es.PlatformerBgm;
import hue.captains.singapura.js.homing.demo.es.SpinningAnimals;
import hue.captains.singapura.js.homing.demo.es.SvgDecomposer;
import hue.captains.singapura.js.homing.demo.es.SvgExtruder;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * RFC 0044 Phase 9 — homing-demo's conformance gate, on the <b>engine</b>
 * (replacing the four per-doctrine scanner subclasses). Runs {@link
 * ConformanceEngine#checkAll} over the demo's own served modules (the games,
 * animations, and SVG demos) — the framework modules they compose are gated in
 * core, not here.
 *
 * <p>These are imperative, SPA-shaped modules that legitimately drive the DOM
 * directly. The engine (which reads the served artifact) surfaces six
 * pre-existing patterns — the animation demos redraw with {@code
 * rootElement.replaceChildren()}, the SVG demos read the view param via {@code
 * URLSearchParams} — grandfathered in {@code demo-conformance-baseline.txt}
 * (warned, not failed). A genuinely NEW violation still fails the build. The
 * global {@code -Dconformance.allowPreExisting=false} makes the baseline fail too.</p>
 */
class DemoConformanceTest {

    private static final boolean ALLOW_PRE_EXISTING =
            Boolean.parseBoolean(System.getProperty("conformance.allowPreExisting", "true"));

    private static final FindingGrader GRADER = FindingGrader.STRICT
            .withBaseline(loadBaseline())
            .allowingPreExisting(ALLOW_PRE_EXISTING);

    /** The demo's own served modules (framework modules are gated in core). */
    private static final List<EsModule<?>> OWN_MODULES = List.of(
            AnimalCell.INSTANCE, DancingAnimals.INSTANCE, SpinningAnimals.INSTANCE,
            MovingAnimal.INSTANCE, MovingAnimalGame.INSTANCE, PlatformerBgm.INSTANCE,
            ExtrudedTurtleDemo.INSTANCE, DecomposedSvgDemo.INSTANCE, ExtrudedSvgDemo.INSTANCE,
            SvgDecomposer.INSTANCE, SvgExtruder.INSTANCE, JumpPhysics.INSTANCE, PlatformEngine.INSTANCE);

    @Test
    void demoServedModulesAreConformant() {
        List<Finding> raw = new ConformanceEngine().checkAll(OWN_MODULES);
        List<GradedFinding> graded = GRADER.grade(raw);

        List<GradedFinding> errors = graded.stream().filter(GradedFinding::isError).toList();
        graded.stream().filter(g -> g.severity() == Severity.WARNING)
                .forEach(g -> System.out.println("[demo-conformance] WARN " + describe(g)));

        assertEquals(List.of(), errors, () -> "demo conformance ERRORS (" + errors.size() + "):\n"
                + errors.stream().map(DemoConformanceTest::describe).collect(Collectors.joining("\n")));
    }

    private static Baseline loadBaseline() {
        try (InputStream in = DemoConformanceTest.class.getResourceAsStream("/demo-conformance-baseline.txt")) {
            if (in == null) return Baseline.EMPTY;
            var lines = new ArrayList<String>();
            try (var r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                for (String line; (line = r.readLine()) != null; ) lines.add(line);
            }
            return Baseline.of(lines);
        } catch (IOException e) {
            throw new UncheckedIOException("failed to load demo conformance baseline", e);
        }
    }

    private static String describe(GradedFinding g) {
        Finding f = g.finding();
        return f.moduleClass() + " [" + f.rule().value() + "] " + f.message()
                + (g.note().isBlank() ? "" : "  (" + g.note() + ")");
    }
}
