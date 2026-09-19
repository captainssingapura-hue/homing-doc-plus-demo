// =============================================================================
// PanesApp — one multi-tab pane as a page. The page is the holder: each tab's
// widget is constructed on a branch the page mints for it, by the base's
// contract (construct(branch, params) → { root, setActive?, dispose? }), and
// handed to the pane. The pane reports; the page acts on the report: it
// tells the active widget so, and dissolves a closed tab's branch after
// the pane has disposed the widget. The plus opens a picker in the dialog.
// Every report goes on the log under the pane, in the studio pane's shapes.
// =============================================================================

const _owner = Object.freeze({ toString: () => "panesPage" });

// Three kinds of widget, each a construct(branch, params).
var KINDS = [
    { key: "card", label: "A card", construct: function (branch, params) {
        var root = Card(branch, "card", { title: params.title, badge: params.badge, text: params.text });
        return { root: root, setActive: function (on) { root.setAttribute("data-active", on ? "true" : "false"); } };
    } },
    { key: "counter", label: "A counter", construct: function (branch, params) {
        var root = branch.createElement("counter", "div");
        var value = params.start || 0;
        var count = branch.createElement("count", "div");
        css.addClass(count, ga_count);
        count.setAttribute("aria-live", "polite");
        var row = branch.createElement("row", "div");
        css.addClass(row, ga_buttons);
        function draw() { count.textContent = String(value); }
        row.appendChild(Button(branch, "dec", { label: "−", kind: "plain", onClick: function () { value--; draw(); } }));
        row.appendChild(Button(branch, "inc", { label: "+", onClick: function () { value++; draw(); } }));
        draw();
        root.appendChild(count);
        root.appendChild(row);
        return { root: root,
                 setActive: function (on) { if (on) row.children[1].focus(); },
                 dispose: function () { value = null; } };
    } },
    { key: "note", label: "A note", construct: function (branch, params) {
        var root = branch.createElement("note", "p");
        css.addClass(root, ga_lede);
        root.textContent = params.text;
        return { root: root };
    } }
];

var NOTES = [
    "A tab's widget is never detached on a switch: its panel is hidden and shown, so a scroll position or a caret survives.",
    "Drag a chip sideways to reorder; the pane reports onTabMoved with this pane as both source and destination.",
    "The pane never calls setActive. This page does, from onTabActivated, as the workspace's focus machinery will.",
    "Close a tab and the pane disposes the widget first, reports onTabRemoved, then activates a neighbour."
];

function appMain(el) {
    var branch = domOpsParty.createBranch("panesPage");
    branch.activate(_owner);

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
        + "What the pane reports is written below it.";
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

    pane = mountMultiTabPane({
        branch: branch, host: host, slotId: "main", budget: 8,
        onAddTab:       function (slotId) { say("onAddTab(" + slotId + ")"); pick(); },
        onTabAdded:     function (slotId, tab, i) { say("onTabAdded(" + slotId + ", " + tab.id + ", " + i + ")"); state(); },
        onTabAttached:  function (slotId, tab, i) { say("onTabAttached(" + slotId + ", " + tab.id + ", " + i + ")"); state(); },
        onTabRemoved:   function (slotId, tab, i) {
            say("onTabRemoved(" + slotId + ", " + tab.id + ", " + i + ")");
            branch.dissolveBranch(branches.get(tab.id));   // the widget was disposed by the pane already
            branches.delete(tab.id);
            state();
        },
        onTabMoved:     function (src, tab, si, dest, di) { say("onTabMoved(" + src + ", " + tab.id + ", " + si + ", " + dest + ", " + di + ")"); state(); },
        onTabActivated: function (slotId, tabId) {
            say("onTabActivated(" + slotId + ", " + tabId + ")");
            pane.tabs().forEach(function (id) {
                var w = pane.widgetOf(id);
                if (w && typeof w.setActive === "function") w.setActive(id === tabId);
            });
            state();
        }
    });

    function add(kind, params, extra) {
        var id = kind.key + "-" + (++seq);
        var name = "tab_" + id;
        var own = branch.createBranch(name);
        own.activate(_owner);
        branches.set(id, name);
        var tab = { id: id, title: params.title || (kind.label + " " + seq), widget: kind.construct(own, params) };
        if (extra) for (var k in extra) tab[k] = extra[k];
        pane.addTab(tab);
        return id;
    }

    // The plus: a picker in the dialog. Enter is the first kind's.
    function pick() {
        openDialog({
            branch: branch,
            title: "Add a tab",
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
    row.appendChild(Button(branch, "moveFirst", { label: "Move the active tab first", kind: "plain", onClick: function () {
        var id = pane.activeTab(); if (id) pane.moveTab(id, 0);
    } }));
    row.appendChild(Button(branch, "moveLast", { label: "Move it last", kind: "plain", onClick: function () {
        var id = pane.activeTab(); if (id) pane.moveTab(id, pane.count() - 1);
    } }));
    row.appendChild(Button(branch, "closeAll", { label: "Close every tab", kind: "plain", onClick: function () {
        pane.getState().tabs.forEach(function (t) { if (!t.pinned) pane.removeTab(t.id); });
    } }));
}
