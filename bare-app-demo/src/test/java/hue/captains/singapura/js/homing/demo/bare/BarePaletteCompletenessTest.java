package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.PaletteClass;
import hue.captains.singapura.js.homing.core.PaletteProvision;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.theme.color.GlobalColorPalette;
import hue.captains.singapura.js.homing.theme.type.GlobalTypePalette;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RFC 0066 — the completeness law, here: every theme this deployment lists
 * provides every prior — colour and type — exactly once, and each provision
 * binds exactly what its palette declares. Same law the studio runs over its
 * eleven; this is the bare app's two.
 */
class BarePaletteCompletenessTest {

    private static Set<CssVar> declaredBy(CssGroup<?> palette) {
        var out = new HashSet<CssVar>();
        for (CssClass<?> c : palette.cssClasses()) if (c instanceof PaletteClass<?> p) out.addAll(p.declares());
        return out;
    }

    @Test
    void thePriorsAreColourAndType() {
        assertEquals(List.of(GlobalColorPalette.INSTANCE, GlobalTypePalette.INSTANCE), BareThemes.Registry.INSTANCE.priors());
    }

    @Test
    void everyThemeProvidesEveryPriorOnce_bindingExactlyWhatItDeclares() {
        for (Theme t : BareThemes.Registry.INSTANCE.themes()) {
            for (CssGroup<?> prior : BareThemes.Registry.INSTANCE.priors()) {
                var mine = BareThemes.Registry.INSTANCE.palettes().stream()
                        .filter(p -> p.theme().slug().equals(t.slug()) && p.group().getClass() == prior.getClass()).toList();
                assertEquals(1, mine.size(), t.slug() + " must provide " + prior.getClass().getSimpleName() + " exactly once");
                PaletteProvision<?, ?> p = mine.get(0);
                var declared = declaredBy(prior);
                var missing = new TreeSet<String>();
                for (CssVar v : declared) if (!p.values().containsKey(v)) missing.add(v.name());
                var stray = new TreeSet<String>();
                for (CssVar v : p.values().keySet()) if (!declared.contains(v)) stray.add(v.name());
                assertTrue(missing.isEmpty(), t.slug() + " does not bind: " + missing);
                assertTrue(stray.isEmpty(), t.slug() + " binds tokens the palette does not declare: " + stray);
            }
        }
    }
}
