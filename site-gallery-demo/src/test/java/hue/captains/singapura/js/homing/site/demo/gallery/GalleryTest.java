package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.ComponentTrees;
import hue.captains.singapura.js.homing.component.keyboard.KeyboardRegistry;
import hue.captains.singapura.js.homing.conformance.rules.CrateDependencyRule;
import hue.captains.singapura.js.homing.conformance.rules.DefaultJsRulePolicy;
import hue.captains.singapura.js.homing.conformance.rules.ServedModule;
import hue.captains.singapura.js.homing.core.ModuleForm;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.util.ResourceReader;
import hue.captains.singapura.js.homing.conformance.rules.OrphanCheck;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.design.Deployment;
import hue.captains.singapura.js.homing.design.Design;
import hue.captains.singapura.js.homing.server.ServedModules;
import hue.captains.singapura.js.homing.site.mpa.ThemesGetAction;
import hue.captains.singapura.js.homing.designs.HomingDesigns;
import hue.captains.singapura.js.homing.ui.menu.ContextMenuRegistry;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The gallery as it is declared: its demos and its preferences as their modules stamp them, its
 * tree, its crate, its modules' lane, the designs' coverage, its components and menus. The pages
 * themselves are the demo site's, which grafts the gallery's tree - and checks them there.
 */
class GalleryTest {

    @Test
    void theDemosNameEveryAppByItsServedAddressAndTheNavigator() {
        var resolver = new hue.captains.singapura.js.homing.server.QueryParamResolver("/module");
        String json = GalleryDemos.INSTANCE.json(resolver);
        assertTrue(json.startsWith("{\"label\":\"Gallery\",\"tree\":{\"segment\":\"gallery\",\"children\":[{\"segment\":\"controls\",\"children\":[{\"segment\":\"buttons\""), json);
        assertTrue(json.contains("\"gallery\\/controls\":\"Controls\""), json);
        assertTrue(json.contains("\"groups\":{\"gallery\\/controls\":{\"label\":\"Controls\",\"summary\":"), json);
        assertTrue(json.contains("\"demos\":[\"gallery\\/controls\\/buttons\",\"gallery\\/controls\\/cards\",\"gallery\\/controls\\/sliders\"]}"), json);
        assertEquals(List.of("controls", "relations", "layout", "dialogs", "focus"), GalleryDemos.GROUPS.stream().map(GalleryDemos.Group::slug).toList());
        assertEquals(GalleryDemos.DEMOS.size(), GalleryDemos.GROUPS.stream().mapToInt(g -> GalleryDemos.demosOf(g).size()).sum(), "every demo in exactly one group");
        assertEquals(GalleryDemos.DEMOS.size(), GalleryDemos.DEMOS.stream().map(GalleryDemos.Demo::slug).distinct().count(), "slugs unique across groups: ?demo=<slug> finds one");
        // jsString escapes the slash, which JSON allows
        assertTrue(json.contains("\"navigator\":{\"module\":\"\\/module?class=hue.captains.singapura.js.homing.site.demo.gallery.prefs.PreferencesTreeWidgetModule\""), json);
        assertTrue(json.contains("\"gallery\\/layout\\/panes\":{\"label\":\"Panes\""), json);
        assertTrue(json.contains("\"widget\":{\"module\":\"\\/module?class=hue.captains.singapura.js.homing.site.demo.gallery.PanesApp\",\"export\":\"PanesWidget\",\"params\":{}}"), json);
        assertTrue(json.contains("\"page\":\"\\/gallery\\/layout\\/panes\""), json);   // its place in the tree, under where a site grafts the gallery
    }

    /** The tree a site grafts: the tour and the plain page at its root, then a catalogue per group, in the order the demos declare them. */
    @Test
    void theTreeIsACataloguePerGroup_inTheDemosOrder() {
        assertEquals("gallery", GalleryCatalogue.INSTANCE.slug().value());
        assertEquals(GalleryDemos.GROUPS.stream().map(GalleryDemos.Group::slug).toList(),
                GalleryCatalogue.INSTANCE.subCatalogues().stream().map(c -> c.slug().value()).toList());
        assertEquals(GalleryDemos.GROUPS.stream().map(GalleryDemos.Group::label).toList(),
                GalleryCatalogue.INSTANCE.subCatalogues().stream().map(c -> c.name()).toList());
    }

    @Test
    void theRegistryNamesEveryWidgetByItsServedAddress() {
        var resolver = new hue.captains.singapura.js.homing.server.QueryParamResolver("/module");
        String json = hue.captains.singapura.js.homing.site.demo.gallery.prefs.GalleryPreferences.INSTANCE.json(resolver);
        assertTrue(json.contains("\"master\":{\"module\":\"/module?class=hue.captains.singapura.js.homing.site.demo.gallery.prefs.PreferencesTreeWidgetModule\""), json);
        assertTrue(json.contains("\"preferences/theme\":{\"label\":\"Theme\""), json);
        assertTrue(json.contains("/module?class=hue.captains.singapura.js.homing.site.mpa.ThemeWidgetModule"), json);
        assertTrue(json.contains("/module?class=hue.captains.singapura.js.homing.preferences.ScaleWidgetModule"), json);
        assertTrue(json.contains("\"preferences/editor/wrap\""), json);
    }

    @Test
    void theCrateHoldsEveryServedModuleAndImportsOnlyWhatItRequires() {
        assertEquals(List.of(), OrphanCheck.check(GalleryCrate.INSTANCE));
        assertEquals(List.of(), CrateDependencyRule.check(GalleryCrate.INSTANCE));
    }

    /** Every JS module the gallery serves keeps the consumer lane over its source: classes exported, no inline style, no raw DOM, no raw href. */
    @Test
    void everyGalleryModuleKeepsTheConsumerLane() {
        int checked = 0;
        for (var entry : GalleryCrate.INSTANCE.entries()) {
            if (entry.form() != ModuleForm.RESOURCE_BACKED) continue;
            var type = entry.declaredType() instanceof StandardJsModuleType t ? t : StandardJsModuleType.CONSUMER;
            String m = entry.moduleClass();
            String src = String.join("\n", ResourceReader.INSTANCE.getStringsFromResource("homing/js/" + m.replace('.', '/') + ".js"));
            var findings = DefaultJsRulePolicy.INSTANCE.rulesFor(type).checkAll(ServedModule.of(m, type, src));
            assertEquals(List.of(), findings, () -> m + ": " + findings.stream().map(f -> f.rule().value() + "@" + f.line() + ": " + f.message()).toList());
            checked++;
        }
        assertTrue(checked >= 10, "the apps, the shell, the relations, the tree widget; checked " + checked);
    }

    @Test
    void themesListsSevenDesignsWithTheirOffers() {
        String json = new ThemesGetAction(HomingDesigns.REGISTRY).serialize();
        assertTrue(json.startsWith("{\"default\":\"editorial\""), json.substring(0, 60));
        assertTrue(json.contains("\"slug\":\"neo-brutalism\",\"label\":"), json);
        assertTrue(json.contains("{\"palette\":\"forest\",\"slug\":\"editorial_forest\",\"fits\":true}"), json);
        assertTrue(json.contains("{\"palette\":\"harbour\",\"slug\":\"editorial\",\"own\":true,\"fits\":true}"), json);
        assertTrue(json.contains("\"anchor\":\"editorial\""), json);
    }

    @Test
    void everyDesignBindsEveryPairTheChromeTheAppsTheGridAndTheTreeWear() {
        var served = ServedModules.of(List.of(GalleryCrate.INSTANCE));
        var groups = new ArrayList<CssGroup<?>>();
        for (var m : served.byName().values()) if (m instanceof CssGroup<?> g) groups.add(g);
        assertTrue(groups.stream().anyMatch(g -> g.getClass().getSimpleName().equals("RelGridStyles")), "the grid's styles are served");
        assertTrue(groups.stream().anyMatch(g -> g.getClass().getSimpleName().equals("RelTreeStyles")), "the tree's styles are served");
        assertTrue(groups.stream().anyMatch(g -> g.getClass().getSimpleName().equals("DialogStyles")), "the dialog's styles are served");
        assertTrue(groups.stream().anyMatch(g -> g.getClass().getSimpleName().equals("PreferencesStyles")), "the preferences' styles are served");
        assertTrue(groups.stream().anyMatch(g -> g.getClass().getSimpleName().equals("PaneStyles")), "the panes' styles are served");
        var worn = Deployment.wornBy(groups);
        assertTrue(worn.size() > 60, "the chrome, the apps, the grid and the tree wear many pairs; found " + worn.size());
        var scaled = Deployment.scaledBy(groups);
        var grown = Deployment.grownBy(groups);
        for (Theme t : HomingDesigns.REGISTRY.themes()) {
            Design d = (Design) t;
            var r = Deployment.of(worn, scaled, grown, d).resolve();
            assertEquals(List.of(), r.findings(), () -> d.slug() + ": " + r.findings());
        }
    }

    /** The gallery is a vehicle: its widgets catalogued, and the page's menus derived from what the catalogued components need. */
    @Test
    void theGalleryIsAVehicle_andItsMenusAreDerivedFromItsComponentsNeeds() {
        assertEquals(List.of(), ComponentTrees.validate(List.of(GalleryCrate.INSTANCE)));
        var composed = ComponentTrees.compose("gallery", List.of(GalleryCrate.INSTANCE));
        assertEquals("gallery", composed.root().children().get(0).segment().value(), "the site's own catalogue first in the closure, then the crates it requires");
        assertEquals(13, composed.root().children().size(), "the gallery and the twelve vehicles in its closure, the base (the keyboard steward), the focus monitor and docking's dock grid among them: the split crate is not required");
        assertEquals(List.of(), ContextMenuRegistry.validate(List.of(GalleryCrate.INSTANCE)));
        assertEquals(List.of("animal", "counter", "swatch", "tab", "split"), GalleryMenus.REGISTRY.kinds().stream().map(k -> k.kind()).toList(), "derived: the context menus widget names three; the pane in the panes crate names the tab menu, the dock grid in the docking crate the split menu; nothing lists them");
        // the keys likewise: the slider and its group declare theirs; the page's map is derived, and no declared component listens for itself
        // one capture left in the closure: the grid's header drag cancels on Escape; the grid migrates with the tree renderer, not before
        assertEquals(List.of("homing-rel-grid: RelGridHeaderDragModule captures keys on the document; only the steward does"), KeyboardRegistry.validate(List.of(GalleryCrate.INSTANCE)));
        assertEquals(List.of(), KeyboardRegistry.undeclaredListeners(List.of(GalleryCrate.INSTANCE)).stream().filter(m -> !m.startsWith("RelGrid") && !m.startsWith("RelTree") && !m.startsWith("Tree")).toList(), "the gallery's own widgets and the components it serves: every key through the party");
        assertEquals(List.of("BooksTab", "Card", "ContextMenuSteward", "ContextMenusWidget", "Dialog", "DomOpsTab", "EventsTab", "FloatLayer", "FocusTab", "FocusWidget", "KeyboardWidget", "Leaf", "ListMasterWidget", "ListPanel", "MultiTabPane", "NoteTab", "Panel", "PictureTab", "PreferencesView", "RegionList", "RelFocusWidget", "ShelvesTab", "Slider", "SliderGroup", "SplitGridMirror", "StewardTab", "TabOpener"),
                KeyboardRegistry.requiredBy(List.of(GalleryCrate.INSTANCE)).byComponent().keySet().stream().map(c -> c.getClass().getSimpleName()).sorted().toList());
    }
}
