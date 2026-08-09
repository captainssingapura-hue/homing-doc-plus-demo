package hue.captains.singapura.js.homing.conformance.ext;

import hue.captains.singapura.js.homing.conformance.rules.DoctrineRef;
import hue.captains.singapura.js.homing.conformance.rules.Finding;
import hue.captains.singapura.js.homing.conformance.rules.JsRule;
import hue.captains.singapura.js.homing.conformance.rules.RuleId;
import hue.captains.singapura.js.homing.conformance.rules.ServedModule;

import java.util.ArrayList;
import java.util.List;

/**
 * RFC 0044 extension demo — a <b>hypothetical downstream</b> conformance rule.
 *
 * <p>The library's doctrine: a {@link GameLoopModuleType#GAME_LOOP} module must
 * drive animation on the compositor clock via {@code requestAnimationFrame},
 * never with wall-clock timers ({@code setInterval} / {@code setTimeout}) — those
 * drift, keep ticking while the tab is hidden, and fight the browser's frame
 * budget.</p>
 *
 * <p>A downstream rule is <b>just a {@link JsRule}</b> — the very contract the
 * framework's own rules implement: a pure check over one {@link ServedModule},
 * unit-testable in isolation, self-describing for the studio via {@link
 * #intent()} + {@link #basis()}. Nothing about it is second-class.</p>
 */
public record RafGameLoopRule() implements JsRule {

    public static final RafGameLoopRule INSTANCE = new RafGameLoopRule();

    @Override public RuleId      id()     { return new RuleId("raf-game-loop"); }
    @Override public String      intent() { return "A game loop must animate via requestAnimationFrame — never a setInterval/setTimeout wall-clock timer."; }
    @Override public DoctrineRef basis()  { return new DoctrineRef("compositor-clock"); }

    @Override
    public List<Finding> check(ServedModule module) {
        var findings = new ArrayList<Finding>();
        List<String> lines = module.lines();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("setInterval(") || line.contains("setTimeout(")) {
                findings.add(new Finding(module.moduleClass(), id(),
                        "wall-clock timer in a game loop (use requestAnimationFrame): " + line.trim(), i));
            }
        }
        return List.copyOf(findings);
    }
}
