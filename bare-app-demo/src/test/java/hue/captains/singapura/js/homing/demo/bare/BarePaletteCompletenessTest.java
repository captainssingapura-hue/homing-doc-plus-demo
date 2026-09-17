package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.conformance.rules.CssConformance;
import hue.captains.singapura.js.homing.conformance.rules.Finding;
import hue.captains.singapura.js.homing.theme.color.GlobalColorPalette;
import hue.captains.singapura.js.homing.theme.type.GlobalTypePalette;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * RFC 0066 — the completeness law, here: every theme this deployment lists
 * provides every prior — colour and type — exactly once, binding exactly what
 * its palette declares. The same rules the studio runs over its eleven, run
 * over the bare app's two: one call, no findings.
 */
class BarePaletteCompletenessTest {

    @Test
    void thePriorsAreColourAndType() {
        assertEquals(List.of(GlobalColorPalette.INSTANCE, GlobalTypePalette.INSTANCE), BareThemes.Registry.INSTANCE.priors());
    }

    @Test
    void everyThemeIsComplete_byTheRules() {
        List<Finding> findings = CssConformance.check(List.of(BareAppCrate.INSTANCE), BareThemes.Registry.INSTANCE.palettes());
        assertEquals(List.of(), findings, () -> findings.stream().map(Finding::fingerprint).toList().toString());
    }
}
