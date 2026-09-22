// =============================================================================
// FocusApp — who is in focus. The logical-focus tree on view, in its first
// and simplest form: a scene of two panels, each a container holding a
// branch of the focus party, with leaves inside; one loose leaf at the root;
// and the monitor beside them showing the tree as it is and the holder of
// the keys as it changes. Every claim here is by the mouse: a press on a
// leaf claims for the leaf, a press on a panel's header for the panel — the
// convention, in the capture phase, so a press on a leaf claims the panel
// first and then the leaf, and the leaf holds. No yield yet: the keys move
// only by a claim, and a focus leaving the scene releases them. A leaf that
// holds counts the arrows it takes, so the keys are seen to arrive. The log
// says who took the keys from whom. No component here knows the steward: a
// panel and a leaf join a branch and call Keys; the page, which is not a
// component, listens to the steward for the log.
// =============================================================================

const _owner = Object.freeze({ toString: () => "focusPage" });

/** A leaf: a box that claims on a press and takes the arrows while it holds. */
class Leaf {
    constructor(branch, host, focusBranch, name) {
        branch.activate(_owner);
        var root = branch.createElement("leaf", "div");
        css.addClass(root, ga_leaf);
        root.setAttribute("tabindex", "0");
        root.setAttribute("role", "button");
        var label = branch.createElement("label", "span");
        label.textContent = name;
        var count = branch.createElement("count", "span");
        css.addClass(count, ga_leaf_count);
        count.textContent = "0";
        root.appendChild(label);
        root.appendChild(count);
        host.appendChild(root);
        this.root = root; this._count = count; this._n = 0;
        this.focus = focusBranch.join(name, this);
        this._off = Keys.claimOn(root, this.focus);
    }
    keyDown(ev) {
        if (ev.key === "ArrowUp") { this._n++; } else if (ev.key === "ArrowDown") { this._n--; } else return false;
        this._count.textContent = String(this._n);
        return true;
    }
    granted() { css.addClass(this.root, ga_holds); }
    taken() { css.removeClass(this.root, ga_holds); }
    dispose() { this._off(); this.focus.leave(); }
}

/** A panel: a container that holds a branch; a press on its header claims for the panel itself. */
class Panel {
    constructor(branch, host, focusBranch, name) {
        branch.activate(_owner);
        var self = this;
        var root = branch.createElement("panel", "div");
        css.addClass(root, ga_panel);
        root.setAttribute("tabindex", "0");
        root.setAttribute("role", "group");
        root.setAttribute("aria-label", name);
        var header = branch.createElement("header", "div");
        css.addClass(header, ga_panel_header);
        header.textContent = name;
        root.appendChild(header);
        host.appendChild(root);
        this.root = root;
        this.focus = focusBranch.createBranch(name, this);
        this._off = Keys.claimOn(root, this.focus.owner);
        this._leaves = [];
    }
    /** A leaf inside: it joins the panel's branch. */
    leaf(branch, name) { var l = new Leaf(branch, this.root, this.focus, name); this._leaves.push(l); return l; }
    keyDown(ev) { return ev.key === "ArrowUp" || ev.key === "ArrowDown"; }   // a panel takes the arrows and does nothing with them, so a leaf's count shows who holds
    granted() { css.addClass(this.root, ga_holds); }
    taken() { css.removeClass(this.root, ga_holds); }
    dispose() { this._leaves.forEach(function (l) { l.dispose(); }); this._off(); this.focus.owner.leave(); }
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
        lede.textContent = "The logical-focus tree, as the focus party keeps it and the monitor shows it. Two panels hold a branch each, "
            + "with leaves inside, and one leaf sits at the root. Press a leaf and it holds the keys; press a panel's header and "
            + "the panel holds; the arrows go to whoever holds, and a leaf counts them. The keys move only by a claim here — "
            + "no yield yet — and leave the scene with the focus.";
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

        // the scene: two panels holding branches under the page's own branch, and a loose leaf beside them
        var page = focusParty.root.createBranch("focus-page", this);
        this.focus = page;
        var a = new Panel(branch.createBranch("panel-a"), scene, page, "panel A");
        a.leaf(branch.createBranch("a1"), "a1");
        a.leaf(branch.createBranch("a2"), "a2");
        var b = new Panel(branch.createBranch("panel-b"), scene, page, "panel B");
        b.leaf(branch.createBranch("b1"), "b1");
        var c = new Leaf(branch.createBranch("c"), scene, page, "c");
        this._parts = [a, b, c];

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
            if (ev.kind === "Granted") say("Granted   " + nameOf(ev.id));
            else if (ev.kind === "Taken") say("Taken     " + nameOf(ev.id) + "  by " + nameOf(ev.by));
            else say("Released  " + nameOf(ev.id));
        });
        say("two panels, three leaves in them, one loose; press one");
        this.root = el;
    }

    // the page holds the scene's branch and takes no keys
    dispose() { this._offKb(); this._monitor.dispose(); this._parts.forEach(function (p) { p.dispose(); }); this.focus.owner.leave(); }
}

function appMain(el, params) {
    el.appendChild(new FocusWidget(domOpsParty.createBranch("focusPage"), params).root);
}
