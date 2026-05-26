package hue.captains.singapura.js.homing.blocks.cataloguepattern;

import hue.captains.singapura.js.homing.blocks.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;

import java.util.List;

/**
 * Block 5 — Catalogue Pattern. The L0/L1/L2 hierarchy + Entry variants
 * + DocProvider integration. This very catalogue IS the demo: its own
 * structure (with the nested {@link CataloguePatternDemoSubCatalogue}
 * L2) demonstrates the pattern.
 */
public record CataloguePatternCatalogue()
        implements L1_Catalogue<BuildingBlocksCatalogue, CataloguePatternCatalogue>, DocProvider {

    public static final CataloguePatternCatalogue INSTANCE = new CataloguePatternCatalogue();

    @Override public BuildingBlocksCatalogue parent() { return BuildingBlocksCatalogue.INSTANCE; }
    @Override public String name()    { return "Catalogue Pattern"; }
    @Override public String summary() {
        return "How catalogues compose into a typed L0 → L1 → L2 hierarchy with Entry "
             + "variants (OfDoc, OfIllustration, OfStudio). This catalogue's own structure "
             + "(with its nested L2 sub-catalogue) is the live exhibit.";
    }
    @Override public String badge()   { return "BLOCK"; }
    @Override public String icon()    { return "🗂️"; }

    @Override public List<Entry<CataloguePatternCatalogue>> leaves() {
        return List.of(
                Entry.of(this, CataloguePatternGuideDoc.INSTANCE)
        );
    }

    @Override public List<? extends L2_Catalogue<CataloguePatternCatalogue, ?>> subCatalogues() {
        // The L2 sub-catalogue is the live demo — it shows how an L1
        // declares an L2 child + how the framework's typed catalogue
        // chain composes.
        return List.of(CataloguePatternDemoSubCatalogue.INSTANCE);
    }

    @Override public List<Doc> docs() {
        return List.of(CataloguePatternGuideDoc.INSTANCE);
    }
}
