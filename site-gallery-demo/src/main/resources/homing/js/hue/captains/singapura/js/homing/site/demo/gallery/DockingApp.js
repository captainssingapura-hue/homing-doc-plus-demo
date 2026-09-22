// =============================================================================
// DockingApp — dock and undock. Two docks side by side in a split grid, the
// divider between them the grid's, and a desk over both: drag a floating
// pane over either strip and it is offered — the dock lit, the mark where it
// would land — and dropped there it is a tab; the cross on a chip closes it.
// A drag along a strip reorders, on its rail — pulling a tab off to float is
// being worked out on the tab strip page and comes here after. A right-click
// on a chip, or Shift+F10 on it, opens the tab's menu: Detach floats the tab
// in a pane of its own with no hand, to be dropped on the other dock; Close
// closes it. Two docks, one desk, tabs that travel: the setup the keyboard
// party is stressed on — a tab's widget keeps its branch while its place
// changes.
// The menu is the pane's own need — this page declares no kind; it holds a
// steward and answers the picks. The Tab key walks the chips. The chips are
// tabs to the design — a hard frame, wide and low — and two sliders set
// their size and their aspect. A pane floats only within the box. Every
// mutation is a line on the log.
// =============================================================================

const _owner = Object.freeze({ toString: () => "dockingPage" });

// The kinds of widget that travel, each a class by the base's contract and the law (§15.1): a member of the focus
// branch handed in as params.focus - the dock's, or a branch of the page's for one that opens afloat - with activate(),
// a claim; its Escape yields back to whatever holds the branch it is in. A dock adopts the membership when the tab
// lands on it, so a widget's place in the tree follows its tab.
function _join(w, branch, params) { w.focus = params.focus.join(branch.name, w); w._off = Keys.claimOn(w.root, w.focus); }
function _leave(w) { w._off(); if (w.focus.in) w.focus.leave(); }

class CardWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        this.root = new CardBuilder().title(params.title).badge(params.badge).text(params.text).aspect(0.6).build(branch.createBranch("card")).root;
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

class CounterWidget {
    constructor(branch, params) {
        var self = this;
        this._value = params.start || 0;
        branch.activate(_owner);
        var root = branch.createElement("counter", "div");
        css.addClass(root, ga_buttons);
        var count = branch.createElement("count", "div");
        css.addClass(count, ga_count);
        var b = new ButtonBuilder().label("Count").onClick(function () { self._value++; self._draw(); });
        var btn = b.build(branch.createElement("btn", b.tag));
        this._count = count;
        this._draw();
        root.appendChild(count);
        root.appendChild(btn.el);
        this.root = root;
        _join(this, branch, params);
    }
    _draw() { this._count.textContent = String(this._value); }
    activate() { Keys.claim(this.focus); }
    /** While the counter holds: ↑ counts, ↓ counts down, Escape yields. */
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
    dispose() { _leave(this); }
}

class NoteWidget {
    constructor(branch, params) {
        branch.activate(_owner);
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
        lede.textContent = "Two docks in a split and a desk over both. Detach a tab by its menu — right-click a chip, or Shift+F10 — and it "
            + "floats; drag the float over either strip: the dock lights and marks where the tab would land; let go and it is a tab "
            + "there. Drag a chip along a strip: it reorders, on its rail. The Tab key walks the chips. A float stays within the box.";
        el.appendChild(lede);

        // ── the chips' size and aspect ────────────────────────────────────
        var controls = branch.createElement("controls", "div");
        css.addClass(controls, ga_buttons);
        el.appendChild(controls);

        // ── the box: two docks in a split, the desk over both ─────────────
        var box = branch.createElement("box", "div");
        css.addClass(box, ga_dock_box);
        el.appendChild(box);
        var grid = new SplitGrid(branch.createBranch("grid"), { host: box, minCellPx: 160, layout: { kind: "split", orientation: "horizontal", children: [
            { node: { kind: "cell", id: "left" }, ratio: 1 }, { node: { kind: "cell", id: "right" }, ratio: 1 } ] } });
        this._grid = grid;

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
                case "DetachRequested": {   // Shift+Down on a dock that holds the keys: the active tab floats under where its chip is, as the menu's detach does
                    say("Detach?   " + ev.tabId + "  from " + ev.slotId);
                    var d = docks.filter(function (x) { return x.slotId === ev.slotId; })[0], i = d ? d.tabIndexOf(ev.tabId) : -1;
                    if (i < 0) break;
                    var r = d.el.children[0].children[i].getBoundingClientRect();
                    docking.undockAt(d, { id: ev.tabId, title: d.getState().tabs[i].title }, { x: r.left + 60, y: r.bottom + 14 });
                    break;
                }
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
        var docks = ["left", "right"].map(function (side) {
            return new MultiTabPane(branch.createBranch("dock-" + side), { host: grid.cell(side), slotId: side, budget: 8, addable: false, onEvent: sink, menus: menus, focusName: "dock-" + side });
        });
        this._docks = docks;
        var left = docks[0], right = docks[1];
        controls.appendChild(new SliderBuilder().keyboard(kb, "docking/size").label("the tabs' size").axis().icon("size").labelWidth("9em").onInput(function (v) { docks.forEach(function (d) { d.size(v); }); }).format(function (v) { return v.toFixed(1); }).build(branch.createBranch("size")).root);
        controls.appendChild(new SliderBuilder().keyboard(kb, "docking/aspect").label("the tabs' aspect").axis().icon("aspect").labelWidth("9em").onInput(function (v) { docks.forEach(function (d) { d.aspect(v); }); })
            .format(function (v) { return v.toFixed(1) + (v === 0 ? "  the design's" : v > 0 ? "  wider" : "  narrower"); }).build(branch.createBranch("aspect")).root);
        this._docking = new Docking(branch.createBranch("docking"), { host: box, onEvent: sink, keyboard: kb, keyboardId: "docking/desk" });
        var docking = this._docking;
        docks.forEach(function (d) { docking.addDock(d); });
        // the tab menu's picks: detach floats the tab under where its chip was, with no hand; close removes it
        menus.handle(MultiTabPane.MENU, {
            pick: function (id, o) {
                if (id === "detach") { var r = o.anchor.getBoundingClientRect(); docking.undockAt(o.pane, o.tab, { x: r.left + 60, y: r.bottom + 14 }); }
                else if (id === "close") o.pane.removeTab(o.tab.id);
            },
            state: function (id, o) { return { disabled: !!o.tab.pinned || o.tab.closable === false }; }
        });

        // the tabs, each on a branch of the page's own: they travel, the page keeps them; a widget joins the dock it
        // starts in, or the page's own afloat branch until the desk holds one
        var afloat = focusParty.root.createBranch("afloat", this);
        this._afloat = afloat;
        function tab(id, title, Widget, params, into) {
            var own = branch.createBranch("tab-" + id);
            return { id: id, title: title, widget: new Widget(own, Object.assign({}, params, { focus: into })) };
        }
        left.addTab(tab("card", "Card", CardWidget, { title: "A card in a dock", badge: "TAB", text: "It travels with its tab: dock, float, dock again." }, left.focus));
        left.addTab(tab("counter", "Counter", CounterWidget, { start: 0 }, left.focus));
        right.addTab(tab("note", "Note", NoteWidget, { text: _NOTE }, right.focus));
        docking.desk.open({ id: "afloat", title: "Afloat", widget: tab("afloat", "Afloat", NoteWidget, { text: "Drop me on the strip." }, afloat).widget, x: 60, y: 200, w: 260, h: 140 });

        this.root = el;
    }

    dispose() { this._offMenus(); if (this._ownMenus) this._ownMenus.dispose(); this._docking.dispose(); this._docks.forEach(function (d) { d.dispose(); }); this._afloat.owner.leave(); this._grid.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new DockingWidget(domOpsParty.createBranch("dockingPage"), params).root);
}
