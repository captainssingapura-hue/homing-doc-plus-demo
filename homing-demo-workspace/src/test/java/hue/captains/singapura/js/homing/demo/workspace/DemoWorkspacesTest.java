package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.workspace.groups.core.models.SplitGrid;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The demo's workspaces, declared: one group - the video room under Media, its
 * default, and the animal platformer under Games - and what the grouped page is
 * handed, the demo's manifests, groups and first states. The page itself is the
 * demo site's, placed in its catalogue; past the handover, the site's and the shell's.
 */
class DemoWorkspacesTest {

    @Test
    void theGroupFilesTheVideoRoomUnderMedia_andThePlatformerUnderGames() {
        String js = String.join("\n", DemoGroupsModule.INSTANCE.selfContent(null));
        assertTrue(js.contains("defaultKind: \"demo\""), js);
        assertTrue(js.contains("kind: \"demo\", title: \"Video room\", path: \"media/demo\""), js);
        assertTrue(js.contains("kind: \"platformer\", title: \"Animal platformer\", path: \"games/platformer\""), js);
    }

    /** The video room: the playlist and the monitors. The platformer: the game - held once - its replays, and the animals. */
    @Test
    void eachWorkspacesManifest_isItsDeclaration() {
        String js = String.join("\n", DemoWorkspaceModule.INSTANCE.selfContent(null));
        assertTrue(js.contains("const DEMO_WORKSPACES = Object.freeze({ \"demo\": Object.freeze({ name: \"demo\""), js);
        for (String kind : List.of("\"video\"", "\"focus-tree\"", "\"steward-lamp\"", "\"domops-tree\"", "\"party-log\"")) assertTrue(js.contains(kind), kind + " in " + js);
        assertTrue(js.contains("\"platformer\": Object.freeze({ Widget: PlatformerPlay, title: \"Animal platformer\", parties: Object.freeze([PLATFORMER, ANIMAL_CHOICE]), single: true })"), js);
        assertTrue(js.contains("\"animal-selector\": Object.freeze({ Widget: AnimalSelector, title: \"Animal\", parties: Object.freeze([ANIMAL_CHOICE]) })"), js);
        assertEquals(List.of("animal-choice", "platformer"), PlatformerWorkspace.INSTANCE.rootParties().stream().map(r -> r.type().name()).sorted().toList(),
                "its root parties: the run, told, and the animal, chosen");
        assertTrue(js.contains("\"platformer-replay\": Object.freeze({ Widget: PlatformerReplay, title: \"Platformer replay\", parties: Object.freeze([PLATFORMER, ANIMAL_CHOICE]) })"), js);
        assertEquals(List.of("platformer", "platformer-replay", "animal-selector"), PlatformerWorkspace.INSTANCE.kinds().stream().map(k -> k.kind()).toList(),
                "the game, its replays and the selector");
    }

    /** The first time, in the split grid: two videos side by side; the animals a quarter of the room, the game above its replay beside them. */
    @Test
    void eachFirstStateIsTheLaunchersArrangement() {
        assertEquals(List.of("left", "right"), DemoArrangements.SPLIT_GRID.placement().regions().stream().map(r -> r.name().value()).toList());
        assertTrue(DemoArrangements.SPLIT_GRID.widgets().stream().allMatch(w -> w.kind().value().equals("video")));
        assertEquals(List.of("animals", "game", "watch"), DemoArrangements.PLATFORMER_GRID.placement().regions().stream().map(r -> r.name().value()).toList());
        String js = String.join("\n", DemoArrangementModule.INSTANCE.selfContent(null));
        assertTrue(js.contains("const DEMO_ARRANGEMENTS = Object.freeze({ \"demo\": Object.freeze({ engine: \"split-grid\", workspace: \"demo\""), js);
        assertTrue(js.contains("\"platformer\": Object.freeze({ engine: \"split-grid\", workspace: \"platformer\""), js);
        assertEquals(DemoArrangements.PLATFORMER_GRID, DemoGroups.SITE.arrangements("platformer").orElseThrow().forEngine(SplitGrid.ENGINE, SplitGrid.class).orElseThrow());
    }
}
