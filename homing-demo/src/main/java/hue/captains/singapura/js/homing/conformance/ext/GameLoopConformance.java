package hue.captains.singapura.js.homing.conformance.ext;

import hue.captains.singapura.js.homing.conformance.rules.DefaultJsRulePolicy;
import hue.captains.singapura.js.homing.conformance.rules.JsRulePolicy;
import hue.captains.singapura.js.homing.conformance.rules.JsRuleSet;
import hue.captains.singapura.js.homing.conformance.rules.RuleSetId;
import hue.captains.singapura.js.homing.core.JsModuleType;

import java.util.Map;

/**
 * RFC 0044 extension demo — the <b>hypothetical downstream's conformance
 * registration</b>. This is the third leg of the Crate model: a component
 * library organizes its code, registers it for serving, and <b>registers it for
 * conformance</b> — the last of which happens here.
 *
 * <p><b>Extend, don't patch.</b> The library takes <i>the</i> framework policy
 * ({@link DefaultJsRulePolicy#INSTANCE}) and composes its own type + rule set on
 * top with {@link DefaultJsRulePolicy#extendedWith}. The framework's standard
 * types keep dispatching through their untouched exhaustive {@code switch}; the
 * new {@link GameLoopModuleType#GAME_LOOP} dispatches through the composite's
 * dictionary. The framework is closed for modification, open for extension.</p>
 *
 * <p><b>Extension is not exemption.</b> A game loop builds and styles DOM, so its
 * rule set sits in the framework's <b>DOM-owner lane</b>: the full discipline a
 * consumer carries, plus the library's own {@link RafGameLoopRule} on top. The
 * earlier shape — the two global rules plus the RAF rule and nothing about the
 * DOM — is exactly what {@code extendedWith} now refuses: it left
 * {@code MovingAnimalGame}'s inline style writes outside every DOM rule while
 * the type looked policed. The lane is the floor; the RAF rule is what the
 * library adds.</p>
 */
public final class GameLoopConformance {

    private GameLoopConformance() {}

    /** The downstream's rule set for a game-loop module: the DOM-owner lane + its own rule. */
    public static final JsRuleSet GAME_LOOP_RULES = DefaultJsRulePolicy.domOwnerLane(
            new RuleSetId("game-loop"), "Game loop", RafGameLoopRule.INSTANCE);

    /** <i>The</i> framework policy, extended with this library's {@code GAME_LOOP} type → rule set. */
    public static final JsRulePolicy POLICY = DefaultJsRulePolicy.INSTANCE.extendedWith(
            Map.<JsModuleType, JsRuleSet>of(GameLoopModuleType.GAME_LOOP, GAME_LOOP_RULES));
}
