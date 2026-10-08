// =============================================================================
// DemoDocks — a room of the tab controls' own, for the demos that open tabs:
// a desk, a dock grid of two regions on it - left and right - and a source of
// tabs on the desk, offering a note and a memo, and whatever kinds the demo
// adds. The docks' focus branches hang under the desk's own, named for this
// room alone - never at the page's root, where the workspace's docks are named
// as these would be.
//
//   new DemoDocks(branch, { host, kinds?, onBecame? })
//   room.desk  room.grid  room.source  room.docks()  room.dock(id)  room.dispose()
// =============================================================================

const _demoDocks = Object.freeze({ toString: () => "demoDocks" });
var _demoDesks = 0;

/** A tab's widget by the pane's law: a member of the branch it is handed, answering activate(), its Escape yielding. */
class _DemoNote {
    constructor(branch, params) {
        branch.activate(_demoDocks);
        this.root = branch.createElement("note", "p");
        css.addClass(this.root, dm_text);
        this.root.textContent = params.text;
        this.focus = params.focus.join(branch.name, this);
        this._off = Keys.claimOn(this.root, this.focus);
    }
    activate() { Keys.claim(this.focus); }
    keyDown(ev) { if (ev.key === "Escape") { Keys.yield(this.focus); return true; } return false; }
    dispose() { if (this._off) this._off(); if (this.focus && this.focus.in) this.focus.leave(); }
}

class DemoDocks {
    constructor(branch, opts) {
        var o = opts || {};
        if (!o.host) throw new Error("[DemoDocks] a host is required: the box the desk fills");
        branch.activate(_demoDocks);
        this.branch = branch;
        this.desk = new Desk(branch.createBranch("desk"), { host: o.host, budget: 6, focusName: "demo-desk-" + (++_demoDesks) });
        this.grid = new DockGrid(branch.createBranch("docks"), { host: o.host, desk: this.desk, dock: { focus: this.desk.focus },
            layout: { kind: "split", orientation: "horizontal", children: [
                { node: { kind: "cell", id: "left" }, ratio: 1 }, { node: { kind: "cell", id: "right" }, ratio: 1 }] } });
        var kinds = [
            { id: "note", label: "A note", title: "Note", make: function (b, p) { return new _DemoNote(b, { focus: p.focus, text: "A note: the budget is due on Friday." }); } },
            { id: "memo", label: "A memo", title: "Memo", make: function (b, p) { return new _DemoNote(b, { focus: p.focus, text: "A memo: the spring launch moves a week." }); } }
        ].concat(o.kinds || []);
        this.source = new TabSource(branch.createBranch("source"), { desk: this.desk, kinds: kinds, onBecame: o.onBecame });
    }

    docks() { return this.grid.regions().map(function (r) { return r.dock; }); }

    dock(id) { var r = this.grid.region(id); return r ? r.dock : null; }

    dispose() {
        try { this.grid.dispose(); } catch (e) {}
        try { this.desk.dispose(); } catch (e) {}
        try { this.branch.dissolve(); } catch (e) {}
    }
}
