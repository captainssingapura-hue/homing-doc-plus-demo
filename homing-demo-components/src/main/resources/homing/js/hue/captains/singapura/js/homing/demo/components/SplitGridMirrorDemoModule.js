// =============================================================================
// SplitGridMirrorDemo — the house's split-grid mirror in action: a grid drawn
// small, its rooms where the grid has them, a cursor on one - moved by a press
// on the drawing, or by the arrows while the demo holds the keys. Its options
// arrange the grid at the cursor: split - the cursor's room split, its new
// half to its right; remove - the cursor's room taken away, the last refused;
// even-out - the outer rooms shared evenly; reset-layout - the rooms as they
// began. Every change is reflected in the drawing, and the cursor's moves said.
//
//   new SplitGridMirrorDemo(container, { leaf })   leaf: "split-grid-mirror"
//   (the rest is a ComponentDemo's)
// =============================================================================

var _MIRRORED_START = Object.freeze({ kind: "split", orientation: "horizontal", children: [
    { node: { kind: "cell", id: "a" } },
    { node: { kind: "split", orientation: "vertical", children: [{ node: { kind: "cell", id: "b" } }, { node: { kind: "cell", id: "c" } }] } }] });

class SplitGridMirrorDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "split-grid-mirror-demo", params);
        var self = this;
        var row = this.el("row", "div", dm_row, this.stage);
        var mirrorBox = this.el("mirror-box", "div", null, row);
        this._host = this.el("host", "div", dm_host, this.stage);
        this._mirror = new SplitGridMirror(this.branch.createBranch("mirror"), {
            host: mirrorBox, scale: 0.35,
            onEvent: function (ev) { if (ev.kind === "CursorMoved") self.say("the cursor at room " + ev.cellId + " - by " + ev.by); }
        });
        this._grid = null;
        this._built = 0;
        this._build();
        this.intro = "press a room in the small drawing, or press in the demo and move the cursor with the arrows; arrange the rooms at the cursor from the controls";
    }

    _build() {
        var self = this, n = ++this._built;
        this._grid = new SplitGrid(this.branch.createBranch("grid-" + n), {
            host: this._host, seam: true, layout: JSON.parse(JSON.stringify(_MIRRORED_START)),
            onEvent: function (ev) {
                if (ev.kind === "Subdivided" || ev.kind === "Removed" || ev.kind === "TracksChanged") self._reflect();
                if (ev.kind === "Subdivided") self.el("room-" + n + "-" + ev.newCellId, "p", dm_text, self._grid.cell(ev.newCellId), "Room " + ev.newCellId);
            }
        });
        this._grid.cells().forEach(function (id) { self.el("room-" + n + "-" + id, "p", dm_text, self._grid.cell(id), "Room " + id); });
        this._reflect();
    }

    /** The grid's layout and box, told to the mirror once its holder has put the grid on the page - read then, synchronously. */
    _reflect() {
        var self = this;
        Promise.resolve().then(function () { if (self.alive) self._mirror.reflect(self._grid.layout(), self._grid.box()); });
    }

    _at() { return this._mirror.cursor() || this._grid.cells()[0]; }

    split() {
        var at = this._at(), made = this._grid.subdivide(at, "right");
        this.say("room " + at + " split in the grid: room " + made + " reflected");
    }

    remove() {
        var at = this._at();
        try { this._grid.remove(at); this.say("room " + at + " removed in the grid: reflected"); }
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
        this.say("the rooms as they began, reflected");
    }

    /** The arrows move the mirror's cursor. */
    key(ev) { return this._mirror.key(ev); }

    disposed() {
        try { this._mirror.dispose(); } catch (e) {}
        try { this._grid.dispose(); } catch (e) {}
    }
}
