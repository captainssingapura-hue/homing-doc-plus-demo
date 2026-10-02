package hue.captains.singapura.js.homing.demo.site;

import hue.captains.singapura.js.homing.catalogue.site.CatalogueSiteCrate;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.demo.workspace.DemoWorkspaceCrate;
import hue.captains.singapura.js.homing.docview.app.DocViewAppCrate;
import hue.captains.singapura.js.homing.docview.site.DocViewSiteCrate;
import hue.captains.singapura.js.homing.site.demo.gallery.GalleryCrate;

import java.util.List;

/**
 * What the demo site serves: the listing, DocView, the gallery's apps, and the demo's workspaces'
 * page. It has no module of its own - its tree and its docs are Java, served as the pages it places.
 */
public final class DemoSiteCrate implements Crate {

    public static final DemoSiteCrate INSTANCE = new DemoSiteCrate();

    private DemoSiteCrate() {}

    @Override public String name() { return "homing-demo-site"; }

    @Override public List<Crate> requires() {
        return List.of(CatalogueSiteCrate.INSTANCE, DocViewSiteCrate.INSTANCE, DocViewAppCrate.INSTANCE, GalleryCrate.INSTANCE, DemoWorkspaceCrate.INSTANCE);
    }

    @Override public List<CrateEntry> entries() { return List.of(); }
}
