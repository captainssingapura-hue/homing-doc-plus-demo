package hue.captains.singapura.js.homing.conformance.ext;

import hue.captains.singapura.js.homing.conformance.rules.DefaultJsRulePolicy;
import hue.captains.singapura.js.homing.conformance.rules.JsRulePolicy;
import hue.captains.singapura.js.homing.conformance.rules.JsRuleSet;
import hue.captains.singapura.js.homing.conformance.rules.MaxEffectiveLinesRule;
import hue.captains.singapura.js.homing.conformance.rules.NoCdnImportRule;
import hue.captains.singapura.js.homing.conformance.rules.RuleSetId;
import hue.captains.singapura.js.homing.core.JsModuleType;

import java.util.List;
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
 * <p>Note the rule set: it <b>reuses two framework global rules</b> ({@code
 * no-cdn-import}, {@code max-effective-lines}) and adds the library's own {@link
 * RafGameLoopRule}. A downstream is free to fold in whichever framework rules it
 * wants and layer its own on top — a rule is shared, composable data.</p>
 */
public final class GameLoopConformance {

    private GameLoopConformance() {}

    /** The downstream's rule set for a game-loop module: framework globals + its own rule. */
    public static final JsRuleSet GAME_LOOP_RULES = new JsRuleSet(
            new RuleSetId("game-loop"), "Game loop",
            List.of(NoCdnImportRule.INSTANCE, MaxEffectiveLinesRule.INSTANCE, RafGameLoopRule.INSTANCE));

    /** <i>The</i> framework policy, extended with this library's {@code GAME_LOOP} type → rule set. */
    public static final JsRulePolicy POLICY = DefaultJsRulePolicy.INSTANCE.extendedWith(
            Map.<JsModuleType, JsRuleSet>of(GameLoopModuleType.GAME_LOOP, GAME_LOOP_RULES));
}
