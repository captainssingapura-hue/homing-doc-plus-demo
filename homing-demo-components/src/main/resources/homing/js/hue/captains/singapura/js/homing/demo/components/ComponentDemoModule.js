// =============================================================================
// ComponentDemo — what every demo is to the panel that holds it, said once; a
// demo extends it, builds its component on `stage`, and answers the options
// its leaf is controlled by - each by the method the option names
// (Controls.forLeaf): size(v), extent(v), setOn(on), openModal(), ask()…
//
// A self-contained widget: made with the container its host lends it and its
// params - { leaf }, the leaf it shows - and nothing else; its DomOps and
// focus parties its own, offered as roots for its host to graft. It joins its
// parties after it is made, top-down, as the host hands them on:
//
//   component-control    what is set and done: a degree or a switch said is
//                        applied - and, joining, it asks what is set, and puts
//                        every degree and switch it answers where the party has
//                        it, or at rest; an action done, or a question asked -
//                        its answer said
//   component-demo-log   what it did, said: say(words). What it says once it
//                        can be heard is its `intro`
//
// Not joined, it stands alone: nothing set, nothing said. A setting it is told
// is applied quietly - what it did is said, never what it was told.
//
// THE KEYS. A logical member of the keyboard's party; what takes keys natively
// inside it - a button - is the native world's. A press anywhere in it claims
// the keys for it; they go to key(ev), the demo's, and Escape that it does not
// take gives them back.
//
//   demo.root  demo.roots { dom, focus }  demo.focus  demo.branch  demo.stage
//   demo.leaf  demo.options  demo.intro
//   demo.el(name, tag, cls, into?, text?)   an element on its own branch
//   demo.say(words)  demo.join(given)  demo.leave()  demo.activate()  demo.keyDown(ev)  demo.dispose()
//   overridden:  key(ev) → true when taken;  disposed()
// =============================================================================

var _componentDemos = 0;

class ComponentDemo {
    constructor(container, name, params) {
        if (!container || typeof container.appendChild !== "function") throw new Error("[" + name + "] a container is required: the one its host lends it");
        var p = params || {};
        if (!p.leaf) throw new Error("[" + name + "] the leaf it shows is required: params.leaf");
        var n = name + "-" + (++_componentDemos);
        this._name = name;
        this.leaf = p.leaf;
        this.options = Controls.forLeaf(p.leaf);
        this.intro = null;
        this._dom = domOpsParties.mobile(n);
        this._dom.activate(Object.freeze({ toString: function () { return name; } }));
        var root = this._dom.createElement("root", "div");
        css.addClass(root, wg_fill);
        root.setAttribute("role", "region");
        root.setAttribute("aria-label", "In action: " + p.leaf);
        var stage = this._dom.createElement("stage", "div");
        css.addClass(stage, dm_stage);
        root.appendChild(stage);
        this.root = root;
        this.stage = stage;
        this._focusParty = focusParties.mobile(n);
        this.focus = this._focusParty.root.join(name, this);
        this._off = Keys.claimOn(root, this.focus);
        this.roots = Object.freeze({ dom: this._dom, focus: this._focusParty });
        this._control = null;
        this._log = null;
        this.alive = true;
        container.appendChild(root);
    }

    get branch() { return this._dom; }

    el(name, tag, cls, into, text) {
        var e = this._dom.createElement(name, tag);
        if (cls) css.addClass(e, cls);
        if (text != null) e.textContent = text;
        if (into) into.appendChild(e);
        return e;
    }

    /** What it did, said to the demo log - when it has joined one. */
    say(words) { if (this._log) this._log.tell({ kind: "Note", words: String(words) }); }

    join(given) {
        if (this._control || this._log) throw new Error("[" + this._name + "] joined already: leave first");
        var self = this, g = given || {};
        var log = g[COMPONENT_DEMO_LOG.name], control = g[COMPONENT_CONTROL.name];
        if (log) this._log = log.join(this._name, {});
        if (control) {
            this._control = control.join(this._name, {
                DegreeSet: function (m) { self._apply(m.option, m.value); },
                SwitchSet: function (m) { self._apply(m.option, m.on); },
                Invoked: function (m) { self._apply(m.option); },
                State: function (m) { self._settle(m); }
            });
            this._control.tell({ kind: "StateRequested" });
        }
        if (this.intro) this.say(this.intro);
    }

    leave() {
        if (this._control) { this._control.leave(); this._control = null; }
        if (this._log) { this._log.leave(); this._log = null; }
    }

    /** An option it is controlled by, applied by the method the option names; a question's answer said. */
    _apply(option, value) {
        if (this.options.indexOf(option) < 0) return;
        var answer = Controls.apply(this, option, value);
        if (Controls.option(option).means === "question") this.say(answer == null ? "it has nothing to say" : answer);
    }

    /** Everything set, put where the party has it: every degree and switch it answers, set or at rest. */
    _settle(m) {
        var self = this, set = {};
        (m.degrees || []).forEach(function (d) { set[d.option] = d.value; });
        (m.switches || []).forEach(function (s) { set[s.option] = s.on; });
        this.options.forEach(function (n) {
            var o = Controls.option(n);
            if (o.means === "extent" || o.means === "switch") Controls.apply(self, n, Object.prototype.hasOwnProperty.call(set, n) ? set[n] : o.rest);
        });
    }

    /** A keydown handed on: the demo's to take; true when taken. */
    key(ev) { return false; }

    /** Called as it is disposed, before its branch goes. */
    disposed() {}

    activate() { Keys.claim(this.focus); }

    granted(by) {}

    keyDown(ev) {
        if (this.key(ev)) return true;
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        return false;
    }

    dispose() {
        if (!this.alive) return;
        this.alive = false;
        this.leave();
        this.disposed();
        if (this._off) { this._off(); this._off = null; }
        this._focusParty.dissolve();
        this._dom.dissolve();
    }
}
