package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.relgrid.RelGridCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.relgrid.protocol.RelGridProtocolCrate;
import hue.captains.singapura.js.homing.reltree.RelTreeCrate;
import hue.captains.singapura.js.homing.preferences.UiPreferencesCrate;
import hue.captains.singapura.js.homing.site.demo.gallery.prefs.GalleryPreferences;
import hue.captains.singapura.js.homing.site.demo.gallery.prefs.PreferencesApp;
import hue.captains.singapura.js.homing.site.demo.gallery.prefs.PreferencesTreeWidgetModule;
import hue.captains.singapura.js.homing.site.mpa.MpaCrate;
import hue.captains.singapura.js.homing.ui.dialog.UiDialogCrate;
import hue.captains.singapura.js.homing.ui.elements.UiElementsCrate;
import hue.captains.singapura.js.homing.ui.panes.UiPanesCrate;
import hue.captains.singapura.js.homing.ui.split.UiSplitCrate;

import java.util.List;

/**
 * The gallery's served modules: six apps, their domain, the preferences
 * registry and two widgets, and their styles, on the MPA's crate, the shared
 * elements, the dialog, the preferences, and the grid family's three crates.
 * The core-js and design crates are named directly although the MPA's crate
 * carries both: the apps import the party themselves, the grid's styles wear
 * words and its crate leaves the targets to its host, and the crate rule reads
 * direct requires only.
 */
public final class GalleryCrate implements Crate {

    public static final GalleryCrate INSTANCE = new GalleryCrate();

    private GalleryCrate() {}

    @Override public String name() { return "homing-site-demo-gallery"; }

    @Override public List<Crate> requires() {
        return List.of(MpaCrate.INSTANCE, UiElementsCrate.INSTANCE, UiDialogCrate.INSTANCE, UiPreferencesCrate.INSTANCE, UiPanesCrate.INSTANCE, UiSplitCrate.INSTANCE,
                       CoreJsCrate.INSTANCE, DesignCrate.INSTANCE, ServerCrate.INSTANCE,
                       RelGridCrate.INSTANCE, RelTreeCrate.INSTANCE, RelGridProtocolCrate.INSTANCE);
    }

    @Override public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(GalleryShellApp.INSTANCE),
                CrateEntry.of(GalleryDemos.INSTANCE),
                CrateEntry.of(WelcomeApp.INSTANCE),
                CrateEntry.of(CounterApp.INSTANCE),
                CrateEntry.of(GridApp.INSTANCE),
                CrateEntry.of(TreeApp.INSTANCE),
                CrateEntry.of(DialogApp.INSTANCE),
                CrateEntry.of(PanesApp.INSTANCE),
                // The preferences: the page, the site's own stamped registry, and the one
                // widget that is the site's - the tree master; the theme widget is the MPA's.
                CrateEntry.of(PreferencesApp.INSTANCE),
                CrateEntry.of(GalleryPreferences.INSTANCE),
                CrateEntry.of(PreferencesTreeWidgetModule.INSTANCE),
                CrateEntry.of(GalleryRelations.INSTANCE),
                CrateEntry.of(GalleryStyles.INSTANCE));
    }
}
