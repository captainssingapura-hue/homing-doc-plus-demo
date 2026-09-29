// =============================================================================
// AnimalSelector — which animal the widgets of a workspace show: the six
// animals, each its picture over its name, one of them chosen. What it shows
// chosen is what the animal choice party says; a press asks the party to
// choose, and every widget that meets there follows — this one too, by the
// same word. On joining it asks what is chosen already, so it is right however
// late it joins; with none chosen yet it marks none, and each widget keeps its
// own. Not joined, it chooses for itself alone.
//
// A self-contained widget (the Workspace & Widgets doctrines): made with the
// container its page lends it and its params, and nothing else; its DomOps and
// focus parties its own, offered as roots for its host to graft; the party it
// needs declared by type, and joined after it is made (Messaging Parties Are
// Joined Top-Down).
//
// THE KEYS. The animals are a listbox with MANUAL choosing: arrows move the
// focus among them, Enter or Space chooses. The animals are native buttons,
// the native world's; the widget is a logical member of the keyboard's party.
// Holding the keys with nothing natively focused, an arrow takes you into the
// list, Enter or Space chooses the one the list is on, Escape gives the keys
// back.
//
//   new AnimalSelector(container, params)   params: none
//   selector.root   selector.roots { dom, focus }   selector.focus
//   (its kind declares, in Java, the type it joins: animal-choice)
//   selector.join(given)   given: { [type name]: party }; a second join without a leave is refused
//   selector.leave()
//   selector.chosen() → the animal's id it shows chosen, or null
//   selector.choose(id)    asked of the party - or, not joined, marked here
//   selector.activate()   selector.keyDown(ev)   the keyboard's member
//   selector.dispose()
// =============================================================================

const _selectorOwner = Object.freeze({ toString: () => "animalSelector" });
var _selectors = 0;

class AnimalSelector {
    constructor(container, params) {
        if (!container || typeof container.appendChild !== "function") throw new Error("[AnimalSelector] a container is required: the one its page lends it");
        var name = "animalSelector-" + (++_selectors), self = this, d;
        this._dom = d = domOpsParties.mobile(name);
        d.activate(_selectorOwner);
        var root = d.createElement("root", "div");
        css.addClass(root, wg_fill);
        css.addClass(root, an_root);
        root.setAttribute("role", "region");
        root.setAttribute("aria-label", "Animal");
        AnimalSelector._el(d, "title", "h2", an_title, root).textContent = "Animal";
        this._note = AnimalSelector._el(d, "note", "p", an_note, root);
        var list = AnimalSelector._el(d, "list", "div", an_list, root);
        list.setAttribute("role", "listbox");
        list.setAttribute("aria-label", "Animals");
        this._options = Animals.ALL.map(function (a, i) { return self._option(list, a, i); });
        list.addEventListener("keydown", function (ev) { if (self._listKey(ev.key)) ev.preventDefault(); });
        container.appendChild(root);
        this.root = root;
        this._focusParty = focusParties.mobile(name);
        this.focus = this._focusParty.root.join("animalSelector", this);
        this._off = Keys.claimOn(root, this.focus);
        this.roots = Object.freeze({ dom: d, focus: this._focusParty });
        this._chosen = null;
        this._focused = 0;
        this._member = null;
        this._joined = false;
        this._mark(null);
    }

    join(given) {
        if (this._joined) throw new Error("[AnimalSelector] joined already: leave first");
        this._joined = true;
        var party = given && given[ANIMAL_CHOICE.name], self = this;
        if (!party) return;
        this._member = party.join("animalSelector", { Chosen: function (m) { self._mark(m.animal); } });
        this._member.tell({ kind: "CurrentRequested" });
    }

    leave() {
        if (this._member) { this._member.leave(); this._member = null; }
        this._joined = false;
    }

    chosen() { return this._chosen; }

    choose(id) {
        if (!Animals.byId(id)) return;
        if (this._member) this._member.tell({ kind: "Choose", animal: id });
        else this._mark(id);
    }

    activate() { Keys.claim(this.focus); }

    /** Holding the keys, nothing natively focused: an arrow goes into the list, Enter or Space chooses, Escape gives the keys back. */
    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        return this._listKey(ev.key);
    }

    /** A key on the list: arrows, Home and End browse, Enter and Space choose. Escape and Tab are left to go on. */
    _listKey(key) {
        var n = this._options.length;
        if (key === "ArrowRight" || key === "ArrowDown") this._move((this._focused + 1) % n);
        else if (key === "ArrowLeft" || key === "ArrowUp") this._move((this._focused + n - 1) % n);
        else if (key === "Home") this._move(0);
        else if (key === "End") this._move(n - 1);
        else if (key === "Enter" || key === " ") this.choose(Animals.ALL[this._focused].id);
        else return false;
        return true;
    }

    _move(i) { this._focused = i; this._paint(); this._options[i].focus(); }

    /** What is chosen, marked - an id this selector does not know leaves the mark as it was. */
    _mark(id) {
        if (id !== null && !Animals.byId(id)) return;
        this._chosen = id;
        var a = id === null ? null : Animals.byId(id);
        this._note.textContent = a ? a.name + " - for every widget here" : "None chosen - each widget shows its own";
        if (a) this._focused = Animals.ALL.indexOf(a);
        this._paint();
    }

    /** Roving tabindex: one stop for the list. The mark follows the choice, the stop the focus - apart while you browse. */
    _paint() {
        for (var i = 0; i < this._options.length; i++) {
            this._options[i].setAttribute("aria-selected", Animals.ALL[i].id === this._chosen ? "true" : "false");
            this._options[i].setAttribute("tabindex", i === this._focused ? "0" : "-1");
        }
    }

    _option(list, animal, i) {
        var self = this, b = this._dom.createElement("animal" + i, "button");
        b.type = "button";
        b.setAttribute("role", "option");
        b.setAttribute("aria-label", animal.name);
        css.addClass(b, an_option);
        var picture = AnimalSelector._el(this._dom, "picture" + i, "span", an_picture, b);
        picture.style.setProperty("--an-art", Animals.art(animal.id));
        AnimalSelector._el(this._dom, "name" + i, "span", an_name, b).textContent = animal.name;
        // The pointer's way to choose; the keys' is the list's own, so one choice comes from one place.
        b.addEventListener("click", function () { self._focused = i; self.choose(animal.id); });
        list.appendChild(b);
        return b;
    }

    static _el(d, name, tag, cls, parent) {
        var el = d.createElement(name, tag);
        css.addClass(el, cls);
        parent.appendChild(el);
        return el;
    }

    dispose() {
        this.leave();
        if (this._off) { this._off(); this._off = null; }
        this._focusParty.dissolve();
        this._dom.dissolve();
    }
}
