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
//       dock that floats, on a DESK of their own, the page's section for a
//       floor. They watch the workspace and are not part of it — no dock of it
//       offers to take them, and a desk's tab-panes never leave it. No tab
//       menu: Detach would only float one on its own desk.
//     instruments.events     the EventsTab
//     instruments.domops()   the DomOpsTab, or null once the dock is closed
//     instruments.close()    the four closed and the dock gone: its cross does this
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
    granted() { if (this.refresh) this.refresh(); }
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
        this._closed = false;
        this.desk = new Desk(branch.createBranch("desk"), { host: o.host, onEvent: o.onEvent, keyboard: o.keyboard, keyboardId: "docking/instruments", focusName: "instruments" });
        // A DOCK THAT FLOATS: a frame on the desk's own layer around a multi-tab pane, the strip the frame's one bar. Not a
        // desk's float - a float is one tab in transit - but a dock the desk holds like any other, lying on its layer: the
        // strip's ground moves the frame, and the cross at its end closes the four.
        this.frame = this.desk.layer.open({ id: "instruments", head: false, closable: false, title: "Instruments", x: 34, y: 430, w: 420, h: 300 });
        var own = this.frame.branch.createBranch("dock");
        own.activate(_monitorOwner);
        this.pane = new MultiTabPane(own.createBranch("host"), { host: this.frame.body, slotId: "instruments", addable: false, focusName: this.frame.branch.name,
            onEvent: o.onEvent, onEmpty: function () { self.close(); }, onClose: function () { self.close(); } });
        this.desk.addDock(this.pane);
        this.frame.handle(this.pane.bar(), function (ev) { return self.pane.barGround(ev.target); });
        [["events", "Events", EventsTab], ["focus", "Focus", FocusTab], ["steward", "Steward", StewardTab], ["domops", "DomOps", DomOpsTab]].forEach(function (d) {
            self.desk.open({ id: d[0], title: d[1], make: function (b, t) { return new d[2](b, { focus: t.focus }); } }, self.pane);
        });
        this.events = this.pane.widgetOf("events");
    }
    domops() { return this._closed ? null : this.pane.widgetOf("domops"); }
    /** The four closed, the dock out of the desk, and its frame gone. */
    close() {
        if (this._closed) return;
        this._closed = true;
        var pane = this.pane;
        pane.tabs().forEach(function (id) { pane.removeTab(id); });
        this.desk.removeDock(pane);
        pane.dispose();
        this.desk.layer.close(this.frame.id);
    }
    dispose() {
        this.close();
        this.desk.dispose();
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

