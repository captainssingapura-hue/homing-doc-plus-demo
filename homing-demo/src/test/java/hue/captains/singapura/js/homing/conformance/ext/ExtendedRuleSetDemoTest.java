package hue.captains.singapura.js.homing.conformance.ext;

import hue.captains.singapura.js.homing.conformance.engine.ModuleClassifier;
import hue.captains.singapura.js.homing.conformance.rules.DefaultJsRulePolicy;
import hue.captains.singapura.js.homing.conformance.rules.Finding;
import hue.captains.singapura.js.homing.conformance.rules.JsRulePolicy;
import hue.captains.singapura.js.homing.conformance.rules.JsRuleSet;
import hue.captains.singapura.js.homing.conformance.rules.JsSource;
import hue.captains.singapura.js.homing.conformance.rules.ServedModule;
import hue.captains.singapura.js.homing.core.JsModuleType;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RFC 0044 extension demo — a runnable, end-to-end walk-through of a <b>downstream
 * component library extending the conformance policy</b> with its own JS module
 * type and rule set. It exercises exactly the dispatch the {@code
 * ConformanceEngine} performs — {@code policy.rulesFor(type).checkAll(served)} —
 * over synthesized served modules, so it proves the mechanism without needing a
 * running server.
 *
 * <p>The story, in order:</p>
 * <ol>
 *   <li>The library declares a {@link GameLoopModuleType#GAME_LOOP} type and
 *       registers a rule set for it via {@code extendedWith} ({@link
 *       GameLoopConformance}).</li>
 *   <li>Its own {@link RafGameLoopRule} flags a timer-driven loop and passes a
 *       {@code requestAnimationFrame} one.</li>
 *   <li>The framework <b>global</b> rules it folded in still ride along.</li>
 *   <li>The framework's standard types are undisturbed, and a downstream cannot
 *       remap one — the framework stays closed for modification.</li>
 *   <li>A crate <b>declares</b> a real demo module as {@code GAME_LOOP}, and the
 *       classifier honours that declaration over structural inference.</li>
 * </ol>
 */
class ExtendedRuleSetDemoTest {

    private static final JsModuleType GAME_LOOP = GameLoopModuleType.GAME_LOOP;
    private static final JsRulePolicy POLICY = GameLoopConformance.POLICY;

    private static ServedModule gameLoop(String name, String... lines) {
        return new ServedModule(name, GAME_LOOP, JsSource.of(lines));
    }

    @Test
    void aGameLoopDrivenByRequestAnimationFrameIsClean() {
        JsRuleSet rules = POLICY.rulesFor(GAME_LOOP);
        ServedModule clean = gameLoop("demo.CleanLoop",
                "function frame(t) { update(t); render(); requestAnimationFrame(frame); }",
                "requestAnimationFrame(frame);");

        List<Finding> findings = rules.checkAll(clean);

        System.out.println("[ext-demo] rAF-driven game loop → " + findings.size() + " finding(s)");
        assertEquals(List.of(), findings, "a requestAnimationFrame loop is compliant");
    }

    @Test
    void aTimerDrivenGameLoopIsFlaggedByTheDownstreamRule() {
        JsRuleSet rules = POLICY.rulesFor(GAME_LOOP);
        ServedModule timer = gameLoop("demo.TimerLoop",
                "setInterval(function () { update(); render(); }, 16);");

        List<Finding> findings = rules.checkAll(timer);

        findings.forEach(f -> System.out.println("[ext-demo] FLAG " + f.rule().value() + ": " + f.message()));
        assertEquals(1, findings.size(), "the timer loop trips exactly the game-loop rule");
        assertEquals("raf-game-loop", findings.get(0).rule().value());
    }

    @Test
    void frameworkGlobalRulesRideAlongInTheDownstreamSet() {
        JsRuleSet rules = POLICY.rulesFor(GAME_LOOP);
        // A CDN import: the library reused the framework's no-cdn-import global rule,
        // so it fires even inside a downstream-owned rule set.
        ServedModule cdn = gameLoop("demo.CdnLoop",
                "import { tween } from \"https://cdn.example.com/tween.js\";",
                "requestAnimationFrame(frame);");

        List<Finding> findings = rules.checkAll(cdn);

        assertTrue(findings.stream().anyMatch(f -> f.rule().value().equals("no-cdn-import")),
                "the folded-in framework global rule still applies to the extension type");
    }

    @Test
    void standardTypesAreUndisturbedAndCannotBeRemapped() {
        // The composite still routes a standard type to the framework's own rule set.
        JsRuleSet consumerViaComposite = POLICY.rulesFor(StandardJsModuleType.CONSUMER);
        JsRuleSet consumerFramework = DefaultJsRulePolicy.INSTANCE.rulesFor(StandardJsModuleType.CONSUMER);
        assertEquals(consumerFramework.id(), consumerViaComposite.id(),
                "a standard type keeps the framework's rule set through the composite");

        // Even if a downstream tries to remap CONSUMER, the framework wins — closed for modification.
        JsRulePolicy sneaky = DefaultJsRulePolicy.INSTANCE.extendedWith(
                Map.<JsModuleType, JsRuleSet>of(StandardJsModuleType.CONSUMER, GameLoopConformance.GAME_LOOP_RULES));
        assertEquals("consumer", sneaky.rulesFor(StandardJsModuleType.CONSUMER).id().value(),
                "a standard type can never be remapped by an extension");

        // An extension type nobody registered is a hard error, not a silent pass.
        JsModuleType unregistered = new JsModuleType() {
            @Override public String slug()  { return "unregistered"; }
            @Override public String label() { return "Unregistered"; }
        };
        assertThrows(IllegalArgumentException.class, () -> POLICY.rulesFor(unregistered),
                "an unregistered extension type must fail loudly");
    }

    @Test
    void aCrateDeclarationOfTheDownstreamTypeIsHonouredByTheClassifier() {
        var entry = GameLoopDemoCrate.INSTANCE.entries().get(0);

        // The crate DECLARED this DomModule as GAME_LOOP; the declaration wins over the
        // structural inference that would otherwise class it CONSUMER.
        JsModuleType classified = ModuleClassifier.classify(entry);
        assertEquals(GameLoopModuleType.GAME_LOOP, classified,
                "the crate's declared type wins over structural classification");

        // …and the engine would then hold it to the downstream's game-loop rule set.
        assertEquals(GameLoopConformance.GAME_LOOP_RULES.id(), POLICY.rulesFor(classified).id(),
                "a declared game-loop module resolves to the downstream rule set");
        System.out.println("[ext-demo] " + entry.moduleClass()
                + " declared as '" + classified.slug() + "' → rule set '"
                + POLICY.rulesFor(classified).id().value() + "'");
    }
}
