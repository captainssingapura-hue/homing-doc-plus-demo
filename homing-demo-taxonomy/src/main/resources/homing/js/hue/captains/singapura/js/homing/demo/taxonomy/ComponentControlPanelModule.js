// =============================================================================
// ComponentControlPanel — what the picked component is controlled by. For each
// pick: controlType = its type (Controls.typeOf), and its options - a degree
// for each axis it varies along, then its type's (Controls.forLeaf) - mounted
// as a ControlPanel, which makes each control as the option's means calls for.
// What is set goes to the component-control party, which keeps it by option -
// a size set on one component is the size of the next - and the panel shows
// what the party says is set, from whoever set it. Back to rest puts
// everything there. What was set, done or asked, is logged in the control
// log; a new control type, a new log - the panel clears it.
//
// THE KEYS. Its own, as a member of the keyboard's party: handed on to the
// sliders; Escape that they do not take gives them back.
//
//   new ComponentControlPanel(container, params)   params: none
//   (the rest is a TaxonomyWidget's)
// =============================================================================

class ComponentControlPanel extends TaxonomyWidget {
    constructor(container, params) {
        super(container, "component-control-panel", "Control panel");
        this._panel = null;
        this._control = null;
        this._log = null;
        this._type = null;
        this.selected(this.picked());
    }

    /** Its own parties first - the control party and its log - then the pick, whose answer mounts the panel. */
    join(given) {
        var self = this, g = given || {};
        if (g[COMPONENT_CONTROL_LOG.name]) this._log = g[COMPONENT_CONTROL_LOG.name].join("component-control-panel", {});
        if (g[COMPONENT_CONTROL.name]) {
            this._control = g[COMPONENT_CONTROL.name].join("component-control-panel", {
                DegreeSet: function (m) { if (self._panel) self._panel.set(m.option, m.value); },
                SwitchSet: function (m) { if (self._panel) self._panel.set(m.option, m.on); },
                State: function (m) { self._settle(m); }
            });
        }
        super.join(given);
    }

    leave() {
        if (this._control) { this._control.leave(); this._control = null; }
        if (this._log) { this._log.leave(); this._log = null; }
        super.leave();
    }

    selected(id) {
        this._drop();
        var self = this, t = this.taxonomy, n = id ? t.node(id) : null, v = this.fresh();
        if (!n || n.is !== "component") {
            this.mint(v, "none", "p", tx_hint, this.body, "Pick a component - a leaf of the tree - to see what it is controlled by.");
            return;
        }
        var type = Controls.typeOf(id);
        this.mint(v, "title", "h3", tx_title, this.body, n.name);
        var line = this.mint(v, "type", "div", tx_line, this.body);
        this.mint(v, "type-key", "span", tx_tag, line, "control type");
        this.mint(v, "type-name", "span", tx_code, line, type);
        if (this._log && this._type !== null && type !== this._type) this._log.tell({ kind: "ClearLog" });
        this._type = type;
        var scroll = this.mint(v, "controls", "div", tx_scroll, this.body);
        this._panel = new ControlPanel(v.createBranch("panel"), {
            options: Controls.forLeaf(id),
            nothing: "Nothing to control: it varies by no degree, and its type, " + type + ", takes no option.",
            onSet: function (option, value, settled) { self._set(option, value, settled); },
            onInvoke: function (option) { self._invoke(option); }
        });
        scroll.appendChild(this._panel.root);
        var rest = new ButtonBuilder().label("Back to rest").plain().size(-1).onClick(function () {
            if (self._control) self._control.tell({ kind: "Reset" });
            self._note("everything back to rest");
        });
        line.appendChild(rest.build(v.createElement("rest", rest.tag)).el);
        if (this._control) this._control.tell({ kind: "StateRequested" });
    }

    /** A degree moved, or a switch switched: told to the party; logged when it settles. */
    _set(option, value, settled) {
        var o = Controls.option(option);
        if (o.means === "extent") {
            if (this._control) this._control.tell({ kind: "SetDegree", option: option, value: value });
            if (settled) this._note(o.label.toLowerCase() + " set to " + value.toFixed(2));
            return;
        }
        if (this._control) this._control.tell({ kind: "SetSwitch", option: option, on: !!value });
        this._note(o.label.toLowerCase() + (value ? " switched on" : " switched off"));
    }

    /** An action done, or a question asked: told to the party, and logged. */
    _invoke(option) {
        var o = Controls.option(option);
        if (this._control) this._control.tell({ kind: "Invoke", option: option });
        this._note((o.means === "question" ? "asked: " : "done: ") + o.label);
    }

    /** Everything set, shown: every degree and switch the panel has, where the party has it, or at rest. */
    _settle(m) {
        if (!this._panel) return;
        var self = this, set = {};
        (m.degrees || []).forEach(function (d) { set[d.option] = d.value; });
        (m.switches || []).forEach(function (s) { set[s.option] = s.on; });
        Controls.forLeaf(this.picked()).forEach(function (n) {
            var o = Controls.option(n);
            if (o.means === "extent" || o.means === "switch") self._panel.set(n, Object.prototype.hasOwnProperty.call(set, n) ? set[n] : o.rest);
        });
    }

    _note(words) { if (this._log) this._log.tell({ kind: "Note", words: words }); }

    _drop() { if (this._panel) { this._panel.dispose(); this._panel = null; } }

    keyDown(ev) {
        if (this._panel && this._panel.key(ev)) return true;
        return super.keyDown(ev);
    }

    disposed() { this._drop(); }
}
