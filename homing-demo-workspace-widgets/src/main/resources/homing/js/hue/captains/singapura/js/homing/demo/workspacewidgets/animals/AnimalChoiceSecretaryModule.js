// =============================================================================
// AnimalChoiceSecretary — the secretary of an animal choice party: which
// animal the widgets that meet in it show, said to every member when it
// changes, and to a member that asks. Diligent (Diligent Secretaries): its
// state answers an operator's questions — what is chosen, who chose it last,
// how often it changed, what came that it does not handle.
//
//   state  { chosen: an animal's id | null, lastChangedBy: a member's id | null,
//            changes: n, recentUnknown: [{ kind, from }] — the last few }
//
//   Choose { animal }    chosen := that animal, unless it is chosen already; Chosen to
//                        every member. The same again is nothing — so a member that shows
//                        what it hears and tells what it shows does not echo for ever
//   CurrentRequested     Chosen to the member that asked, alone — when one is chosen; with
//                        none, nothing: each widget keeps its own until one is
//   anything else        kept in recentUnknown, nothing done: Chosen is the party's own
//                        word, never a member's
//
// It keeps no list of the animals: an id it does not know is a widget's to
// pass over. Pure: no DOM, no clock, no console; the state handed in is never
// changed.
// =============================================================================

var AnimalChoiceSecretary = {

    initial: { chosen: null, lastChangedBy: null, changes: 0, recentUnknown: [] },

    /** How many unknown messages are kept. */
    UNKNOWN_KEPT: 5,

    behavior: function (state, envelope) {
        var m = envelope.message;
        switch (m.kind) {

            case "Choose": {
                if (m.animal === state.chosen) return { newState: state, actions: [] };
                return { newState: { chosen: m.animal, lastChangedBy: envelope.from, changes: state.changes + 1, recentUnknown: state.recentUnknown },
                         actions: [{ kind: "BroadcastToMembers", message: { kind: "Chosen", animal: m.animal } }] };
            }

            case "CurrentRequested":
                if (state.chosen === null) return { newState: state, actions: [] };
                return { newState: state, actions: [{ kind: "SendToMember", to: envelope.from, message: { kind: "Chosen", animal: state.chosen } }] };

            default: {
                var unknown = state.recentUnknown.concat([{ kind: m.kind, from: envelope.from }]).slice(-AnimalChoiceSecretary.UNKNOWN_KEPT);
                return { newState: { chosen: state.chosen, lastChangedBy: state.lastChangedBy, changes: state.changes, recentUnknown: unknown }, actions: [] };
            }
        }
    }
};
