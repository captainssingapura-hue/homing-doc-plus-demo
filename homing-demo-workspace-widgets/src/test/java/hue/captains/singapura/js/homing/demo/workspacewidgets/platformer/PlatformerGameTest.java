package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.ssjs.test.JsModuleTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The platformer's world, headless: a run starts on the start platform with the
 * terrain ahead made; held keys run and turn the animal; the same actions at the
 * same frames make the same world; and a WATCHER - handed each platform the
 * player's terrain made, and every action, stepped once per Tick - re-simulates
 * the run exactly, never making a platform of its own. Off the end, into the
 * lava, and the run is over.
 */
class PlatformerGameTest extends JsModuleTestBase {

    private static final String DIR = "/homing/js/hue/captains/singapura/js/homing/demo/workspacewidgets/platformer/";

    @BeforeEach
    void load() {
        for (String m : new String[]{"PlatformEngine", "JumpPhysics", "PlatformerGame"}) loadModule(DIR + m + "Module.js");
        js.eval("js", """
                // A seeded chance: the same terrain every time it is asked from the start.
                function seeded(seed) { var s = seed >>> 0; return function () { s = (s * 1664525 + 1013904223) >>> 0; return s / 4294967296; }; }
                function never() { throw new Error("a watcher never makes a platform"); }
                function scene(g) { var s = g.state(); return [s.tick, s.x, s.y.toFixed(3), s.cameraX.toFixed(3), s.facingRight, s.score, s.over, s.platforms.length].join(" "); }
                // A run played: actions at frames; what it told, in order, kept - as the widget tells the platformer party.
                function play(seed, plan, frames) {
                    var g = new PlatformerGame({ random: seeded(seed) }), told = [];
                    g.reset().forEach(function (p) { told.push({ kind: "PlatformGenerated", x: p.x, y: p.y, w: p.w, vehicle: p.vehicle }); });
                    for (var f = 0; f < frames && !g.over; f++) {
                        (plan[f] || []).forEach(function (a) { g.apply(a); told.push(a); });
                        var r = g.step();
                        if (r.alive) { g.ahead().forEach(function (p) { told.push({ kind: "PlatformGenerated", x: p.x, y: p.y, w: p.w, vehicle: p.vehicle }); }); g.prune(); }
                        told.push({ kind: "Tick" });
                    }
                    return { game: g, told: told };
                }
                // A watcher: the start alone of its own, then the stream applied in order, a step for each Tick.
                function watch(told) {
                    var w = new PlatformerGame({ random: never });
                    w.reset(false);
                    told.forEach(function (m) {
                        if (m.kind === "PlatformGenerated") w.place(m);
                        else if (m.kind === "Tick") { if (w.step().alive) w.prune(); }
                        else w.apply(m);
                    });
                    return w;
                }
                var RUN = { 0: [{ kind: "MoveStarted", dir: "right" }], 20: [{ kind: "Jumped" }], 45: [{ kind: "GravityChanged", value: 0.8 }],
                            60: [{ kind: "Jumped" }], 90: [{ kind: "MoveStopped", dir: "right" }, { kind: "MoveStarted", dir: "left" }], 100: [{ kind: "MoveStopped", dir: "left" }] };
                """);
    }

    private String eval(String code) { return js.eval("js", code).toString(); }

    @Test
    void aRunStartsAboveTheStart_theTerrainAheadMade_andDropsOntoIt() {
        eval("var g = new PlatformerGame({ random: seeded(7) }); var made = g.reset();");
        assertEquals("0 100 false", eval("[g.tick, g.x, g.over].join(' ')"));
        assertEquals("true", eval("String(made.length > 3 && g.state().platforms.length === made.length + 1)"), "the start, then the terrain made ahead");
        eval("var landed = null; for (var i = 0; i < 60 && !landed; i++) landed = g.step().landed;");
        assertEquals("true false", eval("[landed === g.state().platforms[0], g.inAir()].join(' ')"), "dropped onto the start, and standing");
    }

    @Test
    void heldKeysRunAndTurnTheAnimal_letGoTheyStop() {
        eval("var g = new PlatformerGame({ random: seeded(7) }); g.reset(); g.apply({ kind: 'MoveStarted', dir: 'left' }); for (var i = 0; i < 4; i++) g.step();");
        assertEquals("80 false left", eval("[g.x, g.facingRight, g.held().join()].join(' ')"));
        eval("g.apply({ kind: 'MoveStopped', dir: 'left' }); g.step();");
        assertEquals("80 ", eval("[g.x, g.held().join()].join(' ')"));
    }

    @Test
    void theSameActionsAtTheSameFrames_makeTheSameWorld() {
        assertEquals(eval("scene(play(42, RUN, 150).game)"), eval("scene(play(42, RUN, 150).game)"));
    }

    @Test
    void aWatcherHandedTheStream_reSimulatesTheRunExactly_makingNoPlatformOfItsOwn() {
        eval("var p = play(42, RUN, 150), w = watch(p.told);");
        assertEquals(eval("scene(p.game)"), eval("scene(w)"));
        assertTrue(Integer.parseInt(eval("p.told.filter(function (m) { return m.kind === 'Tick'; }).length")) > 0);
    }

    @Test
    void offTheEnd_intoTheLava_theRunIsOver() {
        eval("var g = new PlatformerGame({ random: seeded(3) }); g.reset(); g.apply({ kind: 'MoveStarted', dir: 'left' }); var r; for (var i = 0; i < 400 && !g.over; i++) r = g.step();");
        assertEquals("true false", eval("[g.over, r.alive].join(' ')"));
        assertEquals("false", eval("String(g.step().alive)"), "over, it steps no more");
    }
}
