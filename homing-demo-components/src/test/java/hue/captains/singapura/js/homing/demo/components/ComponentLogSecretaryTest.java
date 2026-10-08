package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.ssjs.test.SecretaryTestBase;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A component-log party's secretary: a line numbered, kept and said to every member; only the last
 * fifty kept; cleared on command, and with nothing kept that is nothing; a late member answered
 * alone with the lines kept; anything else kept, and nothing done.
 */
class ComponentLogSecretaryTest extends SecretaryTestBase {

    private static final String MODULE = "/homing/js/hue/captains/singapura/js/homing/demo/components/ComponentLogSecretaryModule.js";

    @BeforeEach
    void load() { loadSecretary(MODULE, "ComponentLogSecretary"); }

    private Value noted(Value state, String words) { return dispatch(state, envelope("Note", Map.of("words", words), "panel")).getMember("newState"); }

    @Test
    void aLineIsNumbered_kept_andSaidToEveryMember() {
        Value step = dispatch(initial(), envelope("Note", Map.of("words", "opened"), "panel"));
        assertActionCount(step, 1);
        assertActionKind(step, 0, "BroadcastToMembers");
        Value said = action(step, 0).getMember("message");
        assertEquals("Noted 1 opened", said.getMember("kind").asString() + " " + said.getMember("seq").asInt() + " " + said.getMember("words").asString());
        assertEquals(1, step.getMember("newState").getMember("lines").getArraySize());
    }

    @Test
    void onlyTheLastFifty_areKept() {
        Value s = initial();
        for (int i = 1; i <= 55; i++) s = noted(s, "line " + i);
        assertEquals(50, s.getMember("lines").getArraySize());
        assertEquals("line 6", s.getMember("lines").getArrayElement(0).getMember("words").asString());
        assertEquals(55, s.getMember("seq").asInt());
    }

    @Test
    void clearedOnCommand_andWithNothingKeptThatIsNothing() {
        Value step = dispatch(noted(initial(), "opened"), envelope("ClearLog", Map.of(), "panel"));
        assertActionCount(step, 1);
        assertEquals("Cleared", action(step, 0).getMember("message").getMember("kind").asString());
        assertEquals(0, step.getMember("newState").getMember("lines").getArraySize());
        assertEquals(1, step.getMember("newState").getMember("clears").asInt());
        assertActionCount(dispatch(initial(), envelope("ClearLog", Map.of(), "panel")), 0);
    }

    @Test
    void aLateMember_isAnsweredAlone_withTheLinesKept() {
        Value step = dispatch(noted(noted(initial(), "one"), "two"), envelope("HistoryRequested", Map.of(), "log"));
        assertActionCount(step, 1);
        assertActionKind(step, 0, "SendToMember");
        Value lines = action(step, 0).getMember("message").getMember("lines");
        assertEquals("one two", lines.getArrayElement(0).getMember("words").asString() + " " + lines.getArrayElement(1).getMember("words").asString());
    }

    @Test
    void anythingElse_isKept_andNothingDone() {
        Value step = dispatch(initial(), envelope("Noted", Map.of("seq", 9, "words", "forged"), "log"));
        assertActionCount(step, 0);
        assertEquals("Noted", step.getMember("newState").getMember("recentUnknown").getArrayElement(0).getMember("kind").asString());
    }
}
