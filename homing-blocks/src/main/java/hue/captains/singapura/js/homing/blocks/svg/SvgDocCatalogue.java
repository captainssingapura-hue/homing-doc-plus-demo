package hue.captains.singapura.js.homing.blocks.svg;

import hue.captains.singapura.js.homing.blocks.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.base.app.SvgViewer;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;

import java.util.List;

/** Block 4 — SvgDoc + SvgGroup + SvgBeing. */
public record SvgDocCatalogue()
        implements L1_Catalogue<BuildingBlocksCatalogue, SvgDocCatalogue>, DocProvider {

    public static final SvgDocCatalogue INSTANCE = new SvgDocCatalogue();

    @Override public BuildingBlocksCatalogue parent() { return BuildingBlocksCatalogue.INSTANCE; }
    @Override public String name()    { return "SvgDoc"; }
    @Override public String summary() {
        return "Vector content as a first-class Doc. SvgGroup + SvgBeing typed pairing + "
             + "classpath .svg resource; renders via the SvgViewer shell with theme-aware "
             + "currentColor.";
    }
    @Override public String badge()   { return "BLOCK"; }
    @Override public String icon()    { return "✨"; }

    @Override public List<Entry<SvgDocCatalogue>> leaves() {
        return List.of(
                Entry.of(this, DocReader.INSTANCE,
                        new DocReader.Params(SvgDocGuideDoc.INSTANCE.uuid().toString()),
                        SvgDocGuideDoc.INSTANCE),
                Entry.of(this, SvgViewer.INSTANCE,
                        new SvgViewer.Params(SvgDocDemoDoc.INSTANCE.uuid().toString()),
                        SvgDocDemoDoc.INSTANCE)
        );
    }

    @Override public List<Doc> docs() {
        return List.of(SvgDocGuideDoc.INSTANCE, SvgDocDemoDoc.INSTANCE);
    }
}
