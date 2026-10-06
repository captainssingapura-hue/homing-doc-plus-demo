// =============================================================================
// TaxonomyTree — the house's taxonomy as the tree it is, in a relation tree:
// the root; each kind under its parent, with how many components are under it;
// each component a leaf under its kind, its parts - the roles it names, each
// said with the component that plays it - under it, folded at first.
//
// THE PICK. The node the cursor lands on is picked for every pane - a kind, a
// component or a part. A node picked elsewhere is unfolded to, and the cursor
// goes to it; the chain it falls back along is lit - a part's through its
// base, never its owner, so what lights is elsewhere in the tree.
//
// The keys: the tree's - ↑ ↓ Home End PageUp PageDown walk it, ← → fold and
// unfold, Space toggles. Escape the tree did not take gives them back.
//
//   new TaxonomyTree(container, params)   params: none
//   (the rest is a TaxonomyWidget's)
// =============================================================================

class TaxonomyTree extends TaxonomyWidget {
    constructor(container, params) {
        super(container, "taxonomyTree", "Taxonomy");
        this._hint = this.el("hint", "p", tx_hint, this.body, "");
        this._box = this.el("box", "div", tx_port, this.body);
        this._tree = null;
        this._cells = new Map();
        this._lit = [];
        this._build();
        this.selected(this.picked());
    }

    /** The tree, the root and every kind unfolded: the components shown, their parts folded. */
    _build() {
        this._clear();
        var self = this, t = this.taxonomy, v = this.fresh();
        this._open = new Set([t.root().id]);
        t.nodes().forEach(function (n) { if (n.is === "kind") self._open.add(n.id); });
        this._cellsBranch = v.createBranch("cells");
        this._cellsBranch.activate(this);
        this._seq = 0;
        this._tree = new RelTree({
            container: this._box, branch: v.createBranch("tree"), label: "The house's taxonomy", folder: true,
            relation: { view: function () { return self._places(); }, cellFor: function (key) { return self._cellFor(key); } },
            ask: function (q) { return self._answer(q); },
            onCursorMoved: function (key) { if (key && key !== self.picked()) self.pick(key); }
        });
        this.keysTo = this._tree;
        this._say();
    }

    /** What the tree presents now: every node from the root down, those under a folded one left out. */
    _places() {
        var self = this, out = [];
        (function place(n, depth) {
            var open = self._open.has(n.id);
            out.push({ key: n.id, depth: depth, fold: n.children.length ? (open ? "open" : "closed") : "leaf" });
            if (open) self.taxonomy.children(n.id).forEach(function (c) { place(c, depth + 1); });
        })(this.taxonomy.root(), 0);
        return out;
    }

    _cellFor(key) {
        var n = this.taxonomy.node(key);
        if (!n) throw new Error("[TaxonomyTree] no such node: " + key);
        var c = this._cells.get(key);
        if (!c) {
            c = new RelTreeTextCell({ branch: this._cellsBranch.createBranch("n" + (++this._seq)), text: this._label(n) });
            this._cells.set(key, c);
            if (this._lit.indexOf(key) >= 0) css.addClass(c.cellElement(), tx_on);
        }
        return c;
    }

    /** The root and a kind with how many components are under it; a component with its parts; a part with what plays it. */
    _label(n) {
        var t = this.taxonomy;
        if (n.is === "root" || n.is === "kind") {
            var k = t.componentsUnder(n.id).length;
            return (n.is === "root" ? "Any component" : n.name) + " · " + k + (k === 1 ? " component" : " components");
        }
        if (n.is === "component") {
            var p = n.children.length;
            return p ? n.name + " · " + p + (p === 1 ? " part" : " parts") : n.name;
        }
        return t.label(n.id);
    }

    /** The tree's questions: a fold and an unfold, answered from the taxonomy; its notifications, with nothing. */
    _answer(q) {
        if (q instanceof RelTreeUnfold) { this._open.add(q.key); return Promise.resolve(new RelTreeView(this._places())); }
        if (q instanceof RelTreeFold) { this._open.delete(q.key); return Promise.resolve(new RelTreeView(this._places())); }
        return Promise.resolve();
    }

    /** Picked: unfolded to, the cursor on it, and the chain it falls back along lit. */
    selected(id) {
        if (!this._tree) return;
        var self = this, n = id ? this.taxonomy.node(id) : null;
        this._lit.forEach(function (key) { var c = self._cells.get(key); if (c) css.toggleClass(c.cellElement(), tx_on, false); });
        this._lit = n ? n.fallback.slice(1) : [];
        this._lit.forEach(function (key) { var c = self._cells.get(key); if (c) css.addClass(c.cellElement(), tx_on); });
        var folded = false;
        if (n) this.taxonomy.above(id).forEach(function (up) { if (!self._open.has(up)) { self._open.add(up); folded = true; } });
        if (folded) this._tree.tell(new RelTreeViewChanged());
        if (n && this._tree.cursor() !== id) this._tree.selectNode(id);
        this._say();
    }

    _say() {
        var t = this.taxonomy, n = this.picked() ? t.node(this.picked()) : null;
        this._hint.textContent = (t.count("kind")) + " kinds, " + t.count("component") + " components and " + t.count("part")
            + " parts: each kind and component under its parent up to the root, a component's parts - the roles it names - under it. "
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
