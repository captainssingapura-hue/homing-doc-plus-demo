// =============================================================================
// TreeApp — the relation tree as a page. As the grid page: 'tree' is handed
// whole to the tree, 'domain/cells' is the relation's own, and the page only
// wires the tree's ask to the relation's answer.
// =============================================================================

const _owner = Object.freeze({ toString: () => "treePage" });

class TreeWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        var el = branch.createElement("root", "div");

        var kicker = new Kicker(KickerFactory.create(branch, "kicker"), { text: "homing-rel-grid · rel-tree" });
        css.addClass(kicker.el, ga_kicker);
        el.appendChild(kicker.el);

        var title = new Heading1(Heading1Factory.create(branch, "title"), { text: "Tree" });
        css.addClass(title.el, ga_title);
        el.appendChild(title.el);

        var lede = new Lede(LedeFactory.create(branch, "lede"), { text: "The same twelve books as shelf → book in the relation tree. The shelves start closed; "
            + "→ or Space unfolds, ← folds, ↑↓ move, Enter activates. An unfold is a question on "
            + "the ask channel, answered by the relation with the whole view; the tree owns the rows, the "
            + "carets and the cursor, and the relation owns every cell and its fold state." });
        css.addClass(lede.el, ga_lede);
        el.appendChild(lede.el);

        var host = branch.createElement("host", "div");
        css.addClass(host, ga_host);
        el.appendChild(host);

        var status = branch.createElement("status", "div");
        css.addClass(status, ga_status);
        new Look(Object.freeze({ LEAF: "code-block" }), ROLE.STANDALONE).wear(status);   // the house's block of code: a console's lines
        el.appendChild(status);

        var treeB = branch.createBranch("tree");
        var domainB = branch.createBranch("domain");
        domainB.activate(_owner);
        var cellsB = domainB.createBranch("cells");

        var store = new BooksStore();
        var relation = new ShelfTreeRelation(store, { branch: cellsB });
        var activated = null;
        var tree = null;   // onArranged fires during construction, before this is assigned
        tree = new RelTree({
            container: host,
            branch: treeB,
            relation: relation,
            label: "Shelves",
            folder: true,
            ask: function (question, mask) { var out = relation.answer(question, mask); setTimeout(report, 0); return out; },
            onArranged: report,
            onCursorMoved: report,
            onActivated: function (key) { activated = key; report(); }
        });

        function report() {
            var c = tree ? tree.cursor() : null;
            status.textContent = relation.cellCount() + " cells minted"
                + (c === null ? "" : " · cursor at " + c)
                + (activated === null ? "" : " · activated " + activated);
        }
        report();
        this.root = el;
        this._tree = tree; this._relation = relation;
    }

    dispose() { if (this._tree) this._tree.destroy(); this._relation.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new TreeWidget(domOpsParty.createBranch("treePage"), params).root);
}
