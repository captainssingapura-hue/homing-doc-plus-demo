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
