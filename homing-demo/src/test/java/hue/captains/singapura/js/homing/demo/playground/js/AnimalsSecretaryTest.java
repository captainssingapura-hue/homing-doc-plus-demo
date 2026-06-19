package hue.captains.singapura.js.homing.demo.playground.js;

import hue.captains.singapura.js.homing.ssjs.test.SecretaryTestBase;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Diligent-Secretaries-doctrine test for {@code AnimalsSecretary} —
 * RFC 0028 cycle 6 phase 2.
 *
 * <p>Reuses the framework's GraalVM-backed {@link SecretaryTestBase} so a
 * downstream Secretary gets the same pure-function coverage discipline
 * with zero infrastructure duplication.</p>
 *
 * <p>State shape under test:</p>
 * <pre>{@code { selectedAnimal, lastChangedBy, recentUnknown } }</pre>
 */
class AnimalsSecretaryTest extends SecretaryTestBase {

    private static final String MODULE =
            "/homing/js/hue/captains/singapura/js/homing/demo/playground/AnimalsSecretaryModule.js";

    @BeforeEach
    void loadAnimalsSecretary() {
        loadSecretary(MODULE, "AnimalsSecretary");
    }

    @Test
    @DisplayName("initial — Random sentinel '-1', no provenance, empty unknown ring")
    void initialState() {
        Value s = initial();
        assertEquals("-1", s.getMember("selectedAnimal").asString());
        assertEquals(true, s.getMember("lastChangedBy").isNull());
        assertEquals(0L,   s.getMember("recentUnknown").getArraySize());
    }

    @Nested
    @DisplayName("AnimalSelectionRequested")
    class Selection {
        @Test void recordsSelectionAndBroadcastsChanged() {
            Value step = dispatch(initial(),
                    envelope("AnimalSelectionRequested",
                            Map.of("animal", "turtle"), "animals/ribbon-selector"));
            assertStateField(step, "selectedAnimal", "turtle");
            assertStateField(step, "lastChangedBy", "animals/ribbon-selector");
            assertActionCount(step, 1);
            assertActionKind(step, 0, "BroadcastToMembers");
            Value msg = action(step, 0).getMember("message");
            assertEquals("AnimalChanged", msg.getMember("kind").asString());
            assertEquals("turtle",        msg.getMember("animal").asString());
        }

        @Test void overwritesPreviousSelection() {
            Value first = dispatch(initial(),
                    envelope("AnimalSelectionRequested",
                            Map.of("animal", "turtle"), "a"));
            Value second = dispatch(first.getMember("newState"),
                    envelope("AnimalSelectionRequested",
                            Map.of("animal", "rabbit"), "b"));
            assertStateField(second, "selectedAnimal", "rabbit");
            assertStateField(second, "lastChangedBy", "b");
            assertEquals("rabbit",
                    action(second, 0).getMember("message").getMember("animal").asString());
        }
    }

    @Nested
    @DisplayName("CurrentAnimalRequested — late-join sync")
    class LateJoin {
        @Test void noopWhenStillAtRandomDefault() {
            Value step = dispatch(initial(),
                    envelope("CurrentAnimalRequested", Map.of(), "widget/late-1"));
            assertStateField(step, "selectedAnimal", "-1");   // unchanged
            assertActionCount(step, 0);                       // no useful reply
        }

        @Test void repliesPrivatelyWhenConcreteAnimalChosen() {
            Value picked = dispatch(initial(),
                    envelope("AnimalSelectionRequested",
                            Map.of("animal", "turtle"), "ribbon")).getMember("newState");
            Value step = dispatch(picked,
                    envelope("CurrentAnimalRequested", Map.of(), "widget/late-1"));
            assertStateField(step, "selectedAnimal", "turtle"); // unchanged by query
            assertActionCount(step, 1);
            assertActionKind(step, 0, "SendToMember");
            Value act = action(step, 0);
            assertEquals("widget/late-1", act.getMember("to").asString());
            assertEquals("AnimalChanged", act.getMember("message").getMember("kind").asString());
            assertEquals("turtle",        act.getMember("message").getMember("animal").asString());
        }
    }

    @Nested
    @DisplayName("GameActionRequested / CurrentGameRequested — broadcast feature")
    class GameBroadcast {
        @Test void broadcastsGameActionToMembersWhenNoRecipient() {
            Value step = dispatch(initial(),
                    envelope("GameActionRequested",
                            Map.of("event", Map.of("kind", "Tick", "t", 7)),
                            "animals/moving-play"));
            assertStateField(step, "selectedAnimal", "-1");   // routing leaves state untouched
            assertActionCount(step, 1);
            assertActionKind(step, 0, "BroadcastToMembers");
            Value msg = action(step, 0).getMember("message");
            assertEquals("GameAction", msg.getMember("kind").asString());
            assertEquals("Tick", msg.getMember("event").getMember("kind").asString());
            assertEquals(7, msg.getMember("event").getMember("t").asInt());
        }

        @Test void sendsGameActionPrivatelyWhenRecipientPresent() {
            Value step = dispatch(initial(),
                    envelope("GameActionRequested",
                            Map.of("to", "animals/replay-1",
                                   "event", Map.of("kind", "Snapshot")),
                            "animals/moving-play"));
            assertActionCount(step, 1);
            assertActionKind(step, 0, "SendToMember");
            Value act = action(step, 0);
            assertEquals("animals/replay-1", act.getMember("to").asString());
            assertEquals("GameAction", act.getMember("message").getMember("kind").asString());
            assertEquals("Snapshot",
                    act.getMember("message").getMember("event").getMember("kind").asString());
        }

        @Test void currentGameRequestedFansOutSnapshotRequestCarryingAsker() {
            Value step = dispatch(initial(),
                    envelope("CurrentGameRequested", Map.of(), "animals/replay-7"));
            assertActionCount(step, 1);
            assertActionKind(step, 0, "BroadcastToMembers");
            Value msg = action(step, 0).getMember("message");
            assertEquals("GameSnapshotRequested", msg.getMember("kind").asString());
            assertEquals("animals/replay-7", msg.getMember("asker").asString());
        }
    }

    @Nested
    @DisplayName("default branch — unknown messages")
    class Unknown {
        @Test void appendsToRecentUnknownAndEmitsNoActions() {
            Value step = dispatch(initial(),
                    envelope("BogusMessage", Map.of("x", 1), "stranger"));
            assertActionCount(step, 0);
            assertStateField(step, "selectedAnimal", "-1");
            Value recent = step.getMember("newState").getMember("recentUnknown");
            assertEquals(1L, recent.getArraySize());
            assertEquals("BogusMessage",
                    recent.getArrayElement(0).getMember("kind").asString());
            assertEquals("stranger",
                    recent.getArrayElement(0).getMember("from").asString());
        }

        @Test void recentUnknownRingIsBoundedAtTen() {
            Value state = initial();
            for (int i = 0; i < 15; i++) {
                state = dispatch(state,
                        envelope("Bogus_" + i, Map.of(), "x")).getMember("newState");
            }
            Value recent = state.getMember("recentUnknown");
            assertEquals(10L, recent.getArraySize());
            assertEquals("Bogus_5",  recent.getArrayElement(0).getMember("kind").asString());
            assertEquals("Bogus_14", recent.getArrayElement(9).getMember("kind").asString());
        }
    }
}
