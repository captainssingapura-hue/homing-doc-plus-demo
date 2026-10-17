package hue.captains.singapura.js.homing.demo.conformance;

import hue.captains.singapura.js.homing.conformance.engine.ConformanceEngine;
import hue.captains.singapura.js.homing.conformance.engine.ServedModuleRenderer;
import hue.captains.singapura.js.homing.conformance.rules.DeclaredDebts;
import hue.captains.singapura.js.homing.conformance.rules.Finding;
import hue.captains.singapura.js.homing.conformance.rules.GradedFinding;
import hue.captains.singapura.js.homing.demo.workspacewidgets.conformance.GameLoopModuleType;
import hue.captains.singapura.js.homing.design.styling.InstalledStyling;
import hue.captains.singapura.js.homing.relgrid.house.GridFrameBaseModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerSceneBaseModule;
import hue.captains.singapura.js.homing.ui.styles.HouseStyles;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The demo's gate: every module the demo serves keeps its lane under the extended policy, the
 * game loop among them under its own. Strict - no ledger. The CSS graph laws are each crate's
 * own gate's, over the palettes that crate's classes reach.
 */
class DemoConformanceTest {

    /** The demo's instance, as its site installs it - the house's styling with the grid's frame: what is made from its taxonomy is served (D51). */
    @BeforeAll
    static void theSitesInstance_installed() { InstalledStyling.install(HouseStyles.INSTANCE.styling(List.of(GridFrameBaseModule.INSTANCE, PlatformerSceneBaseModule.INSTANCE))); }

    @Test
    void everyServedModuleKeepsItsLane_strictly() {
        List<Finding> raw = new ConformanceEngine(DemoConformance.POLICY, new ServedModuleRenderer()).checkCrates(DemoConformance.TOP_LEVEL);
        assertEquals(List.of(), DeclaredDebts.INSTANCE.unbalanced(DemoConformance.TOP_LEVEL, raw), "a debt not as declared - owed exactly, never more");
        List<GradedFinding> errors = DemoConformance.grader().grade(raw).stream().filter(GradedFinding::isError).toList();
        assertEquals(List.of(), errors.stream().map(g -> describe(g.finding())).toList(),
                "the demo carries no debt but what its modules declare - fix these");
    }

    @Test
    void theGameLoopIsDeclared_andHeldToItsOwnRuleSet() {
        long loops = DemoConformance.TOP_LEVEL.stream().flatMap(c -> c.entries().stream())
                .filter(e -> e.declaredType() == GameLoopModuleType.GAME_LOOP).count();
        assertTrue(loops >= 1, "the demo declares a game loop, so the extension is exercised, not just defined");
        assertEquals("game-loop", DemoConformance.POLICY.rulesFor(GameLoopModuleType.GAME_LOOP).id().value());
    }

    private static String describe(Finding f) {
        return f.moduleClass() + " [" + f.rule().value() + "] @" + f.line() + " " + f.message();
    }
}
