// =============================================================================
// PlatformerSecretary — the secretary of a platformer party: one run of the
// game, as the widget playing it tells it. Each event is said on to every
// member as it came, in the order it came: a watcher applies them in that
// order, and the world it makes is the player's. The player hears nothing of
// its own stream — it has no reactor for it — so the echo costs it nothing.
//
// It routes the run and never keeps it: a history would grow without end, and
// a watcher that joins late is to be handed the world as it stands instead.
// Diligent (Diligent Secretaries): its state answers an operator's questions —
// how many events it has said, how many frames, how many platforms, the last
// kind and who told it, what came that it does not handle.
//
//   state  { events: n, ticks: n, platforms: n, last: a kind | null, lastFrom: a member's id | null,
//            recentUnknown: [{ kind, from }] — the last few }
//
//   MoveStarted, MoveStopped, Jumped, GravityChanged, PlatformGenerated, Tick
//                        said on to every member, as it came; counted
//   anything else        kept in recentUnknown, nothing done
//
// Pure: no DOM, no clock, no console; the state handed in is never changed.
// =============================================================================

var PlatformerSecretary = {

    initial: { events: 0, ticks: 0, platforms: 0, last: null, lastFrom: null, recentUnknown: [] },

    /** How many unknown messages are kept. */
    UNKNOWN_KEPT: 5,

    /** The kinds of a run: what the player tells, and the party says on. */
    RUN: Object.freeze(["MoveStarted", "MoveStopped", "Jumped", "GravityChanged", "PlatformGenerated", "Tick"]),

    behavior: function (state, envelope) {
        var m = envelope.message;
        if (PlatformerSecretary.RUN.indexOf(m.kind) < 0) {
            var unknown = state.recentUnknown.concat([{ kind: m.kind, from: envelope.from }]).slice(-PlatformerSecretary.UNKNOWN_KEPT);
            return { newState: Object.assign({}, state, { recentUnknown: unknown }), actions: [] };
        }
        return {
            newState: {
                events: state.events + 1,
                ticks: state.ticks + (m.kind === "Tick" ? 1 : 0),
                platforms: state.platforms + (m.kind === "PlatformGenerated" ? 1 : 0),
                last: m.kind,
                lastFrom: envelope.from,
                recentUnknown: state.recentUnknown
            },
            actions: [{ kind: "BroadcastToMembers", message: m }]
        };
    }
};
