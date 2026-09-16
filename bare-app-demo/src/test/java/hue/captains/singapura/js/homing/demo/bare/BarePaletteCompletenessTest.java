package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.PaletteProvision;
import hue.captains.singapura.js.homing.theme.color.GlobalColorPalette;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RFC 0066 — the completeness law, here: every theme this deployment lists
 * binds exactly what the global palette declares. Same law the studio runs
 * over its eleven; this is the bare app's two. A token the palette gains
 * fails both themes at once, which is when it should.
 */
class BarePaletteCompletenessTest {

    private static final Set<CssVar> DECLARED = new GlobalColorPalette.global_color_palette().declares();

    @Test
    void everyThemeBindsEveryDeclaredToken_andNothingElse() {
        for (PaletteProvision<?, ?> p : BareThemes.Registry.INSTANCE.palettes()) {
            var missing = new TreeSet<String>();
            for (CssVar v : DECLARED) if (!p.values().containsKey(v)) missing.add(v.name());
            var stray = new TreeSet<String>();
            for (CssVar v : p.values().keySet()) if (!DECLARED.contains(v)) stray.add(v.name());
            String who = p.theme().slug();
            assertTrue(missing.isEmpty(), who + " does not bind: " + missing);
            assertTrue(stray.isEmpty(), who + " binds tokens the palette does not declare: " + stray);
            assertEquals(GlobalColorPalette.INSTANCE, p.group(), who + " fills another group");
        }
    }

    @Test
    void thePaletteIsTheRegistrysPrior() {
        assertEquals(GlobalColorPalette.INSTANCE, BareThemes.Registry.INSTANCE.palette());
        assertEquals(BareThemes.Registry.INSTANCE.themes().size(), BareThemes.Registry.INSTANCE.palettes().size());
    }
}
