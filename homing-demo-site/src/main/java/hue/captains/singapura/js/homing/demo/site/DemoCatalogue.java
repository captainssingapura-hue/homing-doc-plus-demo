package hue.captains.singapura.js.homing.demo.site;

import hue.captains.singapura.js.homing.demo.workspace.DemoGroups;
import hue.captains.singapura.js.homing.demo.workspace.DemoWorkspaceApp;
import hue.captains.singapura.js.homing.docview.app.DocViewLeaves;
import hue.captains.singapura.js.homing.site.catalogue.Graft;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.demo.gallery.GalleryCatalogue;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.tree.NodeName;
import hue.captains.singapura.js.homing.workspace.site.GroupedWorkspacePageModule;

import java.util.List;

/**
 * The demo site's root: the gallery's tree, grafted - the components, a page each, and the tour -
 * at {@code /gallery}, where its demos' addresses say they are; the composed doc, read in DocView -
 * one doc holding every content kind there is, its table and its image among them; and the demo's
 * workspaces, the group's page placed at the group's own address, {@code /demo}, opening beside the
 * listing as a place of work does, the server keeping its states.
 */
public record DemoCatalogue() implements L0_Catalogue<DemoCatalogue> {

    public static final DemoCatalogue INSTANCE = new DemoCatalogue();

    @Override public String name() { return "Homing demo"; }

    @Override public String summary() { return "The demo's gallery of components, its docs read in DocView, and its workspaces"; }

    @Override
    public List<Graft<DemoCatalogue>> grafts() {
        return List.of(Graft.of(this, GalleryCatalogue.INSTANCE));
    }

    @Override
    public List<Leaf<DemoCatalogue>> leaves(Mpa mpa) {
        var group = DemoGroups.SITE.home();
        return List.of(
                DocViewLeaves.viewed(this, mpa, new NodeName("composed-doc"), ComposedDemoDoc.INSTANCE),
                Leaf.of(this, new NodeName(group.id().value()), group.title(), "The video room under Media, the animal platformer under Games",
                                mpa.page(DemoWorkspaceApp.INSTANCE, new GroupedWorkspacePageModule.Params(group.id().value(), true)))
                        .badge("WORKSPACE").icon("🧩").opens(Leaf.Opening.NEW_TAB));
    }
}
