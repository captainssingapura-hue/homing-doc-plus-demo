package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.theme.color.ThemeColorCrate;
import hue.captains.singapura.js.homing.theme.type.ThemeTypeCrate;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * RFC 0044 crate for the bare app — three served modules, and the three
 * framework crates they import: DomOpsParty (core-js), the CSS manager and
 * the preference steward (server), the colour and type palettes (theme-color,
 * theme-type). That
 * list is the whole dependency of a themed JS app on the framework, and the
 * crate rule proves it on every build.
 */
public final class BareAppCrate implements Crate {

    public static final BareAppCrate INSTANCE = new BareAppCrate();

    private BareAppCrate() {}

    @Override public String name() { return "bare-app-demo"; }

    @Override public List<Crate> requires() {
        return List.of(CoreJsCrate.INSTANCE, ServerCrate.INSTANCE, ThemeColorCrate.INSTANCE, ThemeTypeCrate.INSTANCE);
    }

    @Override public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(Swatchboard.INSTANCE),
                CrateEntry.of(SwatchboardData.INSTANCE),
                CrateEntry.of(SwatchboardStyles.INSTANCE));
    }

    /**
     * Every module class this crate and the crates it requires serve — the
     * allow-list the module action refuses everything outside of (RFC 0044:
     * a module reaches the browser only if it is crated). Direct requires
     * only, like visibility: a module the bare app imports transitively is
     * crated by the crate that serves it.
     */
    public static Set<String> servable() {
        var out = new TreeSet<String>();
        collect(INSTANCE, out);
        return out;
    }

    private static void collect(Crate c, Set<String> out) {
        for (CrateEntry e : c.entries()) out.add(e.moduleClass());
        for (Crate r : c.requires()) collect(r, out);
    }
}
