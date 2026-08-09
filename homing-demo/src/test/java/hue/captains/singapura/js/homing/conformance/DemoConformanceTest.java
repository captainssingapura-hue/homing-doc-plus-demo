package hue.captains.singapura.js.homing.conformance;

import hue.captains.singapura.js.homing.conformance.engine.ConformanceEngine;
import hue.captains.singapura.js.homing.conformance.engine.ServedModuleRenderer;
import hue.captains.singapura.js.homing.conformance.rules.CrateClosure;
import hue.captains.singapura.js.homing.conformance.rules.CrateConformance;
import hue.captains.singapura.js.homing.conformance.rules.Finding;
import hue.captains.singapura.js.homing.conformance.rules.GradedFinding;
import hue.captains.singapura.js.homing.conformance.rules.Severity;
import hue.captains.singapura.js.homing.demo.conformance.DemoConformance;
import hue.captains.singapura.js.homing.demo.conformance.HomingDemoCrate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * RFC 0044 — homing-demo's conformance gate, on the <b>Crate model</b> and the
 * <b>extended policy</b> (shared with the studio export via {@link
 * DemoConformance}). Two checks:
 * <ol>
 *   <li><b>Crate integrity</b> — {@link HomingDemoCrate} declares every served
 *       module in the module (no orphans) and every cross-crate import is
 *       declared in {@code requires()} (no illegal imports).</li>
 *   <li><b>Rule conformance</b> — the served artifact of every crate module is
 *       graded by {@link DemoConformance#POLICY} (framework rules plus the
 *       downstream {@code game-loop} rule set). Pre-existing patterns are
 *       grandfathered in {@code demo-conformance-baseline.txt} (warned); a
 *       genuinely NEW violation fails the build.</li>
 * </ol>
 *
 * <p>These demo modules are imperative, SPA-shaped views that legitimately drive
 * the DOM directly; MovingAnimalGame is declared {@code GAME_LOOP} and so is held
 * to the downstream rule that flags its {@code setTimeout} audio timers. The
 * global {@code -Dconformance.allowPreExisting=false} makes the baseline fail too.</p>
 */
class DemoConformanceTest {

    private static final boolean ALLOW_PRE_EXISTING =
            Boolean.parseBoolean(System.getProperty("conformance.allowPreExisting", "true"));

    @Test
    void demoCrateIsStructurallyComplete() {
        CrateConformance.Result result = CrateConformance.evaluate(CrateClosure.of(DemoConformance.TOP_LEVEL));
        CrateConformance.CrateResult crate = result.crates().get(HomingDemoCrate.INSTANCE.name());
        assertNotNull(crate, "the homing-demo crate must be present in the evaluation");
        assertEquals(List.of(), crate.orphans(),
                "every served homing-demo module must be declared in HomingDemoCrate");
        assertEquals(List.of(), crate.illegalImports(),
                "every cross-crate import must be declared in HomingDemoCrate.requires()");
    }

    @Test
    void demoServedModulesAreConformant() {
        List<Finding> raw = new ConformanceEngine(DemoConformance.POLICY, new ServedModuleRenderer())
                .checkCrates(DemoConformance.TOP_LEVEL);
        List<GradedFinding> graded = DemoConformance.grader(ALLOW_PRE_EXISTING).grade(raw);

        List<GradedFinding> errors = graded.stream().filter(GradedFinding::isError).toList();
        graded.stream().filter(g -> g.severity() == Severity.WARNING)
                .forEach(g -> System.out.println("[demo-conformance] WARN " + describe(g)));

        assertEquals(List.of(), errors, () -> "demo conformance ERRORS (" + errors.size() + "):\n"
                + errors.stream().map(DemoConformanceTest::describe).collect(Collectors.joining("\n")));
    }

    private static String describe(GradedFinding g) {
        Finding f = g.finding();
        return f.moduleClass() + " [" + f.rule().value() + "] " + f.message()
                + (g.note().isBlank() ? "" : "  (" + g.note() + ")");
    }
}
