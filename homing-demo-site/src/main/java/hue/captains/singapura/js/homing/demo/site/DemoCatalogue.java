package hue.captains.singapura.js.homing.demo.site;

import hue.captains.singapura.js.homing.docview.app.DocViewLeaves;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.tree.NodeName;

import java.util.List;

/**
 * The demo site's root: the composed doc, read in DocView - one doc holding every content kind
 * there is, its table and its image among them.
 */
public record DemoCatalogue() implements L0_Catalogue<DemoCatalogue> {

    public static final DemoCatalogue INSTANCE = new DemoCatalogue();

    @Override public String name() { return "Homing demo"; }

    @Override public String summary() { return "The demo's docs, each read in DocView"; }

    @Override
    public List<Leaf<DemoCatalogue>> leaves(Mpa mpa) {
        return List.of(DocViewLeaves.viewed(this, mpa, new NodeName("composed-doc"), ComposedDemoDoc.INSTANCE));
    }
}
