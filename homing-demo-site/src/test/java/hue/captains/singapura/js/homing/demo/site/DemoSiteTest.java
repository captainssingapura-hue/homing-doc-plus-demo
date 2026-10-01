package hue.captains.singapura.js.homing.demo.site;

import hue.captains.singapura.js.homing.catalogue.site.CatalogueListingApp;
import hue.captains.singapura.js.homing.catalogue.site.EntryGetAction;
import hue.captains.singapura.js.homing.docview.app.DocViewApp;
import hue.captains.singapura.js.homing.docview.site.DocViews;
import hue.captains.singapura.js.homing.docview.site.PayloadGetAction;
import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Query;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The demo site: its root a listing page of the site's one MPA, and the composed doc read in
 * DocView at its authentic path - its tree built, every content kind it holds a part of it.
 */
class DemoSiteTest {

    private static String page(String path) {
        return DemoSite.INSTANCE.router().resolve(Path.parse(path)).orElseThrow(() -> new AssertionError("nothing at " + path)).html(Query.NONE).body();
    }

    private static JsonObject entry(String at) throws Exception {
        return new JsonObject(new EntryGetAction(DemoSite.ROUTER).execute(new EntryGetAction.Query(at), new EmptyParam.NoHeaders()).get().body());
    }

    @Test
    void theRootIsAListingPage_underTheDemosBrand() throws Exception {
        String root = page("/");
        assertTrue(root.contains(CatalogueListingApp.class.getCanonicalName()), "the listing app");
        assertTrue(root.contains("label:\"Homing · demo\""), "the demo's brand on the chrome");
        JsonObject e = entry("/");
        assertEquals("Homing demo", e.getString("name"));
        assertEquals(List.of("/composed-doc"), e.getJsonArray("children").stream().map(o -> ((JsonObject) o).getString("to")).toList());
    }

    @Test
    void theComposedDoc_readInDocView_itsTreeBuilt() throws Exception {
        String doc = page("/composed-doc");
        assertTrue(doc.contains(DocViewApp.class.getCanonicalName()), "DocView, as the page");
        var payload = new JsonObject(new PayloadGetAction(new DocViews(DemoSite.ROUTER))
                .execute(new PayloadGetAction.Query("/composed-doc"), new EmptyParam.NoHeaders()).get().body());
        assertEquals("/composed-doc", payload.getString("doc"), "a doc read by the path its page is at");
        JsonObject tree = payload.getJsonObject("tree");
        assertEquals(ComposedDemoDoc.INSTANCE.title(), tree.getJsonObject("label").getString("text"));
        String all = tree.encode();
        for (String kind : List.of("\"prose\"", "\"image\"", "\"table\"", "\"code\"")) {
            assertTrue(all.contains(kind), "the doc's parts hold a " + kind + " part");
        }
        JsonArray sections = tree.getJsonArray("children");
        assertTrue(sections.size() >= 5, "a section for each titled segment, not one body: " + sections.size());
    }
}
