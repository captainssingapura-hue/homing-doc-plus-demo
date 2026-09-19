// =============================================================================
// PreferencesTreeWidget — the master: RelTree over the rigid tree in params.
//
//   construct(branch, params) → { root, onSelect(fn), select(path), dispose() }
//   params: { tree: <canonical rigid tree JSON>, labels: { path: label }, label?, folder? }
//
// The relation is the rigid tree read once: every node a place, keyed by
// its path, open from the start, with the label the tree carries as its
// display dimension. The cursor is the choice: RelTree's onCursorMoved is
// where onSelect fires, so arrows and clicks both choose; select(path) moves
// the cursor from outside. Folds are answered from the relation's own state.
// =============================================================================

const _owner = Object.freeze({ toString: () => "preferencesTree" });

function construct(branch, params) {
    branch.activate(_owner);
    var root = branch.createElement("root", "div");

    // The rigid tree → nodes by path, in tree order, with their depth.
    var nodes = [], byKey = new Map();
    (function walk(node, path, depth) {
        var key = path ? path + "/" + node.segment : node.segment;
        var label = (params.labels && params.labels[key]) || labelOf(node) || node.segment;
        var entry = { key: key, depth: depth, label: label, children: [] };
        nodes.push(entry); byKey.set(key, entry);
        (node.children || []).forEach(function (c) { entry.children.push(walk(c, key, depth + 1)); });
        return entry;
    })(params.tree, "", 0);
    function labelOf(node) {
        var dims = node.dimensions || [];
        for (var i = 0; i < dims.length; i++) if (dims[i].key === "displayLabel") return dims[i].text;
        return null;
    }

    var open = new Set(nodes.filter(function (n) { return n.children.length; }).map(function (n) { return n.key; }));
    var cells = new Map(), seq = 0;
    function places() {
        var out = [];
        (function place(entry) {
            out.push({ key: entry.key, depth: entry.depth, fold: entry.children.length ? (open.has(entry.key) ? "open" : "closed") : "leaf" });
            if (open.has(entry.key)) entry.children.forEach(place);
        })(nodes[0]);
        return out;
    }
    var relation = {
        view: function () { return places(); },
        cellFor: function (key) {
            var c = cells.get(key);
            if (!c) {
                c = new RelTreeTextCell({ branch: branch.createBranch("n" + (++seq)), text: byKey.get(key).label });
                cells.set(key, c);
            }
            return c;
        },
        answer: function (q) {
            if (q instanceof RelTreeUnfold) { open.add(q.key);    return Promise.resolve(new RelTreeView(places())); }
            if (q instanceof RelTreeFold)   { open.delete(q.key); return Promise.resolve(new RelTreeView(places())); }
            return Promise.resolve();
        }
    };

    var listeners = [];
    var tree = new RelTree({
        container: root,
        branch: branch.createBranch("tree"),
        relation: relation,
        label: params.label || "Preferences",
        folder: !!params.folder,
        ask: function (q, mask) { return relation.answer(q, mask); },
        onCursorMoved: function (key) { listeners.forEach(function (fn) { fn(key); }); }
    });

    return {
        root: root,
        onSelect: function (fn) { listeners.push(fn); },
        select: function (path) { tree.selectNode(path); },
        setActive: function (on) { if (on) tree.focus(); },
        dispose: function () { cells.forEach(function (c) { c.dispose(); }); cells.clear(); }
    };
}
