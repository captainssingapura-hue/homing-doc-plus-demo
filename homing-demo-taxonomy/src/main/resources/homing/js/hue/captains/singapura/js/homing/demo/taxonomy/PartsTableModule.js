// =============================================================================
// PartsTable — the picked component's parts, a row each, in a relation grid:
// the role, the component that plays it, and its token.
//
// A VIEW OF THE PICK, never a picker. A component picked: its parts. A part
// picked: its owner's, the cursor on its row. A branch or the root: nothing - the
// parts are a component's. The cursor walks the rows and picks nothing.
//
//   new PartsTable(container, params)   params: none
//   (the rest is a TaxonomyWidget's)
// =============================================================================

var _PT_COLUMNS = ["role", "base", "token"];
var _PT_LABELS = Object.freeze({ role: "Role", base: "Played by", token: "Token" });

class PartsTable extends TaxonomyWidget {
    constructor(container, params) {
        super(container, "partsTable", "Parts");
        var self = this;
        this._hint = this.el("hint", "p", tx_hint, this.body, "");
        var frame = this.el("frame", "div", tx_frame, this.body);
        css.addClass(frame, hrg_frame, hrg_lit);
        var port = this.el("port", "div", tx_port, frame);
        // the domain's cells, on a branch of its own: the grid places them and owns none
        var cells = this.branch.createBranch("cells");
        cells.activate(this);
        var made = new Map(), seq = 0;
        this._owner = "";
        this._rows = [];
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
            frame: false, header: { sticky: true }
        });
        this.keysTo = this._grid;
        this.selected(this.picked());
    }

    /** What a cell of the table says. */
    _said(id, column) {
        var t = this.taxonomy, p = t.node(id);
        if (!p || p.is !== "part") throw new Error("[PartsTable] no such part: " + id);
        switch (column) {
            case "role":  return p.role;
            case "base":  return t.node(p.base).name;
            case "token": return p.id;
            default:      return "";
        }
    }

    /** Picked: the component's parts - a part's owner's, the cursor on its row; a branch's or the root's, none. */
    selected(id) {
        if (!this._grid) return;
        var t = this.taxonomy, n = id ? t.node(id) : null;
        var owner = !n ? "" : n.is === "component" ? n.id : n.is === "part" ? n.owner : "";
        if (owner !== this._owner) {
            this._owner = owner;
            this._rows = owner ? t.partsOf(owner).map(function (p) { return p.id; }) : [];
            this._grid.tell(new RelGridViewChanged());
        }
        var at = this._grid.cursor();
        if (n && n.is === "part" && (!at || at.pk !== id)) this._grid.selectCell(id, "role");
        this._say();
    }

    _say() {
        var c = this._owner ? this.taxonomy.node(this._owner) : null, k = this._rows.length;
        this._hint.textContent = !c ? "Pick a component to see its parts: each role it names, and the component that plays it."
            : k ? c.name + " names " + k + (k === 1 ? " part" : " parts") + ": each a role, played by an independent component."
            : c.name + " names no parts: what it is made of is its implementation's own.";
    }

    disposed() {
        this._grid.destroy();
        this._made.forEach(function (cell) { cell.dispose(); });
        this._made.clear();
    }
}
