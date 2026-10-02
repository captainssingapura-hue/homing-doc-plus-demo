package hue.captains.singapura.js.homing.mermaidmirror;

import hue.captains.singapura.js.homing.catalogue.site.CatalogueListingApp;
import hue.captains.singapura.js.homing.catalogue.site.EntryGetAction;
import hue.captains.singapura.js.homing.docview.app.DocViewApp;
import hue.captains.singapura.js.homing.docview.site.DocViews;
import hue.captains.singapura.js.homing.docview.site.PayloadGetAction;
import hue.captains.singapura.js.homing.libs.ExternalModuleUrlRegistry;
import hue.captains.singapura.js.homing.libs.MermaidProxyModule;
import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Query;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Mermaid-mirror site: its root a listing, its one doc read in DocView at {@code /mermaid} with
 * its diagram a part of it; and the server's one act before serving - Mermaid's proxy pointed at the
 * local CDN, every page's import following it.
 */
class MermaidSiteTest {

    private static String page(String path) {
        return MermaidSite.INSTANCE.router().resolve(Path.parse(path)).orElseThrow(() -> new AssertionError("nothing at " + path)).html(Query.NONE).body();
    }

    @Test
    void theRootListsTheOneDoc_readInDocView() throws Exception {
        assertTrue(page("/").contains(CatalogueListingApp.class.getCanonicalName()), "the listing app");
        var root = new JsonObject(new EntryGetAction(MermaidSite.ROUTER).execute(new EntryGetAction.Query("/"), new EmptyParam.NoHeaders()).get().body());
        assertEquals(List.of("/mermaid"), root.getJsonArray("children").stream().map(o -> ((JsonObject) o).getString("to")).toList());
        assertTrue(page("/mermaid").contains(DocViewApp.class.getCanonicalName()), "DocView, as the page");
    }

    @Test
    void theDocsDiagramIsAPartOfItsTree() throws Exception {
        var payload = new JsonObject(new PayloadGetAction(new DocViews(MermaidSite.ROUTER))
                .execute(new PayloadGetAction.Query("/mermaid"), new EmptyParam.NoHeaders()).get().body());
        assertEquals("/mermaid", payload.getString("doc"));
        assertTrue(payload.getJsonObject("tree").encode().contains("\"type\":\"code\""), "the fences are code parts of the doc's leaf");
        String all = payload.encode();
        assertTrue(all.contains("mermaid") && all.contains("Local CDN :8109"), "the flowchart, its source a part to be drawn: " + all);
    }

    @Test
    void theServerPointsMermaidsProxyAtTheLocalCdn() {
        try {
            assertFalse(String.join("\n", MermaidProxyModule.INSTANCE.selfContent(null)).contains(MermaidStudioServer.DEFAULT_CDN_URL), "the public CDN until told");
            MermaidStudioServer.mirrorTo(MermaidStudioServer.DEFAULT_CDN_URL);
            assertTrue(String.join("\n", MermaidProxyModule.INSTANCE.selfContent(null))
                    .contains("import mermaidLib from \"" + MermaidStudioServer.DEFAULT_CDN_URL + "\";"), "every page's Mermaid import, from the local CDN");
        } finally {
            ExternalModuleUrlRegistry.INSTANCE.reset(MermaidProxyModule.class);
        }
    }
}
