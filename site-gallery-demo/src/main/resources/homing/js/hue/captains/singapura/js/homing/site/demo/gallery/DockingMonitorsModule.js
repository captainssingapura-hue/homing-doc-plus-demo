// =============================================================================
// DockingMonitors — the instruments, each a tab's widget like any other: the
// logical focus tree, the steward's lamp, the DomOps party as it stands, and
// what the page reports, line by line. They watch the page from inside it, so
// they obey the same law everything else in a dock obeys — a member of the
// dock's branch, answering activate(), its Escape yielding to the bar — and
// they hold nothing live of what they render.
//
//   new FocusTab(branch, { focus })      the focus tree, and who holds the keys
//   new StewardTab(branch, { focus })    the steward's activeness, one lamp
//   new DomOpsTab(branch, { focus })     the DomOps party, from its own snapshot;
//                                        refresh() redraws it
//   new EventsTab(branch, { focus })     say(line) adds a line and keeps it in view
//
//   new Instruments(branch, { host, keyboard, onEvent? })   the four together: a
//       float of their own on a DESK of their own, the page's section for a
//       floor. They watch the workspace and are not part of it — no dock of
//       it offers to take them, and a desk's tab-panes never leave it — so
//       they have their own register as well, and rest in a focus branch of
//       their own. No tab menu: Detach would carry one onto the workspace.
//     instruments.events     the EventsTab
//     instruments.domops()   the DomOpsTab, or null once the float is closed
// =============================================================================

const _monitorOwner = Object.freeze({ toString: () => "dockingMonitors" });

/** The law's three things, as the scene's tabs do them: a member of the dock's branch, a press claiming it, and the leaving. */
function _join(w, branch, params) { w.focus = params.focus.join(branch.name, w); w._off = Keys.claimOn(w.root, w.focus); }
function _leave(w) { if (w._off) w._off(); if (w.focus && w.focus.in) w.focus.leave(); }

/**
 * An instrument in a tab: it fills the tab, scrolls on its own, and is a
 * member of the dock's branch like any other widget — the law is the law,
 * even for the things that watch the page. What it mounts is the subclass's.
 */
class MonitorTab {
    constructor(branch, params) {
        branch.activate(_monitorOwner);
        var root = branch.createElement("root", "div");
        css.addClass(root, ga_monitor);
        this.root = root;
        this.branch = branch;
        _join(this, branch, params);
    }
    activate() { Keys.claim(this.focus); }
    granted() { this.root.setAttribute("data-keys", "held"); if (this.refresh) this.refresh(); }
    taken() { this.root.removeAttribute("data-keys"); }
    offered() { if (this.root.getAttribute("data-keys") === null) this.root.setAttribute("data-keys", "candidate"); }
    withdrawn() { if (this.root.getAttribute("data-keys") === "candidate") this.root.removeAttribute("data-keys"); }
    keyDown(ev) { if (ev.key === "Escape") { Keys.yield(this.focus); return true; } return false; }
    dispose() { _leave(this); if (this._it && this._it.dispose) this._it.dispose(); }
}

/** The logical focus tree, and who holds the keys. */
class FocusTab extends MonitorTab {
    constructor(branch, params) {
        super(branch, params);
        this._it = new FocusMonitor(branch.createBranch("monitor"), { host: this.root });
    }
}

/** The steward's activeness, as one lamp. */
class StewardTab extends MonitorTab {
    constructor(branch, params) {
        super(branch, params);
        this._it = new StewardMonitor(branch.createBranch("monitor"), { host: this.root });
    }
}

/**
 * The DomOps party as it stands: one row per branch, indented by depth, with
 * what it holds. Read from the party's own snapshot — plain data, nothing
 * live, so a monitor inside the tree cannot touch the tree it renders — and
 * redrawn when the page says anything happened.
 */
class DomOpsTab extends MonitorTab {
    constructor(branch, params) {
        super(branch, params);
        this._rows = null;
        this._n = 0;
        this.refresh();
    }
    refresh() {
        if (this._rows) this.branch.dissolveBranch(this._rows.name);
        var rows = this.branch.createBranch("rows-" + (++this._n));
        rows.activate(_monitorOwner);
        this._rows = rows;
        while (this.root.firstChild) this.root.removeChild(this.root.firstChild);
        var seq = 0, root = this.root;
        (function draw(node) {
            var row = rows.createElement("r" + (++seq), "div");
            css.addClass(row, ga_domops_row);
            row.style.setProperty("--ga-depth", String(node.depth));
            row.textContent = node.name;
            var count = rows.createElement("c" + seq, "span");
            css.addClass(count, ga_domops_count);
            count.textContent = node.elements.length + (node.elements.length === 1 ? " element" : " elements");
            row.appendChild(count);
            root.appendChild(row);
            node.branches.forEach(draw);
        })(domOpsParty.snapshot());
        return this;
    }
}

class Instruments {
    constructor(branch, opts) {
        var o = opts || {}, self = this;
        branch.activate(_monitorOwner);
        this.branch = branch;
        this.desk = new Desk(branch.createBranch("desk"), { host: o.host, layer: true, onEvent: o.onEvent, keyboard: o.keyboard, keyboardId: "docking/instruments" });
        this._rest = focusParty.root.createBranch("instruments", this);
        this.register = new TabRegister(branch.createBranch("tabs"), { focus: this._rest });
        this.float = new Floater(this.desk, { id: "instruments", x: 34, y: 430, w: 420, h: 300, addable: false, onEvent: o.onEvent });
        [["events", "Events", EventsTab], ["focus", "Focus", FocusTab], ["steward", "Steward", StewardTab], ["domops", "DomOps", DomOpsTab]].forEach(function (d) {
            self.float.take(self.register.open({ id: d[0], title: d[1], make: function (b, t) { return new d[2](b, { focus: t.focus }); } }));
        });
        this.events = this.float.host.widgetOf("events");
    }
    domops() { return this.float.closed() ? null : this.float.host.widgetOf("domops"); }
    dispose() {
        this.register.dispose();   // every instrument closed, and the float with the last
        this.desk.dispose();
        if (this._rest.owner.in) this._rest.owner.leave();
        try { this.branch.dissolve(); } catch (e) {}
    }
}

/** What the page reports, line by line: the events of the docks, the desk and the menus. */
class EventsTab extends MonitorTab {
    constructor(branch, params) {
        super(branch, params);
        var log = branch.createElement("log", "div");
        css.addClass(log, ga_log);
        log.setAttribute("aria-live", "polite");
        this.root.appendChild(log);
        this._log = log;
        this._lines = 0;
    }
    /** One more line, and the last is kept in view. */
    say(line) {
        this._lines++;
        this._log.textContent += (this._lines > 1 ? "\n" : "") + this._lines + "  " + line;
        this._log.scrollTop = this._log.scrollHeight;
        return this;
    }
}

