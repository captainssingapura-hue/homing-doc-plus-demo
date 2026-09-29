package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.ssjs.test.SecretaryTestBase;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The platformer party's secretary: every event of a run said on to every member
 * as it came, and counted - frames and platforms apart - never kept; anything
 * else kept among the unknown, and nothing done.
 */
class PlatformerSecretaryTest extends SecretaryTestBase {

    private static final String MODULE = "/homing/js/hue/captains/singapura/js/homing/demo/workspacewidgets/platformer/PlatformerSecretaryModule.js";

    @BeforeEach
    void load() { loadSecretary(MODULE, "PlatformerSecretary"); }

    @Test
    void initiallyNothingHasBeenSaid() {
        Value s = initial();
        assertEquals(0, s.getMember("events").asInt());
        assertTrue(s.getMember("last").isNull());
    }

    @Test
    void everyEventOfTheRunIsSaidOnAsItCame() {
        Value step = dispatch(initial(), envelope("PlatformGenerated", Map.of("x", 310.5, "y", 280, "w", 120.25, "vehicle", 2), "play"));
        assertActionCount(step, 1);
        assertActionKind(step, 0, "BroadcastToMembers");
        Value m = action(step, 0).getMember("message");
        assertEquals("PlatformGenerated 310.5 280 120.25 2", m.getMember("kind").asString() + " " + m.getMember("x").asDouble() + " " + m.getMember("y").asInt()
                + " " + m.getMember("w").asDouble() + " " + m.getMember("vehicle").asInt());
        assertStateField(step, "last", "PlatformGenerated");
        assertStateField(step, "lastFrom", "play");
    }

    @Test
    void framesAndPlatformsAreCounted_apartFromTheRest() {
        Value s = initial();
        for (String kind : new String[]{"Tick", "MoveStarted", "Tick", "Jumped", "Tick"}) {
            Map<String, Object> fields = kind.equals("MoveStarted") ? Map.of("dir", "right") : Map.of();
            s = dispatch(s, envelope(kind, fields, "play")).getMember("newState");
        }
        s = dispatch(s, envelope("PlatformGenerated", Map.of("x", 1, "y", 2, "w", 3, "vehicle", 1), "play")).getMember("newState");
        assertEquals("6 3 1", s.getMember("events").asInt() + " " + s.getMember("ticks").asInt() + " " + s.getMember("platforms").asInt());
    }

    @Test
    void anythingElseIsKept_andNothingDone() {
        Value step = dispatch(initial(), envelope("Snapshot", Map.of(), "late"));
        assertActionCount(step, 0);
        assertStateField(step, "events", 0);
        assertEquals("Snapshot", step.getMember("newState").getMember("recentUnknown").getArrayElement(0).getMember("kind").asString());
    }
}
