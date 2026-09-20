// =============================================================================
// GalleryRelations — the gallery's domain: a small book store, and the two
// relations the grid and the tree are given over it. DOMAIN CODE: the words
// "grid" and "tree" name components nowhere in it; a relation answers the
// component's questions and owns every cell it hands out.
//
//   new BooksStore()                              twelve books, in memory
//   new BooksRelation(store, { branch })          the grid's relation: one root view, four
//                                               columns, title and rating editable
//   new ShelfTreeRelation(store, { branch })      the tree's relation: shelf → book, with a
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

class BooksStore {
    constructor() {
        this._rows = new Map();
        this._order = [];
        this._listeners = new Set();
        var self = this;
        BOOKS.forEach(function (b) {
            self._rows.set(b[0], { title: b[1], author: b[2], year: b[3], rating: b[4], shelf: b[5] });
            self._order.push(b[0]);
        });
    }
    pks()     { return this._order.slice(); }
    columns() { return COLUMNS.slice(); }
    writableColumns() { return WRITABLE.slice(); }
    shelves() {
        var out = [], rows = this._rows;
        this._order.forEach(function (pk) { var s = rows.get(pk).shelf; if (out.indexOf(s) < 0) out.push(s); });
        return out;
    }
    onShelf(shelf) { var rows = this._rows; return this._order.filter(function (pk) { return rows.get(pk).shelf === shelf; }); }
    get(pk, col) { var r = this._rows.get(pk); return r ? r[col] : undefined; }
    commit(pk, col, v) {
        if (WRITABLE.indexOf(col) < 0 || !this._rows.has(pk)) return;
        if (col === "rating") { var n = Number(v); v = (v !== "" && isFinite(n)) ? Math.max(0, Math.min(5, Math.round(n))) : this._rows.get(pk).rating; }
        this._rows.get(pk)[col] = v;
        this._listeners.forEach(function (fn) { fn(pk, col, v); });
    }
    subscribe(fn) { var l = this._listeners; l.add(fn); return function () { l.delete(fn); }; }
}

// ── The grid's relation ───────────────────────────────────────────────────────

class BooksRelation {
    constructor(store, opts) {
        opts = opts || {};
        if (!opts.branch) throw new Error("[BooksRelation] opts.branch is required: the relation's own");
        var self = this;
        this._store = store;
        this._branch = opts.branch;
        this._cellSeq = 0;
        this._cells = new Map();
        this._branch.activate({ toString: function () { return "BooksRelation"; } });
        this._unsubscribe = store.subscribe(function (pk, col, v) {
            var c = self._cells.get(pk + " " + col);
            if (c) c.set(v);
        });
    }
    view(intent) { return intent ? null : this._store.pks(); }
    columns() { return this._store.columns(); }
    readOnlyColumns() {
        var store = this._store;
        return store.columns().filter(function (c) { return store.writableColumns().indexOf(c) < 0; });
    }
    cellFor(pk, col) {
        var store = this._store;
        if (store.pks().indexOf(pk) < 0) throw new Error("[BooksRelation] no such book: " + pk);
        var k = pk + " " + col, c = this._cells.get(k);
        if (!c) {
            var commit = store.writableColumns().indexOf(col) >= 0
                ? function (text) { store.commit(pk, col, text); }
                : undefined;
            c = new RelGridTextCell({ branch: this._branch.createBranch("c" + (++this._cellSeq)), value: store.get(pk, col), onCommit: commit });
            this._cells.set(k, c);
        }
        return c;
    }
    cellCount() { return this._cells.size; }
    dispose() {
        this._unsubscribe();
        this._cells.forEach(function (c) { c.dispose(); });
        this._cells.clear();
        this._branch.dissolve();
    }
}

// ── The tree's relation ───────────────────────────────────────────────────────

class ShelfTreeRelation {
    constructor(store, opts) {
        opts = opts || {};
        if (!opts.branch) throw new Error("[ShelfTreeRelation] opts.branch is required: the relation's own");
        var self = this;
        this._store = store;
        this._branch = opts.branch;
        this._seq = 0;
        this._cells = new Map();
        this._open = new Set();
        this._branch.activate({ toString: function () { return "ShelfTreeRelation"; } });
        this._unsubscribe = store.subscribe(function (pk) {
            var c = self._cells.get(pk);
            if (c) c.set(self._textOf(pk));
        });
    }
    static _shelfKey(s) { return "shelf:" + s; }
    _places() {
        var out = [], store = this._store, open = this._open;
        store.shelves().forEach(function (s) {
            var k = ShelfTreeRelation._shelfKey(s);
            out.push({ key: k, depth: 0, fold: open.has(k) ? "open" : "closed" });
            if (open.has(k)) store.onShelf(s).forEach(function (pk) { out.push({ key: pk, depth: 1, fold: "leaf" }); });
        });
        return out;
    }
    _textOf(key) {
        var store = this._store;
        if (key.slice(0, 6) === "shelf:") { var s = key.slice(6); return s + " — " + store.onShelf(s).length + " books"; }
        return store.get(key, "title") + " · " + store.get(key, "author") + " · " + store.get(key, "year");
    }
    _known(key) {
        var store = this._store;
        return key.slice(0, 6) === "shelf:" ? store.shelves().indexOf(key.slice(6)) >= 0 : store.get(key, "title") !== undefined;
    }
    view() { return this._places(); }
    cellFor(key) {
        if (!this._known(key)) throw new Error("[ShelfTreeRelation] no such node: " + key);
        var c = this._cells.get(key);
        if (!c) {
            c = new RelTreeTextCell({ branch: this._branch.createBranch("n" + (++this._seq)), text: this._textOf(key) });
            this._cells.set(key, c);
        }
        return c;
    }
    // The channel: an unfold or a fold flips the relation's own fold state
    // and answers the whole View at once; a notification is heard, not answered.
    answer(question) {
        if (question instanceof RelTreeUnfold) { this._open.add(question.key); return Promise.resolve(new RelTreeView(this._places())); }
        if (question instanceof RelTreeFold)   { this._open.delete(question.key); return Promise.resolve(new RelTreeView(this._places())); }
        return Promise.resolve();
    }
    openAll() { var open = this._open; this._store.shelves().forEach(function (s) { open.add(ShelfTreeRelation._shelfKey(s)); }); }
    isOpen(key) { return this._open.has(key); }
    cellCount() { return this._cells.size; }
    dispose() {
        this._unsubscribe();
        this._cells.forEach(function (c) { c.dispose(); });
        this._cells.clear();
        this._branch.dissolve();
    }
}
