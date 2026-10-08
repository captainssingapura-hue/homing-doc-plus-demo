package hue.captains.singapura.js.homing.demo.site;

import hue.captains.singapura.js.homing.demo.taxonomy.TaxonomyWorkbench;
import hue.captains.singapura.js.homing.demo.taxonomy.TaxonomyWorkbenchApp;
import hue.captains.singapura.js.homing.demo.workspace.DemoGroups;
import hue.captains.singapura.js.homing.demo.workspace.DemoWorkspaceApp;
import hue.captains.singapura.js.homing.docview.app.DocViewLeaves;
import hue.captains.singapura.js.homing.site.catalogue.Graft;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.L1_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.demo.gallery.GalleryCatalogue;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.tree.NodeName;
import hue.captains.singapura.js.homing.workspace.site.GroupedWorkspacePageModule;

import java.util.List;

/**
 * The demo site's root: four catalogues, nothing loose. The gallery's tree, grafted - the
 * components, a page each, and the tour - at {@code /gallery}, where its demos' addresses say they
 * are; the docs, each read in DocView; the workspaces, each group's page opening beside the
 * listing as a place of work does, the server keeping its states; and the taxonomy of the
 * house's components (RFC 0067), a workbench beside the gallery that shows them.
 */
public record DemoCatalogue() implements L0_Catalogue<DemoCatalogue> {

    public static final DemoCatalogue INSTANCE = new DemoCatalogue();

    @Override public String name() { return "Homing demo"; }

    @Override public String summary() { return "The demo's gallery of components, its docs, its workspaces and the taxonomy of its components"; }

    @Override
    public List<Graft<DemoCatalogue>> grafts() {
        return List.of(Graft.of(this, GalleryCatalogue.INSTANCE));
    }

    @Override
    public List<? extends L1_Catalogue<DemoCatalogue, ?>> subCatalogues() {
        return List.of(Docs.INSTANCE, Workspaces.INSTANCE, Taxonomy.INSTANCE);
    }

    /** The demo's docs: the composed doc - one doc holding every content kind there is, its table and its image among them. */
    public record Docs() implements L1_Catalogue<DemoCatalogue, Docs> {
        public static final Docs INSTANCE = new Docs();
        @Override public DemoCatalogue parent() { return DemoCatalogue.INSTANCE; }
        @Override public String name() { return "Docs"; }
        @Override public NodeName slug() { return new NodeName("docs"); }
        @Override public String summary() { return "The demo's docs, each read in DocView"; }
        @Override
        public List<Leaf<Docs>> leaves(Mpa mpa) {
            return List.of(DocViewLeaves.viewed(this, mpa, new NodeName("composed-doc"), ComposedDemoDoc.INSTANCE));
        }
    }

    /**
     * The demo's workspaces: each group a page, at its id under {@code /workspaces} - which the page
     * is told ({@code ws_under}), so the addresses it makes of its group, and of the others, are
     * where the site placed them.
     */
    public record Workspaces() implements L1_Catalogue<DemoCatalogue, Workspaces> {
        public static final Workspaces INSTANCE = new Workspaces();
        /** Where this catalogue places the groups' pages: its own address. */
        static final String UNDER = "/workspaces";
        @Override public DemoCatalogue parent() { return DemoCatalogue.INSTANCE; }
        @Override public String name() { return "Workspaces"; }
        @Override public NodeName slug() { return new NodeName("workspaces"); }
        @Override public String summary() { return "The demo's workspaces, each opening beside the listing, the server keeping its states"; }
        @Override
        public List<Leaf<Workspaces>> leaves(Mpa mpa) {
            var group = DemoGroups.SITE.home();   // the demo's one group
            return List.of(Leaf.of(this, new NodeName(group.id().value()), group.title(), "The video room under Media, the animal platformer under Games",
                                    mpa.page(DemoWorkspaceApp.INSTANCE, new GroupedWorkspacePageModule.Params(group.id().value(), true, UNDER)))
                            .badge("WORKSPACE").icon("🧩").opens(Leaf.Opening.NEW_TAB));
        }
    }

    /**
     * The taxonomy of the house's components (RFC 0067): the tree its declarations read as - kinds,
     * components, the parts each names - in a workbench, a place of work of its own, at its group
     * under {@code /taxonomy}.
     */
    public record Taxonomy() implements L1_Catalogue<DemoCatalogue, Taxonomy> {
        public static final Taxonomy INSTANCE = new Taxonomy();
        /** Where this catalogue places the workbench's page: its own address. */
        static final String UNDER = "/taxonomy";
        @Override public DemoCatalogue parent() { return DemoCatalogue.INSTANCE; }
        @Override public String name() { return "Taxonomy"; }
        @Override public NodeName slug() { return new NodeName("taxonomy"); }
        @Override public String summary() { return "The house's components as a tree - kinds, components, the parts each names - in a workbench beside the gallery"; }
        @Override
        public List<Leaf<Taxonomy>> leaves(Mpa mpa) {
            var group = TaxonomyWorkbench.SITE.home();   // the workbench's one group
            return List.of(Leaf.of(this, new NodeName(group.id().value()), group.title(), "The tree of the house's components: a node's parts, what it falls back to, its semantic classes",
                                    mpa.page(TaxonomyWorkbenchApp.INSTANCE, new GroupedWorkspacePageModule.Params(group.id().value(), true, UNDER)))
                            .badge("WORKBENCH").icon("🌳").opens(Leaf.Opening.NEW_TAB));
        }
    }
}
