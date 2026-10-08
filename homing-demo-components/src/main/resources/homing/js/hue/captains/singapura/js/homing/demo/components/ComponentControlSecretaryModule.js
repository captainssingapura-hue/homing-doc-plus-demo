// =============================================================================
// ComponentControlSecretary — the secretary of a component-control party:
// what is set of the options of the house's control catalogue, kept BY OPTION,
// not by component - a size set on one card is the size of the next button -
// and what is done, told. Diligent (Diligent Secretaries): its state answers
// an operator's questions - what is set, how often it changed, what it refused
// and what came that it does not handle. Pure: no DOM, no clock; the state
// handed in is never changed. What it knows of the options is the catalogue.
//
//   state  { degrees: { option: v }, switches: { option: on }, changes: n,
//            recentRefused: [{ kind, option, from }], recentUnknown: [{ kind, from }] }
//
//   SetDegree { option, value }   an option set by degree: kept, and DegreeSet to every
//                                 member; the same value again is nothing
//   SetSwitch { option, on }      an option switched: kept, and SwitchSet to every member;
//                                 the same again is nothing
//   Invoke { option }             an option done or asked: Invoked to every member; nothing kept
//   StateRequested                State to the member that asked, alone: everything set - an
//                                 option not in it is at its rest
//   Reset                         everything back to rest: State, empty, to every member;
//                                 nothing set, and it is nothing
//   an option the catalogue lacks, or sent by the wrong means: refused - kept in
//   recentRefused, nothing done. Anything else: kept in recentUnknown, nothing done -
//   State, DegreeSet, SwitchSet and Invoked are the party's own words, never a member's
// =============================================================================

var ComponentControlSecretary = {

    initial: { degrees: {}, switches: {}, changes: 0, recentRefused: [], recentUnknown: [] },

    /** How many refused, and how many unknown, messages are kept. */
    KEPT: 5,

    behavior: function (state, envelope) {
        var m = envelope.message, S = ComponentControlSecretary;
        switch (m.kind) {

            case "SetDegree": {
                if (!S._is(m.option, ["extent"])) return S._refuse(state, envelope);
                if (state.degrees[m.option] === m.value) return { newState: state, actions: [] };
                var degrees = Object.assign({}, state.degrees);
                degrees[m.option] = m.value;
                return { newState: S._with(state, { degrees: degrees, changes: state.changes + 1 }),
                         actions: [{ kind: "BroadcastToMembers", message: { kind: "DegreeSet", option: m.option, value: m.value } }] };
            }

            case "SetSwitch": {
                if (!S._is(m.option, ["switch"])) return S._refuse(state, envelope);
                if (state.switches[m.option] === m.on) return { newState: state, actions: [] };
                var switches = Object.assign({}, state.switches);
                switches[m.option] = m.on;
                return { newState: S._with(state, { switches: switches, changes: state.changes + 1 }),
                         actions: [{ kind: "BroadcastToMembers", message: { kind: "SwitchSet", option: m.option, on: m.on } }] };
            }

            case "Invoke":
                if (!S._is(m.option, ["action", "question"])) return S._refuse(state, envelope);
                return { newState: state, actions: [{ kind: "BroadcastToMembers", message: { kind: "Invoked", option: m.option } }] };

            case "StateRequested":
                return { newState: state, actions: [{ kind: "SendToMember", to: envelope.from, message: S._state(state) }] };

            case "Reset": {
                if (!Object.keys(state.degrees).length && !Object.keys(state.switches).length) return { newState: state, actions: [] };
                var rested = S._with(state, { degrees: {}, switches: {}, changes: state.changes + 1 });
                return { newState: rested, actions: [{ kind: "BroadcastToMembers", message: S._state(rested) }] };
            }

            default: {
                var unknown = state.recentUnknown.concat([{ kind: m.kind, from: envelope.from }]).slice(-S.KEPT);
                return { newState: S._with(state, { recentUnknown: unknown }), actions: [] };
            }
        }
    },

    /** Whether the catalogue has the option, controlled by one of these means. */
    _is: function (option, means) {
        return Object.prototype.hasOwnProperty.call(CONTROL_OPTIONS, option) && means.indexOf(CONTROL_OPTIONS[option].means) >= 0;
    },

    _refuse: function (state, envelope) {
        var m = envelope.message;
        var refused = state.recentRefused.concat([{ kind: m.kind, option: String(m.option), from: envelope.from }]).slice(-ComponentControlSecretary.KEPT);
        return { newState: ComponentControlSecretary._with(state, { recentRefused: refused }), actions: [] };
    },

    _state: function (state) {
        return {
            kind: "State",
            degrees: Object.keys(state.degrees).map(function (o) { return { option: o, value: state.degrees[o] }; }),
            switches: Object.keys(state.switches).map(function (o) { return { option: o, on: state.switches[o] }; })
        };
    },

    _with: function (state, changed) { return Object.assign({}, state, changed); }
};
