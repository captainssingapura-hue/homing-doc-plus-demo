// =============================================================================
// NodeSelectionSecretary — the secretary of a node-selection party: the one
// node of the taxonomy the workbench's widgets are looking at, by its token,
// none at first. Pure: no page, no clock; the state handed in is never changed.
//
//   state  { id: "" | token, revision }
//
//   Select { id }        a different pick: id := it, revision + 1, and Selected to
//                        every member - the one who picked too, so every widget
//                        hears the same story; the same pick again changes
//                        nothing and says nothing
//   CurrentRequested     Selected to the member that asked, alone
//   anything else        nothing done: Selected is the party's own word
// =============================================================================

var NodeSelectionSecretary = {

    initial: { id: "", revision: 0 },

    behavior: function (state, envelope) {
        var m = envelope.message;
        switch (m.kind) {
            case "Select": {
                if (m.id === state.id) return { newState: state, actions: [] };
                var next = { id: m.id, revision: state.revision + 1 };
                return { newState: next, actions: [{ kind: "BroadcastToMembers", message: { kind: "Selected", id: next.id, revision: next.revision } }] };
            }
            case "CurrentRequested":
                return { newState: state, actions: [{ kind: "SendToMember", to: envelope.from, message: { kind: "Selected", id: state.id, revision: state.revision } }] };
            default:
                return { newState: state, actions: [] };
        }
    }
};
