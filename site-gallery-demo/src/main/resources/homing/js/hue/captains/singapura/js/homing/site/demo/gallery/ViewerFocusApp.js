// =============================================================================
// ViewerFocusApp — an experiment: an SVG viewer, alone, as a widget under the
// focus model as designed and nothing added. The viewer is a member of its own
// focus party, grafted at the root of the page's; the viewport inside it is
// the native world. Nothing else on the page joins the party, so whatever the
// viewer gives up has nowhere to go but up the tree to the root, where no one
// holds. The question it answers: when the viewer is let go of from its
// native control, where do the keys end up, and after how many keys. The
// monitors say where the keys are after every press and every key - the tree
// with the holder lit, the steward's lamp - and the log says every grant,
// every release, every move of the marker and the route of every key.
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
        title.textContent = "A viewer in focus";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "An SVG viewer, alone, as a widget under the focus model as it was designed, nothing added: a member of its "
            + "own focus party, grafted at the root of the page's; the viewport inside it is the native world, with its own keys. "
            + "A press in the viewer claims the keys for it, and it puts the browser's focus in the viewport: + and − zoom, 0 fits, "
            + "the arrows pan. Nothing else here joins the party, so what the viewer gives up goes up the tree to the root, where no "
            + "one holds. Press the drawing, then Escape, and Escape again: the monitors and the log say where the keys are after each.";
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

        // the widget, hosted as any host hosts one: made from its container and params, its roots grafted
        var svg = new DOMParser().parseFromString(focusTree, "image/svg+xml").documentElement;   // served as text by ViewerDrawings
        this._viewer = new SvgViewer(scene, { svg: svg, label: "The viewer's own focus tree" });
        branch.graft("viewer", this._viewer.roots.dom);
        focusParty.root.graft("viewer", this._viewer.roots.focus);

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
        say("one viewer, grafted at the root; press the drawing, then Escape, and Escape again");
        this.root = el;
    }

    dispose() {
        this._offTrace(); this._offKb();
        this._steward.dispose(); this._monitor.dispose();
        this._viewer.dispose();
    }
}

function appMain(el, params) {
    el.appendChild(new ViewerFocusWidget(domOpsParty.createBranch("viewerFocusPage"), params).root);
}
