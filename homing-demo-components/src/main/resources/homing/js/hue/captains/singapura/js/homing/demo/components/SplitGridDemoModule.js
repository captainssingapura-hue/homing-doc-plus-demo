// =============================================================================
// SplitGridDemo — the house's split grid in action: rooms in rows and columns,
// sharing their space by ratio, the lines between them dragged to re-share,
// drawn as one lattice. What each room holds is its owner's and stays put
// through every change. Its options: split - the first room split, its new
// half to its right; remove - the last room taken away, its room given to the
// one beside it, and the last of all refused; even-out - the outer rooms
// shared evenly; reset-layout - the rooms as they began. Every re-share,
// split and removal is said.
//
//   new SplitGridDemo(container, { leaf })   leaf: "split-grid"
//   (the rest is a ComponentDemo's)
// =============================================================================

var _SPLIT_GRID_START = Object.freeze({ kind: "split", orientation: "horizontal", children: [
    { node: { kind: "cell", id: "a" } },
    { node: { kind: "split", orientation: "vertical", children: [{ node: { kind: "cell", id: "b" } }, { node: { kind: "cell", id: "c" } }] } }] });

class SplitGridDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "split-grid-demo", params);
        this._host = this.el("host", "div", dm_host, this.stage);
        this._grid = null;
        this._built = 0;
        this._build();
        this.intro = "drag a line between rooms, or split, remove, even out and reset the rooms from the controls - the last cannot go";
    }

    _build() {
        var self = this, n = ++this._built, b = this.branch.createBranch("grid-" + n);
        var label = function (id) { self.el("room-" + n + "-" + id, "p", dm_text, self._grid.cell(id), "Room " + id); };
        var shares = function (rs) { return rs.map(function (r) { return Math.round(r * 100) + "%"; }).join(" : "); };
        this._grid = new SplitGrid(b, {
            host: this._host, seam: true, layout: JSON.parse(JSON.stringify(_SPLIT_GRID_START)),
            onEvent: function (ev) {
                if (ev.kind === "TracksChanged") self.say("re-shared: " + shares(ev.ratios));
                else if (ev.kind === "Subdivided") { label(ev.newCellId); self.say("room " + ev.cellId + " split: room " + ev.newCellId + " on its " + ev.side); }
                else if (ev.kind === "Removed") self.say("room " + ev.cellId + " removed; its room went " + (ev.toward ? "to room " + ev.toward : "to the one beside it"));
            }
        });
        this._grid.cells().forEach(label);
    }

    split() { this._grid.subdivide(this._grid.cells()[0], "right"); }

    remove() {
        var cells = this._grid.cells();
        try { this._grid.remove(cells[cells.length - 1]); }
        catch (e) { this.say("refused: " + e.message); }
    }

    evenOut() {
        var top = this._grid.layout();
        if (top.kind !== "split") { this.say("one room: nothing to share"); return; }
        this._grid.setRatios("", top.children.map(function () { return 1; }));
    }

    resetLayout() {
        this._grid.dispose();
        this._build();
        this.say("the rooms as they began: a, then b over c");
    }

    disposed() { try { this._grid.dispose(); } catch (e) {} }
}
