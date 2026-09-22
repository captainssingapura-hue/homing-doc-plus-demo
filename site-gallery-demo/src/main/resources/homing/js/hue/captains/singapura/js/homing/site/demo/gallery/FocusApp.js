// =============================================================================
// FocusApp — who is in focus, in two worlds. A scene of three panels, each a
// container holding a branch of the focus party; the monitor beside them
// showing the tree, the holder of the keys, and the steward by state. The
// LOGICAL world: the panels, panel A's and B's leaves, and the loose leaf d
// are members — never natively focused, their ring drawn from granted, their
// keys from the steward while nothing on the page is natively focused. A
// press claims for the innermost member under it and nothing above it; Tab
// is the steward's and walks the members in the tree's order, A, a1, a2, B,
// b1, C, d, and round; Escape yields up the tree to the first ancestor that
// would hold — panel A catches, panel B lets pass, the root holds nothing.
// The NATIVE world: panel C's list and its three leaves — tabindex, the
// browser's focus and ring, their own keydown on their own element. While
// one of them is focused the steward is dormant, whoever holds; the panel
// hears them by a listener on its own root, the DOM's own propagation: Enter
// in the list blurs it and tells the picked leaf to focus itself, Escape in
// the list blurs it and yields the panel, Escape on a leaf puts the focus
// back in the list; and whenever the panel comes to hold it puts the focus
// in its list, so from a header press or a Tab to the last leaf's arrows
// panel C works by keys alone. The framework wires none of it: the panel
// and its controls are one component's own affair. The log says who took
// the keys from whom; no component here knows the steward.
// =============================================================================

const _owner = Object.freeze({ toString: () => "focusPage" });

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

class FocusWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-ui-focus";
        el.appendChild(kicker);
        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Who is in focus";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "The logical-focus tree, as the focus party keeps it and the monitor shows it, in two worlds. The panels, "
            + "panel A's and B's leaves and the loose leaf are logical: never natively focused, a press claims for the innermost, "
            + "the arrows go to whoever holds and a leaf counts them, Escape yields up the tree — panel A catches, panel B lets "
            + "pass to no one — and Tab is the steward's, walking the members in the tree's order and round. Panel C's list "
            + "and leaves are native: the browser's focus, their own keys, the steward dormant while one of them is focused; "
            + "the panel wires them itself — Enter in the list focuses the picked leaf, Escape returns to the list, Escape in "
            + "the list yields the panel, and a panel that comes to hold puts the focus in its list.";
        el.appendChild(lede);

        var row = branch.createElement("row", "div");
        css.addClass(row, ga_focus);
        var scene = branch.createElement("scene", "div");
        css.addClass(scene, ga_focus_scene);
        var monitorBox = branch.createElement("monitorBox", "div");
        css.addClass(monitorBox, ga_focus_monitor);
        row.appendChild(scene);
        row.appendChild(monitorBox);
        el.appendChild(row);

        // the scene: three panels holding branches under the party's root, and a loose leaf beside them
        var top = focusParty.root;
        var panelA = new Panel(branch.createBranch("panel-a"), scene, top, "panel A", true);
        panelA.leaf(branch.createBranch("a1"), "a1");
        panelA.leaf(branch.createBranch("a2"), "a2");
        var panelB = new Panel(branch.createBranch("panel-b"), scene, top, "panel B", false);
        panelB.leaf(branch.createBranch("b1"), "b1");
        var panelC = new ListPanel(branch.createBranch("panel-c"), scene, top, "panel C");
        panelC.leaf(branch.createBranch("c1"), "c1");
        panelC.leaf(branch.createBranch("c2"), "c2");
        panelC.leaf(branch.createBranch("c3"), "c3");
        var loose = new Leaf(branch.createBranch("d"), scene, top, "d");
        this._parts = [panelA, panelB, panelC, loose];

        this._monitor = new FocusMonitor(branch.createBranch("monitor"), { host: monitorBox });

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
        function nameOf(id) { var m = focusParty.find(id); return m ? m.path : id; }
        this._offKb = KeyboardStewardInstance.on(function (ev) {
            if (ev.kind === "Granted") say("Granted   " + nameOf(ev.id) + (ev.by === "claim" ? "" : "  by " + ev.by));
            else if (ev.kind === "Taken") say("Taken     " + nameOf(ev.id) + "  by " + nameOf(ev.by));
            else say("Released  " + nameOf(ev.id));
        });
        say("three panels, six leaves in them, one loose; press one, or Tab; then Escape");
        this.root = el;
    }

    dispose() { this._offKb(); this._monitor.dispose(); this._parts.forEach(function (p) { p.dispose(); }); }
}

function appMain(el, params) {
    el.appendChild(new FocusWidget(domOpsParty.createBranch("focusPage"), params).root);
}
