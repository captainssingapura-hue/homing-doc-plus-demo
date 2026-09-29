package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Query;
import hue.captains.singapura.js.homing.workspace.groups.core.models.SplitGrid;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The demo's workspaces run on their own: a site of one group - the video room
 * under Media, its default, and the animal platformer under Games - the grouped
 * page handed the demo's manifests, groups and first states. What this checks is
 * the handover; past it, the site's and the shell's.
 */
class DemoWorkspaceSiteTest {

    private static String page(String path) {
        return DemoWorkspaceSite.INSTANCE.router().resolve(Path.parse(path)).orElseThrow(() -> new AssertionError("nothing at " + path)).html(Query.NONE).body();
    }

    @Test
    void theGroupIsThePage_theRootSendsToIt_itsServerKeepingItsStates() {
        String html = page("/demo");
        assertTrue(html.contains(DemoWorkspaceApp.class.getCanonicalName()), "the page imports the demo's app and calls its appMain");
        assertTrue(html.contains("appMain(page.main"), "the app is handed the MPA's slot");
        assertTrue(html.contains("\"ws_group\":\"demo\""), "the route's group");
        assertTrue(html.contains("\"ws_server\":\"on\""), "the route says the server keeps its states");
        assertTrue(page("/").contains("window.location.replace(\"\\/demo\" + window.location.hash)"), "the root sends to the group");
        for (String nowhere : List.of("/elsewhere", "/media/demo", "/games/platformer")) {
            assertFalse(DemoWorkspaceSite.INSTANCE.router().resolve(Path.parse(nowhere)).isPresent(), nowhere);
        }
    }

    @Test
    void theGroupFilesTheVideoRoomUnderMedia_andThePlatformerUnderGames() {
        String js = String.join("\n", DemoGroupsModule.INSTANCE.selfContent(null));
        assertTrue(js.contains("defaultKind: \"demo\""), js);
        assertTrue(js.contains("kind: \"demo\", title: \"Video room\", path: \"media/demo\""), js);
        assertTrue(js.contains("kind: \"platformer\", title: \"Animal platformer\", path: \"games/platformer\""), js);
    }

    /** The video room: the playlist and the monitors. The platformer: the game - held once - and the animals, and nothing else. */
    @Test
    void eachWorkspacesManifest_isItsDeclaration() {
        String js = String.join("\n", DemoWorkspaceModule.INSTANCE.selfContent(null));
        assertTrue(js.contains("const DEMO_WORKSPACES = Object.freeze({ \"demo\": Object.freeze({ name: \"demo\""), js);
        for (String kind : List.of("\"video\"", "\"focus-tree\"", "\"steward-lamp\"", "\"domops-tree\"", "\"party-log\"")) assertTrue(js.contains(kind), kind + " in " + js);
        assertTrue(js.contains("\"platformer\": Object.freeze({ Widget: PlatformerPlay, title: \"Animal platformer\", parties: Object.freeze([PLATFORMER, ANIMAL_CHOICE]), single: true })"), js);
        assertTrue(js.contains("\"animal-selector\": Object.freeze({ Widget: AnimalSelector, title: \"Animal\", parties: Object.freeze([ANIMAL_CHOICE]) })"), js);
        assertEquals(List.of("animal-choice", "platformer"), PlatformerWorkspace.INSTANCE.rootParties().stream().map(r -> r.type().name()).sorted().toList(),
                "its root parties: the run, told, and the animal, chosen");
        assertEquals(2, PlatformerWorkspace.INSTANCE.kinds().size(), "the game and the selector alone");
    }

    /** The first time, in the split grid: two videos side by side; the animals beside the game, a quarter of the room. */
    @Test
    void eachFirstStateIsTheLaunchersArrangement() {
        assertEquals(List.of("left", "right"), DemoArrangements.SPLIT_GRID.placement().regions().stream().map(r -> r.name().value()).toList());
        assertTrue(DemoArrangements.SPLIT_GRID.widgets().stream().allMatch(w -> w.kind().value().equals("video")));
        assertEquals(List.of("animals", "game"), DemoArrangements.PLATFORMER_GRID.placement().regions().stream().map(r -> r.name().value()).toList());
        String js = String.join("\n", DemoArrangementModule.INSTANCE.selfContent(null));
        assertTrue(js.contains("const DEMO_ARRANGEMENTS = Object.freeze({ \"demo\": Object.freeze({ engine: \"split-grid\", workspace: \"demo\""), js);
        assertTrue(js.contains("\"platformer\": Object.freeze({ engine: \"split-grid\", workspace: \"platformer\""), js);
        assertEquals(DemoArrangements.PLATFORMER_GRID, DemoGroups.SITE.arrangements("platformer").orElseThrow().forEngine(SplitGrid.ENGINE, SplitGrid.class).orElseThrow());
    }
}
