// =============================================================================
// ContextMenusApp — the context menus, exercised. One steward for the page,
// with the gallery's kinds stamped from Java; three hypothetical cells, each
// asking for a menu of its kind on a right-click or Shift+F10, each with a
// peculiarity: the animal cell rotates, flips and changes animal through a
// second level; the swatch cell picks its colour through one and toggles
// its inverted surface, both checked; the counter cell adds a step, resets
// — disabled at nought — and picks its step, checked. Every open, pick and
// close is a line on the log, and so is what each pick did. Under the cells,
// a SPECIMEN of each kind: the whole tree open at once, every level beside
// its row, bound to its cell and rebound on every pick — the design of the
// menus on view without opening one.
// =============================================================================

const _owner = Object.freeze({ toString: () => "contextMenusPage" });
var _ANIMALS = { cat: "🐱", dog: "🐶", owl: "🦉", fox: "🦊" };
var _COLOURS = ["primary", "success", "warning", "danger"];

/** A cell: a focusable box with a face and a caption; a right-click or Shift+F10 asks the steward for its kind. */
class Cell {
    constructor(branch, name, kind, menus, say) {
        var self = this;
        branch.activate(_owner);
        this.kind = kind;
        this.say = say;
        var root = branch.createElement(name, "div");
        css.addClass(root, ga_cell);
        root.setAttribute("tabindex", "0");
        this.face = branch.createElement(name + "-face", "div");
        css.addClass(this.face, ga_cell_face);
        root.appendChild(this.face);
        this.caption = branch.createElement(name + "-caption", "div");
        css.addClass(this.caption, ga_cell_caption);
        root.appendChild(this.caption);
        root.addEventListener("contextmenu", function (e) {
            if (menus.open(kind, self, { x: e.clientX, y: e.clientY }, { anchor: root })) e.preventDefault();
        });
        root.addEventListener("keydown", function (e) {
            if (e.key === "ContextMenu" || (e.shiftKey && e.key === "F10")) {
                var r = root.getBoundingClientRect();
                if (menus.open(kind, self, { x: r.left + 24, y: r.top + 24 }, { keyboard: true, anchor: root })) e.preventDefault();
            }
        });
        this.root = root;
    }
}

class AnimalCell extends Cell {
    constructor(branch, menus, say) {
        super(branch, "animal", "animal", menus, say);
        this.animal = "cat"; this.turns = 0; this.flipped = false;
        this.draw();
    }
    draw() {
        this.face.textContent = _ANIMALS[this.animal];
        this.face.style.setProperty("--ga-rotate", (this.turns * 90) + "deg");
        this.face.style.setProperty("--ga-flip", this.flipped ? "-1" : "1");
        this.caption.textContent = this.animal + ", " + (this.turns * 90) + "°" + (this.flipped ? ", mirrored" : "");
    }
    state(itemId) { return _ANIMALS[itemId] ? { checked: itemId === this.animal } : null; }
    pick(itemId) {
        if (itemId === "rotate") this.turns = (this.turns + 1) % 4;
        else if (itemId === "flip") this.flipped = !this.flipped;
        else if (_ANIMALS[itemId]) this.animal = itemId;
        this.draw();
        this.say("animal    " + this.caption.textContent);
    }
}

class SwatchCell extends Cell {
    constructor(branch, menus, say) {
        super(branch, "swatch", "swatch", menus, say);
        this.colour = "primary"; this.inverted = false;
        this.draw();
    }
    draw() {
        var f = this.face;
        css.toggleClass(f, ga_swatch_primary, this.colour === "primary");
        css.toggleClass(f, ga_swatch_success, this.colour === "success");
        css.toggleClass(f, ga_swatch_warning, this.colour === "warning");
        css.toggleClass(f, ga_swatch_danger, this.colour === "danger");
        css.toggleClass(f, ga_swatch_inverted, this.inverted);
        this.caption.textContent = this.inverted ? "inverted" : this.colour;
    }
    state(itemId) {
        if (_COLOURS.indexOf(itemId) >= 0) return { checked: itemId === this.colour, disabled: this.inverted };
        if (itemId === "invert") return { checked: this.inverted };
        return null;
    }
    pick(itemId) {
        if (itemId === "invert") this.inverted = !this.inverted;
        else if (_COLOURS.indexOf(itemId) >= 0) this.colour = itemId;
        this.draw();
        this.say("swatch    " + this.caption.textContent);
    }
}

class CounterCell extends Cell {
    constructor(branch, menus, say) {
        super(branch, "counter", "counter", menus, say);
        this.value = 0; this.step = 1;
        this.draw();
    }
    draw() {
        this.face.textContent = String(this.value);
        this.caption.textContent = "step " + this.step;
    }
    state(itemId) {
        if (itemId === "reset") return { disabled: this.value === 0 };
        if (itemId.charAt(0) === "s") return { checked: Number(itemId.slice(1)) === this.step };
        return null;
    }
    pick(itemId) {
        if (itemId === "add") this.value += this.step;
        else if (itemId === "reset") this.value = 0;
        else if (itemId.charAt(0) === "s") this.step = Number(itemId.slice(1));
        this.draw();
        this.say("counter   " + this.value + ", step " + this.step);
    }
}

class ContextMenusWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-ui-menu";
        el.appendChild(kicker);
        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Context menus";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "Right-click a cell, or focus it and press Shift+F10. One steward holds the page's menus, declared once in "
            + "Java; a cell asks for its kind and is bound to the menu while it is open. Arrows move, Right opens a second level, "
            + "Enter picks, Escape closes; a press outside closes and is swallowed. Each kind is a typed tree in Java, a class per row, "
            + "its marks icon words the design draws; under the cells, each kind as a specimen with every level open.";
        el.appendChild(lede);

        var cells = branch.createElement("cells", "div");
        css.addClass(cells, ga_menu_cells);
        el.appendChild(cells);

        var specimens = branch.createElement("specimens", "div");
        css.addClass(specimens, ga_specimens);
        el.appendChild(specimens);

        var log = branch.createElement("log", "div");
        css.addClass(log, ga_log);
        log.setAttribute("aria-live", "polite");
        el.appendChild(log);
        var lines = 0;
        function say(line) {
            lines++;
            log.textContent += (lines > 1 ? "\n" : "") + lines + "  " + line;
            log.scrollTop = log.scrollHeight;
        }

        this._menus = new ContextMenuSteward(branch.createBranch("menus"), { types: MENUS, onEvent: function (ev) {
            switch (ev.kind) {
                case "Opened": say("Opened    " + ev.menuKind + "  at " + Math.round(ev.x) + "," + Math.round(ev.y)); break;
                case "Picked": say("Picked    " + ev.menuKind + " / " + ev.itemId); break;
                case "Closed": say("Closed    " + ev.menuKind + "  " + ev.reason); break;
                default:       say(ev.kind);
            }
        } });
        var menus = this._menus;
        // the handlers: each kind's pick and state are the bound cell's own
        ["animal", "swatch", "counter"].forEach(function (kind) {
            menus.handle(kind, { pick: function (id, cell) { cell.pick(id); }, state: function (id, cell) { return cell.state(id); } });
        });
        this._cells = [new AnimalCell(branch.createBranch("animal"), menus, say), new SwatchCell(branch.createBranch("swatch"), menus, say), new CounterCell(branch.createBranch("counter"), menus, say)];
        this._cells.forEach(function (c) { cells.appendChild(c.root); });
        // the specimens: each kind's tree, open, bound to its cell; rebound on a pick so the checks follow
        this._specimens = this._cells.map(function (c) {
            var box = branch.createElement("specimen-" + c.kind, "div");
            var name = branch.createElement("specimen-" + c.kind + "-name", "div");
            css.addClass(name, ga_specimen_name);
            name.textContent = c.kind + " — every level open";
            box.appendChild(name);
            var host = branch.createElement("specimen-" + c.kind + "-host", "div");
            box.appendChild(host);
            specimens.appendChild(box);
            return { cell: c, menu: menus.specimen(c.kind, host, c) };
        });
        var specimensOf = this._specimens;
        this._cells.forEach(function (c) {
            var pick = c.pick;
            c.pick = function (id) { pick.call(c, id); specimensOf.forEach(function (s) { if (s.cell === c) s.menu.bind(c, function (i) { return c.state(i); }); }); };
        });
        say("three cells; right-click one");
        this.root = el;
    }

    dispose() { this._menus.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new ContextMenusWidget(domOpsParty.createBranch("contextMenusPage"), params).root);
}
