// =============================================================================
// StyleCoverageMatrix — how each house style draws each casting, a row a
// casting (a component on its own, then in each role it is cast in), a column
// a style, Base first - read on the Java side from the style manager
// (STYLE_COVERAGE).
//
// A cell: "own" - the style's own CSS for the casting; "as ..." - drawn as
// another casting of its component, by delegation; "-" - the style leaves it,
// and Base draws it. Base's column is never "-". "Worn": whether the
// component wears the new sheet yet, the old design's classes, or has no
// implementation.
//
// A VIEW OF THE PICK, never a picker: the root or nothing picked, every
// casting; a branch, its components' castings; a component, the castings of
// the components beside it, the cursor on its own; a part, its component's.
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
        this._columns = ["casting", "worn"].concat(data.columns.map(function (c) { return c.id; }));
        var labels = { casting: "Casting", worn: "Worn" };
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
        var heads = new RelGridLabels({ branch: this.branch.createBranch("heads"), labels: self._labels });
        var relation = {
            view: function () { return self._rows.slice(); },
            columns: function () { return self._columns.slice(); },
            headerFor: function (col) { return heads.headerFor(col); },   // what a column is called: its label header
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

    /** A casting by its words: its component's name, and the role it is cast in. */
    _named(id) {
        var row = this._byId.get(id);
        if (!row) throw new Error("[StyleCoverageMatrix] no such casting: " + id);
        return this.taxonomy.label(row.component) + (row.role ? " as " + row.role.replace(/-/g, " ") : "");
    }

    /** What a cell says: the casting, whether its component wears the new sheet, or how a style draws it. */
    _said(id, column) {
        var row = this._byId.get(id);
        if (!row) throw new Error("[StyleCoverageMatrix] no such casting: " + id);
        if (column === "casting") return this._named(id);
        if (column === "worn") return _SC_WORN[row.worn];
        var cell = row.cells[column];
        if (cell === "own") return "own";
        if (cell === "base") return "-";
        return "as " + this._named(cell.substring(3));
    }

    /** Picked: the castings it covers - every one, a branch's components', or a component's and those beside it. */
    selected(id) {
        if (!this._grid) return;
        var t = this.taxonomy, n = id ? t.node(id) : null;
        if (n && n.is === "part") n = t.node(n.base);
        var under = !n || n.is === "root" ? t.root().id : n.is === "branch" ? n.id : n.parent;
        if (under !== this._shown) {
            this._shown = under;
            var components = new Set(t.componentsUnder(under).map(function (c) { return c.id; }));
            this._rows = this._data.rows.filter(function (r) { return components.has(r.component); }).map(function (r) { return r.id; });
            this._grid.tell(new RelGridViewChanged());
        }
        var at = this._grid.cursor();
        if (n && n.is === "component" && this._byId.has(n.id) && (!at || at.pk !== n.id)) this._grid.selectCell(n.id, "casting");
        this._say();
    }

    /** What the matrix covers here: how many castings, how many wear the new sheet, how many each style draws itself. */
    _say() {
        var self = this, d = this._data, rows = this._rows.map(function (id) { return self._byId.get(id); });
        var under = this.taxonomy.node(this._shown), k = rows.length;
        var components = new Set(rows.map(function (r) { return r.component; }));
        var worn = new Set(rows.filter(function (r) { return r.worn === "look"; }).map(function (r) { return r.component; })).size;
        var said = d.columns.filter(function (c) { return c.is === "style"; }).map(function (c) {
            var own = rows.filter(function (r) { return r.cells[c.id] === "own"; }).length;
            var as = rows.filter(function (r) { return r.cells[c.id].indexOf("as:") === 0; }).length;
            return c.label + " " + (own + as) + (as ? " (" + as + " as another)" : "");
        });
        this._hint.textContent = (under ? under.name + ": " : "") + k + (k === 1 ? " casting" : " castings") + " of " + components.size
            + (components.size === 1 ? " component" : " components") + ", " + worn + " wearing the new sheet."
            + " A cell: own - the style's own CSS; as ... - drawn as another casting of its component; - left to Base, which draws every casting."
            + " Drawn by the style itself here: " + said.join(", ") + ".";
    }

    disposed() {
        this._grid.destroy();
        this._made.forEach(function (cell) { cell.dispose(); });
        this._made.clear();
    }
}
