// =============================================================================
// FocusApp — who is in focus, in two worlds. A scene of three panels, each a
// container holding a branch of the focus party (FocusScene has the parts);
// the monitors beside them: the tree with the holder of the keys and the
// steward by state, and the steward's activeness with where every key went.
// A press claims for the innermost member under it and nothing above it; Tab
// is the steward's and walks the members in the tree's order, A, a1, a2, B,
// b1, C, d, and round; Escape yields up the tree to the first ancestor that
// would hold — panel A catches, panel B lets pass, the root holds nothing.
// Panel C's list and leaves are the native world, wired by the panel: Enter
// in the list focuses the picked leaf, Escape returns to the list, Escape in
// the list yields the panel, and a panel that comes to hold puts the focus
// in its list. More of the native world stresses the line: outside every
// container, a search field, a notes field that keeps its Tab, a reset
// button; inside, a note field in panel A with Escape wired to let go, a
// button in panel B wired to nothing, a checkbox inside the logical leaf a2.
// The log says who took the keys from whom; no component here knows the
// steward.
// =============================================================================

const _owner = Object.freeze({ toString: () => "focusPage" });

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
            + "the list yields the panel, and a panel that comes to hold puts the focus in its list. More of the native world "
            + "stresses the line: a search field and a notes field outside every container — the notes keep their Tab — a note "
            + "field in panel A that lets go on Escape, a button in panel B wired to nothing, a checkbox inside the logical leaf a2. "
            + "The steward monitor shows whether the steward is routing keys, and where every key went.";
        el.appendChild(lede);

        var row = branch.createElement("row", "div");
        css.addClass(row, ga_focus);
        var column = branch.createElement("column", "div");
        css.addClass(column, ga_focus_column);
        var tools = branch.createElement("tools", "div");
        css.addClass(tools, ga_focus_tools);
        var scene = branch.createElement("scene", "div");
        css.addClass(scene, ga_focus_scene);
        column.appendChild(tools);
        column.appendChild(scene);
        var monitorBox = branch.createElement("monitorBox", "div");
        css.addClass(monitorBox, ga_focus_monitor);
        row.appendChild(column);
        row.appendChild(monitorBox);
        el.appendChild(row);

        // the native world outside every container: a search field, a notes field that keeps its Tab, a reset button
        var search = branch.createElement("search", "input");
        search.type = "search";
        css.addClass(search, ga_field);
        search.setAttribute("aria-label", "search");
        search.placeholder = "search — outside every container";
        tools.appendChild(search);
        var notes = branch.createElement("notes", "textarea");
        css.addClass(notes, ga_field);
        notes.setAttribute("aria-label", "notes");
        notes.rows = 1;
        notes.placeholder = "notes — keeps its Tab";
        notes.addEventListener("keydown", function (ev) {   // a component that stops a key's propagation keeps it: the steward never sees this Tab
            if (ev.key !== "Tab" || ev.ctrlKey || ev.altKey || ev.metaKey || ev.shiftKey) return;
            var at = notes.selectionStart, to = notes.selectionEnd;
            notes.value = notes.value.slice(0, at) + "\t" + notes.value.slice(to);
            notes.selectionStart = notes.selectionEnd = at + 1;
            ev.preventDefault();
            ev.stopPropagation();
        });
        tools.appendChild(notes);
        var reset = branch.createElement("reset", "button");
        reset.type = "button";
        css.addClass(reset, ga_button);
        reset.textContent = "reset counts";
        reset.addEventListener("click", function () { self._leaves.forEach(function (l) { l.reset(); }); });
        tools.appendChild(reset);

        // the scene: three panels holding branches under the party's root, and a loose leaf beside them
        var top = focusParty.root;
        var panelA = new Panel(branch.createBranch("panel-a"), scene, top, "panel A", true);
        var a1 = panelA.leaf(branch.createBranch("a1"), "a1");
        var a2 = panelA.leaf(branch.createBranch("a2"), "a2");
        a2.check(branch.createBranch("a2-check"), "a2 checked");
        panelA.field(branch.createBranch("a-note"), "a note in panel A — Escape lets go");
        var panelB = new Panel(branch.createBranch("panel-b"), scene, top, "panel B", false);
        var b1 = panelB.leaf(branch.createBranch("b1"), "b1");
        var beeps = 0;
        var beep = panelB.button(branch.createBranch("b-beep"), "beep 0", function () { beep.textContent = "beep " + (++beeps); });
        var panelC = new ListPanel(branch.createBranch("panel-c"), scene, top, "panel C");
        var c1 = panelC.leaf(branch.createBranch("c1"), "c1");
        var c2 = panelC.leaf(branch.createBranch("c2"), "c2");
        var c3 = panelC.leaf(branch.createBranch("c3"), "c3");
        var loose = new Leaf(branch.createBranch("d"), scene, top, "d");
        this._parts = [panelA, panelB, panelC, loose];
        this._leaves = [a1, a2, b1, c1, c2, c3, loose];

        this._monitor = new FocusMonitor(branch.createBranch("monitor"), { host: monitorBox });
        this._steward = new StewardMonitor(branch.createBranch("steward"), { host: monitorBox });

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
        say("three panels, six leaves in them, one loose, and the native world around them; press one, or Tab; then Escape");
        this.root = el;
    }

    dispose() { this._offKb(); this._steward.dispose(); this._monitor.dispose(); this._parts.forEach(function (p) { p.dispose(); }); }
}

function appMain(el, params) {
    el.appendChild(new FocusWidget(domOpsParty.createBranch("focusPage"), params).root);
}
