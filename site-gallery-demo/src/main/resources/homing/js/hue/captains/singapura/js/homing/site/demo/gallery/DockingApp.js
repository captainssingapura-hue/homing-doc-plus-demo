// =============================================================================
// DockingApp — dock and undock. One dock filling the box and a desk over it:
// pull a chip down off the strip and the tab floats under the hand; drag a
// floating pane over the strip and it is offered — the dock lit, the mark
// where it would land — and dropped there it is a tab. A drag along the
// strip only reorders; the Tab key walks the chips. The chips are tabs to
// the design — a hard frame, wide and low — and two sliders set their size
// and their aspect. A pane floats only within the box. Every mutation is a
// line on the log.
// =============================================================================

const _owner = Object.freeze({ toString: () => "dockingPage" });

// The kinds of widget that travel, each a class by the base's contract.
class CardWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        this.root = new CardBuilder().title(params.title).badge(params.badge).text(params.text).aspect(0.6).build(branch.createBranch("card")).root;
    }
}

class CounterWidget {
    constructor(branch, params) {
        var self = this;
        this._value = params.start || 0;
        branch.activate(_owner);
        var root = branch.createElement("counter", "div");
        css.addClass(root, ga_buttons);
        var count = branch.createElement("count", "div");
        css.addClass(count, ga_count);
        var b = new ButtonBuilder().label("Count").onClick(function () { self._value++; draw(); });
        var btn = b.build(branch.createElement("btn", b.tag));
        function draw() { count.textContent = String(self._value); }
        draw();
        root.appendChild(count);
        root.appendChild(btn.el);
        this.root = root;
    }
}

class NoteWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        var root = branch.createElement("note", "p");
        css.addClass(root, ga_lede);
        root.textContent = params.text;
        this.root = root;
    }
}

var _NOTE = "A tab is one record — id, title, widget — and has one placement at a time: in a dock's strip, or afloat in "
    + "a frame of its own. The dock is the multi-tab pane; the desk floats over it. "
    + "Pull this chip down off its strip and the tab floats under your hand; drop the float on the strip and it lands where "
    + "the mark says; let go over content and it stays afloat.";

class DockingWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-ui-docking";
        el.appendChild(kicker);
        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Dock and undock";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "One dock and a desk over it. Drag a chip along the strip: it reorders, and only reorders. Pull it down off "
            + "the strip: the tab floats under your hand. Drag a float over the strip: the dock lights and marks where the tab would land; "
            + "let go and it is a tab there. The Tab key walks the chips. A float stays within the box.";
        el.appendChild(lede);

        // ── the chips' size and aspect ────────────────────────────────────
        var controls = branch.createElement("controls", "div");
        css.addClass(controls, ga_buttons);
        el.appendChild(controls);

        // ── the box: the dock filling it, the desk over it ────────────────
        var box = branch.createElement("box", "div");
        css.addClass(box, ga_dock_box);
        el.appendChild(box);

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
        function sink(ev) {
            switch (ev.kind) {
                case "Undocked":     say("Undocked  " + ev.tabId + "  from " + ev.slotId); break;
                case "Docked":       say("Docked    " + ev.tabId + "  into " + ev.slotId + " at " + ev.index); break;
                case "Opened":       say("Opened    " + ev.id + "  at " + ev.x + "," + ev.y); break;
                case "Released":     say("Released  " + ev.id + "  (left the desk for a dock)"); break;
                case "Moved":        say("Moved     " + ev.id + "  to " + ev.x + "," + ev.y); break;
                case "Resized":      say("Resized   " + ev.id + "  to " + ev.w + "×" + ev.h); break;
                case "Raised":       say("Raised    " + ev.id); break;
                case "Closed":       say("Closed    " + ev.id); break;
                case "TabAttached":  say("Attached  " + ev.tab.id + "  to " + ev.slotId + " at " + ev.atIndex); break;
                case "TabActivated": say("Active    " + ev.slotId + " : " + ev.tabId); break;
                case "TabMoved":     say("Moved tab " + ev.tab.id + "  " + ev.srcIndex + " → " + ev.destIndex + " in " + ev.srcSlotId); break;
                case "TabRemoved":   say("Removed   " + ev.tab.id + "  from " + ev.slotId); break;
                case "TabAdded":     say("Added     " + ev.tab.id + "  to " + ev.slotId); break;
                default:             say(ev.kind);
            }
        }

        var dock = new MultiTabPane(branch.createBranch("dock"), {
            host: box, slotId: "dock", budget: 8, addable: false, onEvent: sink,
            onDragOut: function (tab, e, grab) { docking.undock(dock, tab, e, grab); }
        });
        this._dock = dock;
        controls.appendChild(this._slider(branch, "size", "the tabs' size", -1, 1, 0.1, 0, function (v) { dock.size(v); return v.toFixed(1); }));
        controls.appendChild(this._slider(branch, "aspect", "the tabs' aspect", -1, 1, 0.1, 0, function (v) { dock.aspect(v); return v.toFixed(1) + (v === 0 ? "  the design's" : v > 0 ? "  wider" : "  narrower"); }));
        this._docking = new Docking(branch.createBranch("docking"), { host: box, onEvent: sink });
        var docking = this._docking;
        docking.addDock(dock);

        // the tabs, each on a branch of the page's own: they travel, the page keeps them
        var n = 0;
        function tab(id, title, Widget, params) {
            var own = branch.createBranch("tab-" + id);
            return { id: id, title: title, widget: new Widget(own, params) };
        }
        dock.addTab(tab("card", "Card", CardWidget, { title: "A card in a dock", badge: "TAB", text: "It travels with its tab: dock, float, dock again." }));
        dock.addTab(tab("counter", "Counter", CounterWidget, { start: 0 }));
        dock.addTab(tab("note", "Note", NoteWidget, { text: _NOTE }));
        docking.desk.open({ id: "afloat", title: "Afloat", widget: tab("afloat", "Afloat", NoteWidget, { text: "Drop me on the strip." }).widget, x: 60, y: 200, w: 260, h: 140 });

        this.root = el;
    }

    /** A labelled range with a readout; onValue draws the readout and does the work. */
    _slider(branch, name, label, min, max, step, value, onValue) {
        var wrap = branch.createElement(name + "-wrap", "div");
        css.addClass(wrap, ga_control);
        var lab = branch.createElement(name + "-label", "span");
        css.addClass(lab, ga_control_label);
        lab.textContent = label;
        var range = branch.createElement(name + "-range", "input");
        range.type = "range";
        css.addClass(range, pv_range);
        range.min = min; range.max = max; range.step = step; range.value = value;
        range.setAttribute("aria-label", label);
        var out = branch.createElement(name + "-out", "span");
        css.addClass(out, ga_control_readout);
        function draw() { out.textContent = onValue(Number(range.value)); }
        range.addEventListener("input", draw);
        wrap.appendChild(lab);
        wrap.appendChild(range);
        wrap.appendChild(out);
        draw();
        return wrap;
    }

    dispose() { this._docking.dispose(); this._dock.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new DockingWidget(domOpsParty.createBranch("dockingPage"), params).root);
}
