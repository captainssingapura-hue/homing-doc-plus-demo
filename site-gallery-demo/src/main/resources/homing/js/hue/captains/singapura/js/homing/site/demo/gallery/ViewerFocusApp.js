// =============================================================================
// ViewerFocusApp — viewers in focus: a case board for where the keys go when a
// widget's native control lets them go. An SVG viewer is a widget, a member of
// its own focus party, the viewport inside it the native world. One Escape the
// viewport has no use for lets it go - a yield from it:
//   1. the steward finds the widget holding it, and asks it first;
//   2. a widget with nothing designed for holding the keys is passed by;
//   3. the keys reach the first super-ordinate that would hold them;
//   4. none would - as in the doc reader - and the root's default has them:
//      the home, named once the page is laid out, holding from the start.
// Each case is a cell with its checks, and every check a lamp the board
// (CaseBoard) lights from the route of each Escape and who holds after it.
// The monitors and the log say the same, step by step.
// =============================================================================

const _owner = Object.freeze({ toString: () => "viewerFocusPage" });

class ViewerFocusWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        var self = this, steward = KeyboardStewardInstance;
        var el = branch.createElement("root", "div");

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-ui-panzoom · homing-ui-focus · a case board";
        el.appendChild(kicker);
        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Viewers in focus";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "Where the keys go when a widget's native control lets them go. Each viewer is a widget, a member of its own "
            + "focus party; the viewport inside it is the native world: a press puts the browser's focus there, + and − zoom, 0 fits, "
            + "the arrows pan. One Escape it has no use for lets it go - a yield: the viewer is asked first and, with nothing "
            + "designed for holding the keys, passed by; the keys reach the first that would hold them, else the root's default, "
            + "the home. Every case says what to do and what to expect, and its lamps say what happened.";
        el.appendChild(lede);
        var chain = new DOMParser().parseFromString(focusTree, "image/svg+xml").documentElement;   // served as text by ViewerDrawings
        css.addClass(chain, ga_case_figure);
        chain.setAttribute("role", "img");
        chain.setAttribute("aria-label", "The chain: the viewport lets go, the viewer is passed by, the keys go on up to the first that holds, else the home");
        el.appendChild(chain);

        var row = branch.createElement("row", "div");
        css.addClass(row, ga_focus);
        var scene = branch.createElement("scene", "div");
        css.addClass(scene, ga_focus_scene);
        var monitorBox = branch.createElement("monitorBox", "div");
        css.addClass(monitorBox, ga_focus_monitor);
        row.appendChild(scene);
        row.appendChild(monitorBox);
        el.appendChild(row);

        var top = focusParty.root, board = new CaseBoard(steward);
        this._board = board;
        this._viewers = [];
        this._parts = [];
        function aCase(n, name, how) { var c = new ViewerCase(branch.createBranch("case" + n), scene, { n: n, title: name, how: how }); self._parts.push(c); return c; }
        // a viewer, hosted as any host hosts a widget: made from a container and params, its roots grafted where it sits
        function viewer(name, host, focusAt, label, keeps) {
            var svg = new DOMParser().parseFromString(specimen, "image/svg+xml").documentElement;
            var v = new SvgViewer(host, { svg: svg, label: "the viewer " + label, keeps: keeps === true });
            branch.graft(name, v.roots.dom);
            focusAt.graft(name, v.roots.focus);
            board.name(v.focus, "the viewer " + label);
            self._viewers.push(v);
            return v;
        }
        function toRoot(check) { return function () { var d = board.rootDefault(); return { check: check, id: d.id, says: d.says }; }; }
        function to(check, m, says) { return function () { return { check: check, id: m.id, says: says }; }; }

        var c1 = aCase(1, "The home: the root's default", "A leaf standing in for the doc reader's contents, named the home once the page "
            + "is laid out. Its keys: the arrows count. The home is the anchor: press it, then Escape - its yield reaches the page and is given straight back, granted anew by home; an Escape by mistake moves nothing.");
        var home = new Leaf(branch.createBranch("home"), c1.body, top, "the home");
        this._parts.push(home);
        board.name(home.focus, "the home");
        var entering = c1.check("Entering the page"), ownEscape = c1.check("Its own Escape");
        board.watch(home.focus, function () {   // the anchor while it is the home; taken away (case 7), a leaf like any
            return steward.homed() === home.focus.id ? { check: ownEscape, id: home.focus.id, says: "the home, given straight back: the anchor" }
                                                     : { check: ownEscape, id: null, says: "no one: it is not the home now" };
        });

        var c2 = aCase(2, "A viewer at the root", "Press the drawing, then Escape: the viewer is asked, has nothing designed for holding the "
            + "keys, and is passed by; nothing is above it but the root.");
        var v2 = viewer("atRoot", c2.body, top, "at the root");
        board.watch(v2.focus, toRoot(c2.check("Escape in the drawing")));

        var c3 = aCase(3, "A viewer in a panel that catches", "Press the drawing, then Escape: the viewer is passed by and the panel, "
            + "which would hold what is yielded to it, has the keys. Escape again: the panel's own yield, to the root.");
        var catches = new Panel(branch.createBranch("catches"), c3.body, top, "a panel that catches", true);
        this._parts.push(catches);
        board.name(catches.focus.owner, "the panel that catches");
        var v3 = viewer("caught", catches.root, catches.focus, "in the panel that catches");
        board.watch(v3.focus, to(c3.check("Escape in the drawing"), catches.focus.owner, "the panel that catches"));
        board.watch(catches.focus.owner, toRoot(c3.check("The panel's own Escape")));

        var c4 = aCase(4, "A viewer in a panel that lets pass", "Press the drawing, then Escape: the viewer is passed by, and so is the "
            + "panel, which would not hold what is yielded to it.");
        var passes = new Panel(branch.createBranch("passes"), c4.body, top, "a panel that lets pass", false);
        this._parts.push(passes);
        board.name(passes.focus.owner, "the panel that lets pass");
        var v4 = viewer("passed", passes.root, passes.focus, "in the panel that lets pass");
        board.watch(v4.focus, toRoot(c4.check("Escape in the drawing")));

        var c5 = aCase(5, "A viewer that keeps the keys", "Made to keep them, it says so when asked: its viewport letting go leaves the "
            + "keys with it, held, nothing focused - + and − still zoom and, zoomed in, the arrows pan, through the party. Press, Escape, +, an arrow, Escape again.");
        var v5 = viewer("keeper", c5.body, top, "that keeps the keys", true);
        var k5a = c5.check("Escape in the drawing"), k5b = c5.check("Its own Escape, held");
        board.watch(v5.focus, function (route) { return route === "escape" ? { check: k5a, id: v5.focus.id, says: "the viewer itself, held" } : toRoot(k5b)(); });

        var c6 = aCase(6, "A viewer in a layer that keeps Escape", "As a stage or a dialog closing: while the layer is open its Escape is "
            + "the layer's - it closes, and the keys stay where they were. Closed, it lets the next Escape by. A press opens it again.");
        var layer = new EscapeLayer(branch.createBranch("layer"), c6.body, { title: "the layer" });
        this._parts.push(layer);
        var v6 = viewer("layered", layer.body, top, "in the layer");
        var k6a = c6.check("Escape, the layer open"), k6b = c6.check("Escape, the layer closed");
        board.watch(v6.focus, function () { return layer.closedNow() ? { check: k6a, id: v6.focus.id, says: "the viewer still: the layer kept the Escape" } : toRoot(k6b)(); });

        var c7 = aCase(7, "No home", "Take the home away, press the drawing, Escape: the yield reaches the root, and no one holds. Name "
            + "the home again: no one holding, it holds at once.");
        var toggle = branch.createElement("toggleHome", "button");
        toggle.type = "button";
        css.addClass(toggle, ga_button);
        c7.body.appendChild(toggle);
        var v7 = viewer("homeless", c7.body, top, "with no home");
        board.watch(v7.focus, toRoot(c7.check("Escape in the drawing")));
        var renamed = c7.check("Naming the home again");
        function toggled() { toggle.textContent = steward.homed() ? "Take the home away" : "Name the home again"; }
        toggle.addEventListener("click", function () {
            if (steward.homed()) { Keys.home(null); toggled(); return; }
            var before = steward.holder();
            Keys.home(home.focus);
            var after = steward.holder();
            renamed.expect(before === null ? "the home, at once: no one held" : "no change: " + board.nameOf(before) + " holds");
            renamed.verdict(before === null ? after === home.focus.id : after === before, "the keys at: " + board.nameOf(after));
            toggled();
        });

        this._monitor = new FocusMonitor(branch.createBranch("monitor"), { host: monitorBox });
        this._steward = new StewardMonitor(branch.createBranch("steward"), { host: monitorBox });
        var log = branch.createElement("log", "div");
        css.addClass(log, ga_log);
        el.appendChild(log);
        var lines = 0;
        function say(line) {
            lines++;
            log.textContent += (lines > 1 ? "\n" : "") + lines + "  " + line;
            log.scrollTop = log.scrollHeight;
        }
        this._offKb = steward.on(function (ev) {
            if (ev.kind === "Granted") say("Granted   " + board.nameOf(ev.id) + "  by " + ev.by);
            else if (ev.kind === "Released") say("Released  " + board.nameOf(ev.id) + "  - no one holds");
            else if (ev.kind === "Marked") say("Marked    " + board.nameOf(ev.id) + "  " + ev.state);
        });
        this._offTrace = steward.trace(function (t) {
            if (t.kind === "KeyDown") say("key " + t.key + "  → " + t.route + (t.route === "holder" ? " " + board.nameOf(t.to) + (t.taken ? ", taken" : ", left") : ""));
        });

        // the root's default allocation, named once the page is laid out: entering the page is the user's own act
        Keys.home(home.focus);
        entering.expect("the home, from the start");
        entering.verdict(steward.holder() === home.focus.id, "the keys at: " + board.nameOf(steward.holder()));
        toggled();
        this.root = el;
    }

    dispose() {
        Keys.home(null);
        this._offTrace(); this._offKb(); this._board.dispose();
        this._steward.dispose(); this._monitor.dispose();
        this._viewers.forEach(function (v) { v.dispose(); });
        this._parts.forEach(function (p) { p.dispose(); });
    }
}

function appMain(el, params) {
    el.appendChild(new ViewerFocusWidget(domOpsParty.createBranch("viewerFocusPage"), params).root);
}
