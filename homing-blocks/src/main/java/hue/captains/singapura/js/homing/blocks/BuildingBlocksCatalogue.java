package hue.captains.singapura.js.homing.blocks;

import hue.captains.singapura.js.homing.blocks.composed.ComposedDocCatalogue;
import hue.captains.singapura.js.homing.blocks.cataloguepattern.CataloguePatternCatalogue;
import hue.captains.singapura.js.homing.blocks.modal.ModalCatalogue;
import hue.captains.singapura.js.homing.blocks.multitabpane.MultiTabPaneCatalogue;
import hue.captains.singapura.js.homing.blocks.prose.ProseDocCatalogue;
import hue.captains.singapura.js.homing.blocks.rigid.RigidDocKitDoc;
import hue.captains.singapura.js.homing.blocks.scaffold.StudioScaffoldCatalogue;
import hue.captains.singapura.js.homing.blocks.splitpane.SplitPaneCatalogue;
import hue.captains.singapura.js.homing.blocks.splitpane.SplitPaneDemoApp;
import hue.captains.singapura.js.homing.blocks.svg.SvgDocCatalogue;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;

import java.util.List;

/**
 * L0 root of the Building Blocks reference studio. Lists the per-block
 * L1 sub-catalogues — each one a focused mini-section explaining a
 * single framework primitive with both a guide doc and a live demo.
 *
 * <p>The first slice covers the five foundational blocks a downstream
 * studio author needs immediately:</p>
 * <ol>
 *   <li><b>Studio Scaffold</b> — the minimal Studio class + Fixtures + Bootstrap</li>
 *   <li><b>ProseDoc</b> — the basic markdown-body Doc kind</li>
 *   <li><b>ComposedDoc + Segments</b> — the rich typed-segment content model</li>
 *   <li><b>SvgDoc</b> — vector content as a Doc, themable via {@code currentColor}</li>
 *   <li><b>Catalogue Pattern</b> — L0/L1/L2 hierarchy with typed Entry variants</li>
 * </ol>
 *
 * <p>Subsequent slices will add TableDoc, ImageDoc, Custom Widget, Plan
 * tracker, Theme variants, References, etc. — each as its own L1.</p>
 */
public record BuildingBlocksCatalogue()
        implements L0_Catalogue<BuildingBlocksCatalogue>, DocProvider {

    public static final BuildingBlocksCatalogue INSTANCE = new BuildingBlocksCatalogue();

    @Override public String name()    { return "Homing · Building Blocks"; }
    @Override public String summary() {
        return "Reference catalogue of the framework's main primitives — Studio Scaffold, "
             + "ProseDoc, ComposedDoc, SvgDoc, Catalogue Pattern. Each block has a guide "
             + "explaining usage + a live demo you can study side-by-side. Intended for "
             + "downstream agents authoring a new studio on top of Homing.";
    }
    @Override public String badge()   { return "STUDIO"; }
    @Override public String icon()    { return "🧱"; }

    @Override public List<Entry<BuildingBlocksCatalogue>> leaves() {
        return List.of(
                Entry.of(this, BuildingBlocksIntroDoc.INSTANCE),
                // Featured top-level demo on the L0 home — the flagship
                // workspace-substrate exhibit. Same Navigable shape as the
                // demo studio's "Moving Animal" featured tile: an AppModule
                // tile rendered alongside the doc + sub-catalogue tiles.
                Entry.of(this, new Navigable<>(
                        SplitPaneDemoApp.INSTANCE,
                        new SplitPaneDemoApp.Params(),
                        "SplitPane — Live Demo",
                        "Top-level app demoing the SplitPane primitive. Fixed 4-quadrant "
                      + "layout: swatch, counter, log, prose. Drag the dividers — the "
                      + "substrate beneath the future flexible workspace.")),
                // RFC 0042 — the leveled tree-builder DSL, demonstrated as a
                // RigidDoc (ComposedDoc's successor) authored entirely through
                // root().l1().l2()... — a genuinely nested, foldable document.
                Entry.of(this, RigidDocKitDoc.INSTANCE)
        );
    }

    @Override public List<? extends L1_Catalogue<BuildingBlocksCatalogue, ?>> subCatalogues() {
        return List.of(
                StudioScaffoldCatalogue.INSTANCE,
                ProseDocCatalogue.INSTANCE,
                ComposedDocCatalogue.INSTANCE,
                SvgDocCatalogue.INSTANCE,
                CataloguePatternCatalogue.INSTANCE,
                SplitPaneCatalogue.INSTANCE,
                MultiTabPaneCatalogue.INSTANCE,
                ModalCatalogue.INSTANCE
        );
    }

    @Override public List<Doc> docs() {
        return List.of(BuildingBlocksIntroDoc.INSTANCE, RigidDocKitDoc.INSTANCE);
    }
}
