package hue.captains.singapura.js.homing.blocks.scaffold;

import hue.captains.singapura.js.homing.blocks.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;

import java.util.List;

/**
 * Block 1 — Studio Scaffold. How to wire up a minimal Studio + Fixtures
 * + Bootstrap + server. The meta-block: this very catalogue's parent
 * studio (BuildingBlocksStudio) is itself an exhibit of the pattern.
 */
public record StudioScaffoldCatalogue()
        implements L1_Catalogue<BuildingBlocksCatalogue, StudioScaffoldCatalogue>, DocProvider {

    public static final StudioScaffoldCatalogue INSTANCE = new StudioScaffoldCatalogue();

    @Override public BuildingBlocksCatalogue parent() { return BuildingBlocksCatalogue.INSTANCE; }
    @Override public String name()    { return "Studio Scaffold"; }
    @Override public String summary() {
        return "Minimal Studio class + Fixtures + Bootstrap + standalone server. The "
             + "entry point for any new downstream studio.";
    }
    @Override public String badge()   { return "BLOCK"; }
    @Override public String icon()    { return "🏗️"; }

    @Override public List<Entry<StudioScaffoldCatalogue>> leaves() {
        return List.of(
                Entry.of(this, StudioScaffoldGuideDoc.INSTANCE),
                Entry.of(this, StudioScaffoldDemoDoc.INSTANCE)
        );
    }

    @Override public List<Doc> docs() {
        return List.of(StudioScaffoldGuideDoc.INSTANCE, StudioScaffoldDemoDoc.INSTANCE);
    }
}
