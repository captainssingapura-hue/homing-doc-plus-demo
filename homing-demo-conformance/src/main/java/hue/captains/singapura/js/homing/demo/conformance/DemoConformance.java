package hue.captains.singapura.js.homing.demo.conformance;

import hue.captains.singapura.js.homing.conformance.rules.FindingGrader;
import hue.captains.singapura.js.homing.conformance.rules.JsRulePolicy;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.demo.bare.BareAppCrate;
import hue.captains.singapura.js.homing.demo.site.DemoSiteCrate;
import hue.captains.singapura.js.homing.demo.workspace.DemoWorkspaceCrate;
import hue.captains.singapura.js.homing.demo.workspacewidgets.DemoWorkspaceWidgetsCrate;
import hue.captains.singapura.js.homing.demo.workspacewidgets.conformance.GameLoopConformance;
import hue.captains.singapura.js.homing.mermaidmirror.MermaidSiteCrate;
import hue.captains.singapura.js.homing.site.demo.gallery.GalleryCrate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The demo's conformance, in one place so the gate ({@code DemoConformanceTest}), the export
 * ({@link DemoConformanceExport}) and the studio ({@link DemoConformanceStudioServer}) grade
 * identically. A downstream of the framework's, with one difference that is the point of the
 * demo: the policy is extended - the widgets declare their own module type, the game loop, and
 * their own rule set for it ({@link GameLoopConformance}), and the platformer's play loop is held
 * to it. Strict, with no ledger: the demo was cut on the new stack and carries no debt.
 *
 * <p>The JS rules only. The CSS graph laws stay with each crate's own gate, beside the check that
 * its designs bind every word it wears - the bare app's design is its own, not the framework's.</p>
 */
public final class DemoConformance {

    private DemoConformance() {}

    /** Every crate the demo serves: what the gate grades and the studio browses. */
    public static final List<Crate> TOP_LEVEL = List.of(
            DemoWorkspaceWidgetsCrate.INSTANCE,
            DemoWorkspaceCrate.INSTANCE,
            GalleryCrate.INSTANCE,
            DemoSiteCrate.INSTANCE,
            MermaidSiteCrate.INSTANCE,
            BareAppCrate.INSTANCE);

    /** The framework's policy, extended with the widgets' game-loop rule set (RFC 0044). */
    public static final JsRulePolicy POLICY = GameLoopConformance.POLICY;

    /** The served identities of the demo's own modules - what is graded. */
    public static Set<String> ownModules() {
        return TOP_LEVEL.stream().flatMap(c -> c.entries().stream()).map(e -> e.moduleClass()).collect(Collectors.toSet());
    }

    /** Strict, with no allowances and no baseline: a finding is fixed, never filed. */
    public static FindingGrader grader() {
        return FindingGrader.STRICT;
    }
}
