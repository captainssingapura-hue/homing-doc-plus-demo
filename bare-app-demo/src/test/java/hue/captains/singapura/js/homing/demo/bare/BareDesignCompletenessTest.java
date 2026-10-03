package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.conformance.rules.CrateClosure;
import hue.captains.singapura.js.homing.conformance.rules.CssConformance;
import hue.captains.singapura.js.homing.conformance.rules.Finding;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.design.Deployment;
import hue.captains.singapura.js.homing.design.Design;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * RFC 0066 — the completeness law, here: every theme this deployment lists
 * binds every word the board wears or reads. The same check the framework's
 * crates run over the house designs, run over the bare app's one design in
 * its two palettes: no findings.
 */
class BareDesignCompletenessTest {

    @Test
    void oneBase_inTwoPalettes() {
        assertEquals(List.of("bare"), BareThemes.REGISTRY.bases().stream().map(Theme::slug).toList());
        assertEquals(List.of("chalk", "slate"), BareThemes.REGISTRY.colours().stream().map(Theme::slug).toList());
        assertEquals(List.of("bare", "bare_slate"), BareThemes.REGISTRY.themes().stream().map(Theme::slug).toList());
    }

    @Test
    void everyThemeBindsEveryWordTheBoardWears() {
        List<CssGroup<?>> groups = List.of(SwatchboardStyles.INSTANCE);
        var worn = Deployment.wornBy(groups);
        for (Theme t : BareThemes.REGISTRY.themes()) {
            var r = Deployment.of(worn, Deployment.scaledBy(groups), Deployment.grownBy(groups), (Design) t).resolve();
            assertEquals(List.of(), r.findings(), () -> t.slug() + ": " + r.findings());
        }
    }

    @Test
    void theCssGraphKeepsItsLaws() {
        List<Finding> findings = CssConformance.check(new ArrayList<>(CrateClosure.of(List.of(BareAppCrate.INSTANCE))));
        assertEquals(List.of(), findings, () -> findings.stream().map(Finding::fingerprint).toList().toString());
    }
}
