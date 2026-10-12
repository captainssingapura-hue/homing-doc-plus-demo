// =============================================================================
// StyleCoverageMatrix — what each house style and palette says of each
// component, a row a component, a column a style or a palette, derived on the
// Java side (STYLE_COVERAGE) from the answers.
//
// A cell: the looks at rest the column answers itself · the treatments it
// answers itself. A style's looks are counted of every target that is not
// colour, a palette's of the colours. What a style leaves falls to Base, then
// to the root - the element as the browser leaves it. "Worn": whether the
// component wears the new sheet yet, the old design's classes, or has no
// implementation.
//
// A VIEW OF THE PICK, never a picker: the root or nothing picked, every
// component; a branch, the components under it; a component, the components
// beside it, the cursor on its row; a part, its component's.
//
//   new StyleCoverageMatrix(container, params)   params: none
//   (the rest is a TaxonomyWidget's)
// =============================================================================

var _SC_WORN = Object.freeze({ look: "new sheet", old: "old design", none: "-" });

class StyleCoverageMatrix extends TaxonomyWidget {
    constructor(container, params) {
        super(container, "styleCoverage", "Style coverage");
        var self = this, data = STYLE_COVERAGE;
        this._data = data;
        this._byId = new Map(data.rows.map(function (r) { return [r.id, r]; }));
        this._columns = ["component", "worn"].concat(data.columns.map(function (c) { return c.id; }));
        var labels = { component: "Component", worn: "Worn" };
        data.columns.forEach(function (c) { labels[c.id] = c.label; });
        this._labels = Object.freeze(labels);
        this._hint = this.el("hint", "p", tx_hint, this.body, "");
        var frame = this.el("frame", "div", tx_frame, this.body);
        var port = this.el("port", "div", tx_port, frame);
        // the domain's cells, on a branch of its own: the grid places them and owns none
        var cells = this.branch.createBranch("cells");
        cells.activate(this);
        var made = new Map(), seq = 0;
        this._rows = [];
        this._shown = "";
        var relation = {
            view: function () { return self._rows.slice(); },
            columns: function () { return self._columns.slice(); },
            labels: function () { return self._labels; },
            readOnlyColumns: function () { return self._columns.slice(); },
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
            container: port, branch: this.branch.createBranch("grid"), relation: relation, label: "Style coverage",
            design: HouseGrid, box: frame, header: { sticky: true }   // the frame round the scrollport wears the grid's edge
        });
        this.keysTo = this._grid;
        this.selected(this.picked());
    }

    /** What a cell says: the component, whether it wears the new sheet, or a column's own looks · own treatments. */
    _said(id, column) {
        var row = this._byId.get(id);
        if (!row) throw new Error("[StyleCoverageMatrix] no such component: " + id);
        if (column === "component") return this.taxonomy.label(id);
        if (column === "worn") return _SC_WORN[row.worn];
        var cell = row.cells[column];
        return cell[0] === 0 && cell[2] === 0 ? "-" : cell[0] + " · " + cell[2];
    }

    /** Picked: the components it covers - every one, a branch's, or a component's and those beside it. */
    selected(id) {
        if (!this._grid) return;
        var t = this.taxonomy, n = id ? t.node(id) : null;
        if (n && n.is === "part") n = t.node(n.base);
        var under = !n || n.is === "root" ? t.root().id : n.is === "branch" ? n.id : n.parent;
        if (under !== this._shown) {
            this._shown = under;
            var self = this;
            this._rows = t.componentsUnder(under).map(function (c) { return c.id; }).filter(function (c) { return self._byId.has(c); });
            this._grid.tell(new RelGridViewChanged());
        }
        var at = this._grid.cursor();
        if (n && n.is === "component" && (!at || at.pk !== n.id)) this._grid.selectCell(n.id, "component");
        this._say();
    }

    /** What the matrix covers here: how many components, how many each style answers itself, how many wear the new sheet. */
    _say() {
        var self = this, d = this._data, rows = this._rows.map(function (id) { return self._byId.get(id); });
        var under = this.taxonomy.node(this._shown), k = rows.length;
        var worn = rows.filter(function (r) { return r.worn === "look"; }).length;
        var said = d.columns.filter(function (c) { return c.is === "style"; }).map(function (c) {
            var n = rows.filter(function (r) { var x = r.cells[c.id]; return x[0] + x[2] > 0; }).length;
            return c.label + " " + n;
        });
        this._hint.textContent = (under ? under.name + ": " : "") + k + (k === 1 ? " component" : " components") + ", " + worn + " wearing the new sheet."
            + " A cell: the looks at rest a column answers itself, of " + d.looks + " (a palette's of " + d.colours + " colours), and the treatments it answers itself;"
            + " what a style leaves falls to Base, then to the root - the element as the browser leaves it."
            + " Answering any of these components itself: " + said.join(", ") + ".";
    }

    disposed() {
        this._grid.destroy();
        this._made.forEach(function (cell) { cell.dispose(); });
        this._made.clear();
    }
}
