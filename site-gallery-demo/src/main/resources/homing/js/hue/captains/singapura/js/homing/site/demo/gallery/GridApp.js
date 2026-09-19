// =============================================================================
// GridApp — the relation grid as a page. Two branches under the page's own:
// 'grid', handed whole to the grid, which activates it and mints everything
// it makes on it; 'domain', the page's, with 'cells' under it as the
// relation's own. The page wires nothing but the two together.
// =============================================================================

const _owner = Object.freeze({ toString: () => "gridPage" });

function construct(branch, params) {
    branch.activate(_owner);
    var el = branch.createElement("root", "div");

    var kicker = branch.createElement("kicker", "div");
    css.addClass(kicker, ga_kicker);
    kicker.textContent = "homing-rel-grid";
    el.appendChild(kicker);

    var title = branch.createElement("title", "h1");
    css.addClass(title, ga_title);
    title.textContent = "Grid";
    el.appendChild(title);

    var lede = branch.createElement("lede", "p");
    css.addClass(lede, ga_lede);
    lede.textContent = "Twelve books as a relation in the relation grid, from its own repo. Arrow keys move the cursor, "
        + "Enter edits a title or a rating (the year and the author are the relation's read-only columns), "
        + "and the grid wears the page's design because its classes wear words, not values.";
    el.appendChild(lede);

    var host = branch.createElement("host", "div");
    css.addClass(host, ga_host);
    el.appendChild(host);

    var status = branch.createElement("status", "div");
    css.addClass(status, ga_status);
    el.appendChild(status);

    var gridB = branch.createBranch("grid");
    var domainB = branch.createBranch("domain");
    domainB.activate(_owner);
    var cellsB = domainB.createBranch("cells");

    var store = createBooksStore();
    var relation = createBooksRelation(store, { branch: cellsB });
    var grid = null;   // onArranged fires during construction, before this is assigned
    grid = new RelGrid({
        container: host,
        branch: gridB,
        relation: relation,
        header: { show: true, sticky: true },
        label: "Books",
        onArranged: report,
        onCursorMoved: report
    });
    grid.setColumnWidths({ title: 260, author: 180, year: 70, rating: 70 });

    function report() {
        var c = grid ? grid.cursor() : null;
        status.textContent = store.pks().length + " rows · " + relation.cellCount() + " cells minted"
            + (c ? " · cursor at " + JSON.stringify(c) : "");
    }
    report();
    return { root: el, dispose: function () { if (grid) grid.destroy(); relation.dispose(); } };
}

function appMain(el, params) {
    el.appendChild(construct(domOpsParty.createBranch("gridPage"), params).root);
}
