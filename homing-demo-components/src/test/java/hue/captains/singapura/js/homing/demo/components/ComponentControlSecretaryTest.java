package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.ssjs.test.SecretaryTestBase;
import hue.captains.singapura.js.homing.ui.controlpanel.ControlCatalogueModule;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The component-control party's secretary: nothing set at first; a degree or a switch kept by
 * option and said to every member, the same again nothing; an option done said, nothing kept; a
 * late member answered alone with everything set; a reset said to all, and nothing set it is
 * nothing; an option the catalogue lacks, or sent by the wrong means, refused; anything else kept.
 */
class ComponentControlSecretaryTest extends SecretaryTestBase {

    private static final String MODULE = "/homing/js/hue/captains/singapura/js/homing/demo/components/ComponentControlSecretaryModule.js";

    @BeforeEach
    void load() {
        global("Object");                                                                        // the context, booted
        js.eval("js", String.join("\n", ControlCatalogueModule.INSTANCE.selfContent(null)));   // the catalogue it imports, as generated
        loadSecretary(MODULE, "ComponentControlSecretary");
    }

    private Value step(Value state, String kind, Map<String, Object> payload) { return dispatch(state, envelope(kind, payload, "panel")); }

    private Value sized(double v) { return step(initial(), "SetDegree", Map.of("option", "size", "value", v)).getMember("newState"); }

    @Test
    void initiallyNothingIsSet() {
        Value s = initial();
        assertEquals(0, s.getMember("degrees").getMemberKeys().size());
        assertEquals(0, s.getMember("switches").getMemberKeys().size());
        assertEquals(0, s.getMember("changes").asInt());
    }

    @Test
    void aDegreeIsKeptByOption_andSaidToEveryMember_theSameAgainNothing() {
        Value step = step(initial(), "SetDegree", Map.of("option", "size", "value", 0.4));
        assertActionCount(step, 1);
        assertActionKind(step, 0, "BroadcastToMembers");
        Value said = action(step, 0).getMember("message");
        assertEquals("DegreeSet size 0.4", said.getMember("kind").asString() + " " + said.getMember("option").asString() + " " + said.getMember("value").asDouble());
        assertEquals(0.4, step.getMember("newState").getMember("degrees").getMember("size").asDouble());
        assertActionCount(step(sized(0.4), "SetDegree", Map.of("option", "size", "value", 0.4)), 0);
    }

    @Test
    void aSwitchIsKept_andSaid() {
        Value step = step(initial(), "SetSwitch", Map.of("option", "enabled", "on", false));
        assertActionCount(step, 1);
        assertEquals("SwitchSet", action(step, 0).getMember("message").getMember("kind").asString());
        assertTrue(!step.getMember("newState").getMember("switches").getMember("enabled").asBoolean());
    }

    @Test
    void anOptionDone_isSaid_andNothingKept() {
        Value step = step(sized(1), "Invoke", Map.of("option", "open-modal"));
        assertActionCount(step, 1);
        assertEquals("Invoked open-modal", action(step, 0).getMember("message").getMember("kind").asString() + " "
                + action(step, 0).getMember("message").getMember("option").asString());
        assertEquals(1, step.getMember("newState").getMember("changes").asInt(), "the size set before, and nothing for the opening");
    }

    @Test
    void aLateMember_isAnsweredAlone_withEverythingSet() {
        Value step = dispatch(sized(-0.5), envelope("StateRequested", Map.of(), "late"));
        assertActionCount(step, 1);
        assertActionKind(step, 0, "SendToMember");
        assertEquals("late", action(step, 0).getMember("to").asString());
        Value state = action(step, 0).getMember("message");
        assertEquals("State", state.getMember("kind").asString());
        assertEquals("size", state.getMember("degrees").getArrayElement(0).getMember("option").asString());
        assertEquals(-0.5, state.getMember("degrees").getArrayElement(0).getMember("value").asDouble());
    }

    @Test
    void aReset_isSaidToAll_andWithNothingSetItIsNothing() {
        Value step = step(sized(1), "Reset", Map.of());
        assertActionCount(step, 1);
        assertEquals("State", action(step, 0).getMember("message").getMember("kind").asString());
        assertEquals(0, step.getMember("newState").getMember("degrees").getMemberKeys().size());
        assertActionCount(step(initial(), "Reset", Map.of()), 0);
    }

    @Test
    void anOptionTheCatalogueLacks_orSentByTheWrongMeans_isRefused() {
        Value lacks = step(initial(), "SetDegree", Map.of("option", "loudness", "value", 1));
        assertActionCount(lacks, 0);
        assertEquals("loudness", lacks.getMember("newState").getMember("recentRefused").getArrayElement(0).getMember("option").asString());
        assertActionCount(step(initial(), "SetSwitch", Map.of("option", "size", "on", true)), 0);
        assertActionCount(step(initial(), "Invoke", Map.of("option", "enabled")), 0);
    }

    @Test
    void thePartysOwnWords_fromAMember_areKept_andNothingDone() {
        Value step = step(initial(), "DegreeSet", Map.of("option", "size", "value", 1));
        assertActionCount(step, 0);
        assertEquals("DegreeSet", step.getMember("newState").getMember("recentUnknown").getArrayElement(0).getMember("kind").asString());
    }
}
