package hue.captains.singapura.js.homing.blocks.prose;

import hue.captains.singapura.js.homing.blocks.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;

import java.util.List;

/** Block 2 — ProseDoc / ClasspathMarkdownDoc. */
public record ProseDocCatalogue()
        implements L1_Catalogue<BuildingBlocksCatalogue, ProseDocCatalogue>, DocProvider {

    public static final ProseDocCatalogue INSTANCE = new ProseDocCatalogue();

    @Override public BuildingBlocksCatalogue parent() { return BuildingBlocksCatalogue.INSTANCE; }
    @Override public String name()    { return "ProseDoc"; }
    @Override public String summary() {
        return "The basic markdown-body Doc kind. Java record + companion .md file on the "
             + "classpath; ClasspathMarkdownDoc auto-resolves the markdown by package path.";
    }
    @Override public String badge()   { return "BLOCK"; }
    @Override public String icon()    { return "📄"; }

    @Override public List<Entry<ProseDocCatalogue>> leaves() {
        return List.of(
                Entry.of(this, ProseDocGuideDoc.INSTANCE),
                Entry.of(this, ProseDocDemoDoc.INSTANCE)
        );
    }

    @Override public List<Doc> docs() {
        return List.of(ProseDocGuideDoc.INSTANCE, ProseDocDemoDoc.INSTANCE);
    }
}
