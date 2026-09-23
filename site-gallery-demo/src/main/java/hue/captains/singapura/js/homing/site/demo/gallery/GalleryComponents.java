package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.C0_Components;
import hue.captains.singapura.js.homing.component.C1_Components;
import hue.captains.singapura.js.homing.component.ComponentEntry;
import hue.captains.singapura.js.homing.site.demo.gallery.prefs.PreferencesTreeWidgetModule;

import java.util.List;

/**
 * The gallery's catalogue of components: the demo widgets, one per page,
 * and the site's own preferences widget. What the page's steward must hold
 * is derived from here — the context menus widget names its kinds.
 */
public record GalleryComponents() implements C0_Components<GalleryComponents> {

    public static final GalleryComponents INSTANCE = new GalleryComponents();

    @Override public String name() { return "Gallery"; }
    @Override public String summary() { return "The demo widgets, one per page, and the site's preferences tree."; }

    @Override public List<? extends C1_Components<GalleryComponents, ?>> subCatalogues() { return List.of(DemosComponents.INSTANCE); }

    @Override public List<ComponentEntry<GalleryComponents>> leaves() {
        return List.of(ComponentEntry.of(this, new PreferencesTreeWidgetModule.PreferencesTreeWidget()));
    }

    /** One widget per demo page, in the site's order. */
    public record DemosComponents() implements C1_Components<GalleryComponents, DemosComponents> {
        public static final DemosComponents INSTANCE = new DemosComponents();
        @Override public GalleryComponents parent() { return GalleryComponents.INSTANCE; }
        @Override public String name() { return "Demos"; }
        @Override public String summary() { return "One widget per demo page."; }
        @Override public List<ComponentEntry<DemosComponents>> leaves() {
            return List.of(
                    ComponentEntry.of(this, new GridApp.GridWidget()),
                    ComponentEntry.of(this, new TreeApp.TreeWidget()),
                    ComponentEntry.of(this, new DialogApp.DialogWidget()),
                    ComponentEntry.of(this, new PanesApp.PanesWidget()),
                    ComponentEntry.of(this, new ButtonsApp.ButtonsWidget()),
                    ComponentEntry.of(this, new CardsApp.CardsWidget()),
                    ComponentEntry.of(this, new FloatingApp.FloatingWidget()),
                    ComponentEntry.of(this, new DockingApp.DockingWidget()),
                    ComponentEntry.of(this, new SplitGridApp.SplitGridWidget()),
                    ComponentEntry.of(this, new TabStripApp.TabStripWidget()),
                    ComponentEntry.of(this, new ContextMenusApp.ContextMenusWidget()),
                    ComponentEntry.of(this, new SlidersApp.SlidersWidget()),
                    ComponentEntry.of(this, new KeyboardApp.KeyboardWidget()),
                    ComponentEntry.of(this, new FocusApp.FocusWidget()),
                    ComponentEntry.of(this, new RelFocusApp.RelFocusWidget()),
                    ComponentEntry.of(this, new FocusSceneModule.Leaf()),
                    ComponentEntry.of(this, new FocusSceneModule.Panel()),
                    ComponentEntry.of(this, new FocusSceneModule.ListPanel()),
                    ComponentEntry.of(this, new DockingSceneModule.BooksTab()),
                    ComponentEntry.of(this, new DockingSceneModule.ShelvesTab()),
                    ComponentEntry.of(this, new DockingSceneModule.PictureTab()),
                    ComponentEntry.of(this, new DockingSceneModule.NoteTab()),
                    ComponentEntry.of(this, new DockingSceneModule.RegionList()));
        }
    }
}
