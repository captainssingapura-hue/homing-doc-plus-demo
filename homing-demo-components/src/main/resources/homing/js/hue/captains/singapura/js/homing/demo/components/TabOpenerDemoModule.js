// =============================================================================
// TabOpenerDemo — the house's tab opener in action: a chooser in a tab of its
// own, in a room of two regions, offering what the tab can hold; a choice
// makes the tab what was chosen - the same chip, in the same place. Its
// options: open - another chooser, in front in the right region; close - the
// tab in front of the right region, else of the left, closed.
//
//   new TabOpenerDemo(container, { leaf })   leaf: "tab-opener"
//   (the rest is a ComponentDemo's)
// =============================================================================

class TabOpenerDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "tab-opener-demo", params);
        var self = this;
        var room = this.el("room", "div", dm_host, this.stage);
        this._docks = new DemoDocks(this.branch.createBranch("docks"), {
            host: room,
            kinds: [{ id: "opener", label: "Open…", title: "Open", listed: false,
                      make: function (b, q) { return new TabOpener(b, { focus: q.focus, tab: q.tab, source: self._docks.source }); } }],
            onBecame: function (tp, kindId) { self.say("the chooser's tab became a " + kindId + " - the same chip, in the same place"); }
        });
        this._docks.source.addTo(this._docks.dock("left"), "note", "quiet");
        this._docks.source.addTo(this._docks.dock("left"), "opener", "front");
        this.intro = "a chooser in the left region: pick what its tab should hold; open another, or close one, from the controls";
    }

    /** open: another chooser, in front in the right region. */
    open() {
        var at = this._docks.source.addTo(this._docks.dock("right"), "opener", "front");
        this.say(at < 0 ? "no room for another tab" : "a chooser opened in the right region");
    }

    /** close: the tab in front of the right region, else of the left. */
    close() {
        var docks = [this._docks.dock("right"), this._docks.dock("left")];
        for (var i = 0; i < docks.length; i++) {
            var id = docks[i] ? docks[i].activeTab() : null;
            if (id) { docks[i].removeTab(id); this.say("closed: the tab in front in the " + (i ? "left" : "right") + " region"); return; }
        }
        this.say("nothing is open");
    }

    disposed() { try { this._docks.dispose(); } catch (e) {} }
}
