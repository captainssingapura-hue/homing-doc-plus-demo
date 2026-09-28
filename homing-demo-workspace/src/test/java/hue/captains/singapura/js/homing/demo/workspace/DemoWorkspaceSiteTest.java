package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Query;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The demo workspace runs on its own: a site whose one page is the workspace,
 * the shell's page handed the demo's manifest. What this checks is the
 * handover; past it, the shell's.
 */
class DemoWorkspaceSiteTest {

    @Test
    void theRootIsTheDemoWorkspace_itsServerKeepingItsStates() {
        var found = DemoWorkspaceSite.INSTANCE.router().resolve(Path.parse("/"));
        assertTrue(found.isPresent());
        String html = found.get().html(Query.NONE).body();
        assertTrue(html.contains(DemoWorkspaceApp.class.getCanonicalName()), "the page imports the demo's app and calls its appMain");
        assertTrue(html.contains("appMain(page.main"), "the app is handed the MPA's slot");
        assertTrue(html.contains("\"ws_server\":\"on\""), "the route says the server keeps its states");
        assertFalse(DemoWorkspaceSite.INSTANCE.router().resolve(Path.parse("/elsewhere")).isPresent());
    }

    /** The ported widgets and the monitors, in one manifest. */
    @Test
    void theManifestPutsThePortedWidgetsAndTheMonitorsTogether() {
        String js = String.join("\n", DemoWorkspaceModule.INSTANCE.selfContent(null));
        assertTrue(js.contains("const DEMO_WORKSPACE = Object.freeze({ name: \"demo\""), js);
        for (String kind : List.of("\"video\"", "\"focus-tree\"", "\"steward-lamp\"", "\"domops-tree\"", "\"party-log\"")) assertTrue(js.contains(kind), kind + " in " + js);
        assertTrue(js.contains("Widget: EmbeddedVideo, title: \"Video\""), js);
    }
}
