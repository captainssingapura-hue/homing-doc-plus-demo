// =============================================================================
// ComponentLogSecretary — the secretary of a component-log party: lines of
// words, the last few kept so a log that opens late shows them, each told as
// it is written; all of them gone on the command of the widget that writes
// them. Diligent (Diligent Secretaries): its state answers an operator's
// questions - the lines kept, how many were ever written, how often it was
// cleared, what came that it does not handle. Pure: no DOM, no clock; the
// state handed in is never changed.
//
//   state  { lines: [{ seq, words }], seq: n, clears: n, recentUnknown: [{ kind, from }] }
//
//   Note { words }       a line: seq + 1, kept - the last KEPT of them - and Noted to every member
//   ClearLog             the lines gone, and Cleared to every member; none kept, and it is nothing
//   HistoryRequested     History to the member that asked, alone: the lines kept, the oldest first
//   anything else        kept in recentUnknown, nothing done: Noted, Cleared and History are
//                        the party's own words, never a member's
// =============================================================================

var ComponentLogSecretary = {

    initial: { lines: [], seq: 0, clears: 0, recentUnknown: [] },

    /** How many lines are kept. */
    KEPT: 50,

    /** How many unknown messages are kept. */
    UNKNOWN_KEPT: 5,

    behavior: function (state, envelope) {
        var m = envelope.message, S = ComponentLogSecretary;
        switch (m.kind) {

            case "Note": {
                var line = { seq: state.seq + 1, words: m.words };
                return { newState: { lines: state.lines.concat([line]).slice(-S.KEPT), seq: line.seq, clears: state.clears, recentUnknown: state.recentUnknown },
                         actions: [{ kind: "BroadcastToMembers", message: { kind: "Noted", seq: line.seq, words: line.words } }] };
            }

            case "ClearLog":
                if (!state.lines.length) return { newState: state, actions: [] };
                return { newState: { lines: [], seq: state.seq, clears: state.clears + 1, recentUnknown: state.recentUnknown },
                         actions: [{ kind: "BroadcastToMembers", message: { kind: "Cleared" } }] };

            case "HistoryRequested":
                return { newState: state, actions: [{ kind: "SendToMember", to: envelope.from, message: { kind: "History", lines: state.lines } }] };

            default: {
                var unknown = state.recentUnknown.concat([{ kind: m.kind, from: envelope.from }]).slice(-S.UNKNOWN_KEPT);
                return { newState: { lines: state.lines, seq: state.seq, clears: state.clears, recentUnknown: unknown }, actions: [] };
            }
        }
    }
};
