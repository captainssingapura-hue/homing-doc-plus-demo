// =============================================================================
// GridApp — the relation grid as a page. Two branches under the page's own:
// 'grid', handed whole to the grid, which activates it and mints everything
// it makes on it; 'domain', the page's, with 'cells' under it as the
// relation's own. The page wires nothing but the two together.
// =============================================================================

const _owner = Object.freeze({ toString: () => "gridPage" });

class GridWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        var el = branch.createElement("root", "div");

        var kicker = new Kicker(KickerFactory.create(branch, "kicker"), { text: "homing-rel-grid" });
        css.addClass(kicker.el, ga_kicker);
        el.appendChild(kicker.el);

        var title = new Heading1(Heading1Factory.create(branch, "title"), { text: "Grid" });
        css.addClass(title.el, ga_title);
        el.appendChild(title.el);

        var lede = new Lede(LedeFactory.create(branch, "lede"), { text: "Twelve books as a relation in the relation grid, from its own repo. Arrow keys move the cursor, "
            + "Enter edits a title or a rating (the year and the author are the relation's read-only columns), "
            + "and the grid wears the page's design because its classes wear words, not values." });
        css.addClass(lede.el, ga_lede);
        el.appendChild(lede.el);

        var host = branch.createElement("host", "div");
        css.addClass(host, ga_host);
        el.appendChild(host);

        var status = branch.createElement("status", "div");
        css.addClass(status, ga_status);
        new Look(Object.freeze({ LEAF: "code-block" }), ROLE.STANDALONE).wear(status);   // the house's block of code: a console's lines
        el.appendChild(status);

        var gridB = branch.createBranch("grid");
        var domainB = branch.createBranch("domain");
        domainB.activate(_owner);
        var cellsB = domainB.createBranch("cells");

        var store = new BooksStore();
        var relation = new BooksRelation(store, { branch: cellsB });
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
        this.root = el;
        this._grid = grid; this._relation = relation;
    }

    dispose() { if (this._grid) this._grid.destroy(); this._relation.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new GridWidget(domOpsParty.createBranch("gridPage"), params).root);
}
