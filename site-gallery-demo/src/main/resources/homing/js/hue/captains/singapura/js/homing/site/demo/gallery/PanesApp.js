// =============================================================================
// PanesApp — one multi-tab pane as a page. The page is the holder: each tab's
// widget is constructed on a branch the page mints for it, by the base's
// contract (a class: new Widget(branch, params) → root, setActive?, dispose?), and
// handed to the pane. The pane reports; the page acts on the report: it
// tells the active widget so, and dissolves a closed tab's branch after
// the pane has disposed the widget. The plus opens a picker in the dialog.
// Every event goes on the log under the pane as the data it is.
// =============================================================================

const _owner = Object.freeze({ toString: () => "panesPage" });

// Three kinds of tab widget, each a class by the base's contract.
class CardTab {
    constructor(branch, params) {
        this.root = new CardBuilder().title(params.title).badge(params.badge).text(params.text).build(branch.createBranch("card")).root;
    }
    setActive(on) { this.root.setAttribute("data-active", on ? "true" : "false"); }
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
    }
    _draw() { this._count.textContent = String(this._value); }
    setActive(on) { if (on) this._inc.el.focus(); }
    dispose() { this._value = null; }
}

class NoteTab {
    constructor(branch, params) {
        var root = branch.createElement("note", "p");
        css.addClass(root, ga_lede);
        root.textContent = params.text;
        this.root = root;
    }
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
    "Close a tab and the pane disposes the widget first, reports TabRemoved, then activates a neighbour."
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
        lede.textContent = "One pane of tabs. Each tab holds a widget by the base's contract, constructed by this page on a branch "
            + "of its own; the plus asks the page, the page asks you. Switch by click, reorder by drag, close on the cross. "
            + "Every event the pane reports is written below it, as the data it is.";
        el.appendChild(lede);

        var host = branch.createElement("host", "div");
        css.addClass(host, ga_pane_host);
        el.appendChild(host);

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

        // The holder's book: tab id → the branch its widget lives on.
        var branches = new Map();
        var seq = 0;
        var pane = null;
        function state() { status.textContent = "state: " + JSON.stringify(pane.getState()); }

        // The event as data: every field but the widget, which does not travel.
        function line(ev) {
            return JSON.stringify(ev, function (k, v) { return k === "widget" ? undefined : v; });
        }

        pane = new MultiTabPane(branch.createBranch("pane"), {
            host: host, slotId: "main", budget: 8, keyboard: kb, keyboardId: "panes/pane",
            onEvent: function (ev) {
                say(line(ev));
                switch (ev.kind) {
                    case "AddRequested":
                        pick();
                        break;
                    case "TabRemoved":
                        branch.dissolveBranch(branches.get(ev.tab.id));   // the widget was disposed by the pane already
                        branches.delete(ev.tab.id);
                        break;
                    case "TabActivated":
                        pane.tabs().forEach(function (id) {
                            var w = pane.widgetOf(id);
                            if (w && typeof w.setActive === "function") w.setActive(id === ev.tabId);
                        });
                        break;
                    default:
                        break;   // TabAdded, TabAttached, TabMoved: the log is enough
                }
                state();
            }
        });

        function add(kind, params, extra) {
            var id = kind.key + "-" + (++seq);
            var name = "tab_" + id;
            var own = branch.createBranch(name);
            own.activate(_owner);
            branches.set(id, name);
            var tab = { id: id, title: params.title || (kind.label + " " + seq), widget: new kind.Widget(own, params) };
            if (extra) for (var k in extra) tab[k] = extra[k];
            pane.addTab(tab);
            return id;
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

        // A pinned welcome, a card and a counter to start with.
        add(KINDS[2], { title: "About", text: NOTES[0] + " " + NOTES[1] }, { pinned: true });
        add(KINDS[0], { title: "Card 2", badge: "TAB", text: NOTES[2] });
        add(KINDS[1], { start: 7 });

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
    }

    dispose() { this._pane.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new PanesWidget(domOpsParty.createBranch("panesPage"), params).root);
}
