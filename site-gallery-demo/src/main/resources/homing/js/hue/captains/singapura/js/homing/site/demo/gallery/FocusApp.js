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
// Panel C picks which of its leaves holds with a native list: while the list
// has the physical focus the panel holds the logical one and the list keeps
// its arrows, as a field does; the pick is confirmed by Enter, on which the
// panel tells the leaf to activate itself, and the leaf takes the focus as a
// press on it would — the list's released first, then the claim; and when
// the panel comes to hold it puts the focus in its list, so it works by keys
// alone. A leaf that holds counts the arrows it takes. The log says who took the keys from whom.
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
    /** Told to activate by its container: what a press on it does — the physical focus taken, and the convention claims for it. */
    activate() { try { this.root.focus({ preventScroll: true }); } catch (e) {} }
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
        this.root = root; this._header = header; this._catches = !!catches;
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

/**
 * Panel C: a panel with a native list that picks which of its leaves holds the keys. While the list has the physical
 * focus the panel holds the logical one (the convention), and the list's own keys — arrows, Home, End — walk its
 * options natively and reach no member; the pick is a proposal until Enter confirms it. On Enter, forwarded to the
 * panel since a select's Enter is the holder's, the panel tells the picked leaf to activate itself, and the leaf does
 * what a press on it does: takes the physical focus, which releases the list's, and the convention claims for it.
 * And whenever the panel comes to hold — a press on its header, the focus arriving, a leaf's yield — it puts the
 * physical focus in its list, so from a leaf's Escape to the next leaf's arrows the panel works by keys alone.
 */
class ListPanel extends Panel {
    constructor(branch, host, focusBranch, name) {
        super(branch, host, focusBranch, name, true);
        var list = branch.createElement("list", "select");
        css.addClass(list, ga_panel_list);
        list.setAttribute("size", "3");
        list.setAttribute("aria-label", "which leaf holds the keys; Enter confirms");
        this.root.appendChild(list);
        this._list = list;
        this._byName = {};
        // a press on the header claimed for the panel already (pointerdown, capture), and granted put the focus in the
        // list; the press's own default — the focus to the panel's root — would take it back out, so it is stopped
        this._header.addEventListener("mousedown", function (ev) { ev.preventDefault(); });
    }
    /** Enter while the panel holds and the list is picked in: the picked leaf is told to activate itself. */
    keyDown(ev) {
        if (ev.key === "Enter") { var l = this._byName[this._list.value]; if (l) l.activate(); return true; }
        return super.keyDown(ev);
    }
    /** Granted, however: the list takes the physical focus, so the next pick can be made by keys. A claim it raises for the panel is nothing — the panel holds. */
    granted(by) { super.granted(by); try { this._list.focus({ preventScroll: true }); } catch (e) {} }
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
            + "to no one. Panel C picks its leaf with a native list: the arrows walk the list, Enter confirms — the panel tells the "
            + "leaf to activate itself, and it takes the focus the way a press on it would; and when the panel comes to hold, by "
            + "a press or a leaf's Escape, it puts the focus in its list, so panel C works by keys alone.";
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
        var panelA = new Panel(branch.createBranch("panel-a"), scene, page, "panel A", true);
        panelA.leaf(branch.createBranch("a1"), "a1");
        panelA.leaf(branch.createBranch("a2"), "a2");
        var panelB = new Panel(branch.createBranch("panel-b"), scene, page, "panel B", false);
        panelB.leaf(branch.createBranch("b1"), "b1");
        var panelC = new ListPanel(branch.createBranch("panel-c"), scene, page, "panel C");
        panelC.leaf(branch.createBranch("c1"), "c1");
        panelC.leaf(branch.createBranch("c2"), "c2");
        panelC.leaf(branch.createBranch("c3"), "c3");
        var loose = new Leaf(branch.createBranch("d"), scene, page, "d");
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
        say("three panels, six leaves in them, one loose; press one, then Escape");
        this.root = el;
    }

    // the page holds the scene's branch and takes no keys; a yield from below passes it
    dispose() { this._offKb(); this._monitor.dispose(); this._parts.forEach(function (p) { p.dispose(); }); this.focus.owner.leave(); }
}

function appMain(el, params) {
    el.appendChild(new FocusWidget(domOpsParty.createBranch("focusPage"), params).root);
}
