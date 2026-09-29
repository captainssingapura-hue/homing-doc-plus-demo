package hue.captains.singapura.js.homing.demo.workspacewidgets.animals;

import hue.captains.singapura.js.homing.ssjs.test.SecretaryTestBase;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The animal choice party's secretary: nothing chosen at first; a choice said to
 * every member, and the same one again nothing; a late member answered alone,
 * but only when something is chosen; anything else kept, and nothing done.
 */
class AnimalChoiceSecretaryTest extends SecretaryTestBase {

    private static final String MODULE = "/homing/js/hue/captains/singapura/js/homing/demo/workspacewidgets/animals/AnimalChoiceSecretaryModule.js";

    @BeforeEach
    void load() { loadSecretary(MODULE, "AnimalChoiceSecretary"); }

    private Value chose(String animal) { return dispatch(initial(), envelope("Choose", Map.of("animal", animal), "p1")).getMember("newState"); }

    @Test
    void initiallyNothingIsChosen_byNoOne() {
        Value s = initial();
        assertTrue(s.getMember("chosen").isNull());
        assertTrue(s.getMember("lastChangedBy").isNull());
        assertEquals(0, s.getMember("changes").asInt());
    }

    @Test
    void aChoiceIsSaidToEveryMember_andWhoAndHowOftenKept() {
        Value step = dispatch(initial(), envelope("Choose", Map.of("animal", "penguin"), "p1"));
        assertStateField(step, "chosen", "penguin");
        assertStateField(step, "lastChangedBy", "p1");
        assertStateField(step, "changes", 1);
        assertActionCount(step, 1);
        assertActionKind(step, 0, "BroadcastToMembers");
        assertEquals("Chosen penguin", action(step, 0).getMember("message").getMember("kind").asString() + " " + action(step, 0).getMember("message").getMember("animal").asString());
    }

    @Test
    void theSameChoiceAgainIsNothing() {
        Value again = dispatch(chose("penguin"), envelope("Choose", Map.of("animal", "penguin"), "p2"));
        assertActionCount(again, 0);
        assertStateField(again, "lastChangedBy", "p1");
        assertStateField(again, "changes", 1);
    }

    @Test
    void aLateMemberIsAnsweredAlone_butOnlyWhenOneIsChosen() {
        assertActionCount(dispatch(initial(), envelope("CurrentRequested", Map.of(), "p3")), 0);
        Value step = dispatch(chose("whale"), envelope("CurrentRequested", Map.of(), "p3"));
        assertActionCount(step, 1);
        assertActionKind(step, 0, "SendToMember");
        assertEquals("p3", action(step, 0).getMember("to").asString());
        assertEquals("whale", action(step, 0).getMember("message").getMember("animal").asString());
    }

    @Test
    void anythingElseIsKept_andNothingDone() {
        Value step = dispatch(chose("whale"), envelope("Chosen", Map.of("animal", "ghost"), "p4"));
        assertActionCount(step, 0);
        assertStateField(step, "chosen", "whale");
        assertEquals("Chosen p4", step.getMember("newState").getMember("recentUnknown").getArrayElement(0).getMember("kind").asString()
                + " " + step.getMember("newState").getMember("recentUnknown").getArrayElement(0).getMember("from").asString());
    }
}
