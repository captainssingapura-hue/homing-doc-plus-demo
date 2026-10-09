// =============================================================================
// ComponentDemoPanel — the picked component in action, and nothing more: a
// thin wrapper. For each pick it finds where the leaf's demo is (HOUSE_DEMOS)
// - a widget of its own - and loads its module the first time any leaf asks for
// it: the page opens with no demo loaded, and a module once loaded is kept. It
// makes the demo in a slot, grafts its DomOps and focus parties under its own,
// and hands it the parties it joined, top-down: the demo joins the
// component-control party and the demo log itself. A new demo, a new log: the
// panel clears the demo log as it mounts one. While a demo's module loads, the
// slot says so; a pick that has moved on by the time it arrives is left
// alone. A leaf with no demo says where the page around shows it, or that
// nothing realizes it; a branch or a part says to pick a leaf.
//
// THE KEYS. Its own, as a member of the keyboard's party; the demo is a member
// of its own beneath it - a press in the demo claims them for the demo. When
// the demo gives them back, Escape gives them on.
//
//   new ComponentDemoPanel(container, params)   params: none
//   (the rest is a TaxonomyWidget's)
// =============================================================================

/** Every demo module asked for so far, by its address: loaded once, the first time a leaf it shows is picked. */
var _demoModules = new Map();

/** A demo's class, from its module - loaded now if no pick has loaded it yet; a load that failed is tried again. */
function _demoClass(entry) {
    var loading = _demoModules.get(entry.module);
    if (!loading) {
        loading = import(entry.module);
        _demoModules.set(entry.module, loading);
        loading.catch(function () { _demoModules.delete(entry.module); });
    }
    return loading.then(function (m) {
        var Demo = m[entry.export];
        if (typeof Demo !== "function") throw new Error("[ComponentDemoPanel] " + entry.module + " exports no class " + entry.export);
        return Demo;
    });
}

class ComponentDemoPanel extends TaxonomyWidget {
    constructor(container, params) {
        super(container, "component-demo-panel", "Component demo");
        var self = this;
        this._demo = null;
        this._given = null;
        this._log = null;
        // where the demo's focus party is grafted: a branch of its own, held by a holder that gives the keys on
        this._holder = {
            keyDown: function (ev) { if (ev.key === "Escape") { Keys.yield(self._place.owner); return true; } return false; },
            activate: function () { if (self._demo) self._demo.activate(); }
        };
        this._place = this.roots.focus.root.createBranch("demo", this._holder);
        this.selected(this.picked());
    }

    /** Its own parties first - the demo log, then the pick, whose answer mounts the demo, handed them all. */
    join(given) {
        var g = given || {};
        this._given = g;
        if (g[COMPONENT_DEMO_LOG.name]) this._log = g[COMPONENT_DEMO_LOG.name].join("component-demo-panel", {});
        super.join(given);
    }

    /** The demo leaves first, then the panel: the mirror of joining. */
    leave() {
        if (this._demo) this._demo.leave();
        if (this._log) { this._log.leave(); this._log = null; }
        super.leave();
        this._given = null;
    }

    selected(id) {
        this._drop();
        var t = this.taxonomy, n = id ? t.node(id) : null, v = this.fresh();
        if (!n || n.is !== "component") {
            this.mint(v, "none", "p", tx_hint, this.body, n
                ? "A " + n.is + " is not seen in action: pick a component - a leaf of the tree."
                : "Pick a component - a leaf of the tree - to see it in action.");
            return;
        }
        this.mint(v, "title", "h3", tx_title, this.body, n.name);
        var entry = HOUSE_DEMOS[id];
        if (!entry) {
            this.mint(v, "none", "p", tx_hint, this.body, HOUSE_AROUND[id]
                ? "Shown in action by the page around this one: " + HOUSE_AROUND[id] + "."
                : HOUSE_UNREALIZED.indexOf(id) >= 0
                ? "No realization yet: nothing implements it - an owner mints it, or nothing does - so what it means is all there is to show."
                : "Realized, and its demo is still to come.");
            return;
        }
        if (this._log) this._log.tell({ kind: "ClearLog" });
        var self = this, slot = this.mint(v, "slot", "div", tx_slot, this.body);
        var waiting = v.createBranch("waiting");
        waiting.activate(this);
        this.mint(waiting, "note", "p", tx_hint, slot, "Loading its demo…");
        _demoClass(entry).then(function (Demo) {
            if (!self.alive || self._view !== v) return;   // picked away meanwhile, or the panel is gone: that pick shows its own
            waiting.dissolve();
            self._mount(v, slot, Demo, id);
        }, function (e) {
            if (!self.alive || self._view !== v) return;
            waiting.dissolve();
            self.mint(v, "failed", "p", tx_hint, slot, "Its demo could not be loaded: " + (e && e.message ? e.message : String(e)));
        });
    }

    /** The demo made in its slot, its parties grafted under the panel's, and handed what the panel joined. */
    _mount(v, slot, Demo, id) {
        this._demo = new Demo(slot, { leaf: id });
        v.graft("demo", this._demo.roots.dom);
        this._place.graft("demo", this._demo.roots.focus);
        if (this._given) this._demo.join(this._given);
    }

    /** The demo gone, with its parties - and their grafts with them. */
    _drop() {
        if (this._demo) { this._demo.dispose(); this._demo = null; }
    }

    disposed() { this._drop(); }
}
