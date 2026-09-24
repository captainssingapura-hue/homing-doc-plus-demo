// =============================================================================
// DockingApp — dock and undock, and the room the docks sit in. Regions in a
// split grid, each a dock in a cell of it and nothing in between - no panel,
// no frame, nothing functional
// goes through it. RIGHT-CLICK A TAB BAR'S OWN GROUND - the room the chips
// leave - and the page offers the split menu: part the region beside or
// below, MERGE IT AWAY - the tabs to the region named, the room to the pane
// across a splitter of this one's own - or close it, which is the merge with
// nobody named. THE INSTRUMENTS - the focus tree, the steward's
// lamp, the DomOps party, the page's own log - are a dock like any other, in a
// pane that floats over the work ON A DESK OF ITS OWN: they watch the workspace
// and are not part of it, so no dock offers to take them and the box does not
// pen them in. F6 ASKS FOR THE SWITCHER, a modal that is the
// only way the keys move between docks: inside one, Escape comes back to the
// tab bar and stops there. The
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

// What the tabs hold is DockingScene's: the books as a relation grid, the shelves as a tree, a picture, a note. Each is
// a member of the focus branch handed in as params.focus - the dock's, or the page's own afloat branch - and a dock
// adopts the membership when the tab lands on it, so a widget's place in the tree follows its tab.

var _NOTE = "A tab is one record — id, title, widget — and has one placement at a time: in a dock's strip, or afloat in "
    + "a frame of its own. The dock is the multi-tab pane; the desk floats over it. Drop the float on a strip and it lands where "
    + "the mark says; let go over content and it stays afloat. Right-click the ground of a strip to part the room.";

class DockingWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");
        css.addClass(el, ga_floor);   // the instruments' desk lies over the whole section, not over the workspace's box
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
        lede.textContent = "Docks in a split grid and a desk over them, holding what a workspace holds: the books as a relation grid, the shelves as a "
            + "relation tree, a picture zoomed by its own keys. Right-click the empty ground of a tab bar to part the room — beside or below — or to "
            + "merge one away: its tabs go to the region you name and its room to the pane across a splitter of its own. Detach a tab by its menu — right-click a chip, or Shift+F10 — and it floats; drag the "
            + "float over any strip: the dock lights and marks where the tab would land; let go and it is a tab there. Drag a chip along a strip: it "
            + "reorders, on its rail. Inside a dock, Escape comes back to the tab bar and stops there; F6 asks for the switcher, and picking a "
            + "region is how the keys move between them. THE WORKSPACE IS FLAT, and the grid owns every line in it: the splitter IS the line between two rooms, "
            + "one line shared by both and as thick as the slider says, with the hand reaching past it either way so a hairline is still something you can take "
            + "hold of, and the grid's own outer edge is that same line, so every side of every room is alike whether a neighbour lies beyond it or the end of "
            + "the workspace. Nothing else draws a line - there is nothing round a dock at all, the dock draws no frame, the box holding the grid draws none - and the room "
            + "you are working in is said on its tab bar, lit a shade by the dock itself, because a second outline would say what the lines already say. The panel's "
            + "depth registers are shown on the sheets page instead; a workspace is a room full of rooms and lifting one says the wrong thing about the others. The instruments float in a dock of their own — the focus tree, the steward's lamp, the DomOps "
            + "party, the log — so the page watches itself with the same parts it is made of. A NEW TAB is asked for above: the little picture is the "
            + "workspace at the size it really is, so you point at the room you mean, the list says what to mount, and the button is the same call the "
            + "strip's own plus makes, and the third list says HOW the tab arrives - quietly, in front, or in front with the keys - so every way it can go is here "
            + "to try. A room that is full dims rather than disappearing, because where it is belongs to the picture. THE PLUS ON A STRIP asks the "
            + "same question the other way about: it opens a tab with the chooser in it, and what you pick becomes that very tab, in the place you made it. The Tab key walks the chips, "
            + "and the region you are working in is the lit one.";
        el.appendChild(lede);

        // ── the chips' size and aspect ────────────────────────────────────
        var controls = branch.createElement("controls", "div");
        css.addClass(controls, ga_buttons);
        el.appendChild(controls);
        // the new-tab control's own row, above the workspace it points at: its picture of the panes is the same
        // arrangement you are looking at, so it wants to be read next to it rather than tucked among the sliders
        var newRow = branch.createElement("new-row", "div");
        css.addClass(newRow, ga_buttons);
        el.appendChild(newRow);

        // ── the box: two docks in a split, the desk over both ─────────────
        var box = branch.createElement("box", "div");
        css.addClass(box, ga_dock_box);
        el.appendChild(box);
        // the seam: the workspace is flat, and flat is not the same as featureless - a hairline says where one region
        // ends and the next begins, which the panels' own edges are too pale to do against the ground
        var grid = new SplitGrid(branch.createBranch("grid"), { host: box, minCellPx: 160, seam: true, thickness: 1,
            onEvent: function () { if (tabs) tabs.refresh(); },   // a splitter moved, a room was parted: the picture is of where the panes ARE
            layout: { kind: "split", orientation: "horizontal", children: [
            { node: { kind: "cell", id: "left" }, ratio: 1 }, { node: { kind: "cell", id: "right" }, ratio: 1 } ] } });
        this._grid = grid;
        var tabs = null;    // DockingTabs: the source and the new-tab control, built once the scene is up

        // what the page reports goes to the instruments' Events tab, which is built below and floats with the rest
        var events = null, waiting = [];
        function say(line) { if (events) events.say(line); else waiting.push(line); }
        function sink(ev) {
            if (domopsOf()) domopsOf().refresh();   // the party changes with every one of these
            if (tabs) tabs.refresh();             // and so does how full each dock is, which is what the new-tab control shows
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
                    var d = null; for (var k = 0; k < regions.length; k++) if (regions[k].id === ev.slotId) d = regions[k].dock;
                    var i = d ? d.tabIndexOf(ev.tabId) : -1;
                    if (i < 0) break;
                    var r = d.el.children[0].children[i].getBoundingClientRect();
                    docking.undockAt(d, { id: ev.tabId, title: d.getState().tabs[i].title }, { x: r.left + 60, y: r.bottom + 14 });
                    break;
                }
                // REMOVED, not detached: the tab is gone for good, so the source dissolves the branch it was built
                // on - the only thing that frees the name for the party, and the reason a source hands names out
                case "TabRemoved":   say("Removed   " + ev.tab.id + "  from " + ev.slotId); if (tabs) tabs.release(ev.tab.id); break;
                case "TabAdded":     say("Added     " + ev.tab.id + "  to " + ev.slotId); break;
                // THE PLUS CANNOT KNOW WHICH KIND YOU MEANT, and a plus that guesses one is worse than a plus that
                // asks. So it opens a tab holding the OPENER, and picking in there turns that same tab into what was
                // picked - the chip stays where it was made. The page says nothing about which kinds; the opener
                // reads them off the source, which is the one list there is.
                case "AddRequested": {
                    var mine = regionById(ev.slotId);
                    if (mine && tabs) { say("Opener    in " + mine.name); tabs.opener(mine.dock); }
                    break;
                }
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
        // ── the desk, then the regions on the grid ───────────────────────
        this._docking = new Docking(branch.createBranch("docking"), { host: box, onEvent: sink, keyboard: kb, keyboardId: "docking/desk" });
        var docking = this._docking;
        // A REGION IS A CELL OF THE GRID AND A DOCK IN IT. Nothing sits between them: a panel round a dock would be
        // a box that draws no line, takes no keys and holds nothing the cell does not already hold - and the one job
        // it had left was watching the dock for where the keys are and telling the dock about itself, a loop through
        // a third object for a fact the pane is handed directly. The cell is already a flex column that fills its
        // room; the dock stretches into it.
        var regions = [], named = 0;
        this._regions = regions;
        function region(id) {
            var name = "region " + (++named);   // for the log alone: nothing shows a name, the chips say what is in it
            var dock = new MultiTabPane(branch.createBranch("dock-" + id), { host: grid.cell(id), slotId: id, budget: 8, addable: true,
                                                                            onEvent: sink, menus: menus, stripMenu: "split", focusName: "dock-" + id });
            docking.addDock(dock);
            var r = { id: id, name: name, dock: dock };
            regions.push(r);
            return r;
        }
        function regionOf(pane) { for (var i = 0; i < regions.length; i++) if (regions[i].dock === pane) return regions[i]; return null; }
        function part(r, side) { var made = region(grid.subdivide(r.id, side)); say("Split     " + r.name + " " + side + " - " + made.name); if (tabs) tabs.refresh(); }
        // ONE OWNER FOR THE LINES, AND THE HINT IS NOT A LINE. The grid draws every line in the workspace and nothing
        // else draws any: there is nothing round a dock to draw one, the dock never drew a frame, and the box that
        // holds the grid stopped drawing one, because the grid draws its OWN outer edge in the same line as the rest.
        // The splitter IS the line - one of them shared by two rooms, flush on both sides, with the hand reaching 3px
        // past it either way - so there is no gutter to notice. And the room being worked in is said on its TAB BAR,
        // lit a shade, by the dock itself: a second set of lines around one room would say what the grid's lines
        // already say. Flat throughout; the chips keep their lift, which is theirs.

        function regionById(id) { for (var k = 0; k < regions.length; k++) if (regions[k].id === id) return regions[k]; return null; }
        // The panes this region's room can go to WHOLE: those across a splitter of its own - a whole divider with this
        // region alone on one side and that pane alone on the other. At most two, and the grid says which.
        function acrossOf(r) {
            return grid.splitters(r.id).map(function (s) {
                return { way: s.axis === "horizontal" ? (s.side === "before" ? "left" : "right") : (s.side === "before" ? "up" : "down"),
                         region: regionById(s.id) };
            }).filter(function (n) { return n.region; });
        }
        function towards(r, way) { var n = acrossOf(r).filter(function (x) { return x.way === way; })[0]; return n ? n.region : null; }
        // where the room would go with nobody named: one pane across a splitter, or the group beside it - the tabs follow it
        function heirOf(r) { var ids = grid.heirs(r.id); for (var k = 0; k < regions.length; k++) if (ids.indexOf(regions[k].id) >= 0) return regions[k]; return null; }
        // ONE OPERATION, TWO DESTINATIONS. The tabs go to the region named - any region, since moving tabs asks nothing
        // of the geometry. The room goes where the grid can put it: the whole of it to that same region when they share
        // a splitter of their own, else to the neighbour holding it. And it is all or nothing: the plan is made from the
        // two docks' names and the budget before a single tab moves, so a merge cannot stop half way and strand a widget.
        function mergeRegion(r, to) {
            if (regions.length < 2 || !to || to === r) { say("Refused   the last region stays"); return; }
            var plan = PaneMerge.plan(r.dock.tabs(), to.dock.tabs(), to.dock.budget());
            if (!plan.ok) { say("Refused   " + r.name + " into " + to.name + " - " + plan.says); return; }
            var whole = acrossOf(r).some(function (n) { return n.region === to; });
            plan.ids.forEach(function (id) { to.dock.attachTab(r.dock.detachTab(id), to.dock.count()); });
            docking.removeDock(r.dock);
            r.dock.dispose();
            if (tabs) tabs.refresh();
            grid.remove(r.id, to.id);
            regions.splice(regions.indexOf(r), 1);
            say("Merged    " + r.name + " into " + to.name + (whole ? " - its room went with them" : " - its room went to the neighbour"));
        }
        var left = region("left").dock, right = region("right").dock;

        // ── moving BETWEEN regions is its own gesture ─────────────────────
        // Inside a dock, Escape comes back to the bar and stops there: nothing overshoots out of the room. To go to
        // another region you ask for the switcher - F6, a key of the page's, which the steward offers before anyone,
        // so it works wherever the hand is - and pick one. Inter-pane movement and intra-pane work never share a key.
        var switcher = null;
        this._offF6 = kb.shortcut(function (ev) {
            if (ev.key !== "F6" || ev.ctrlKey || ev.altKey || ev.metaKey) return false;
            if (switcher) { switcher.close(); return true; }
            openSwitcher();
            return true;
        });
        // the same list, asked twice: which region to go to, and which one a merge's tabs go into
        function askRegion(title, of, at, pick) {
            var rows = of.map(function (r) {
                var active = r.dock.activeTab(), shown = null;
                r.dock.getState().tabs.forEach(function (t) { if (t.id === active) shown = t.title; });
                return { label: r.name, hint: r.dock.count() + (r.dock.count() === 1 ? " tab" : " tabs") + (shown ? "  \u2014  " + shown : "") };
            });
            var list = null;
            switcher = new Dialog(branch.createBranch("switcher-" + (++switches)), {
                title: title, keyboard: kb, keyboardId: "docking/switcher", size: { w: 360, h: 260 },
                content: function (b, body) {
                    list = new RegionList(b.createBranch("regions"), body, rows, at, function (i) { switcher.close(); pick(of[i]); });
                    return { onKeydown: function (ev) { return list.keyDown(ev); } };
                },
                onClose: function () { switcher = null; }
            });
        }
        function openSwitcher() {
            var holder = kb.holder(), at = 0;
            regions.forEach(function (r, i) { if (r.dock.focus.owner.id === holder || (r.dock.focus.owner.holds && r.dock.focus.owner.holds(focusParty.find(holder) || {}))) at = i; });
            askRegion("Go to a region", regions.slice(), at, function (r) { Keys.claim(r.dock.focus.owner); say("Went to   " + r.name); });
        }
        var switches = 0;
        function domopsOf() { return monitors && monitors.has("domops") ? monitors.widgetOf("domops") : null; }
        // the tab bar's own ground, right-clicked: the page's menu about the room the dock sits in
        menus.handle("split", {
            pick: function (id, o) {
                var r = regionOf(o.pane);
                if (!r) return;
                if (id === "beside") part(r, "right");
                else if (id === "below") part(r, "bottom");
                else if (id === "close") mergeRegion(r, heirOf(r));
                else if (id === "merge-into") askRegion("Merge " + r.name + " into", regions.filter(function (x) { return x !== r; }), 0,
                                                       function (to) { mergeRegion(r, to); });
                else if (id.indexOf("merge-") === 0) mergeRegion(r, towards(r, id.slice(6)));
            },
            // a direction with no pane across a splitter of this region's own is not offered at all; one whose dock
            // cannot take the tabs is offered and refused, so the reason can be read rather than guessed at
            state: function (id, o) {
                var r = regionOf(o.pane), to;
                if (id === "close" || id === "merge-into") return { hidden: id === "merge-into" && !r, disabled: !r || regions.length < 2 };
                if (id.indexOf("merge-") !== 0) return { disabled: !r };   // every row here is about a region, and a dock afloat is not one
                to = r && towards(r, id.slice(6));
                return { hidden: !to, disabled: !!to && !PaneMerge.plan(r.dock.tabs(), to.dock.tabs(), to.dock.budget()).ok };
            }
        });
        controls.appendChild(new SliderBuilder().keyboard(kb, "docking/lines").label("the lines").range(0, 6, 1).detent(1).value(1).icon("size")
            .format(function (v) { return v === 0 ? "none" : v + "px"; })
            .onInput(function (v) { grid.thickness(v); }).labelWidth("6em")
            .build(branch.createBranch("lines")).root);
        controls.appendChild(new SliderBuilder().keyboard(kb, "docking/size").label("the tabs' size").axis().icon("size").labelWidth("9em").onInput(function (v) { regions.forEach(function (r) { r.dock.size(v); }); }).format(function (v) { return v.toFixed(1); }).build(branch.createBranch("size")).root);
        controls.appendChild(new SliderBuilder().keyboard(kb, "docking/aspect").label("the tabs' aspect").axis().icon("aspect").labelWidth("9em").onInput(function (v) { regions.forEach(function (r) { r.dock.aspect(v); }); })
            .format(function (v) { return v.toFixed(1) + (v === 0 ? "  the design's" : v > 0 ? "  wider" : "  narrower"); }).build(branch.createBranch("aspect")).root);
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
        // ── the instruments ───────────────────────────────────────────────
        // A dock like any other, in a pane that floats: the focus tree, the steward's lamp, the DomOps party and the
        // page's own log, one to a tab. They watch the page from inside it and obey the law the other widgets obey.
        var monHost = branch.createElement("instruments", "div");   // the float's widget: a box the dock fills
        css.addClass(monHost, ga_tab_fill);
        var monitors = new MultiTabPane(branch.createBranch("monitors"), { host: monHost, slotId: "monitors", budget: 6, addable: false,
                                                                          menus: menus, stripMenu: "split", focusName: "monitors" });
        function instrument(id, title, Widget) { return { id: id, title: title, widget: new Widget(branch.createBranch("mon-" + id), { focus: monitors.focus }) }; }
        monitors.addTab(instrument("events", "Events", EventsTab));
        monitors.addTab(instrument("focus", "Focus", FocusTab));
        monitors.addTab(instrument("steward", "Steward", StewardTab));
        monitors.addTab(instrument("domops", "DomOps", DomOpsTab));
        events = monitors.widgetOf("events");
        waiting.forEach(function (line) { events.say(line); });
        this._monitors = monitors;
        var domops = monitors.widgetOf("domops");
        // A DESK OF THEIR OWN, and the page's whole section for a floor. The instruments watch the workspace; they are
        // not part of it - no dock will ever offer to take them, because docking's protocol is between ITS desk and its
        // docks, and this is not that desk - and they are not penned into the box the workspace lives in.
        this._stage = new Desk(branch.createBranch("instruments-desk"), { host: el, layer: true, onEvent: sink,
                                                                         keyboard: kb, keyboardId: "docking/instruments" });
        // over the work rather than over the heading: it is free to go anywhere on the section, but it should not open
        // on top of what the page says about itself
        this._stage.open({ id: "instruments", title: "Instruments", widget: { root: monHost, dispose: function () {} },
                           x: 34, y: 430, w: 420, h: 300 });

        var store = new BooksStore();
        var domain = branch.createBranch("domain");
        domain.activate(_owner);
        // WHERE THE TABS COME FROM, all of them: DockingTabs holds the source and the new-tab control, and the four
        // this page opens with are asked for exactly the way a later one is.
        tabs = new DockingTabs(branch.createBranch("tabs"), { host: newRow, store: store, domain: domain, note: _NOTE,
            panes: function () { return regions.map(function (r) { return r.dock; }); },
            onAdded: function (pane, tab) { var r = regionOf(pane); say("Added     " + tab.id + "  to " + (r ? r.name : pane.slotId)); } });
        this._tabs = tabs;
        // QUIET: a page seeding its own workspace is not answering anybody, so nothing is brought forward and
        // nothing takes the keys - the pane's own rule leaves the first of each pair showing, which is all that is
        // wanted. The plus and the control say otherwise, each in its own way.
        tabs.addTo(left, "books", "quiet");
        tabs.addTo(left, "shelves", "quiet");
        tabs.addTo(right, "plate", "quiet");
        tabs.addTo(right, "note", "quiet");
        docking.desk.open({ id: "afloat", title: "Afloat", widget: tab("afloat", "Afloat", PictureTab, { title: "A plate afloat" }, afloat).widget, x: 60, y: 210, w: 280, h: 190 });

        this.root = el;
    }

    dispose() { this._offMenus(); this._offF6(); if (this._ownMenus) this._ownMenus.dispose(); this._tabs.dispose(); this._docking.dispose();
                this._regions.forEach(function (r) { r.dock.dispose(); }); this._stage.dispose(); this._monitors.dispose(); this._afloat.owner.leave(); this._grid.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new DockingWidget(domOpsParty.createBranch("dockingPage"), params).root);
}
