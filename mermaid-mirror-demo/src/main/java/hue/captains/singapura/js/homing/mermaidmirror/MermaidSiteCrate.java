package hue.captains.singapura.js.homing.mermaidmirror;

import hue.captains.singapura.js.homing.catalogue.site.CatalogueSiteCrate;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.docview.app.DocViewAppCrate;
import hue.captains.singapura.js.homing.docview.site.DocViewSiteCrate;

import java.util.List;

/**
 * What the Mermaid-mirror site serves: the listing, and DocView - Mermaid's proxy among DocView's
 * own. It has no module of its own; its tree and its doc are Java and markdown.
 */
public final class MermaidSiteCrate implements Crate {

    public static final MermaidSiteCrate INSTANCE = new MermaidSiteCrate();

    private MermaidSiteCrate() {}

    @Override public String name() { return "mermaid-mirror-demo"; }

    @Override public List<Crate> requires() {
        return List.of(CatalogueSiteCrate.INSTANCE, DocViewSiteCrate.INSTANCE, DocViewAppCrate.INSTANCE);
    }

    @Override public List<CrateEntry> entries() { return List.of(); }
}
