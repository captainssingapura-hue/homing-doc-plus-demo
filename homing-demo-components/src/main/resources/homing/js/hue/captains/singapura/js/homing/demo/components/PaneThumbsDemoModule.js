// =============================================================================
// PaneThumbsDemo — the house's pane thumbnails in action: the regions of a
// room pictured small, each chosen by pointing at it - the right one shown and
// refused. Its options arrange the room, and the picture follows: split - the
// left region split, its new one below; remove - the last region made merged
// into the region its room goes to, the last of all kept; reset-layout - the
// room as it began. Its regions share their room by the lines between them,
// dragged: evened out by call, it says so, and leaves them.
//
//   new PaneThumbsDemo(container, { leaf })   leaf: "pane-thumbs"
//   (the rest is a ComponentDemo's)
// =============================================================================

class PaneThumbsDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "pane-thumbs-demo", params);
        this._row = this.el("row", "div", dm_row, this.stage);
        this._room = this.el("room", "div", dm_host, this.stage);
        this._docks = null;
        this._thumbs = null;
        this._built = 0;
        this._build();
        this.intro = "point at a region in the small picture; the right one is shown and cannot be chosen; split, remove and reset the regions from the controls";
    }

    _build() {
        var self = this, n = ++this._built;
        this._docks = new DemoDocks(this.branch.createBranch("docks-" + n), { host: this._room });
        this._docks.source.addTo(this._docks.dock("left"), "note", "quiet");
        this._docks.source.addTo(this._docks.dock("left"), "memo", "quiet");
        this._docks.source.addTo(this._docks.dock("right"), "note", "quiet");
        var regionOf = function (pane) { var r = self._docks.grid.regionOf(pane); return r ? r.id : pane.slotId; };
        this._thumbs = new PaneThumbs(this.branch.createBranch("thumbs-" + n), {
            host: this._row, width: "140px",
            panes: function () { return self._docks.docks(); },
            labelOf: regionOf,
            enabledOf: function (pane) { return regionOf(pane) !== "right"; },
            onPick: function (pane) { self.say("the " + regionOf(pane) + " region picked, by pointing at its picture - " + pane.count() + (pane.count() === 1 ? " tab" : " tabs") + " in it"); }
        });
    }

    split() {
        this._docks.grid.part("left", "bottom");
        this._thumbs.refresh();
        this.say("the left region split: the picture takes the new one in");
    }

    remove() {
        var regions = this._docks.grid.regions();
        if (regions.length < 2) { this.say("the last region stays"); return; }
        var last = regions[regions.length - 1].id, plan = this._docks.grid.close(last);
        this._thumbs.refresh();
        this.say(plan && plan.ok === false ? "the " + last + " region kept: " + (plan.reason || "it could not go") : "the " + last + " region merged away: the picture follows");
    }

    evenOut() { this.say("its regions share their room by the lines between them: drag one to re-share"); }

    resetLayout() {
        try { this._thumbs.dispose(); } catch (e) {}
        try { this._docks.dispose(); } catch (e) {}
        this._build();
        this.say("the room as it began: two regions, the picture with them");
    }

    disposed() {
        try { this._thumbs.dispose(); } catch (e) {}
        try { this._docks.dispose(); } catch (e) {}
    }
}
