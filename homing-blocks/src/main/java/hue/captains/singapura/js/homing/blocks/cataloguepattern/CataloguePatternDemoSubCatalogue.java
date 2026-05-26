package hue.captains.singapura.js.homing.blocks.cataloguepattern;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;

import java.util.List;

/**
 * Live L2 sub-catalogue under {@link CataloguePatternCatalogue}.
 * Demonstrates the typed-levels stack: the {@code parent()} returns
 * the L1, which itself has a typed parent (the L0). The breadcrumb
 * chain reads naturally end-to-end.
 */
public record CataloguePatternDemoSubCatalogue()
        implements L2_Catalogue<CataloguePatternCatalogue, CataloguePatternDemoSubCatalogue>,
                   DocProvider {

    public static final CataloguePatternDemoSubCatalogue INSTANCE =
            new CataloguePatternDemoSubCatalogue();

    @Override public CataloguePatternCatalogue parent() { return CataloguePatternCatalogue.INSTANCE; }
    @Override public String name()    { return "L2 Demo — Nested Sub-Catalogue"; }
    @Override public String summary() {
        return "A live L2 sub-catalogue under Catalogue Pattern. Its parent() returns the "
             + "L1; the framework builds the breadcrumb chain by walking parent() "
             + "recursively until null (L0).";
    }
    @Override public String badge()   { return "SUB-CATALOGUE"; }
    @Override public String icon()    { return "📦"; }

    @Override public List<Entry<CataloguePatternDemoSubCatalogue>> leaves() {
        return List.of(
                Entry.of(this, CataloguePatternDemoLeafDoc.INSTANCE)
        );
    }

    @Override public List<Doc> docs() {
        return List.of(CataloguePatternDemoLeafDoc.INSTANCE);
    }
}
