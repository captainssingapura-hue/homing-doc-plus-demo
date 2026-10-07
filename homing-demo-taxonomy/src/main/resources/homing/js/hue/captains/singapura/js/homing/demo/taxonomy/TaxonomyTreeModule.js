// =============================================================================
// TaxonomyTree — the house's components as the tree they are, in a relation
// tree: the root; each branch under its parent, with how many components are
// under it; each component a leaf under its branch, with how many parts it
// names. Components only: the parts are the Parts table's.
//
// THE PICK. The node the cursor lands on is picked for every pane - a branch or
// a component. A node picked elsewhere is unfolded to and the cursor goes to
// it; a part, picked elsewhere, takes the cursor to the component that plays
// it - the first of its chain the tree holds, never its owner. The rest of the
// chain it falls back along is lit.
//
// The keys: the tree's - ↑ ↓ Home End PageUp PageDown walk it, ← → fold and
// unfold, Space toggles. Escape the tree did not take gives them back. A press
// on the row the cursor is on already picks it again.
//
//   new TaxonomyTree(container, params)   params: none
//   (the rest is a TaxonomyWidget's)
// =============================================================================

class TaxonomyTree extends TaxonomyWidget {
    constructor(container, params) {
        super(container, "taxonomyTree", "Taxonomy");
        var self = this;
        this._hint = this.el("hint", "p", tx_hint, this.body, "");
        this._box = this.el("box", "div", tx_port, this.body);
        this._box.addEventListener("click", function () { self._pickAtCursor(); });
        this._tree = null;
        this._cells = new Map();
        this._lit = [];
        this._steering = false;
        this._build();
        this.selected(this.picked());
    }

    /** The tree, the root and every branch unfolded: every component shown. */
    _build() {
        this._clear();
        var self = this, t = this.taxonomy, v = this.fresh();
        this._open = new Set([t.root().id]);
        t.nodes().forEach(function (n) { if (n.is === "branch") self._open.add(n.id); });
        this._cellsBranch = v.createBranch("cells");
        this._cellsBranch.activate(this);
        this._seq = 0;
        this._tree = new RelTree({
            container: this._box, branch: v.createBranch("tree"), label: "The house's components", folder: true,
            relation: { view: function () { return self._places(); }, cellFor: function (key) { return self._cellFor(key); } },
            ask: function (q) { return self._answer(q); },
            onCursorMoved: function (key) { if (!self._steering && key && key !== self.picked()) self.pick(key); }
        });
        this.keysTo = this._tree;
        this._say();
    }

    /** What the tree presents now: the root, the branches and the components under them, those under a folded one left out. */
    _places() {
        var self = this, out = [];
        (function place(n, depth) {
            var branch = n.is !== "component", open = branch && self._open.has(n.id);
            out.push({ key: n.id, depth: depth, fold: branch && n.children.length ? (open ? "open" : "closed") : "leaf" });
            if (open) self.taxonomy.children(n.id).forEach(function (c) { place(c, depth + 1); });
        })(this.taxonomy.root(), 0);
        return out;
    }

    _cellFor(key) {
        var n = this.taxonomy.node(key);
        if (!n || n.is === "part") throw new Error("[TaxonomyTree] no such component or branch: " + key);
        var c = this._cells.get(key);
        if (!c) {
            c = new RelTreeTextCell({ branch: this._cellsBranch.createBranch("n" + (++this._seq)), text: this._label(n) });
            this._cells.set(key, c);
            if (this._lit.indexOf(key) >= 0) css.addClass(c.cellElement(), tx_on);
        }
        return c;
    }

    /** The root and a branch with how many components are under it; a component with how many parts it names. */
    _label(n) {
        if (n.is === "component") {
            var p = n.children.length;
            return p ? n.name + " · " + p + (p === 1 ? " part" : " parts") : n.name;
        }
        var k = this.taxonomy.componentsUnder(n.id).length;
        return (n.is === "root" ? "Any component" : n.name) + " · " + k + (k === 1 ? " component" : " components");
    }

    /** The tree's questions: a fold and an unfold, answered from the taxonomy; its notifications, with nothing. */
    _answer(q) {
        if (q instanceof RelTreeUnfold) { this._open.add(q.key); return Promise.resolve(new RelTreeView(this._places())); }
        if (q instanceof RelTreeFold) { this._open.delete(q.key); return Promise.resolve(new RelTreeView(this._places())); }
        return Promise.resolve();
    }

    /** A press on the row the cursor is on already: picked again - after a part took the cursor to what plays it. */
    _pickAtCursor() {
        var key = this._tree ? this._tree.cursor() : null;
        if (key && key !== this.picked()) this.pick(key);
    }

    /** Picked: its chain as the tree holds it - a part's from what plays it - the cursor on the first, unfolded to, the rest lit. */
    selected(id) {
        if (!this._tree) return;
        var self = this, t = this.taxonomy, n = id ? t.node(id) : null;
        var chain = n ? n.fallback.filter(function (x) { return t.node(x).is !== "part"; }) : [];
        this._lit.forEach(function (key) { var c = self._cells.get(key); if (c) css.toggleClass(c.cellElement(), tx_on, false); });
        this._lit = chain.slice(1);
        this._lit.forEach(function (key) { var c = self._cells.get(key); if (c) css.addClass(c.cellElement(), tx_on); });
        var at = chain[0], folded = false;
        if (at) t.above(at).forEach(function (up) { if (!self._open.has(up)) { self._open.add(up); folded = true; } });
        if (folded) this._tree.tell(new RelTreeViewChanged());
        if (at && this._tree.cursor() !== at) {
            this._steering = true;
            try { this._tree.selectNode(at); } finally { this._steering = false; }
        }
        this._say();
    }

    _say() {
        var t = this.taxonomy, n = this.picked() ? t.node(this.picked()) : null;
        this._hint.textContent = t.count("branch") + " branches and " + t.count("component") + " components: each under its parent up to the root. "
            + (n ? "Lit: the chain " + t.label(n.id) + " falls back along." : "The node the cursor is on is picked.");
    }

    _clear() {
        if (this._tree) { this._tree.destroy(); this._tree = null; }
        this._cells.forEach(function (c) { c.dispose(); });
        this._cells.clear();
        this._lit = [];
        this.keysTo = null;
    }

    disposed() { this._clear(); }
}
