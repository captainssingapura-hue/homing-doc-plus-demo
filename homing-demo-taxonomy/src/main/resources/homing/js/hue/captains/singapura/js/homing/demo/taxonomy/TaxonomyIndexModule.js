// =============================================================================
// TaxonomyIndex — the house's taxonomy as the widgets ask about it, read off
// TAXONOMY. Pure: no page, no clock.
//
// A node is { id, is, name, parent, children, fallback } - `is` the root, a
// branch, a component or a part; its id its token. A component carries
// `playedIn`, the parts it plays in others; a part its `owner`, `base` and
// `role`. A component's children are its parts, so a part's parent is its
// owner - but it falls back through its base.
//
//   TaxonomyIndex.the()          the one index over TAXONOMY
//   index.node(id)  index.root()  index.nodes()  index.children(id)
//   index.componentsUnder(id)    every component under a node, however deep
//   index.partsOf(id)            a component's parts, in the order it names them
//   index.above(id)              the nodes above one in the tree, nearest first
//   index.label(id)              a part said as its role and what plays it
//   index.classes(id)            its semantic classes, one per target leaf
//   index.count(is)
// =============================================================================

class TaxonomyIndex {
    constructor(data) {
        var by = new Map();
        data.nodes.forEach(function (n) { by.set(n.id, n); });
        this.data = data;
        this._by = by;
    }

    /** The one index over TAXONOMY. */
    static the() {
        if (!TaxonomyIndex._one) TaxonomyIndex._one = new TaxonomyIndex(TAXONOMY);
        return TaxonomyIndex._one;
    }

    node(id) { return this._by.get(id) || null; }

    root() { return this.node(this.data.root); }

    nodes() { return this.data.nodes; }

    /** What is under a node: a branch's branches and components, a component's parts. */
    children(id) {
        var self = this, n = this.node(id);
        return n ? n.children.map(function (c) { return self.node(c); }) : [];
    }

    /** Every component under a node, however deep. */
    componentsUnder(id) {
        var self = this, out = [];
        (function walk(n) {
            n.children.forEach(function (c) {
                var k = self.node(c);
                if (k.is === "component") out.push(k);
                if (k.is === "branch") walk(k);
            });
        })(this.node(id));
        return out;
    }

    /** A component's parts, in the order it names them; anything else has none. */
    partsOf(id) {
        var n = this.node(id);
        return n && n.is === "component" ? this.children(id) : [];
    }

    /** The nodes above one in the tree, nearest first - for a part, its owner and the owner's. */
    above(id) {
        var out = [], n = this.node(id);
        while (n && n.parent) { out.push(n.parent); n = this.node(n.parent); }
        return out;
    }

    /** How a node is said: a part as its role and the component that plays it. */
    label(id) {
        var n = this.node(id);
        if (!n) return id;
        return n.is === "part" ? n.name + " · " + this.node(n.base).name : n.name;
    }

    /** Every semantic class of a node: the node × each target leaf, derived. */
    classes(id) { return this.data.targets.map(function (t) { return id + "-" + t; }); }

    count(is) { return this.data.nodes.filter(function (n) { return n.is === is; }).length; }
}
