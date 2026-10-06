// =============================================================================
// PartsTable — the parts of the house's components, a row each, in a relation
// grid: the component that names it, the role, the component that plays it,
// and its token.
//
// WHAT IT SHOWS follows the pick. The root: every part. A kind: every part the
// components under it name. A component: its own. A part: the rows it is
// among already, kept - so walking the table never narrows it - or else its
// owner's, the cursor on its row.
//
// THE PICK. The row the cursor lands on is picked for every pane; a press on
// the row the cursor is on already picks it again. Rows changing under the
// grid pick nothing.
//
//   new PartsTable(container, params)   params: none
//   (the rest is a TaxonomyWidget's)
// =============================================================================

var _PT_COLUMNS = ["owner", "role", "base", "token"];
var _PT_LABELS = Object.freeze({ owner: "Component", role: "Role", base: "Played by", token: "Token" });

class PartsTable extends TaxonomyWidget {
    constructor(container, params) {
        super(container, "partsTable", "Parts");
        var self = this;
        this._hint = this.el("hint", "p", tx_hint, this.body, "");
        var frame = this.el("frame", "div", tx_frame, this.body);
        css.addClass(frame, hrg_frame, hrg_lit);
        var port = this.el("port", "div", tx_port, frame);
        port.addEventListener("click", function () { self._pickAtCursor(); });
        // the domain's cells, on a branch of its own: the grid places them and owns none
        var cells = this.branch.createBranch("cells");
        cells.activate(this);
        var made = new Map(), seq = 0;
        this._scope = this.taxonomy.root().id;
        this._rows = this._partsUnder(this._scope);
        this._steering = false;
        var relation = {
            view: function () { return self._rows.slice(); },
            columns: function () { return _PT_COLUMNS.slice(); },
            labels: function () { return _PT_LABELS; },
            readOnlyColumns: function () { return _PT_COLUMNS.slice(); },
            cellFor: function (pk, column) {
                var key = pk + "\n" + column, cell = made.get(key);
                if (!cell) {
                    cell = new RelGridTextCell({ branch: cells.createBranch("c" + (++seq)), value: self._said(pk, column) });
                    made.set(key, cell);
                }
                return cell;
            }
        };
        this._made = made;
        this._grid = new RelGrid({
            container: port, branch: this.branch.createBranch("grid"), relation: relation, label: "Parts",
            frame: false, header: { sticky: true },
            onCursorMoved: function (pk) { if (!self._steering && pk && pk !== self.picked()) self.pick(pk); }
        });
        this.keysTo = this._grid;
        this.selected(this.picked());
    }

    _partsUnder(id) { return this.taxonomy.partsUnder(id).map(function (p) { return p.id; }); }

    /** What a cell of the table says. */
    _said(id, column) {
        var t = this.taxonomy, p = t.node(id);
        if (!p || p.is !== "part") throw new Error("[PartsTable] no such part: " + id);
        switch (column) {
            case "owner": return t.node(p.owner).name;
            case "role":  return p.role;
            case "base":  return t.node(p.base).name;
            case "token": return p.id;
            default:      return "";
        }
    }

    /** A press on the row the cursor is on already: picked again. */
    _pickAtCursor() {
        var at = this._grid ? this._grid.cursor() : null;
        if (at && at.pk !== this.picked()) this.pick(at.pk);
    }

    /** Picked: what the table shows follows it - a part the rows hold keeps them; the cursor on a picked part's row. */
    selected(id) {
        if (!this._grid) return;
        var t = this.taxonomy, n = id ? t.node(id) : null;
        var scope = !n ? t.root().id : n.is !== "part" ? id : this._rows.indexOf(id) >= 0 ? this._scope : n.owner;
        this._steering = true;
        try {
            if (scope !== this._scope) {
                this._scope = scope;
                this._rows = this._partsUnder(scope);
                this._grid.tell(new RelGridViewChanged());
            }
            var at = this._grid.cursor();
            if (n && n.is === "part" && (!at || at.pk !== id)) this._grid.selectCell(id, "role");
        } finally { this._steering = false; }
        this._say();
    }

    _say() {
        var t = this.taxonomy, s = t.node(this._scope), k = this._rows.length;
        var parts = k + (k === 1 ? " part" : " parts");
        this._hint.textContent = (s.is === "root" ? "Every part the house's components name: " + parts + "."
            : s.is === "kind" ? "The parts the components under " + s.name + " name: " + parts + "."
            : k ? s.name + " names " + parts + "." : s.name + " names no parts: what it is made of is its implementation's own.")
            + " Each is a role its component names, played by an independent component. The row the cursor is on is picked.";
    }

    disposed() {
        this._grid.destroy();
        this._made.forEach(function (cell) { cell.dispose(); });
        this._made.clear();
    }
}
