package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.server.HtmlPageContent;
import hue.captains.singapura.js.homing.site.Navigable;
import hue.captains.singapura.js.homing.site.catalogue.Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.L1_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.tree.NodeName;

import java.util.List;

/**
 * The gallery's tree, written alone: it knows no site - a site grafts it, and hands in its MPA,
 * the demos made pages of that site with it. At its root the tour - the shell: every demo beside
 * what it shows and what it explains - and the plain page; under it a catalogue per group, a page
 * per demo, in the order {@link GalleryDemos} declares them. Grafted at {@code /gallery}, every
 * demo is at the address {@link GalleryDemos.Demo#page()} names, which the tour links to.
 */
public record GalleryCatalogue() implements L0_Catalogue<GalleryCatalogue> {

    public static final GalleryCatalogue INSTANCE = new GalleryCatalogue();

    /**
     * A page served beside the others but not under the chrome: a string, no module, no theme. The
     * MPA is opt-in per page - a page is under it because the site made it so, not because the
     * site has one.
     */
    static final Navigable PLAIN = q -> new HtmlPageContent("""
            <!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>Plain · Gallery</title></head>
            <body style="font: 16px/1.5 Georgia, serif; max-width: 40rem; margin: 3rem auto; padding: 0 1rem">
            <h1>A plain page</h1>
            <p>Served by the same router as the JS pages, but not under the chrome: a string, no module,
               no theme. The MPA is opt-in per page — a page is under it because the site made it so
               with <code>MPA.page(app)</code>, not because the site has one.</p>
            <p><a href="/gallery">Back to the gallery</a></p>
            </body></html>
            """);

    @Override public String name() { return "Gallery"; }

    @Override public NodeName slug() { return new NodeName("gallery"); }

    @Override public String summary() { return "The components, each on a page of its own - and the tour, every demo beside what it explains"; }

    @Override
    public List<Leaf<GalleryCatalogue>> leaves(Mpa mpa) {
        return List.of(
                Leaf.of(this, new NodeName("tour"), "Tour", "Every demo beside what it shows and what it explains",
                        mpa.page(GalleryShellApp.INSTANCE, new GalleryShellApp.Params(""))),
                Leaf.of(this, new NodeName("plain"), "A plain page", "Served beside the others, but not under the chrome", PLAIN));
    }

    @Override
    public List<? extends L1_Catalogue<GalleryCatalogue, ?>> subCatalogues() {
        return List.of(Controls.INSTANCE, Relations.INSTANCE, Layout.INSTANCE, Dialogs.INSTANCE, Focus.INSTANCE);
    }

    /** A group's demos as leaves of its catalogue: each the page its app is, made with the site's MPA. */
    static <C extends Catalogue<C>> List<Leaf<C>> demos(C host, Mpa mpa, String group) {
        return GalleryDemos.demosOf(group(group)).stream()
                .map(d -> Leaf.of(host, new NodeName(d.slug()), d.label(), d.summary(), d.pageIn().apply(mpa)))
                .toList();
    }

    static GalleryDemos.Group group(String slug) {
        return GalleryDemos.GROUPS.stream().filter(g -> g.slug().equals(slug)).findFirst()
                .orElseThrow(() -> new IllegalStateException("GalleryCatalogue: no group '" + slug + "' in GalleryDemos"));
    }

    /** Controls: buttons, cards, sliders. */
    public record Controls() implements L1_Catalogue<GalleryCatalogue, Controls> {
        public static final Controls INSTANCE = new Controls();
        private static final GalleryDemos.Group GROUP = group("controls");
        @Override public GalleryCatalogue parent() { return GalleryCatalogue.INSTANCE; }
        @Override public String name() { return GROUP.label(); }
        @Override public NodeName slug() { return new NodeName(GROUP.slug()); }
        @Override public String summary() { return GROUP.summary(); }
        @Override public List<Leaf<Controls>> leaves(Mpa mpa) { return demos(this, mpa, GROUP.slug()); }
    }

    /** Relations: the grid and the tree. */
    public record Relations() implements L1_Catalogue<GalleryCatalogue, Relations> {
        public static final Relations INSTANCE = new Relations();
        private static final GalleryDemos.Group GROUP = group("relations");
        @Override public GalleryCatalogue parent() { return GalleryCatalogue.INSTANCE; }
        @Override public String name() { return GROUP.label(); }
        @Override public NodeName slug() { return new NodeName(GROUP.slug()); }
        @Override public String summary() { return GROUP.summary(); }
        @Override public List<Leaf<Relations>> leaves(Mpa mpa) { return demos(this, mpa, GROUP.slug()); }
    }

    /** Layout: cells, panes, docks and the desk. */
    public record Layout() implements L1_Catalogue<GalleryCatalogue, Layout> {
        public static final Layout INSTANCE = new Layout();
        private static final GalleryDemos.Group GROUP = group("layout");
        @Override public GalleryCatalogue parent() { return GalleryCatalogue.INSTANCE; }
        @Override public String name() { return GROUP.label(); }
        @Override public NodeName slug() { return new NodeName(GROUP.slug()); }
        @Override public String summary() { return GROUP.summary(); }
        @Override public List<Leaf<Layout>> leaves(Mpa mpa) { return demos(this, mpa, GROUP.slug()); }
    }

    /** Dialogs and menus: a dialog, the preferences, a context menu. */
    public record Dialogs() implements L1_Catalogue<GalleryCatalogue, Dialogs> {
        public static final Dialogs INSTANCE = new Dialogs();
        private static final GalleryDemos.Group GROUP = group("dialogs");
        @Override public GalleryCatalogue parent() { return GalleryCatalogue.INSTANCE; }
        @Override public String name() { return GROUP.label(); }
        @Override public NodeName slug() { return new NodeName(GROUP.slug()); }
        @Override public String summary() { return GROUP.summary(); }
        @Override public List<Leaf<Dialogs>> leaves(Mpa mpa) { return demos(this, mpa, GROUP.slug()); }
    }

    /** Keyboard and focus: the party, the steward, the logical-focus tree. */
    public record Focus() implements L1_Catalogue<GalleryCatalogue, Focus> {
        public static final Focus INSTANCE = new Focus();
        private static final GalleryDemos.Group GROUP = group("focus");
        @Override public GalleryCatalogue parent() { return GalleryCatalogue.INSTANCE; }
        @Override public String name() { return GROUP.label(); }
        @Override public NodeName slug() { return new NodeName(GROUP.slug()); }
        @Override public String summary() { return GROUP.summary(); }
        @Override public List<Leaf<Focus>> leaves(Mpa mpa) { return demos(this, mpa, GROUP.slug()); }
    }
}
