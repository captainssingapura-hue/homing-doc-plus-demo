// =============================================================================
// PanesApp — one multi-tab pane as a page: a DESK WITH ONE HOST (RFC 0066 E3,
// appendix "tab-panes"). Every tab is a tab-pane the desk opens in its
// register, its widget made by the base's contract (a class: new
// Widget(branch, params) → root, focus, activate(), setActive?, dispose?) on
// a branch of the tab-pane's own, and put in the pane; a close is the
// tab-pane's own, branch and all. THE LAW (§15.1): a tab's
// widget is logically focusable — it joins the dock's focus branch, handed in
// as params.focus, and answers activate(), a claim; its Escape yields back to
// the pane. The pane and the desk report; the page acts on the report: it
// tells the active widget so. The plus opens a picker in the dialog. Every event
// goes on the log under the pane as the data it is; the focus monitor and
// the steward's lamp sit beside the pane, so the two levels can be watched:
// ← → walk the tabs while the pane holds, Enter enters the widget, Escape
// comes back up.
// =============================================================================

const _owner = Object.freeze({ toString: () => "panesPage" });

/** What every tab widget here shares: the membership of the dock's branch, the claim, the ring, the yield. */
function _join(w, branch, params) {
    w.focus = params.focus.join(branch.name, w);
    w._off = Keys.claimOn(w.root, w.focus);
}
function _leave(w) { w._off(); if (w.focus.in) w.focus.leave(); }

// Three kinds of tab widget, each a class by the base's contract and the law.
class CardTab {
    constructor(branch, params) {
        this.root = new CardBuilder().title(params.title).badge(params.badge).text(params.text).build(branch.createBranch("card")).root;
        _join(this, branch, params);
    }
    activate() { Keys.claim(this.focus); }
    keyDown(ev) { if (ev.key === "Escape") { Keys.yield(this.focus); return true; } return false; }
    granted() { this.root.setAttribute("data-keys", "held"); }
    taken() { this.root.removeAttribute("data-keys"); }
    /** The walk rests here: a confirming key would bring the keys. Never over what it already says. */
    offered() { if (this.root.getAttribute("data-keys") === null) this.root.setAttribute("data-keys", "candidate"); }
    withdrawn() { if (this.root.getAttribute("data-keys") === "candidate") this.root.removeAttribute("data-keys"); }
    setActive(on) { this.root.setAttribute("data-active", on ? "true" : "false"); }
    dispose() { _leave(this); }
}

class CounterTab {
    constructor(branch, params) {
        var self = this;
        this._value = params.start || 0;
        var root = branch.createElement("counter", "div");
        this._count = branch.createElement("count", "div");
        css.addClass(this._count, ga_count);
        this._count.setAttribute("aria-live", "polite");
        var row = branch.createElement("row", "div");
        css.addClass(row, ga_buttons);
        row.appendChild(new Button(branch.createElement("dec", Button.TAG), { label: "−", kind: "plain", onClick: function () { self._value--; self._draw(); } }).el);
        this._inc = new Button(branch.createElement("inc", Button.TAG), { label: "+", onClick: function () { self._value++; self._draw(); } });
        row.appendChild(this._inc.el);
        this._draw();
        root.appendChild(this._count);
        root.appendChild(row);
        this.root = root;
        _join(this, branch, params);
    }
    _draw() { this._count.textContent = String(this._value); }
    activate() { Keys.claim(this.focus); }
    /** While the counter holds: ↑ and ↓ count, Escape yields to the pane. */
    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        if (ev.key === "ArrowUp") { this._value++; } else if (ev.key === "ArrowDown") { this._value--; } else return false;
        this._draw();
        return true;
    }
    granted() { this.root.setAttribute("data-keys", "held"); }
    taken() { this.root.removeAttribute("data-keys"); }
    /** The walk rests here: a confirming key would bring the keys. Never over what it already says. */
    offered() { if (this.root.getAttribute("data-keys") === null) this.root.setAttribute("data-keys", "candidate"); }
    withdrawn() { if (this.root.getAttribute("data-keys") === "candidate") this.root.removeAttribute("data-keys"); }
    setActive(on) { this.root.setAttribute("data-active", on ? "true" : "false"); }
    dispose() { _leave(this); this._value = null; }
}

class NoteTab {
    constructor(branch, params) {
        var root = branch.createElement("note", "p");
        css.addClass(root, ga_lede);
        root.textContent = params.text;
        this.root = root;
        _join(this, branch, params);
    }
    activate() { Keys.claim(this.focus); }
    keyDown(ev) { if (ev.key === "Escape") { Keys.yield(this.focus); return true; } return false; }
    granted() { this.root.setAttribute("data-keys", "held"); }
    taken() { this.root.removeAttribute("data-keys"); }
    /** The walk rests here: a confirming key would bring the keys. Never over what it already says. */
    offered() { if (this.root.getAttribute("data-keys") === null) this.root.setAttribute("data-keys", "candidate"); }
    withdrawn() { if (this.root.getAttribute("data-keys") === "candidate") this.root.removeAttribute("data-keys"); }
    dispose() { _leave(this); }
}

var KINDS = [
    { key: "card",    label: "A card",    Widget: CardTab },
    { key: "counter", label: "A counter", Widget: CounterTab },
    { key: "note",    label: "A note",    Widget: NoteTab }
];

var NOTES = [
    "A tab's widget is never detached on a switch: its panel is hidden and shown, so a scroll position or a caret survives.",
    "Drag a chip sideways to reorder; the pane reports a TabMoved with this pane as both source and destination.",
    "The pane never calls setActive. This page does, on TabActivated, as the workspace's focus machinery will.",
    "Close a tab and the pane disposes the widget first, reports TabRemoved, then activates a neighbour.",
    "Press a chip and the pane holds the keys: ← → walk the tabs, Shift+← → move the active one, Enter enters its widget, Escape comes back up."
];

class PanesWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        var dialogs = 0;   // each picker is a branch of its own
        var el = branch.createElement("root", "div");
        // the keys, through the party: the page's steward, made by the chrome and handed in the params
        var kb = params && params.keyboard;
        if (!kb) throw new Error("[gallery] the page's keyboard steward is required: params.keyboard");

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-ui-panes";
        el.appendChild(kicker);

        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Panes";
        el.appendChild(title);

        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "One pane of tabs - a desk with one host. Each tab holds a widget by the base's contract, made on a branch "
            + "of the tab's own and a member of the dock's focus branch — the law; the plus asks the page, the page asks you. Switch by "
            + "click, reorder by drag, close on the cross; or press a chip and the pane holds the keys: ← → walk the tabs, Home and "
            + "End the ends, Shift+← → move the active tab along the rail, Shift+↓ asks to detach, Enter enters the widget — the "
            + "counter then counts ↑ ↓ — and Escape comes back up to the pane, and from the pane to no one. Every event the pane "
            + "reports is written below it, as the data it is; the tree and the lamp beside it show who holds.";
        el.appendChild(lede);

        var row0 = branch.createElement("row0", "div");
        css.addClass(row0, ga_focus);
        var host = branch.createElement("host", "div");
        css.addClass(host, ga_pane_host);
        var monitorBox = branch.createElement("monitorBox", "div");
        css.addClass(monitorBox, ga_focus_monitor);
        row0.appendChild(host);
        row0.appendChild(monitorBox);
        el.appendChild(row0);

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

        var status = branch.createElement("status", "div");
        css.addClass(status, ga_status);
        el.appendChild(status);

        var seq = 0;
        var pane = null, desk = null;
        function state() { status.textContent = "state: " + JSON.stringify(pane.getState()); }

        // The event as data: a tab-pane as what it says it is - its id, its title, whether pinned - and never its widget.
        function line(ev) {
            return JSON.stringify(ev, function (k, v) {
                if (k === "widget") return undefined;
                return k === "tab" && v && typeof v.title === "function" ? { id: v.id, title: v.title(), pinned: v.pinned } : v;
            });
        }

        // the pane's reports and the desk's - a tab's arrival is the desk's to say - on one log
        function onEvent(ev) {
            say(line(ev));
            switch (ev.kind) {
                case "AddRequested":
                    pick();
                    break;
                case "TabActivated":
                    pane.tabs().forEach(function (id) {
                        var w = pane.widgetOf(id);
                        if (w && typeof w.setActive === "function") w.setActive(id === ev.tabId);
                    });
                    break;
                default:
                    break;   // TabAdded, TabRemoved, TabMoved: the log is enough
            }
            if (pane) state();
        }
        pane = new MultiTabPane(branch.createBranch("pane"), { host: host, slotId: "main", budget: 8, onEvent: onEvent });
        desk = new Desk(branch.createBranch("desk"), { host: host, onEvent: onEvent });
        desk.addDock(pane);

        function add(kind, params, extra) {
            ++seq;
            var spec = { title: params.title || (kind.label + " " + seq),
                         make: function (b, t) { b.activate(_owner); return new kind.Widget(b, Object.assign({}, params, { focus: t.focus })); } };
            if (extra) for (var k in extra) spec[k] = extra[k];
            return desk.open(spec, pane).id;
        }

        // The plus: a picker in the dialog. Enter is the first kind's.
        function pick() {
            new Dialog(branch.createBranch("dialog" + (++dialogs)), {
                title: "Add a tab", keyboard: kb, keyboardId: "panes/picker",
                size: { w: 420, h: 220 },
                content: function (b, body) {
                    var p = b.createElement("text", "p");
                    css.addClass(p, ga_lede);
                    p.textContent = "Which widget goes in the new tab?";
                    body.appendChild(p);
                    return {};
                },
                actions: KINDS.map(function (kind, i) {
                    return { id: kind.key, label: kind.label, primary: i === 0, onClick: function (h) {
                        h.close();
                        if (kind.key === "card") add(kind, { title: "Card " + (seq + 1), badge: "TAB", text: NOTES[seq % NOTES.length] });
                        else if (kind.key === "counter") add(kind, { start: seq * 10 });
                        else add(kind, { text: NOTES[seq % NOTES.length] });
                    } };
                })
            });
        }

        // A pinned welcome, a card and a counter to start with; the monitors beside the pane.
        add(KINDS[2], { title: "About", text: NOTES[0] + " " + NOTES[1] }, { pinned: true });
        add(KINDS[0], { title: "Card 2", badge: "TAB", text: NOTES[2] });
        add(KINDS[1], { start: 7 });
        this._monitor = new FocusMonitor(branch.createBranch("monitor"), { host: monitorBox });
        this._steward = new StewardMonitor(branch.createBranch("steward"), { host: monitorBox });

        var row = branch.createElement("row", "div");
        css.addClass(row, ga_buttons);
        el.appendChild(row);
        row.appendChild(new Button(branch.createElement("moveFirst", Button.TAG), { label: "Move the active tab first", kind: "plain", onClick: function () {
            var id = pane.activeTab(); if (id) pane.moveTab(id, 0);
        } }).el);
        row.appendChild(new Button(branch.createElement("moveLast", Button.TAG), { label: "Move it last", kind: "plain", onClick: function () {
            var id = pane.activeTab(); if (id) pane.moveTab(id, pane.count() - 1);
        } }).el);
        row.appendChild(new Button(branch.createElement("closeAll", Button.TAG), { label: "Close every tab", kind: "plain", onClick: function () {
            pane.getState().tabs.forEach(function (t) { if (!t.pinned) pane.removeTab(t.id); });
        } }).el);
        this.root = el;
        this._pane = pane;
        this._desk = desk;
    }

    dispose() { this._steward.dispose(); this._monitor.dispose(); this._desk.dispose(); this._pane.dispose(); }   // the desk first: a pane is disposed only empty
}

function appMain(el, params) {
    el.appendChild(new PanesWidget(domOpsParty.createBranch("panesPage"), params).root);
}
