// =============================================================================
// GalleryRelations — the gallery's domain: a small book store, and the two
// relations the grid and the tree are given over it. DOMAIN CODE: the words
// "grid" and "tree" name components nowhere in it; a relation answers the
// component's questions and owns every cell it hands out.
//
//   createBooksStore()                          twelve books, in memory
//   createBooksRelation(store, { branch })      the grid's relation: one root view, four
//                                               columns, title and rating editable
//   createShelfTreeRelation(store, { branch })  the tree's relation: shelf → book, with a
//                                               fold state of its own, answered at once
//
// Both relations take the branch that is THEIR OWN — unactivated when handed;
// they activate it, mint every cell on a sub-branch of it, and dispose()
// dissolves it. That is the seam the grid family draws: the component's
// branch is the component's, the domain's is the domain's, and a cell is a
// noun the domain owns and the component only places.
// =============================================================================

var BOOKS = [
    ["middlemarch",  "Middlemarch",                 "George Eliot",      1871, 5, "Fiction"],
    ["solaris",      "Solaris",                     "Stanislaw Lem",     1961, 4, "Fiction"],
    ["left-hand",    "The Left Hand of Darkness",   "Ursula K. Le Guin", 1969, 5, "Fiction"],
    ["hard-times",   "Hard Times",                  "Charles Dickens",   1854, 3, "Fiction"],
    ["origin",       "On the Origin of Species",    "Charles Darwin",    1859, 5, "Science"],
    ["qed",          "QED",                         "Richard Feynman",   1985, 4, "Science"],
    ["cosmos",       "Cosmos",                      "Carl Sagan",        1980, 4, "Science"],
    ["silent-spring","Silent Spring",               "Rachel Carson",     1962, 4, "Science"],
    ["peloponnesian","The Peloponnesian War",       "Thucydides",        -411, 4, "History"],
    ["guns-germs",   "Guns, Germs, and Steel",      "Jared Diamond",     1997, 3, "History"],
    ["sapiens",      "Sapiens",                     "Yuval Noah Harari", 2011, 3, "History"],
    ["roman-empire", "The Decline and Fall",        "Edward Gibbon",     1776, 4, "History"]
];
var COLUMNS  = ["title", "author", "year", "rating"];
var WRITABLE = ["title", "rating"];

function createBooksStore() {
    var rows = new Map(), order = [], listeners = new Set();
    BOOKS.forEach(function (b) {
        rows.set(b[0], { title: b[1], author: b[2], year: b[3], rating: b[4], shelf: b[5] });
        order.push(b[0]);
    });
    return {
        pks:     function () { return order.slice(); },
        columns: function () { return COLUMNS.slice(); },
        writableColumns: function () { return WRITABLE.slice(); },
        shelves: function () {
            var out = [];
            order.forEach(function (pk) { var s = rows.get(pk).shelf; if (out.indexOf(s) < 0) out.push(s); });
            return out;
        },
        onShelf: function (shelf) { return order.filter(function (pk) { return rows.get(pk).shelf === shelf; }); },
        get:     function (pk, col) { var r = rows.get(pk); return r ? r[col] : undefined; },
        commit:  function (pk, col, v) {
            if (WRITABLE.indexOf(col) < 0 || !rows.has(pk)) return;
            if (col === "rating") { var n = Number(v); v = (v !== "" && isFinite(n)) ? Math.max(0, Math.min(5, Math.round(n))) : rows.get(pk).rating; }
            rows.get(pk)[col] = v;
            listeners.forEach(function (fn) { fn(pk, col, v); });
        },
        subscribe: function (fn) { listeners.add(fn); return function () { listeners.delete(fn); }; }
    };
}

// ── The grid's relation ───────────────────────────────────────────────────────

function createBooksRelation(store, opts) {
    opts = opts || {};
    if (!opts.branch) throw new Error("[BooksRelation] opts.branch is required: the relation's own");
    var branch = opts.branch, cellSeq = 0, cells = new Map();
    branch.activate({ toString: function () { return "BooksRelation"; } });
    var unsubscribe = store.subscribe(function (pk, col, v) {
        var c = cells.get(pk + " " + col);
        if (c) c.set(v);
    });
    return {
        view:    function (intent) { return intent ? null : store.pks(); },
        columns: function () { return store.columns(); },
        readOnlyColumns: function () {
            return store.columns().filter(function (c) { return store.writableColumns().indexOf(c) < 0; });
        },
        cellFor: function (pk, col) {
            if (store.pks().indexOf(pk) < 0) throw new Error("[BooksRelation] no such book: " + pk);
            var k = pk + " " + col, c = cells.get(k);
            if (!c) {
                var commit = store.writableColumns().indexOf(col) >= 0
                    ? function (text) { store.commit(pk, col, text); }
                    : undefined;
                c = new RelGridTextCell({ branch: branch.createBranch("c" + (++cellSeq)), value: store.get(pk, col), onCommit: commit });
                cells.set(k, c);
            }
            return c;
        },
        cellCount: function () { return cells.size; },
        dispose: function () {
            unsubscribe();
            cells.forEach(function (c) { c.dispose(); });
            cells.clear();
            branch.dissolve();
        }
    };
}

// ── The tree's relation ───────────────────────────────────────────────────────

function createShelfTreeRelation(store, opts) {
    opts = opts || {};
    if (!opts.branch) throw new Error("[ShelfTreeRelation] opts.branch is required: the relation's own");
    var branch = opts.branch, seq = 0, cells = new Map(), open = new Set();
    branch.activate({ toString: function () { return "ShelfTreeRelation"; } });
    function shelfKey(s) { return "shelf:" + s; }
    function places() {
        var out = [];
        store.shelves().forEach(function (s) {
            var k = shelfKey(s);
            out.push({ key: k, depth: 0, fold: open.has(k) ? "open" : "closed" });
            if (open.has(k)) store.onShelf(s).forEach(function (pk) { out.push({ key: pk, depth: 1, fold: "leaf" }); });
        });
        return out;
    }
    function textOf(key) {
        if (key.slice(0, 6) === "shelf:") { var s = key.slice(6); return s + " — " + store.onShelf(s).length + " books"; }
        return store.get(key, "title") + " · " + store.get(key, "author") + " · " + store.get(key, "year");
    }
    function known(key) {
        return key.slice(0, 6) === "shelf:" ? store.shelves().indexOf(key.slice(6)) >= 0 : store.get(key, "title") !== undefined;
    }
    var unsubscribe = store.subscribe(function (pk) {
        var c = cells.get(pk);
        if (c) c.set(textOf(pk));
    });
    return {
        view: function () { return places(); },
        cellFor: function (key) {
            if (!known(key)) throw new Error("[ShelfTreeRelation] no such node: " + key);
            var c = cells.get(key);
            if (!c) {
                c = new RelTreeTextCell({ branch: branch.createBranch("n" + (++seq)), text: textOf(key) });
                cells.set(key, c);
            }
            return c;
        },
        // The channel: an unfold or a fold flips the relation's own fold state
        // and answers the whole View at once; a notification is heard, not answered.
        answer: function (question) {
            if (question instanceof RelTreeUnfold) { open.add(question.key); return Promise.resolve(new RelTreeView(places())); }
            if (question instanceof RelTreeFold)   { open.delete(question.key); return Promise.resolve(new RelTreeView(places())); }
            return Promise.resolve();
        },
        openAll:  function () { store.shelves().forEach(function (s) { open.add(shelfKey(s)); }); },
        isOpen:   function (key) { return open.has(key); },
        cellCount: function () { return cells.size; },
        dispose: function () {
            unsubscribe();
            cells.forEach(function (c) { c.dispose(); });
            cells.clear();
            branch.dissolve();
        }
    };
}
