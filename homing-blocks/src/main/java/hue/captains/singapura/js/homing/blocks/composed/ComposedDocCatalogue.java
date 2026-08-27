package hue.captains.singapura.js.homing.blocks.composed;

import hue.captains.singapura.js.homing.blocks.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedViewer;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;

import java.util.List;

/** Block 3 — ComposedDoc + sealed Segment hierarchy. */
public record ComposedDocCatalogue()
        implements L1_Catalogue<BuildingBlocksCatalogue, ComposedDocCatalogue>, DocProvider {

    public static final ComposedDocCatalogue INSTANCE = new ComposedDocCatalogue();

    @Override public BuildingBlocksCatalogue parent() { return BuildingBlocksCatalogue.INSTANCE; }
    @Override public String name()    { return "ComposedDoc"; }
    @Override public String summary() {
        return "Rich typed-segment content. A ComposedDoc is an ordered list of typed "
             + "Segments (Markdown, Text, Code, Svg, Table, Image, ...) — the framework's "
             + "default Doc shape for documentation richer than plain markdown.";
    }
    @Override public String badge()   { return "BLOCK"; }
    @Override public String icon()    { return "🧩"; }

    @Override public List<Entry<ComposedDocCatalogue>> leaves() {
        return List.of(
                Entry.of(this, DocReader.INSTANCE,
                        new DocReader.Params(ComposedDocGuideDoc.INSTANCE.uuid().toString()),
                        ComposedDocGuideDoc.INSTANCE),
                Entry.of(this, ComposedViewer.INSTANCE,
                        new ComposedViewer.Params(ComposedDocDemo.INSTANCE.uuid().toString()),
                        ComposedDocDemo.INSTANCE)
        );
    }

    @Override public List<Doc> docs() {
        return List.of(ComposedDocGuideDoc.INSTANCE, ComposedDocDemo.INSTANCE);
    }
}
