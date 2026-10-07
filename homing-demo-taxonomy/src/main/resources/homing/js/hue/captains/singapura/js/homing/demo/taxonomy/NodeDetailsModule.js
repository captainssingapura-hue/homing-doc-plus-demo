// =============================================================================
// NodeDetails — the picked node of the taxonomy, in panels.
//
// MEANS: what it is for - its own meaning, then those it narrows, nearest
// first; a part's, its role's, then the meaning of what plays it.
//
// WHAT IT IS: the root, a branch, a component or a part; its token; where it
// sits - a branch's or a component's parent, a part's owner, its role and the
// component that plays it.
//
// VARIES BY DEGREE: the axes it varies along - colour, size, aspect - each
// with the node that declares it; a part's are its base's, never its owner's.
//
// FALLS BACK: the chain a design walks for a class of it, on the same target,
// most specific first - a part's through its base, never its owner.
//
// A component's PARTS - the roles it names, each with what plays it - and the
// roles it PLAYS in others; a branch's or the root's components UNDER it.
//
// SEMANTIC CLASSES: one for every target leaf - the node × the target,
// derived, never declared.
//
// Every node named is a link: pressing it picks it for every pane.
//
//   new NodeDetails(container, params)   params: none
//   (the rest is a TaxonomyWidget's)
// =============================================================================

class NodeDetails extends TaxonomyWidget {
    constructor(container, params) {
        super(container, "nodeDetails", "Node");
        this._box = this.el("box", "div", tx_scroll, this.body);
        this.selected(this.picked());
    }

    selected(id) {
        var v = this.fresh(), box = this._box, t = this.taxonomy, n = id ? t.node(id) : null;
        if (!n) {
            this.mint(v, "none", "p", tx_hint, box, id ? "No node " + id + " in the taxonomy."
                : "Pick a node of the taxonomy - in the tree, or here - to see what it is, how it falls back, what it is made of or where it is a part, and its semantic classes.");
            return;
        }
        this.mint(v, "title", "h3", tx_title, box, n.is === "root" ? "Any component" : t.label(id));
        this._means(v, box, n);
        this._what(v, box, n);
        this._degrees(v, box, n);
        this._fallback(v, box, n);
        if (n.is === "component") { this._parts(v, box, n); this._plays(v, box, n); }
        if (n.is === "branch" || n.is === "root") this._under(v, box, n);
        this._classes(v, box, n);
    }

    _panel(v, box, key, title) {
        var panel = this.mint(v, key, "section", tx_panel, box);
        this.mint(v, key + "-title", "h4", tx_title, panel, title);
        return panel;
    }

    /** What it means: its own meaning, then the meanings it narrows, nearest first; a part's, its role's and its base's. */
    _means(v, box, n) {
        var t = this.taxonomy, panel = this._panel(v, box, "means", "Means");
        if (n.is === "part") {
            this._prose(v, panel, "m", t.roleNode(n.roleId).meaning, tx_prose);
            var by = this.mint(v, "m-by", "div", tx_line, panel);
            this.mint(v, "m-by-tag", "span", tx_tag, by, "played by");
            this.link(v, "m-by-base", n.base, by);
            this._prose(v, panel, "m-base", t.node(n.base).meaning, tx_hint);
            return;
        }
        this._prose(v, panel, "m", n.meaning, tx_prose);
        var i = 0;
        for (var up = n.parent ? t.node(n.parent) : null; up; up = up.parent ? t.node(up.parent) : null, i++) {
            var line = this.mint(v, "m-up-" + i, "div", tx_line, panel);
            this.mint(v, "m-up-" + i + "-tag", "span", tx_tag, line, "within");
            this.link(v, "m-up-" + i + "-node", up.id, line);
            this._prose(v, panel, "m-up-" + i + "-text", up.meaning, tx_hint);
        }
    }

    /** A meaning's words, a paragraph each: a blank line between two. */
    _prose(v, panel, key, markdown, cls) {
        var self = this;
        String(markdown || "").split(/\n\s*\n/).forEach(function (para, i) {
            var words = para.replace(/\s+/g, " ").trim();
            if (words) self.mint(v, key + "-" + i, "p", cls, panel, words);
        });
    }

    /** The axes it varies along by degree, each with the node that declares it. */
    _degrees(v, box, n) {
        var self = this, t = this.taxonomy, panel = this._panel(v, box, "degrees", "Varies by degree (" + n.extents.length + ")");
        var from = n.is === "part" ? t.node(n.base) : n;
        this.mint(v, "degrees-hint", "p", tx_hint, panel, n.is === "part"
            ? "A part follows its own component, never its owner: these are what plays it."
            : "Each a number from −1 to 1 on the element, set live - no component of its own, and no state. Declared on a branch, every leaf under it has it.");
        if (!n.extents.length) { this.mint(v, "degrees-none", "p", tx_hint, panel, "None: it varies by no degree."); return; }
        n.extents.forEach(function (axis, i) {
            var line = self.mint(v, "d" + i, "div", tx_line, panel);
            self.mint(v, "d" + i + "-axis", "span", tx_code, line, axis);
            var by = from;
            while (by && by.declares.indexOf(axis) < 0) by = by.parent ? t.node(by.parent) : null;
            if (by && by.id === n.id) { self.mint(v, "d" + i + "-own", "span", tx_tag, line, "its own"); return; }
            self.mint(v, "d" + i + "-from", "span", tx_tag, line, "from");
            if (by) self.link(v, "d" + i + "-by", by.id, line);
        });
    }

    /** What it is, its token, and where it sits. */
    _what(v, box, n) {
        var self = this, panel = this._panel(v, box, "what", "What it is");
        var facts = this.mint(v, "facts", "dl", tx_facts, panel);
        var fact = function (key, term) {
            self.mint(v, "k-" + key, "dt", tx_key, facts, term);
            return self.mint(v, "v-" + key, "dd", null, facts);
        };
        fact("is", "is").textContent = {
            root: "the root: any component - what every chain ends at",
            branch: "a branch: abstract, at its level - never realized, never worn on its own",
            component: "a component: a leaf, the only concrete node - realized by one implementation",
            part: "a part: a role its owner names, played by an independent component"
        }[n.is];
        var token = fact("token", "token");
        css.addClass(token, tx_code);
        token.textContent = n.id;
        if (n.is === "branch" || n.is === "component") this.link(v, "parent", n.parent, fact("parent", "parent"));
        if (n.is === "part") {
            this.link(v, "owner", n.owner, fact("owner", "owner"));
            var role = fact("role", "role");
            css.addClass(role, tx_code);
            role.textContent = n.role;
            this.link(v, "base", n.base, fact("base", "played by"));
        }
    }

    /** The chain a design walks, most specific first. */
    _fallback(v, box, n) {
        var self = this, panel = this._panel(v, box, "fallback", "Falls back");
        this.mint(v, "fallback-hint", "p", tx_hint, panel, n.is === "part"
            ? "A design asked for a class of this part walks this chain on the same target: the part, then the component that plays it and its kinds - never its owner."
            : "A design asked for a class of it walks this chain on the same target, most specific first, to the root.");
        var line = this.mint(v, "chain", "div", tx_line, panel);
        n.fallback.forEach(function (x, i) {
            if (i) self.mint(v, "chain-" + i + "-to", "span", tx_tag, line, "→");
            if (x === n.id) self.mint(v, "chain-" + i, "span", tx_code, line, x);
            else self.link(v, "chain-" + i, x, line);
        });
    }

    /** A component's parts: each role it names, and what plays it. */
    _parts(v, box, n) {
        var self = this, t = this.taxonomy, panel = this._panel(v, box, "parts", "Parts (" + n.children.length + ")");
        if (!n.children.length) { this.mint(v, "parts-none", "p", tx_hint, panel, "No parts: what it is made of is its implementation's own."); return; }
        n.children.forEach(function (pid, i) {
            var p = t.node(pid), line = self.mint(v, "p" + i, "div", tx_line, panel);
            self.link(v, "p" + i + "-role", pid, line, p.name);
            self.mint(v, "p" + i + "-by", "span", tx_tag, line, "played by");
            self.link(v, "p" + i + "-base", p.base, line);
        });
    }

    /** The roles a component plays in others. */
    _plays(v, box, n) {
        var self = this, t = this.taxonomy, panel = this._panel(v, box, "plays", "Plays (" + n.playedIn.length + ")");
        if (!n.playedIn.length) { this.mint(v, "plays-none", "p", tx_hint, panel, "It plays no role in another component."); return; }
        n.playedIn.forEach(function (pid, i) {
            var p = t.node(pid), line = self.mint(v, "r" + i, "div", tx_line, panel);
            self.link(v, "r" + i + "-role", pid, line, p.name);
            self.mint(v, "r" + i + "-of", "span", tx_tag, line, "of");
            self.link(v, "r" + i + "-owner", p.owner, line);
        });
    }

    /** What is under a branch or the root: its own children, and how many components in all. */
    _under(v, box, n) {
        var self = this, t = this.taxonomy, all = t.componentsUnder(n.id).length;
        var panel = this._panel(v, box, "under", "Under it (" + all + (all === 1 ? " component)" : " components)"));
        var line = this.mint(v, "under-line", "div", tx_line, panel);
        n.children.forEach(function (c, i) { self.link(v, "u" + i, c, line); });
    }

    /** One semantic class for every target leaf. */
    _classes(v, box, n) {
        var self = this, classes = this.taxonomy.classes(n.id);
        var panel = this._panel(v, box, "classes", "Semantic classes (" + classes.length + ")");
        this.mint(v, "classes-hint", "p", tx_hint, panel, "One for every target leaf: this node × the target, derived and never declared - what a style or a palette answers, or lets fall back.");
        var line = this.mint(v, "classes-line", "div", tx_line, panel);
        classes.forEach(function (c, i) { self.mint(v, "c" + i, "span", tx_code, line, c); });
    }
}
