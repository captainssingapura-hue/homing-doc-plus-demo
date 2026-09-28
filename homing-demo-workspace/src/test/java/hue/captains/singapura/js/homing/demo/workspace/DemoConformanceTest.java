package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.component.keyboard.KeyboardRegistry;
import hue.captains.singapura.js.homing.conformance.engine.ConformanceEngine;
import hue.captains.singapura.js.homing.conformance.engine.ServedModuleRenderer;
import hue.captains.singapura.js.homing.conformance.rules.CrateClosure;
import hue.captains.singapura.js.homing.conformance.rules.CrateConformance;
import hue.captains.singapura.js.homing.conformance.rules.CssConformance;
import hue.captains.singapura.js.homing.conformance.rules.DefaultJsRulePolicy;
import hue.captains.singapura.js.homing.conformance.rules.Finding;
import hue.captains.singapura.js.homing.conformance.rules.FindingGrader;
import hue.captains.singapura.js.homing.conformance.rules.GradedFinding;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.designs.HomingDesigns;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The demo's gate: the framework's conformance over {@code homing-demo-workspace},
 * as the widget sets' gates run it over theirs - crate integrity; every served
 * module graded strictly, with no baseline; the CSS graph laws; and the keys,
 * through the party. It has no sheet of its own: the widget sets' gates bind theirs.
 */
class DemoConformanceTest {

    private static final List<Crate> TOP = List.of(DemoWorkspaceCrate.INSTANCE);

    private static Set<String> own() {
        return TOP.stream().flatMap(c -> c.entries().stream()).map(e -> e.moduleClass()).collect(Collectors.toSet());
    }

    @Test
    void theCrateIsStructurallyComplete() {
        var result = CrateConformance.evaluate(new ArrayList<>(CrateClosure.of(TOP))).crates().get(DemoWorkspaceCrate.INSTANCE.name());
        assertEquals(List.of(), result.orphans(), "every served module must be declared");
        assertEquals(List.of(), result.illegalImports(), "every cross-crate import must be declared in requires()");
    }

    @Test
    void everyServedModuleKeepsItsLane_strictly() {
        List<Finding> raw = new ConformanceEngine(DefaultJsRulePolicy.INSTANCE, new ServedModuleRenderer()).checkCrates(TOP);
        List<GradedFinding> errors = FindingGrader.STRICT.grade(raw).stream().filter(GradedFinding::isError).toList();
        assertEquals(List.of(), errors.stream().map(g -> describe(g.finding())).toList(),
                "the demo carries no debt - fix these, there is no ledger to file them in");
    }

    @Test
    void theCssGraphKeepsItsLaws() {
        Set<String> own = own();
        List<Finding> css = CssConformance.check(new ArrayList<>(CrateClosure.of(TOP)), HomingDesigns.REGISTRY.palettes())
                .stream().filter(f -> own.contains(f.moduleClass())).toList();
        assertEquals(List.of(), css.stream().map(DemoConformanceTest::describe).toList());
    }

    /** The demo's modules take their keys through the party: none listens undeclared, none captures on the document. */
    @Test
    void keysComeThroughTheParty() {
        Set<String> ownNames = DemoWorkspaceCrate.INSTANCE.entries().stream()
                .map(e -> e.module().getClass().getSimpleName()).collect(Collectors.toSet());
        assertEquals(List.of(), KeyboardRegistry.undeclaredListeners(TOP).stream().filter(ownNames::contains).toList(),
                "a module that listens for keys declares them (NeedKeyboard)");
        assertEquals(List.of(), KeyboardRegistry.validate(TOP).stream()
                .filter(s -> ownNames.stream().anyMatch(s::contains)).toList(),
                "only the steward captures keys on the document");
    }

    private static String describe(Finding f) {
        return f.moduleClass() + " [" + f.rule().value() + "] @" + f.line() + " " + f.message();
    }
}
