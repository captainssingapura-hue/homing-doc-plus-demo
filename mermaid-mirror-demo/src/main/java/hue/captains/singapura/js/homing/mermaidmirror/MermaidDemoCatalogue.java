package hue.captains.singapura.js.homing.mermaidmirror;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;

import java.util.List;

/** L0 root of the tiny Mermaid-mirror studio — one Mermaid doc. */
public record MermaidDemoCatalogue()
        implements L0_Catalogue<MermaidDemoCatalogue>, DocProvider {

    public static final MermaidDemoCatalogue INSTANCE = new MermaidDemoCatalogue();

    @Override public String name()    { return "Mermaid Mirror Demo"; }
    @Override public String summary() {
        return "A Mermaid-enabled studio wired to a self-hosted local CDN instead of the "
             + "public one.";
    }
    @Override public String badge() { return "STUDIO"; }
    @Override public String icon()  { return "🧜"; }

    @Override public List<Entry<MermaidDemoCatalogue>> leaves() {
        return List.of(Entry.of(this, DocReader.INSTANCE,
                        new DocReader.Params(MermaidMirrorDoc.INSTANCE.uuid().toString()),
                        MermaidMirrorDoc.INSTANCE));
    }

    @Override public List<Doc> docs() {
        return List.of(MermaidMirrorDoc.INSTANCE);
    }
}
