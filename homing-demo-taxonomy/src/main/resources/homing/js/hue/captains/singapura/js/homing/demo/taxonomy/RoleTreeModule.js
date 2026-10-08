// =============================================================================
// RoleTree — the house's role catalogue, studied on its own: its branches and
// roles in a relation tree - the root, the branches by what their roles do for
// their owner, the roles at the leaves - and, under the tree, the uses of the
// role the cursor is on: every component that names it, what plays it there,
// how many.
//
// A VIEW, NOT A PICKER. The cursor walks the catalogue and picks nothing for
// the other panes; a part picked anywhere brings the cursor to its role. The
// components named in a role's uses are links, and pressing one picks it.
//
// The keys: the tree's - ↑ ↓ Home End PageUp PageDown walk it, ← → fold and
// unfold, Space toggles. Escape the tree did not take gives them back.
//
//   new RoleTree(container, params)   params: none
//   (the rest is a TaxonomyWidget's)
// =============================================================================

class RoleTree extends TaxonomyWidget {
    constructor(container, params) {
        super(container, "roleTree", "Roles");
        this._hint = this.el("hint", "p", tx_hint, this.body, "");
        this._box = this.el("box", "div", tx_port, this.body);
        this._uses = this.el("uses", "section", tx_panel, this.body);
        this._tree = null;
        this._cells = new Map();
        this._at = null;
        this._build();
        this._show(this.taxonomy.roleRoot().id);
        this.selected(this.picked());
    }

    /** The catalogue, every branch unfolded: every role shown - on a branch of its own, apart from the views fresh() makes for the uses. */
    _build() {
        var self = this, t = this.taxonomy;
        this._catalogueView = this.branch.createBranch("catalogue");
        this._catalogueView.activate(this);
        this._open = new Set();
        t.roleNodes().forEach(function (n) { if (n.is !== "role") self._open.add(n.id); });
        this._cellsBranch = this._catalogueView.createBranch("cells");
        this._cellsBranch.activate(this);
        this._seq = 0;
        this._tree = new RelTree({
            container: this._box, branch: this._catalogueView.createBranch("tree"), label: "The house's roles", folder: true,
            relation: { view: function () { return self._places(); }, cellFor: function (key) { return self._cellFor(key); } },
            ask: function (q) { return self._answer(q); },
            onCursorMoved: function (key) { if (key) self._show(key); }
        });
        this.keysTo = this._tree;
        this._hint.textContent = t.roleCount("branch") + " branches and " + t.roleCount("role")
            + " roles, filed by what each part does for its owner. The role the cursor is on shows its uses below.";
    }

    /** What the tree presents now: the root, the branches and the roles under them, those under a folded one left out. */
    _places() {
        var self = this, out = [];
        (function place(n, depth) {
            var branch = n.is !== "role", open = branch && self._open.has(n.id);
            out.push({ key: n.id, depth: depth, fold: branch && n.children.length ? (open ? "open" : "closed") : "leaf" });
            if (open) self.taxonomy.roleChildren(n.id).forEach(function (c) { place(c, depth + 1); });
        })(this.taxonomy.roleRoot(), 0);
        return out;
    }

    _cellFor(key) {
        var n = this.taxonomy.roleNode(key);
        if (!n) throw new Error("[RoleTree] no such branch or role: " + key);
        var c = this._cells.get(key);
        if (!c) {
            c = new RelTreeTextCell({ branch: this._cellsBranch.createBranch("n" + (++this._seq)), text: this._label(n) });
            this._cells.set(key, c);
        }
        return c;
    }

    /** The root and a branch with how many roles are under it; a role with how many parts name it, when any do. */
    _label(n) {
        var t = this.taxonomy;
        if (n.is === "role") {
            var u = t.usesOf(n.id).length;
            return u ? n.name + " · " + u + (u === 1 ? " use" : " uses") : n.name;
        }
        var k = t.rolesUnder(n.id).length;
        return (n.is === "root" ? "Any role" : n.name) + " · " + k + (k === 1 ? " role" : " roles");
    }

    /** The tree's questions: a fold and an unfold, answered from the catalogue; its notifications, with nothing. */
    _answer(q) {
        if (q instanceof RelTreeUnfold) { this._open.add(q.key); return Promise.resolve(new RelTreeView(this._places())); }
        if (q instanceof RelTreeFold) { this._open.delete(q.key); return Promise.resolve(new RelTreeView(this._places())); }
        return Promise.resolve();
    }

    /** Under the tree: what the cursor is on - a role's uses, or what a branch holds. */
    _show(id) {
        if (id === this._at) return;
        this._at = id;
        var self = this, t = this.taxonomy, n = t.roleNode(id), v = this.fresh(), box = this._uses;
        if (!n) return;
        this.mint(v, "title", "h4", tx_title, box, n.is === "root" ? "Any role" : n.name);
        this.mint(v, "path", "p", tx_tag, box, this._path(n));
        String(n.meaning || "").split(/\n\s*\n/).forEach(function (para, i) {
            var words = para.replace(/\s+/g, " ").trim();
            if (words) self.mint(v, "meaning-" + i, "p", tx_prose, box, words);
        });
        if (n.is !== "role") {
            this.mint(v, "none", "p", tx_hint, box, "A branch organises its roles and nothing more. The cursor on a role shows its uses.");
            return;
        }
        var uses = t.usesOf(id);
        if (!uses.length) {
            this.mint(v, "none", "p", tx_hint, box, "Named by no component: the catalogue files it, and no slot plays it yet.");
            return;
        }
        uses.forEach(function (p, i) {
            var line = self.mint(v, "u" + i, "div", tx_line, box);
            self.link(v, "u" + i + "-owner", p.owner, line);
            self.mint(v, "u" + i + "-by", "span", tx_tag, line, "played by");
            self.link(v, "u" + i + "-base", p.base, line);
            self.mint(v, "u" + i + "-count", "span", tx_code, line, p.count);
        });
    }

    /** Where a node is filed: its branches from the top, root left out. */
    _path(n) {
        var t = this.taxonomy, names = [];
        for (var up = n.parent ? t.roleNode(n.parent) : null; up && up.is !== "root"; up = up.parent ? t.roleNode(up.parent) : null) names.unshift(up.name);
        return names.length ? "Filed under " + names.join(" › ") : (n.is === "root" ? "The root of the catalogue" : "Filed at the root");
    }

    /** A part picked anywhere: the cursor to its role, which shows its uses. Anything else: the cursor stays. */
    selected(id) {
        if (!this._tree) return;
        var n = id ? this.taxonomy.node(id) : null;
        if (!n || n.is !== "part" || !n.roleId) return;
        if (this._tree.cursor() !== n.roleId) this._tree.selectNode(n.roleId);
        this._show(n.roleId);
    }

    disposed() {
        if (this._tree) { this._tree.destroy(); this._tree = null; }
        this._cells.forEach(function (c) { c.dispose(); });
        this._cells.clear();
        this.keysTo = null;
    }
}
