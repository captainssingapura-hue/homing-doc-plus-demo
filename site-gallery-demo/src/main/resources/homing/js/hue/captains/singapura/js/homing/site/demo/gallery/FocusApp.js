// =============================================================================
// FocusApp — who is in focus. The logical-focus tree on view: a scene of three
// panels, each a container holding a branch of the focus party, with leaves
// inside; one loose leaf at the root; and the monitor beside them showing the
// tree as it is and the holder of the keys as it changes. A claim is by the
// mouse: a press on a leaf claims for the leaf, a press on a panel's header
// for the panel — the convention, in the capture phase, so a press on a leaf
// claims the panel first and then the leaf, and the leaf holds. A yield is a
// leaf's Escape, or its button: the keys go up the tree to the first ancestor
// that would hold them — panel A catches, panel B lets them pass and the page
// above it does too, so from b1 or from the loose leaf they go to no one.
// Panel C picks which of its leaves holds with a native list: the list has
// the physical focus, the leaf the logical one, side by side — the list keeps
// its arrows, as a field does; a chord and Escape reach the leaf. A leaf that
// holds counts the arrows it takes. The log says who took the keys from whom.
// No component here knows the steward: a panel and a leaf join a branch and
// call Keys; the page, which is not a component, listens for the log.
// =============================================================================

const _owner = Object.freeze({ toString: () => "focusPage" });

/** A leaf: a box that claims on a press, takes the arrows while it holds, and yields on Escape or its button. */
class Leaf {
    constructor(branch, host, focusBranch, name, onHold) {
        branch.activate(_owner);
        var self = this;
        var root = branch.createElement("leaf", "div");
        css.addClass(root, ga_leaf);
        root.setAttribute("tabindex", "0");
        root.setAttribute("role", "button");
        var label = branch.createElement("label", "span");
        label.textContent = name;
        var count = branch.createElement("count", "span");
        css.addClass(count, ga_leaf_count);
        count.textContent = "0";
        var yieldBtn = branch.createElement("yield", "button");
        yieldBtn.type = "button";
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
        yieldBtn.addEventListener("click", function () { Keys.yield(self.focus); });   // the press claimed by the convention first; the click gives up
    }
    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        if (ev.key === "ArrowUp") { this._n++; } else if (ev.key === "ArrowDown") { this._n--; } else return false;
        this._count.textContent = String(this._n);
        return true;
    }
    granted() { css.addClass(this.root, ga_holds); if (this._onHold) this._onHold(this); }
    taken() { css.removeClass(this.root, ga_holds); }
    dispose() { this._off(); this.focus.leave(); }
}

/** A panel: a container that holds a branch; a press on its header claims for the panel; a yield from below it catches or lets pass, as built. */
class Panel {
    constructor(branch, host, focusBranch, name, catches) {
        branch.activate(_owner);
        var root = branch.createElement("panel", "div");
        css.addClass(root, ga_panel);
        root.setAttribute("tabindex", "0");
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
        this.root = root; this._catches = !!catches;
        this.focus = focusBranch.createBranch(name, this);
        this._off = Keys.claimOn(root, this.focus.owner);
        this._leaves = [];
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

/** Panel C: a panel with a native list that picks which of its leaves holds the keys — the list physically focused, the leaf logically. */
class ListPanel extends Panel {
    constructor(branch, host, focusBranch, name) {
        super(branch, host, focusBranch, name, true);
        var self = this;
        var list = branch.createElement("list", "select");
        css.addClass(list, ga_panel_list);
        list.setAttribute("size", "3");
        list.setAttribute("aria-label", "which leaf holds the keys");
        this.root.appendChild(list);
        this._list = list;
        this._byName = {};
        // a pick in the list claims for that leaf: the list keeps the physical focus, the leaf takes the logical one
        list.addEventListener("change", function () { var l = self._byName[list.value]; if (l) Keys.claim(l.focus); });
    }
    /** A leaf inside, and an option for it; a leaf that comes to hold by any other way is shown in the list. */
    leaf(branch, name) {
        var self = this;
        var l = super.leaf(branch, name, function (held) { self._list.value = held.focus.name; });
        var option = branch.createElement("option-" + name, "option");
        option.value = name;
        option.textContent = name;
        this._list.appendChild(option);
        this._byName[name] = l;
        return l;
    }
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
        lede.textContent = "The logical-focus tree, as the focus party keeps it and the monitor shows it. Press a leaf and it holds the keys; "
            + "press a panel's header and the panel holds; the arrows go to whoever holds, and a leaf counts them. Escape, or a leaf's "
            + "button, yields: the keys go up to the first ancestor that would hold them — panel A catches, panel B lets them pass "
            + "to no one. Panel C picks its leaf with a native list: the list keeps the physical focus and its arrows, the leaf "
            + "takes the logical focus and a chord.";
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

        // the scene: three panels holding branches under the page's own branch, and a loose leaf beside them
        var page = focusParty.root.createBranch("focus-page", this);
        this.focus = page;
        var a = new Panel(branch.createBranch("panel-a"), scene, page, "panel A", true);
        a.leaf(branch.createBranch("a1"), "a1");
        a.leaf(branch.createBranch("a2"), "a2");
        var b = new Panel(branch.createBranch("panel-b"), scene, page, "panel B", false);
        b.leaf(branch.createBranch("b1"), "b1");
        var c = new ListPanel(branch.createBranch("panel-c"), scene, page, "panel C");
        c.leaf(branch.createBranch("c1"), "c1");
        c.leaf(branch.createBranch("c2"), "c2");
        c.leaf(branch.createBranch("c3"), "c3");
        var d = new Leaf(branch.createBranch("d"), scene, page, "d");
        this._parts = [a, b, c, d];

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
        say("three panels, six leaves in them, one loose; press one, then Escape");
        this.root = el;
    }

    // the page holds the scene's branch and takes no keys; a yield from below passes it
    dispose() { this._offKb(); this._monitor.dispose(); this._parts.forEach(function (p) { p.dispose(); }); this.focus.owner.leave(); }
}

function appMain(el, params) {
    el.appendChild(new FocusWidget(domOpsParty.createBranch("focusPage"), params).root);
}
