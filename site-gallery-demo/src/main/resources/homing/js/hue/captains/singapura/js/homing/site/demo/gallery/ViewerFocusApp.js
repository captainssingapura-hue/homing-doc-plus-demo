// =============================================================================
// ViewerFocusApp — an experiment: an SVG viewer as a widget under the focus
// model, and where the keys go when its native control lets them go. The
// viewer is a member of its own focus party; the viewport inside it is the
// native world. The chain to validate, one Escape in the viewport:
//   1. the viewport lets go - a yield from it;
//   2. the steward finds the widget holding it, and asks it first;
//   3. the widget, with nothing designed for holding the keys, is passed by;
//   4. the keys reach the first super-ordinate that would hold them;
//   5. none would - as in the doc reader - and the root's default has them:
//      the home, a leaf standing in for the reader's contents, named once the
//      page is laid out, so it holds from the start.
// Three viewers: one straight at the root, one in a panel that would hold
// what is yielded to it, one in a panel that would not. The monitors say
// where the keys are after every press and every key, and the log says every
// grant, release, mark and the route of every key.
// =============================================================================

const _owner = Object.freeze({ toString: () => "viewerFocusPage" });

class ViewerFocusWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        var el = branch.createElement("root", "div");

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-ui-panzoom · homing-ui-focus · an experiment";
        el.appendChild(kicker);
        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Viewers in focus";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "An SVG viewer as a widget: a member of its own focus party, the viewport inside it the native world. A press "
            + "in a viewer claims the keys for it and puts the browser's focus in the viewport: + and − zoom, 0 fits, the arrows pan. "
            + "One Escape there lets the viewport go - a yield from it: the viewer is asked first and, with nothing designed for "
            + "holding the keys, is passed by; the keys go up to the first that would hold them - the panel that catches - or, "
            + "none would, to the root's default: the home, holding from the start, as the doc reader's contents would. Press a "
            + "drawing, then Escape: the monitors and the log say where the keys went.";
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

        // the widgets, hosted as any host hosts one: made from a container and params, their roots grafted where they sit
        var top = focusParty.root;
        var home = new Leaf(branch.createBranch("home"), scene, top, "home — the root's default");
        var catches = new Panel(branch.createBranch("catches"), scene, top, "a panel that catches", true);
        var passes = new Panel(branch.createBranch("passes"), scene, top, "a panel that lets pass", false);
        function viewer(name, host, focusAt, label) {
            var svg = new DOMParser().parseFromString(focusTree, "image/svg+xml").documentElement;   // served as text by ViewerDrawings
            var v = new SvgViewer(host, { svg: svg, label: label });
            branch.graft(name, v.roots.dom);
            focusAt.graft(name, v.roots.focus);
            return v;
        }
        this._viewers = [viewer("atRoot", scene, top, "at the root"), viewer("caught", catches.root, catches.focus, "in the panel that catches"),
                         viewer("passed", passes.root, passes.focus, "in the panel that lets pass")];
        this._parts = [home, catches, passes];

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
        function nameOf(id) { var m = id ? focusParty.find(id) : null; return m ? m.path : String(id); }
        this._offKb = KeyboardStewardInstance.on(function (ev) {
            if (ev.kind === "Granted") say("Granted   " + nameOf(ev.id) + "  by " + ev.by);
            else if (ev.kind === "Taken") say("Taken     " + nameOf(ev.id) + "  by " + nameOf(ev.by));
            else if (ev.kind === "Released") say("Released  " + nameOf(ev.id) + "  - the keys at the root: no one holds");
            else if (ev.kind === "Marked") say("Marked    " + nameOf(ev.id) + "  " + ev.state);
        });
        this._offTrace = KeyboardStewardInstance.trace(function (t) {
            if (t.kind !== "KeyDown") return;
            say("key " + t.key + "  → " + t.route + (t.route === "holder" ? " " + nameOf(t.to) + (t.taken ? ", taken" : ", left") : ""));
        });
        say("three viewers: at the root, in a panel that catches, in one that lets pass; the home named once laid out");
        Keys.home(home.focus);   // the root's default allocation: entering the page is the user's own act, so it holds now
        this.root = el;
    }

    dispose() {
        Keys.home(null);
        this._offTrace(); this._offKb();
        this._steward.dispose(); this._monitor.dispose();
        this._viewers.forEach(function (v) { v.dispose(); });
        this._parts.forEach(function (p) { p.dispose(); });
    }
}

function appMain(el, params) {
    el.appendChild(new ViewerFocusWidget(domOpsParty.createBranch("viewerFocusPage"), params).root);
}
