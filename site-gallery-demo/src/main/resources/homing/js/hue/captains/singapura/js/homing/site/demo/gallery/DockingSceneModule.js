// =============================================================================
// DockingScene — what the docking page's tabs hold: the books as a relation
// grid, the shelves as a relation tree, and a picture. Each is a tab's widget
// by the law (§15.1) — a member of the branch it is handed in params.focus,
// answering activate(), its Escape yielding — and each fills the tab it is
// shown in, since a dock gives its widget the whole room.
//
//   new BooksTab(branch, { focus, store, domain })      the relation grid
//   new ShelvesTab(branch, { focus, store, domain })    the relation tree
//   new PictureTab(branch, { focus, title? })           a picture, zoomed by keys
//   new RegionList(branch, host, regions, at, onPick)   the switcher's rows
//     .root .focus .activate() .keyDown(ev) .granted() .taken() .dispose()
//
// The two relation widgets are the NATIVE world inside a logical member: the
// host takes the browser's focus and the keys are the grid's or the tree's
// own. The seam is wired here, by the widget, as a panel wires its controls:
// an Escape the host did not want blurs it — the widget, the member, has the
// keys again — and the next Escape is the widget's own and yields. While the
// host has the focus the widget says its keys are lent, so a design marks it
// as it marks any member holding them for something inside it.
// =============================================================================

const _sceneOwner = Object.freeze({ toString: () => "dockingScene" });

/** The law's three things, done once: a member of the dock's branch, a press claiming it, and the leaving. */
function _join(w, branch, params) { w.focus = params.focus.join(branch.name, w); w._off = Keys.claimOn(w.root, w.focus); }
function _leave(w) { if (w._off) w._off(); if (w.focus && w.focus.in) w.focus.leave(); }

/** Held, or lent while something of its own has the browser's focus: one attribute, which the design answers and a panel follows. */
function _mark(w) {
    var a = typeof document === "undefined" ? null : document.activeElement;
    w.root.setAttribute("data-keys", a && a !== document.body && w.root.contains(a) ? "lent" : "held");
}

/** A tab holding a relation widget: the box, the seam, and the member's answers. What is mounted is the subclass's. */
class RelTab {
    constructor(branch, params) {
        branch.activate(_sceneOwner);
        var self = this;
        var root = branch.createElement("root", "div");
        css.addClass(root, ga_tab_fill);
        var box = branch.createElement("box", "div");
        css.addClass(box, ga_tab_host);
        root.appendChild(box);
        this.root = root;
        this.box = box;
        this._widget = null;
        // the seam: an Escape the host let through blurs it — the widget, the holder since the press, has the keys again
        root.addEventListener("keydown", function (ev) {
            if (ev.key !== "Escape" || !box.contains(ev.target) || ev.target === root) return;
            ev.target.blur();
            ev.preventDefault();
            ev.stopPropagation();
            _mark(self);
        });
        root.addEventListener("focusin", function () { if (self.root.getAttribute("data-keys")) _mark(self); });
        root.addEventListener("focusout", function () { setTimeout(function () { if (self.root.getAttribute("data-keys")) _mark(self); }, 0); });
    }
    /** Told to activate by the pane: the keys are claimed, and the host takes the focus so its own keys work at once. */
    activate() { Keys.claim(this.focus); }
    granted() { _mark(this); if (this._widget && this._widget.focus) { try { this._widget.focus(); } catch (e) {} } }
    taken() { this.root.removeAttribute("data-keys"); }
    offered() { if (this.root.getAttribute("data-keys") === null) this.root.setAttribute("data-keys", "candidate"); }
    withdrawn() { if (this.root.getAttribute("data-keys") === "candidate") this.root.removeAttribute("data-keys"); }
    /** Nothing is natively focused and the keys are the widget's: Escape gives them back to the dock. */
    keyDown(ev) { if (ev.key === "Escape") { Keys.yield(this.focus); return true; } return false; }
    dispose() {
        _leave(this);
        if (this._widget && this._widget.destroy) { try { this._widget.destroy(); } catch (e) {} }
        if (this._relation && this._relation.dispose) { try { this._relation.dispose(); } catch (e) {} }
    }
}

/** The books, as the relation grid: titles and ratings edit, the arrows walk the cells. */
class BooksTab extends RelTab {
    constructor(branch, params) {
        super(branch, params);
        this._relation = new BooksRelation(params.store, { branch: params.domain.createBranch("cells-" + branch.name) });
        this._widget = new RelGrid({ container: this.box, branch: branch.createBranch("grid"), relation: this._relation,
                                     header: { show: true, sticky: true }, label: "Books" });
        this._widget.setColumnWidths({ title: 170, author: 120, year: 56, rating: 56 });
        _join(this, branch, params);
    }
}

/** The same books as shelf → book, in the relation tree: an unfold is a question the relation answers. */
class ShelvesTab extends RelTab {
    constructor(branch, params) {
        super(branch, params);
        var relation = new ShelfTreeRelation(params.store, { branch: params.domain.createBranch("cells-" + branch.name) });
        this._relation = relation;
        this._widget = new RelTree({ container: this.box, branch: branch.createBranch("tree"), relation: relation, label: "Shelves",
                                     folder: true, ask: function (question, mask) { return relation.answer(question, mask); } });
        _join(this, branch, params);
    }
}

/** A picture, and the room it is given: it fits the tab, and the keys zoom it — a tab that is not text. */
class PictureTab {
    constructor(branch, params) {
        branch.activate(_sceneOwner);
        var p = params || {};
        this._zoom = 1;
        var root = branch.createElement("root", "div");
        css.addClass(root, ga_tab_fill);
        var box = branch.createElement("box", "div");
        css.addClass(box, ga_picture);
        // the plate is drawn with the design's words - a sky, a sun, two hills, the ground - so it is themed and carries no colour of its own
        var plate = branch.createElement("plate", "div");
        css.addClass(plate, ga_plate);
        plate.setAttribute("role", "img");
        plate.setAttribute("aria-label", p.title || "A plate: a sun over two hills");
        var parts = [ga_plate_sun, ga_plate_far, ga_plate_near, ga_plate_ground];
        ["sun", "far", "near", "ground"].forEach(function (part, i) {
            var e = branch.createElement(part, "div");
            css.addClass(e, parts[i]);
            plate.appendChild(e);
        });
        box.appendChild(plate);
        root.appendChild(box);
        var note = branch.createElement("note", "p");
        css.addClass(note, ga_picture_note);
        root.appendChild(note);
        this.root = root;
        this._plate = plate;
        this._note = note;
        this._title = p.title || "A plate";
        this._say();
        _join(this, branch, params);
    }
    _say() { this._note.textContent = this._title + " — " + Math.round(this._zoom * 100) + "%  (+ − 0 to zoom, Escape to let go)"; }
    zoom(z) {
        this._zoom = Math.max(0.25, Math.min(4, z));
        this._plate.style.setProperty("--ga-zoom", String(this._zoom));
        this._say();
        return this;
    }
    activate() { Keys.claim(this.focus); }
    granted() { this.root.setAttribute("data-keys", "held"); }
    taken() { this.root.removeAttribute("data-keys"); }
    offered() { if (this.root.getAttribute("data-keys") === null) this.root.setAttribute("data-keys", "candidate"); }
    withdrawn() { if (this.root.getAttribute("data-keys") === "candidate") this.root.removeAttribute("data-keys"); }
    /** The keys, while the picture holds them: the zoom, and Escape back to the dock. */
    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        if (ev.key === "+" || ev.key === "=") { this.zoom(this._zoom * 1.25); return true; }
        if (ev.key === "-") { this.zoom(this._zoom / 1.25); return true; }
        if (ev.key === "0") { this.zoom(1); return true; }
        return false;
    }
    dispose() { _leave(this); }
}

/**
 * The switcher's list: one row per region, the cursor on one of them. The
 * dialog around it is the page's; this is what it shows. Arrows walk the
 * rows, Enter picks, and the dialog's own Escape cancels. The rows are
 * options to the design, so the cursor is drawn as a chosen row is drawn.
 */
class RegionList {
    constructor(branch, host, regions, at, onPick) {
        branch.activate(_sceneOwner);
        var self = this;
        this._rows = [];
        this._at = Math.max(0, Math.min(regions.length - 1, at | 0));
        this._onPick = onPick;
        var list = branch.createElement("list", "div");
        css.addClass(list, ga_switch_list);
        list.setAttribute("role", "listbox");
        list.setAttribute("aria-label", "The regions");
        regions.forEach(function (r, i) {
            var row = branch.createElement("row-" + i, "div");
            css.addClass(row, ga_switch_row);
            row.setAttribute("role", "option");
            row.textContent = r.label;
            var hint = branch.createElement("hint-" + i, "span");
            css.addClass(hint, ga_switch_hint);
            hint.textContent = r.hint;
            row.appendChild(hint);
            row.addEventListener("click", function () { self._at = i; self.pick(); });
            list.appendChild(row);
            self._rows.push(row);
        });
        host.appendChild(list);
        this.root = list;
        this._draw();
    }
    _draw() {
        for (var i = 0; i < this._rows.length; i++) {
            var on = i === this._at;
            css.toggleClass(this._rows[i], ga_switch_row_at, on);
            this._rows[i].setAttribute("aria-selected", on ? "true" : "false");
        }
    }
    /** The keys the dialog hands on: the arrows walk the rows, Enter picks the one the cursor is on. */
    keyDown(ev) {
        if (ev.key === "ArrowDown" || ev.key === "ArrowUp") {
            var n = this._rows.length;
            this._at = (this._at + (ev.key === "ArrowDown" ? 1 : -1) + n) % n;
            this._draw();
            return true;
        }
        if (ev.key === "Enter") { this.pick(); return true; }
        return false;
    }
    pick() { var at = this._at; if (typeof this._onPick === "function") this._onPick(at); }
}

/** A note in a tab: a paragraph that says what to try, for the one that floats. */
class NoteTab {
    constructor(branch, params) {
        branch.activate(_sceneOwner);
        var root = branch.createElement("note", "p");
        css.addClass(root, ga_lede);
        root.textContent = (params && params.text) || "";
        this.root = root;
        _join(this, branch, params);
    }
    activate() { Keys.claim(this.focus); }
    granted() { this.root.setAttribute("data-keys", "held"); }
    taken() { this.root.removeAttribute("data-keys"); }
    offered() { if (this.root.getAttribute("data-keys") === null) this.root.setAttribute("data-keys", "candidate"); }
    withdrawn() { if (this.root.getAttribute("data-keys") === "candidate") this.root.removeAttribute("data-keys"); }
    keyDown(ev) { if (ev.key === "Escape") { Keys.yield(this.focus); return true; } return false; }
    dispose() { _leave(this); }
}
