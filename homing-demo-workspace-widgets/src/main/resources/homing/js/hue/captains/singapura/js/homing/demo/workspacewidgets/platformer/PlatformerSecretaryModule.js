// =============================================================================
// PlatformerSecretary — the secretary of a platformer party: one run of the
// game, as the widget playing it tells it. Each event is said on to every
// member as it came, in the order it came: a watcher applies them in that
// order, and the world it makes is the player's. The player hears nothing of
// its own stream — it has no reactor for it — so the echo costs it nothing.
//
// It routes the run and never keeps it: a history would grow without end. A
// watcher that joins mid-run asks for the world as it stands instead, and the
// secretary asks the one that has it — the player — naming who asked; the
// player's answer, a Snapshot, goes to that member alone. A snapshot to no one
// in particular — a run begun, joined or begun again — goes to every member.
// Diligent (Diligent Secretaries): its state answers an operator's questions —
// how many events it has said, how many steps, platforms and snapshots, the
// last kind and who told it, what came that it does not handle.
//
//   state  { events: n, ticks: n, platforms: n, snapshots: n, last: a kind | null,
//            lastFrom: a member's id | null, recentUnknown: [{ kind, from }] — the last few }
//
//   MoveStarted, MoveStopped, Jumped, GravityChanged, PlatformGenerated, Tick
//                        said on to every member, as it came; counted
//   WorldRequested       WorldWanted { asker: the member that asked } to every member:
//                        the player answers it; the rest have no ear for it
//   Snapshot { to, world }   to the member `to` alone; to every member when `to` is blank
//   anything else        kept in recentUnknown, nothing done: WorldWanted is the
//                        party's own word, never a member's
//
// Pure: no DOM, no clock, no console; the state handed in is never changed.
// =============================================================================

var PlatformerSecretary = {

    initial: { events: 0, ticks: 0, platforms: 0, snapshots: 0, last: null, lastFrom: null, recentUnknown: [] },

    /** How many unknown messages are kept. */
    UNKNOWN_KEPT: 5,

    /** The kinds of a run: what the player tells, and the party says on. */
    RUN: Object.freeze(["MoveStarted", "MoveStopped", "Jumped", "GravityChanged", "PlatformGenerated", "Tick"]),

    behavior: function (state, envelope) {
        var m = envelope.message;
        if (m.kind === "WorldRequested") {
            return { newState: PlatformerSecretary._told(state, m, envelope.from),
                     actions: [{ kind: "BroadcastToMembers", message: { kind: "WorldWanted", asker: envelope.from } }] };
        }
        if (m.kind === "Snapshot") {
            var s = PlatformerSecretary._told(state, m, envelope.from);
            s.snapshots = state.snapshots + 1;
            return { newState: s, actions: [m.to ? { kind: "SendToMember", to: m.to, message: m } : { kind: "BroadcastToMembers", message: m }] };
        }
        if (PlatformerSecretary.RUN.indexOf(m.kind) < 0) {
            var unknown = state.recentUnknown.concat([{ kind: m.kind, from: envelope.from }]).slice(-PlatformerSecretary.UNKNOWN_KEPT);
            return { newState: Object.assign({}, state, { recentUnknown: unknown }), actions: [] };
        }
        var run = PlatformerSecretary._told(state, m, envelope.from);
        run.events = state.events + 1;
        run.ticks = state.ticks + (m.kind === "Tick" ? 1 : 0);
        run.platforms = state.platforms + (m.kind === "PlatformGenerated" ? 1 : 0);
        return { newState: run, actions: [{ kind: "BroadcastToMembers", message: m }] };
    },

    /** The state after something is told: the last kind, and who told it. */
    _told: function (state, m, from) {
        return Object.assign({}, state, { last: m.kind, lastFrom: from });
    }
};
