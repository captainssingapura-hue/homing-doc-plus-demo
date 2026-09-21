// =============================================================================
// DockingApp — dock and undock. One dock filling the box and a desk over it:
// drag a floating pane over the strip and it is offered — the dock lit, the
// mark where it would land — and dropped there it is a tab; the cross on a
// chip closes it. A drag along the strip reorders, on its rail — pulling a
// tab off to float is being worked out on the tab strip page and comes here
// after. A right-click on a chip, or Shift+F10 on it, opens the tab's menu:
// Detach floats the tab in a pane of its own with no hand, Close closes it.
// The menu is the pane's own need — this page declares no kind; it holds a
// steward and answers the picks. The Tab key walks the chips. The chips are
// tabs to the design — a hard frame, wide and low — and two sliders set
// their size and their aspect. A pane floats only within the box. Every
// mutation is a line on the log.
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
    + "Drop the float on the strip and it lands where the mark says; let go over content and it stays afloat.";

class DockingWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");
        // the keys, through the party: the page's steward, made by the chrome and handed in the params;
        // the members' ids qualified by the page, since the shell's one party has every page's members in it
        var kb = params && params.keyboard;
        if (!kb) throw new Error("[gallery] the page's keyboard steward is required: params.keyboard");

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
        lede.textContent = "One dock and a desk over it. Drag a chip along the strip: it reorders, on its rail. Drag a float over the "
            + "strip: the dock lights and marks where the tab would land; let go and it is a tab there. The Tab key walks the chips. "
            + "A float stays within the box. Pulling a tab off to float comes here once the strip has it.";
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

        // the menu steward: the shell's when handed one (one per document), else the page's own; the kinds it holds are derived — the pane names the tab menu as its need
        var menus = params && params.menus ? params.menus : new ContextMenuSteward(branch.createBranch("menus"), { types: MENUS, keyboard: kb, keyboardId: "docking/menus" });
        this._ownMenus = params && params.menus ? null : menus;
        this._offMenus = menus.on(function (ev) {
            switch (ev.kind) {
                case "Opened": say("Menu      " + ev.menuKind + "  at " + Math.round(ev.x) + "," + Math.round(ev.y)); break;
                case "Picked": say("Picked    " + ev.menuKind + " / " + ev.itemId); break;
                case "Closed": say("Menu      " + ev.menuKind + "  " + ev.reason); break;
                default:       say(ev.kind);
            }
        });
        var dock = new MultiTabPane(branch.createBranch("dock"), { host: box, slotId: "dock", budget: 8, addable: false, onEvent: sink, menus: menus, keyboard: kb, keyboardId: "docking/dock" });
        this._dock = dock;
        controls.appendChild(new SliderBuilder().keyboard(kb, "docking/size").label("the tabs' size").axis().icon("size").labelWidth("9em").onInput(function (v) { dock.size(v); }).format(function (v) { return v.toFixed(1); }).build(branch.createBranch("size")).root);
        controls.appendChild(new SliderBuilder().keyboard(kb, "docking/aspect").label("the tabs' aspect").axis().icon("aspect").labelWidth("9em").onInput(function (v) { dock.aspect(v); })
            .format(function (v) { return v.toFixed(1) + (v === 0 ? "  the design's" : v > 0 ? "  wider" : "  narrower"); }).build(branch.createBranch("aspect")).root);
        this._docking = new Docking(branch.createBranch("docking"), { host: box, onEvent: sink, keyboard: kb, keyboardId: "docking/desk" });
        var docking = this._docking;
        docking.addDock(dock);
        // the tab menu's picks: detach floats the tab under where its chip was, with no hand; close removes it
        menus.handle(MultiTabPane.MENU, {
            pick: function (id, o) {
                if (id === "detach") { var r = o.anchor.getBoundingClientRect(); docking.undockAt(o.pane, o.tab, { x: r.left + 60, y: r.bottom + 14 }); }
                else if (id === "close") o.pane.removeTab(o.tab.id);
            },
            state: function (id, o) { return { disabled: !!o.tab.pinned || o.tab.closable === false }; }
        });

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

    dispose() { this._offMenus(); if (this._ownMenus) this._ownMenus.dispose(); this._docking.dispose(); this._dock.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new DockingWidget(domOpsParty.createBranch("dockingPage"), params).root);
}
