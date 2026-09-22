// =============================================================================
// FocusScene — the scene's components for the focus page, in two worlds. The
// LOGICAL world: Leaf and Panel are members of the focus party — never
// natively focused, their ring drawn from granted, their keys from the
// steward while nothing on the page is natively focused; a press claims for
// the innermost member under it; a panel holds a branch and answers
// wouldHold. The NATIVE world: NativeLeaf, and the controls a panel puts
// inside itself — a text field wired to let go on Escape, a button wired to
// nothing, a checkbox inside a logical leaf — are natively focusable, the
// browser's focus and ring, their own keys on their own element; while one
// is focused the steward is dormant, whoever holds. ListPanel is a container
// of the logical world whose contents are the native world, wired by a
// listener on its own root. The framework wires none of it. No component
// here knows the steward: they join a branch and call Keys.
// =============================================================================

const _owner = Object.freeze({ toString: () => "focusScene" });

/** A leaf of the logical world: a member; a press claims, the arrows count while it holds, Escape or its button yields; never natively focused. */
class Leaf {
    constructor(branch, host, focusBranch, name, onHold) {
        branch.activate(_owner);
        var self = this;
        var root = branch.createElement("leaf", "div");
        css.addClass(root, ga_leaf);
        root.setAttribute("role", "group");
        root.setAttribute("aria-label", name);
        var label = branch.createElement("label", "span");
        label.textContent = name;
        var count = branch.createElement("count", "span");
        css.addClass(count, ga_leaf_count);
        count.textContent = "0";
        var yieldBtn = branch.createElement("yield", "button");
        yieldBtn.type = "button";
        yieldBtn.tabIndex = -1;
        css.addClass(yieldBtn, ga_leaf_yield);
        yieldBtn.textContent = "yield";
        yieldBtn.setAttribute("aria-label", "yield the keys");
        root.appendChild(label);
        root.appendChild(count);
        root.appendChild(yieldBtn);
        host.appendChild(root);
        this.root = root; this._count = count; this._n = 0; this._onHold = onHold || null;
        this.focus = focusBranch.join(name, this);
        this._off = Keys.claimOn(root, this.focus);
        yieldBtn.addEventListener("mousedown", function (ev) { ev.preventDefault(); });   // the press claimed by the convention; the button takes no focus of its own
        yieldBtn.addEventListener("click", function () { Keys.yield(self.focus); });
    }
    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        if (ev.key === "ArrowUp") { this._n++; } else if (ev.key === "ArrowDown") { this._n--; } else return false;
        this._count.textContent = String(this._n);
        return true;
    }
    /** Told to activate by its container: what a press on it does — a claim. */
    activate() { Keys.claim(this.focus); }
    /** A native checkbox inside this logical leaf: a press on it claims the leaf (the innermost member) and focuses the box; Space toggles it natively. */
    check(branch, label) {
        branch.activate(_owner);
        var box = branch.createElement("check", "input");
        box.type = "checkbox";
        box.setAttribute("aria-label", label);
        this.root.insertBefore(box, this._count);
        return box;
    }
    reset() { this._n = 0; this._count.textContent = "0"; }
    granted() { css.addClass(this.root, ga_holds); if (this._onHold) this._onHold(this); }
    taken() { css.removeClass(this.root, ga_holds); }
    dispose() { this._off(); this.focus.leave(); }
}

/** A leaf of the native world: natively focusable, the browser's ring, its own arrows on its own element; not a member. Escape it lets through to whoever contains it. */
class NativeLeaf {
    constructor(branch, host, name) {
        branch.activate(_owner);
        var self = this;
        var root = branch.createElement("leaf", "div");
        css.addClass(root, ga_leaf, ga_leaf_native);
        root.setAttribute("tabindex", "0");
        root.setAttribute("role", "button");
        root.setAttribute("aria-label", name);
        var label = branch.createElement("label", "span");
        label.textContent = name;
        var count = branch.createElement("count", "span");
        css.addClass(count, ga_leaf_count);
        count.textContent = "0";
        root.appendChild(label);
        root.appendChild(count);
        host.appendChild(root);
        this.root = root; this.name = name; this._count = count; this._n = 0;
        root.addEventListener("keydown", function (ev) {
            if (ev.key === "ArrowUp") { self._n++; } else if (ev.key === "ArrowDown") { self._n--; } else return;
            self._count.textContent = String(self._n);
            ev.preventDefault();
            ev.stopPropagation();
        });
    }
    /** Told to activate by its container: what a press on it does — the native focus taken. */
    activate() { try { this.root.focus({ preventScroll: true }); } catch (e) {} }
    reset() { this._n = 0; this._count.textContent = "0"; }
    dispose() {}
}

/** A panel: a container that holds a branch; a press on its header claims for the panel; a yield from below it catches or lets pass, as built. Never natively focused. */
class Panel {
    constructor(branch, host, focusBranch, name, catches) {
        branch.activate(_owner);
        var root = branch.createElement("panel", "div");
        css.addClass(root, ga_panel);
        root.setAttribute("role", "group");
        root.setAttribute("aria-label", name);
        var header = branch.createElement("header", "div");
        css.addClass(header, ga_panel_header);
        header.textContent = name;
        var note = branch.createElement("note", "span");
        css.addClass(note, ga_panel_note);
        note.textContent = catches ? "catches a yield" : "lets a yield pass";
        header.appendChild(note);
        root.appendChild(header);
        host.appendChild(root);
        this.root = root; this._header = header; this._catches = !!catches;
        this.focus = focusBranch.createBranch(name, this);
        this._off = Keys.claimOn(root, this.focus.owner);
        this._leaves = [];
        // a press on the header claims; a native focus elsewhere on the page is let go, since the browser will not
        // do it for a press on unselectable text (a control of this panel's own keeps it: panel C's list)
        var self = this;
        header.addEventListener("pointerdown", function () { var a = document.activeElement; if (a && a !== document.body && !self.root.contains(a)) a.blur(); });
    }
    /** A leaf inside: it joins the panel's branch. */
    leaf(branch, name, onHold) { var l = new Leaf(branch, this.root, this.focus, name, onHold); this._leaves.push(l); return l; }
    /** A native text field inside the panel, wired by the panel: Escape lets it go, so the panel — the holder since the press — has the keys again. */
    field(branch, label) {
        branch.activate(_owner);
        var input = branch.createElement("field", "input");
        input.type = "text";
        css.addClass(input, ga_field);
        input.setAttribute("aria-label", label);
        input.placeholder = label;
        input.addEventListener("keydown", function (ev) { if (ev.key === "Escape") { input.blur(); ev.preventDefault(); ev.stopPropagation(); } });
        this.root.appendChild(input);
        return input;
    }
    /** A native button inside the panel, wired to nothing but its click: Enter and Space are the browser's, Escape goes nowhere. */
    button(branch, label, onClick) {
        branch.activate(_owner);
        var btn = branch.createElement("button", "button");
        btn.type = "button";
        css.addClass(btn, ga_button);
        btn.textContent = label;
        btn.addEventListener("click", onClick);
        this.root.appendChild(btn);
        return btn;
    }
    /** Asked when a descendant yields: a catching panel holds, the other lets the keys go on up. */
    wouldHold(from) { return this._catches; }
    /** The arrows taken and dropped, so a leaf's count shows who holds; Escape yields on up. */
    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus.owner); return true; }
        return ev.key === "ArrowUp" || ev.key === "ArrowDown";
    }
    granted() { css.addClass(this.root, ga_holds); }
    taken() { css.removeClass(this.root, ga_holds); }
    dispose() { this._leaves.forEach(function (l) { l.dispose(); }); this._off(); this.focus.owner.leave(); }
}

/**
 * Panel C: a container of the logical world whose contents are the native world — a select and three native leaves.
 * While one of them is focused the steward is dormant and their keys are their own; the panel hears them by a listener
 * on its own root, bubbled: Enter in the list blurs it and tells the picked leaf to focus itself; Escape in the list
 * blurs it and yields the panel; Escape on a leaf puts the focus back in the list. When the panel comes to hold — a
 * press on its header, a Tab arrival, a leaf's yield — it puts the focus in its list. App-layer wiring, all of it.
 */
class ListPanel extends Panel {
    constructor(branch, host, focusBranch, name) {
        super(branch, host, focusBranch, name, true);
        var self = this;
        var list = branch.createElement("list", "select");
        css.addClass(list, ga_panel_list);
        list.setAttribute("size", "3");
        list.setAttribute("aria-label", "which leaf holds the keys; Enter confirms");
        this.root.appendChild(list);
        this._list = list;
        this._byName = {};
        // a press on the header claimed for the panel already, and granted put the focus in the list; the press's own
        // default — the native focus to the body — would take it back out, so here it is stopped
        this._header.addEventListener("mousedown", function (ev) { ev.preventDefault(); });
        this.root.addEventListener("keydown", function (ev) {
            var leaf = self._leafAt(ev.target);
            if (ev.target === list) {
                if (ev.key === "Enter") { list.blur(); var l = self._byName[list.value]; if (l) l.activate(); }
                else if (ev.key === "Escape") { list.blur(); Keys.yield(self.focus.owner); }
                else return;
            } else if (leaf && ev.key === "Escape") { list.value = leaf.name; list.focus(); }
            else return;
            ev.preventDefault();
            ev.stopPropagation();
        });
    }
    /** The native leaf whose element contains `el`, or null. */
    _leafAt(el) {
        for (var i = 0; i < this._leaves.length; i++) if (this._leaves[i].root.contains(el)) return this._leaves[i];
        return null;
    }
    /** A native leaf inside, and an option for it. */
    leaf(branch, name) {
        var l = new NativeLeaf(branch, this.root, name);
        this._leaves.push(l);
        var option = branch.createElement("option-" + name, "option");
        option.value = name;
        option.textContent = name;
        this._list.appendChild(option);
        this._byName[name] = l;
        return l;
    }
    /** Granted, however: the list takes the native focus, so the pick is made by keys. The steward is dormant from here until the list lets go. */
    granted(by) { super.granted(by); try { this._list.focus({ preventScroll: true }); } catch (e) {} }
}
