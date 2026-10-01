package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.ssjs.test.JsModuleTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The platformer party as a page has it - its type generated from Java, its
 * secretary - with a player that answers for the world and watchers that join:
 * a Snapshot, records inside records, checked all the way down where it enters,
 * handed to the watcher that asked and no other, frozen all the way down; a run
 * begun again, a snapshot to every watcher; a world that does not read, refused.
 */
class PlatformerPartyTest extends JsModuleTestBase {

    private static final String DIR = "/homing/js/hue/captains/singapura/js/homing/demo/workspacewidgets/platformer/";

    @BeforeEach
    void load() {
        loadModule("/homing/js/hue/captains/singapura/js/homing/workspace/parties/MessagingPartyModule.js");
        for (String m : new String[]{"PlatformEngine", "JumpPhysics", "PlatformerGame", "PlatformerSecretary"}) loadModule(DIR + m + "Module.js");
        js.eval("js", Platformer.TYPE.js() + """

                var console = { error: function () {} };
                function seeded(seed) { var s = seed >>> 0; return function () { s = (s * 1664525 + 1013904223) >>> 0; return s / 4294967296; }; }
                var party = new MessagingParty(PLATFORMER, PlatformerSecretary);
                var game = new PlatformerGame({ random: seeded(5) }); game.reset();
                game.apply({ kind: "MoveStarted", dir: "right" }); for (var i = 0; i < 20; i++) game.step();
                var player = party.join("player", { WorldWanted: function (m) { player.tell({ kind: "Snapshot", to: m.asker, world: game.world() }); } });
                var got = [], worlds = {};
                function watcher(name) {
                    return party.join(name, { Snapshot: function (m) { got.push(name + " " + m.world.tick + " " + m.world.platforms.length); worlds[name] = m.world; },
                                              Tick: function () { got.push(name + " tick"); } });
                }
                """);
    }

    private String eval(String code) { return js.eval("js", code).toString(); }

    @Test
    void aWatcherThatAsks_isHandedTheWorldAlone_recordsInsideRecords() {
        eval("var early = watcher('early'), late = watcher('late'); late.tell({ kind: 'WorldRequested' });");
        assertEquals("late 20 " + eval("game.world().platforms.length"), eval("got.join('|')"), "the one that asked, and no other");
        assertEquals("true true", eval("[Object.isFrozen(worlds.late.platforms), Object.isFrozen(worlds.late.platforms[0])].join(' ')"), "frozen all the way down");
        assertEquals("0", eval("String(party.inspect().refused.length)"));
    }

    @Test
    void aRunBegunAgain_isASnapshotToEveryWatcher_andTheStreamGoesOnToAll() {
        eval("watcher('a'); watcher('b'); game.reset(); player.tell({ kind: 'Snapshot', to: '', world: game.world() }); player.tell({ kind: 'Tick' });");
        assertEquals("a 0 " + eval("game.world().platforms.length") + "|b 0 " + eval("game.world().platforms.length") + "|a tick|b tick", eval("got.join('|')"));
    }

    @Test
    void aWorldThatDoesNotRead_isRefusedWhereItEnters_andGoesNowhere() {
        eval("watcher('w'); var bad = game.world(); bad.platforms[2].vehicle = 'tank'; player.tell({ kind: 'Snapshot', to: '', world: bad });");
        assertEquals("", eval("got.join('|')"));
        assertEquals("Snapshot.world.platforms[2].vehicle is a number, not string", eval("party.inspect().refused[0].reason"));
    }
}
