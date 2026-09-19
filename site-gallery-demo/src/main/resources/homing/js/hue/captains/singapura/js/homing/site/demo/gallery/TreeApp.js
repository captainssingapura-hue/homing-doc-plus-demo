// =============================================================================
// TreeApp — the relation tree as a page. As the grid page: 'tree' is handed
// whole to the tree, 'domain/cells' is the relation's own, and the page only
// wires the tree's ask to the relation's answer.
// =============================================================================

const _owner = Object.freeze({ toString: () => "treePage" });

function appMain(el) {
    var branch = domOpsParty.createBranch("treePage");
    branch.activate(_owner);

    var kicker = branch.createElement("kicker", "div");
    css.addClass(kicker, ga_kicker);
    kicker.textContent = "homing-rel-grid · rel-tree";
    el.appendChild(kicker);

    var title = branch.createElement("title", "h1");
    css.addClass(title, ga_title);
    title.textContent = "Tree";
    el.appendChild(title);

    var lede = branch.createElement("lede", "p");
    css.addClass(lede, ga_lede);
    lede.textContent = "The same twelve books as shelf → book in the relation tree. The shelves start closed; "
        + "→ or Space unfolds, ← folds, ↑↓ move, Enter activates. An unfold is a question on "
        + "the ask channel, answered by the relation with the whole view; the tree owns the rows, the "
        + "carets and the cursor, and the relation owns every cell and its fold state.";
    el.appendChild(lede);

    var host = branch.createElement("host", "div");
    css.addClass(host, ga_host);
    el.appendChild(host);

    var status = branch.createElement("status", "div");
    css.addClass(status, ga_status);
    el.appendChild(status);

    var treeB = branch.createBranch("tree");
    var domainB = branch.createBranch("domain");
    domainB.activate(_owner);
    var cellsB = domainB.createBranch("cells");

    var store = createBooksStore();
    var relation = createShelfTreeRelation(store, { branch: cellsB });
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
}
