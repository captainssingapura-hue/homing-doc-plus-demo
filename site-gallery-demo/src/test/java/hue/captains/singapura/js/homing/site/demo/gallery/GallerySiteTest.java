package hue.captains.singapura.js.homing.site.demo.gallery;

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
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Query;
import hue.captains.singapura.js.homing.site.SiteGetAction;
import hue.captains.singapura.js.homing.site.mpa.ThemesGetAction;
import hue.captains.singapura.js.homing.studio.themes.StudioThemeRegistry;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The router's arms, the scaffold each page writes, the routes the MPA mounts, and the designs' coverage. */
class GallerySiteTest {

    @Test
    void theShellIsTheRootAndImportsTheChromeThenTheApp() {
        var body = GallerySite.INSTANCE.router().resolve(Path.ROOT).orElseThrow().html(Query.NONE).body();
        assertTrue(body.contains("<title>Gallery · Gallery</title>"), body);
        assertTrue(body.contains("const theme = \"editorial\";"), body);
        assertTrue(body.contains("brand: Object.freeze({label:\"Gallery\",home:\"\\/\"})"), body);   // jsString escapes the slash
        assertTrue(body.contains("crumbs: Object.freeze([])"), body);
        assertTrue(body.contains("preferences: Object.freeze({module:\"\\/module?class=hue.captains.singapura.js.homing.site.demo.gallery.prefs.GalleryPreferences\"})"), body);
        int chrome = body.indexOf("site.mpa.MpaChrome");
        int app    = body.indexOf("demo.gallery.GalleryShellApp");
        assertTrue(chrome > 0 && app > chrome, "chrome import before app import: " + body);
        assertTrue(body.contains("const params = Object.freeze({});"), body);   // no demo asked for: the first
        var asked = GallerySite.INSTANCE.router().resolve(Path.ROOT).orElseThrow().html(Query.of("demo", "grid")).body();
        assertTrue(asked.contains("const params = Object.freeze({\"demo\":\"grid\"});"), asked);
        assertFalse(body.contains("WelcomeApp"), "the welcome page is not on the shell");
    }

    @Test
    void theWelcomePageStaysUnderItsOwnArm() {
        var body = GallerySite.INSTANCE.router().resolve(Path.of("welcome")).orElseThrow().html(Query.NONE).body();
        assertTrue(body.contains("<title>Welcome · Gallery</title>"), body);
        assertTrue(body.contains("appMain(main);"), body);   // paramless: nothing stamped
    }

    @Test
    void theDemosNameEveryAppByItsServedAddressAndTheNavigator() {
        var resolver = new hue.captains.singapura.js.homing.server.QueryParamResolver("/module");
        String json = GalleryDemos.INSTANCE.json(resolver);
        assertTrue(json.startsWith("{\"label\":\"Gallery\",\"tree\":{\"segment\":\"gallery\",\"children\":[{\"segment\":\"welcome\""), json);
        // jsString escapes the slash, which JSON allows
        assertTrue(json.contains("\"navigator\":{\"module\":\"\\/module?class=hue.captains.singapura.js.homing.site.demo.gallery.prefs.PreferencesTreeWidgetModule\""), json);
        assertTrue(json.contains("\"gallery\\/counter\":{\"label\":\"Counter\""), json);
        assertTrue(json.contains("\"widget\":{\"module\":\"\\/module?class=hue.captains.singapura.js.homing.site.demo.gallery.CounterApp\",\"export\":\"CounterWidget\",\"params\":{\"start\":\"7\"}}"), json);
        assertTrue(json.contains("\"page\":\"\\/panes\""), json);
        for (var d : GalleryDemos.DEMOS) assertTrue(GallerySite.INSTANCE.router().resolve(Path.parse(d.page())).isPresent(), d.page() + " is a page");
    }

    @Test
    void theCounterIsBoundOffThePathAndToldItsTrail() {
        var body = GallerySite.INSTANCE.router().resolve(Path.of("counter", "7")).orElseThrow().html(Query.NONE).body();
        assertTrue(body.contains("<title>Counter · Gallery</title>"), body);
        assertTrue(body.contains("const params = Object.freeze({\"start\":\"7\"});"), body);
        assertTrue(body.contains("Object.freeze({text:\"Gallery\",to:\"\\/\"})"), body);
        assertTrue(body.contains("Object.freeze({text:\"Counter\",to:\"\\/counter\\/7\"})"), body);
        assertTrue(body.contains("appMain(main, params);"), body);
    }

    @Test
    void theBindingWinsOverTheQuery() {
        var body = GallerySite.INSTANCE.router().resolve(Path.of("counter", "7")).orElseThrow()
                .html(Query.of("start", "99")).body();
        assertTrue(body.contains("\"7\""), body);
        assertFalse(body.contains("99"), body);
    }

    @Test
    void theArms() {
        var r = GallerySite.INSTANCE.router();
        assertSame(GallerySite.PLAIN, r.resolve(Path.of("plain")).orElseThrow());
        assertTrue(r.resolve(Path.of("counter")).isPresent());
        assertTrue(r.resolve(Path.of("counter", "x")).isEmpty());
        assertTrue(r.resolve(Path.of("counter", "1", "2")).isEmpty());
        assertTrue(r.resolve(Path.of("nowhere")).isEmpty());
    }

    @Test
    void theGridAndTheTreeArePagesToldTheirTrail() {
        for (String arm : List.of("grid", "tree", "dialog", "preferences", "panes", "buttons", "cards", "floating")) {
            var body = GallerySite.INSTANCE.router().resolve(Path.of(arm)).orElseThrow().html(Query.NONE).body();
            assertTrue(body.contains(Character.toUpperCase(arm.charAt(0)) + arm.substring(1) + "App"), body);
            assertTrue(body.contains("Object.freeze({text:\"Gallery\",to:\"\\/\"})"), body);
            assertTrue(body.contains("to:\"\\/" + arm + "\""), body);
            assertTrue(GallerySite.INSTANCE.router().resolve(Path.of(arm, "more")).isEmpty());
        }
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
        // the page imports the view and the registry only; no widget module is on it
        var body = GallerySite.INSTANCE.router().resolve(Path.of("preferences")).orElseThrow().html(Query.NONE).body();
        assertFalse(body.contains("ThemeWidget") || body.contains("ScaleWidget"), "widgets are not imported by the page");
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
    void theMpaMountsTheFrameworkRoutesFirstAndTheCatchAllLast() {
        var routes = new ArrayList<>(GallerySite.MPA.registry(GallerySite.INSTANCE).getActions().keySet());
        assertTrue(routes.containsAll(List.of("/module", "/css-content", "/app", ThemesGetAction.ROUTE, SiteGetAction.ROUTE)), routes.toString());
        assertEquals(SiteGetAction.ROUTE, routes.get(routes.size() - 1), routes.toString());
    }

    @Test
    void themesListsSevenDesignsWithTheirOffers() {
        String json = new ThemesGetAction(StudioThemeRegistry.INSTANCE).serialize();
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
        for (Theme t : StudioThemeRegistry.INSTANCE.themes()) {
            Design d = (Design) t;
            var r = Deployment.of(worn, scaled, grown, d).resolve();
            assertEquals(List.of(), r.findings(), () -> d.slug() + ": " + r.findings());
        }
    }
}
