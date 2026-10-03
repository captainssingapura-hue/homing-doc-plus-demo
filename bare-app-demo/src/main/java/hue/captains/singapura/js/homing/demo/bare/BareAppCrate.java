package hue.captains.singapura.js.homing.demo.bare;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;

import java.util.List;

/**
 * RFC 0044 crate for the bare app — three served modules, and the three
 * framework crates they import: DomOpsParty (core-js), the CSS manager and
 * the preference steward (server), the design targets its classes wear
 * (design-core). That list is the whole dependency of a designed JS app on
 * the framework, and the crate rule proves it on every build.
 */
public final class BareAppCrate implements Crate {

    public static final BareAppCrate INSTANCE = new BareAppCrate();

    private BareAppCrate() {}

    @Override public String name() { return "bare-app-demo"; }

    @Override public List<Crate> requires() {
        return List.of(CoreJsCrate.INSTANCE, ServerCrate.INSTANCE, DesignCrate.INSTANCE);
    }

    @Override public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(Swatchboard.INSTANCE),
                CrateEntry.of(SwatchboardData.INSTANCE),
                CrateEntry.of(SwatchboardStyles.INSTANCE));
    }

}
