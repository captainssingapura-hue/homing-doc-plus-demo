// =============================================================================
// PreferencesTreeWidget — the master: RelTree over the rigid tree in params.
//
//   new PreferencesTreeWidget(branch, params) → root, onSelect(fn), select(path), setActive(on), dispose()
//   params: { tree: <canonical rigid tree JSON>, labels: { path: label }, label?, folder? }
//
// The relation is the rigid tree read once: every node a place, keyed by
// its path, open from the start, with the label the tree carries as its
// display dimension. The cursor is the choice: RelTree's onCursorMoved is
// where onSelect fires, so arrows and clicks both choose; select(path) moves
// the cursor from outside. Folds are answered from the relation's own state.
// =============================================================================

const _owner = Object.freeze({ toString: () => "preferencesTree" });

class PreferencesTreeWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        var self = this;
        this._branch = branch;
        this._listeners = [];
        this._cells = new Map();
        this._seq = 0;
        var root = branch.createElement("root", "div");
        this.root = root;

        // The rigid tree → nodes by path, in tree order, with their depth.
        this._nodes = [];
        this._byKey = new Map();
        (function walk(node, path, depth) {
            var key = path ? path + "/" + node.segment : node.segment;
            var label = (params.labels && params.labels[key]) || PreferencesTreeWidget._labelOf(node) || node.segment;
            var entry = { key: key, depth: depth, label: label, children: [] };
            self._nodes.push(entry); self._byKey.set(key, entry);
            (node.children || []).forEach(function (c) { entry.children.push(walk(c, key, depth + 1)); });
            return entry;
        })(params.tree, "", 0);
        this._open = new Set(this._nodes.filter(function (n) { return n.children.length; }).map(function (n) { return n.key; }));

        var relation = {
            view: function () { return self._places(); },
            cellFor: function (key) {
                var c = self._cells.get(key);
                if (!c) {
                    c = new RelTreeTextCell({ branch: branch.createBranch("n" + (++self._seq)), text: self._byKey.get(key).label });
                    self._cells.set(key, c);
                }
                return c;
            },
            answer: function (q) {
                if (q instanceof RelTreeUnfold) { self._open.add(q.key);    return Promise.resolve(new RelTreeView(self._places())); }
                if (q instanceof RelTreeFold)   { self._open.delete(q.key); return Promise.resolve(new RelTreeView(self._places())); }
                return Promise.resolve();
            }
        };
        this._tree = new RelTree({
            container: root,
            branch: branch.createBranch("tree"),
            relation: relation,
            label: params.label || "Preferences",
            folder: !!params.folder,
            ask: function (q, mask) { return relation.answer(q, mask); },
            onCursorMoved: function (key) { self._listeners.forEach(function (fn) { fn(key); }); }
        });
    }

    static _labelOf(node) {
        var dims = node.dimensions || [];
        for (var i = 0; i < dims.length; i++) if (dims[i].key === "displayLabel") return dims[i].text;
        return null;
    }

    _places() {
        var out = [], open = this._open;
        (function place(entry) {
            out.push({ key: entry.key, depth: entry.depth, fold: entry.children.length ? (open.has(entry.key) ? "open" : "closed") : "leaf" });
            if (open.has(entry.key)) entry.children.forEach(place);
        })(this._nodes[0]);
        return out;
    }

    onSelect(fn) { this._listeners.push(fn); }
    select(path) { this._tree.selectNode(path); }
    setActive(on) { if (on) this._tree.focus(); }
    dispose() { this._cells.forEach(function (c) { c.dispose(); }); this._cells.clear(); }
}
