// =============================================================================
// AddTabDemo — the house's add-tab control in action: put a new tab somewhere,
// saying where - a picture of the panes, pointed at - what - a list of the
// kinds - and how it arrives - quietly, in front, or in front with the keys -
// then one button. It asks whether a pane will take another, and shows the
// answer; it never decides for you. Each tab added is said, with where it
// landed. The panes are a room of its own, two regions of a dock. It is
// controlled by nothing more than itself.
//
//   new AddTabDemo(container, { leaf })   leaf: "add-tab"
//   (the rest is a ComponentDemo's)
// =============================================================================

class AddTabDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "add-tab-demo", params);
        var self = this;
        var control = this.el("control", "div", dm_row, this.stage);
        var room = this.el("room", "div", dm_host, this.stage);
        this._docks = new DemoDocks(this.branch.createBranch("docks"), { host: room });
        this._docks.source.addTo(this._docks.dock("left"), "note", "quiet");
        this._add = new AddTab(this.branch.createBranch("add"), {
            host: control, source: this._docks.source, modes: true,
            panes: function () { return self._docks.docks(); },
            onAdded: function (pane, tab, index) {
                var r = self._docks.grid.regionOf(pane);
                self.say((tab.title ? tab.title() : "a tab") + " added to the " + (r ? r.id : "pane") + " region, at " + index);
            }
        });
        this.intro = "point at a pane in the small picture, pick what and how, and add it";
    }

    disposed() {
        try { this._add.dispose(); } catch (e) {}
        try { this._docks.dispose(); } catch (e) {}
    }
}
