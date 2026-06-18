// =============================================================================
// AnimalsSecretaryModule — the Secretary for AnimalsPlayground's AnimalsParty.
//
// Per the Diligent Secretaries doctrine: pure (state, envelope) -> Step
// behavior plus initial state, both exposed as members of a single
// top-level object for clean framework import + isolated test harness load.
//
// Diligence enrichment in state:
//   - selectedAnimal : current value ('-1' = Random default, else animal id)
//   - lastChangedBy  : provenance of the most recent state change (envelope.from)
//   - recentUnknown  : last 10 unrecognised messages (kind + origin), bounded
//
// Side-effect-free; no DOM, no console, no captures. Per the
// Lambdas-Are-Functional-Object-Shortcuts doctrine, this entire module
// is a Functional Object simplification — pure behavior + immutable state shape.
// =============================================================================

var AnimalsSecretary = {

    initial: {
        selectedAnimal: "-1",
        lastChangedBy : null,
        recentUnknown : []
    },

    /**
     * Pure routing function. (state, envelope) -> { newState, actions }.
     * Same inputs always produce same outputs; no side effects.
     */
    behavior: function (state, envelope) {
        var msg = envelope.message;

        switch (msg.kind) {

            case "AnimalSelectionRequested": {
                return {
                    newState: {
                        selectedAnimal: msg.animal,
                        lastChangedBy : envelope.from,
                        recentUnknown : state.recentUnknown
                    },
                    actions: [{
                        kind   : "BroadcastToMembers",
                        message: { kind: "AnimalChanged", animal: msg.animal }
                    }]
                };
            }

            case "GameActionRequested": {
                // RFC 0028 broadcast feature — a live game event from the
                // MovingAnimalPlayWidget (or a directed snapshot reply). When
                // `to` is present, deliver privately to that one member (the
                // late-join snapshot path); otherwise broadcast to all members.
                // The play source has no GameAction reactor, so it harmlessly
                // ignores the echo of its own broadcast. State is untouched —
                // the Secretary routes game traffic, it does not store it
                // (history would be unbounded; the snapshot path covers
                // late-join instead).
                if (msg.to) {
                    return {
                        newState: state,
                        actions: [{
                            kind   : "SendToMember",
                            to     : msg.to,
                            message: { kind: "GameAction", event: msg.event }
                        }]
                    };
                }
                return {
                    newState: state,
                    actions: [{
                        kind   : "BroadcastToMembers",
                        message: { kind: "GameAction", event: msg.event }
                    }]
                };
            }

            case "CurrentGameRequested": {
                // Late-join sync for replay widgets. Fan the request out to
                // members; the live play widget answers with a directed
                // GameActionRequested(to: asker, event: Snapshot). Replays and
                // the asker itself have no GameSnapshotRequested reactor and
                // ignore it. Carries the asker's id so the play side can reply
                // privately.
                return {
                    newState: state,
                    actions: [{
                        kind   : "BroadcastToMembers",
                        message: { kind: "GameSnapshotRequested", asker: envelope.from }
                    }]
                };
            }

            case "CurrentAnimalRequested": {
                // Late-join sync. If a concrete animal has been chosen, reply
                // privately to the asker reusing the AnimalChanged fact shape —
                // the widget's existing reactor handles both paths uniformly.
                // If still at the '-1' Random default, no useful answer; the
                // widget keeps its own per-instance default.
                if (state.selectedAnimal !== "-1") {
                    return {
                        newState: state,
                        actions: [{
                            kind   : "SendToMember",
                            to     : envelope.from,
                            message: { kind: "AnimalChanged", animal: state.selectedAnimal }
                        }]
                    };
                }
                return { newState: state, actions: [] };
            }

            default: {
                // Diligent defensive observability: track unknowns in state so
                // they surface via inspect() rather than silently disappearing.
                // Bounded at 10 entries via .slice(-10) so state cannot grow.
                var recent = state.recentUnknown.concat([{
                    kind: msg.kind,
                    from: envelope.from
                }]).slice(-10);
                return {
                    newState: {
                        selectedAnimal: state.selectedAnimal,
                        lastChangedBy : state.lastChangedBy,
                        recentUnknown : recent
                    },
                    actions: []
                };
            }
        }
    }
};
