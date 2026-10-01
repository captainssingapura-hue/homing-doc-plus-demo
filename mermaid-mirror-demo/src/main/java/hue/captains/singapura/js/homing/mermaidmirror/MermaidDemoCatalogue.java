package hue.captains.singapura.js.homing.mermaidmirror;

import hue.captains.singapura.js.homing.docview.app.DocViewLeaves;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.tree.NodeName;

import java.util.List;

/** The root of the tiny Mermaid-mirror site — one Mermaid doc, read in DocView at {@code /mermaid}. */
public record MermaidDemoCatalogue() implements L0_Catalogue<MermaidDemoCatalogue> {

    public static final MermaidDemoCatalogue INSTANCE = new MermaidDemoCatalogue();

    @Override public String name()    { return "Mermaid Mirror Demo"; }
    @Override public String summary() {
        return "A Mermaid-enabled site wired to a self-hosted local CDN instead of the "
             + "public one.";
    }
    @Override public String icon()  { return "🧜"; }

    @Override
    public List<Leaf<MermaidDemoCatalogue>> leaves(Mpa mpa) {
        return List.of(DocViewLeaves.viewed(this, mpa, new NodeName("mermaid"), MermaidMirrorDoc.INSTANCE));
    }
}
