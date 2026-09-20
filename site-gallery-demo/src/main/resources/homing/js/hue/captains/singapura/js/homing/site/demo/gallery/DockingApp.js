// =============================================================================
// DockingApp — dock and undock. Three docks in a split — two on top, one below
// — and a desk over them: pull a chip off a strip and it floats under the
// hand; drag a floating pane over a dock and it is offered — the dock lit,
// the mark where it would land — and dropped there it is a tab. A pane
// floats only within the box. Every mutation is a line on the log.
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
    + "a frame of its own. The dock is the multi-tab pane; the split only subdivides; the desk floats over both. "
    + "Pull this chip off its strip and the tab floats under your hand; drop the float on a strip and it lands where "
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
        lede.textContent = "Three docks in a split — two on top, one below — and a desk over them. Pull a chip off a strip: it floats "
            + "under your hand. Drag a float over a dock: the dock lights and marks where the tab would land; let go and it is a tab there. "
            + "A float stays within the box.";
        el.appendChild(lede);

        // ── the box: the split of docks, the desk over it ─────────────────
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

        var split = new SplitPane(branch.createBranch("split"), {
            host: box, minPanePx: 120, onEvent: sink,
            layout: { kind: "split", orientation: "vertical", children: [
                { pane: { kind: "split", orientation: "horizontal", children: [
                    { pane: { kind: "leaf", slotId: "left" }, ratio: 1 },
                    { pane: { kind: "leaf", slotId: "right" }, ratio: 1 } ] }, ratio: 3 },
                { pane: { kind: "leaf", slotId: "bottom" }, ratio: 2 } ] }
        });
        this._docking = new Docking(branch.createBranch("docking"), { host: box, onEvent: sink });
        var docking = this._docking;

        this._docks = ["left", "right", "bottom"].map(function (slotId) {
            var dock = new MultiTabPane(branch.createBranch("dock-" + slotId), {
                host: split.slot(slotId), slotId: slotId, budget: 8, addable: false, onEvent: sink,
                onDragOut: function (tab, e) { docking.undock(dock, tab, e); }
            });
            docking.addDock(dock);
            return dock;
        });

        // the tabs, each on a branch of the page's own: they travel, the page keeps them
        var n = 0;
        function tab(id, title, Widget, params) {
            var own = branch.createBranch("tab-" + id);
            return { id: id, title: title, widget: new Widget(own, params) };
        }
        this._docks[0].addTab(tab("card", "Card", CardWidget, { title: "A card in a dock", badge: "TAB", text: "It travels with its tab: dock, float, dock again." }));
        this._docks[0].addTab(tab("counter", "Counter", CounterWidget, { start: 0 }));
        this._docks[1].addTab(tab("note", "Note", NoteWidget, { text: _NOTE }));
        docking.desk.open({ id: "afloat", title: "Afloat", widget: tab("afloat", "Afloat", NoteWidget, { text: "Drop me on a dock." }).widget, x: 60, y: 200, w: 260, h: 140 });

        this._split = split;
        this.root = el;
    }

    dispose() { this._docking.dispose(); this._docks.forEach(function (d) { d.dispose(); }); this._split.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new DockingWidget(domOpsParty.createBranch("dockingPage"), params).root);
}
