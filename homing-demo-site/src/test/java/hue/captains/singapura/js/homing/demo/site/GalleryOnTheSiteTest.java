package hue.captains.singapura.js.homing.demo.site;

import hue.captains.singapura.js.homing.catalogue.site.EntryGetAction;
import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Query;
import hue.captains.singapura.js.homing.site.demo.gallery.GalleryDemos;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The gallery on the demo site: its tree grafted at {@code /gallery}, every demo a page of the site's
 * MPA at the address its tour links to, told the trail read off it; the tour a page beside them; the
 * plain page not under the chrome at all.
 */
class GalleryOnTheSiteTest {

    private static String page(String path, Query query) {
        return DemoSite.INSTANCE.router().resolve(Path.parse(path)).orElseThrow(() -> new AssertionError("nothing at " + path)).html(query).body();
    }

    private static String page(String path) { return page(path, Query.NONE); }

    private static List<String> children(String at) throws Exception {
        var e = new JsonObject(new EntryGetAction(DemoSite.ROUTER).execute(new EntryGetAction.Query(at), new EmptyParam.NoHeaders()).get().body());
        return e.getJsonArray("children").stream().map(o -> ((JsonObject) o).getString("to")).toList();
    }

    @Test
    void theGalleryIsACataloguePerGroup_thenTheTourAndThePlainPage() throws Exception {
        assertEquals(List.of("/gallery/controls", "/gallery/relations", "/gallery/layout", "/gallery/dialogs", "/gallery/focus", "/gallery/tour", "/gallery/plain"),
                children("/gallery"));
        assertEquals(List.of("/gallery/controls/buttons", "/gallery/controls/cards", "/gallery/controls/sliders"), children("/gallery/controls"));
    }

    @Test
    void theTourImportsTheChromeThenTheShell_andOpensOnTheDemoAskedFor() {
        var body = page("/gallery/tour");
        assertTrue(body.contains("<title>Tour · Homing · demo</title>"), body);
        assertTrue(body.contains("preferences: Object.freeze({module:\"\\/module?class=hue.captains.singapura.js.homing.site.demo.gallery.prefs.GalleryPreferences\"})"), body);
        int chrome = body.indexOf("site.mpa.MpaChrome");
        int app    = body.indexOf("demo.gallery.GalleryShellApp");
        assertTrue(chrome > 0 && app > chrome, "chrome import before app import: " + body);
        assertTrue(body.contains("const params = Object.freeze({});"), body);   // no demo asked for: the first
        assertTrue(page("/gallery/tour", Query.of("demo", "grid")).contains("const params = Object.freeze({\"demo\":\"grid\"});"));
    }

    @Test
    void everyDemoIsAPageAtTheAddressTheTourLinksTo_toldItsTrail() {
        for (var d : GalleryDemos.DEMOS) {
            var body = page(d.page());
            assertTrue(body.contains(d.app().getClass().getCanonicalName()), d.page() + ": its app");
            assertTrue(body.contains("{text:\"Gallery\",to:\"\\/gallery\"}"), d.page() + ": the gallery in its trail");
            assertTrue(body.contains("to:\"" + d.page().replace("/", "\\/") + "\"}"), d.page() + ": itself, last in its trail");
            assertFalse(DemoSite.INSTANCE.router().resolve(Path.parse(d.page() + "/more")).isPresent(), d.page() + ": a demo is a leaf");
        }
        var buttons = page("/gallery/controls/buttons");
        assertTrue(buttons.contains("appMain(page.main, Object.freeze(Object.assign({}, {}, { keyboard: page.keyboard, trail: page.trail })));"), buttons);   // paramless: the page's own alone
        for (String old : List.of("/buttons", "/grid", "/gallery/buttons", "/plain")) {
            assertFalse(DemoSite.INSTANCE.router().resolve(Path.parse(old)).isPresent(), old + ": the gallery's old addresses are nobody's");
        }
    }

    @Test
    void thePreferencesPageImportsTheViewAndTheRegistry_noWidget() {
        var prefs = GalleryDemos.DEMOS.stream().filter(d -> d.slug().equals("preferences")).findFirst().orElseThrow();
        var body = page(prefs.page());
        assertFalse(body.contains("ThemeWidget") || body.contains("ScaleWidget"), "widgets are not imported by the page");
    }

    @Test
    void thePlainPageIsNotUnderTheChrome() {
        var body = page("/gallery/plain");
        assertTrue(body.contains("<h1>A plain page</h1>"), body);
        assertTrue(body.contains("<a href=\"/gallery\">Back to the gallery</a>"), body);
        assertFalse(body.contains("MpaChrome"), "a string, no module, no theme");
    }
}
