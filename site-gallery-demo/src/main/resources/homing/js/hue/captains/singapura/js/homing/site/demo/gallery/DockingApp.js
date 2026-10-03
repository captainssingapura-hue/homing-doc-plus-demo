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
// divider between them the grid's, and a desk over both. A CHIP IS CARRIED AS
// A CHIP, by the desk's hand: along its strip it reorders on the rail; pulled
// off past the escape it tears into a float of its own, which follows the
// hand from where it crossed; brought near either
// strip it is captured onto it at once, still in the hand. A float moves as a
// window by its bar's own ground and lands nowhere: docking is by the chip
// alone. The cross on a chip closes it. A right-click
// on a chip, or Shift+F10 on it, opens the tab's menu: Detach floats the tab
// in a pane of its own with no hand, to be dropped on the other dock; Close
// closes it. Two docks, one desk, tabs that travel: the setup the keyboard
// party is stressed on — a tab's widget keeps its branch while its place
// changes.
// EVERY TAB IS A TAB-PANE (RFC 0066 E3, appendix "tab-panes"): its chip and
// its pane minted once on a branch of its own under the page's desk register,
// and carried whole from dock to float and back - the same chip in every strip.
// Detach puts it in a float of its own: one tab in transit, its chip the bar;
// dragged by that chip it is carried as a chip again - a float is never a
// landing itself. The plus's chooser becomes what you pick IN PLACE. The instruments
// are tab-panes too, on a desk and in a register of their own.
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
            + "merge one away: its tabs go to the region you name and its room to the pane across a splitter of its own. Drag a chip along a strip: it "
            + "reorders, on its rail. Pull it off the strip and it tears into a float of its own, which follows your hand from where you "
            + "crossed; bring the chip near either strip and it is in that strip at once, still in your hand - let go and it is a tab "
            + "there. A float's bar moves it as a window and docks nowhere: only its chip docks it. Detach a tab by its menu too — right-click a "
            + "chip, or Shift+F10. Inside a dock, Escape comes back to the tab bar and stops there; F6 asks for the switcher, and picking a "
            + "region is how the keys move between them. THE WORKSPACE IS FLAT, and the grid owns every line in it: the splitter IS the line between two rooms, "
            + "one line shared by both and as thick as the slider says, with the hand reaching past it either way so a hairline is still something you can take "
            + "hold of, and the grid's own outer edge is that same line, so every side of every room is alike whether a neighbour lies beyond it or the end of "
            + "the workspace. Nothing else draws a line - there is nothing round a dock at all, the dock draws no frame, the box holding the grid draws none - and the room "
            + "you are working in is said on its tab bar, lit a shade by the dock itself, because a second outline would say what the lines already say. THE KEYS A DOCK "
            + "ANSWERS are a list it is given, and the third control swaps them while you stand here: the arrows, a browser's Ctrl+Tab, or both, since they do not "
            + "overlap. A browser keeps Ctrl+Tab for its own tabs and hands it to nobody, so in a browser tab that row answers on Ctrl+Shift+\u2190 and Ctrl+Shift+\u2192 "
            + "instead - and it answers them while you are typing in a tab, which is the whole difference between the two schemes. The panel's "
            + "depth registers are shown on the sheets page instead; a workspace is a room full of rooms and lifting one says the wrong thing about the others. The instruments float in a dock of their own — the focus tree, the steward's lamp, the DomOps "
            + "party, the log — so the page watches itself with the same parts it is made of. A NEW TAB is asked for above: the little picture is the "
            + "workspace at the size it really is, so you point at the room you mean, the list says what to mount, and the button is the same call the "
            + "strip's own plus makes, and the third list says HOW the tab arrives - quietly, in front, or in front with the keys - so every way it can go is here "
            + "to try. A room that is full dims rather than disappearing, because where it is belongs to the picture. THE PLUS ON A STRIP asks the "
            + "same question the other way about: it opens a tab with the chooser in it, and what you pick becomes that very tab, in the place you made it. The Tab key walks the chips, "
            + "and the region you are working in is the lit one. THE ROOM'S EDGE is a strip's: the thin lip along the foot of the box is all it takes of the room, "
            + "and the hand brought to it lays the strip over the room's foot - what the room holds, and the switcher offered to the hand as F6 offers it to the keys - "
            + "until the hand leaves it, when the room has its foot back. The workspace's own strip is this one.";
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
        var tabs = null;    // DockingTabs: the source and the new-tab control, built once the scene is up
        var edge = null;    // the strip on the room's edge, built once the regions are

        // what the page reports goes to the instruments' Events tab, which is built below and floats with the rest
        var events = null, waiting = [];
        function say(line) { if (events) events.say(line); else waiting.push(line); }
        function labelOf(tab) { return tab.id + (typeof tab.title === "function" ? " \u201c" + tab.title() + "\u201d" : ""); }   // which tab, and what it says it is
        function sink(ev) {
            if (domopsOf()) domopsOf().refresh();   // the party changes with every one of these
            if (tabs) tabs.refresh();             // and so does how full each dock is, and where the panes are, which is what the new-tab control shows
            if (edge) countRoom();                // and what the strip on the room's edge says it holds
            switch (ev.kind) {
                case "Subdivided":   say("Split     " + nameOf(ev.cellId) + " " + ev.side + " - " + nameOf(ev.newCellId)); break;
                case "Removed":      say("Merged    " + nameOf(ev.cellId) + " - its tabs and its room gone to a neighbour"); break;
                case "TracksChanged": say("Tracks    " + ev.path.join(".") + "  " + ev.ratios.map(function (r) { return r.toFixed(2); }).join(" : ")); break;
                case "Opened":       say("Opened    " + ev.id + "  at " + ev.x + "," + ev.y); break;
                case "Released":     say("Released  " + ev.id + "  (left the desk for a dock)"); break;
                case "Moved":        say("Moved     " + ev.id + "  to " + ev.x + "," + ev.y); break;
                case "Resized":      say("Resized   " + ev.id + "  to " + ev.w + "×" + ev.h); break;
                case "Raised":       say("Raised    " + ev.id); break;
                case "Closed":       say("Closed    " + ev.id); break;
                case "TabActivated": say("Active    " + ev.slotId + " : " + ev.tabId); break;
                case "TabMoved":     say("Moved tab " + labelOf(ev.tab) + "  " + (ev.srcSlotId === ev.destSlotId ? ev.srcIndex + " → " + ev.destIndex + " in " + ev.srcSlotId
                                                                                  : ev.srcSlotId + " → " + ev.destSlotId + " at " + ev.destIndex)); break;
                case "DetachRequested": {   // Shift+Down on a dock that holds the keys: the active tab floats under where its chip is, as the menu's detach does
                    say("Detach?   " + ev.tabId + "  from " + ev.slotId);
                    var tp = desk.register.get(ev.tabId), r = tp ? tp.chip.getBoundingClientRect() : null;
                    if (tp) desk.detach(tp, { x: r.left + 60, y: r.bottom + 14 });
                    break;
                }
                // REMOVED: the tab-pane closed itself - its widget, its branch, its name - so there is nothing to release
                case "TabRemoved":   say("Removed   " + labelOf(ev.tab) + "  from " + ev.slotId); break;
                case "TabAdded":     say("Added     " + labelOf(ev.tab) + "  to " + ev.slotId); break;
                // THE PLUS CANNOT KNOW WHICH KIND YOU MEANT, and a plus that guesses one is worse than a plus that
                // asks. So it opens a tab holding the OPENER, and picking in there turns that same tab into what was
                // picked - the chip stays where it was made. The page says nothing about which kinds; the opener
                // reads them off the source, which is the one list there is.
                case "AddRequested": {
                    var host = desk.docks().filter(function (p) { return p.slotId === ev.slotId; })[0];
                    if (host && tabs) { say("Opener    in " + ev.slotId); tabs.opener(host); }
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
        // THE DESK, the whole: its register - every tab on the page is a tab-pane opened there, owned there for its whole
        // life - its focus branch, where a widget rests while no dock holds it, its floats, and every move between them
        this._desk = new Desk(branch.createBranch("desk"), { host: box, budget: 16, onEvent: sink, keyboard: kb, keyboardId: "docking/desk", menus: menus, focusName: "afloat" });
        var desk = this._desk;
        // THE REGIONS ARE THE DOCK GRID'S: a region is a cell of the grid and a dock in it, nothing between, and parting,
        // merging and closing one is said once, in the component - the workspace is built from the same one (RFC 0066 E3,
        // the workspace detour). The grid draws every line and nothing else draws any; the room being worked in is said on
        // its tab bar, lit a shade, by the dock itself. The page names the regions for its log and its switcher, and asks
        // which region a merge goes into.
        var names = {}, named = 0;
        function nameOf(id) { return names[id] || (names[id] = "region " + (++named)); }
        var docks = new DockGrid(branch.createBranch("docks"), { host: box, desk: desk, menus: menus, onEvent: sink, dock: { addable: true },
            chooseRegion: function (from, others, pick) {
                askRegion("Merge " + nameOf(from.id) + " into", others, 0, function (to) {
                    var plan = pick(to);
                    if (!plan.ok) say("Refused   " + nameOf(from.id) + " into " + nameOf(to.id) + " - " + plan.says);
                });
            },
            layout: { kind: "split", orientation: "horizontal", children: [
                { node: { kind: "cell", id: "left" }, ratio: 1 }, { node: { kind: "cell", id: "right" }, ratio: 1 } ] } });
        this._docks = docks;
        var grid = docks.grid;
        var left = docks.region("left").dock, right = docks.region("right").dock;
        nameOf("left"); nameOf("right");

        // ── the room's edge: a strip over the box's foot, a thin lip all it takes of the room ──
        // What the room holds, and the way to another region by the hand, as F6 is by the keys. It lies over the
        // room while the hand is at the lip or on it: the workspace's control strip is this same component.
        edge = new EdgeStripBuilder().label("The room").host(box).build(branch.createBranch("edge"));
        this._edge = edge;
        var census = branch.createElement("census", "span");
        css.addClass(census, ga_strip_census);
        edge.root.appendChild(census);
        var goTo = new ButtonBuilder();
        var goEl = branch.createElement("go", goTo.tag);
        goTo.label("Go to a region…").plain().size(-1).onClick(function () { if (!switcher) openSwitcher(); }).build(goEl);
        edge.root.appendChild(goEl);
        function countRoom() {
            var regions = docks.regions(), held = 0, afloat = desk.floats().length;
            regions.forEach(function (r) { held += r.dock.count(); });
            census.textContent = regions.length + (regions.length === 1 ? " region" : " regions") + " · " + held + (held === 1 ? " tab" : " tabs")
                + " · " + afloat + " afloat";
        }
        countRoom();

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
                return { label: nameOf(r.id), hint: r.dock.count() + (r.dock.count() === 1 ? " tab" : " tabs") + (shown ? "  \u2014  " + shown : "") };
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
            var holder = kb.holder(), at = 0, regions = docks.regions();
            regions.forEach(function (r, i) { if (r.dock.focus.owner.id === holder || (r.dock.focus.owner.holds && r.dock.focus.owner.holds(focusParty.find(holder) || {}))) at = i; });
            askRegion("Go to a region", regions, at, function (r) { Keys.claim(r.dock.focus.owner); say("Went to   " + nameOf(r.id)); });
        }
        var switches = 0;
        function domopsOf() { return instruments ? instruments.domops() : null; }
        controls.appendChild(new SliderBuilder().keyboard(kb, "docking/lines").label("the lines").range(0, 6, 1).detent(1).value(1).icon("size")
            .format(function (v) { return v === 0 ? "none" : v + "px"; })
            .onInput(function (v) { grid.thickness(v); }).labelWidth("6em")
            .build(branch.createBranch("lines")).root);
        controls.appendChild(new SliderBuilder().keyboard(kb, "docking/size").label("the tabs' size").axis().icon("size").labelWidth("9em").onInput(function (v) { docks.regions().forEach(function (r) { r.dock.size(v); }); }).format(function (v) { return v.toFixed(1); }).build(branch.createBranch("size")).root);
        controls.appendChild(new SliderBuilder().keyboard(kb, "docking/aspect").label("the tabs' aspect").axis().icon("aspect").labelWidth("9em").onInput(function (v) { docks.regions().forEach(function (r) { r.dock.aspect(v); }); })
            .format(function (v) { return v.toFixed(1) + (v === 0 ? "  the design's" : v > 0 ? "  wider" : "  narrower"); }).build(branch.createBranch("aspect")).root);
        // the tab menu's picks are the desk's, which was handed the steward: Detach floats the tab under its chip - off
        // on a tab afloat already - and Close asks it to close

        // ── the instruments ───────────────────────────────────────────────
        // A floating dock of their own on a DESK of their own, the page's whole section for a floor: they watch the workspace
        // and are not part of it, so no dock of it offers to take them - DockingMonitors' Instruments.
        var instruments = new Instruments(branch.createBranch("instruments"), { host: el, keyboard: kb, onEvent: sink });
        this._instruments = instruments;
        events = instruments.events;
        waiting.forEach(function (line) { events.say(line); });

        var store = new BooksStore();
        var domain = branch.createBranch("domain");
        domain.activate(_owner);
        // WHERE THE TABS COME FROM, all of them: DockingTabs holds the source and the new-tab control, and the four
        // this page opens with are asked for exactly the way a later one is.
        tabs = new DockingTabs(branch.createBranch("tabs"), { host: newRow, store: store, domain: domain, note: _NOTE,
            desk: desk,
            panes: function () { return docks.regions().map(function (r) { return r.dock; }); },
            onAdded: function (pane, tab) { var r = docks.regionOf(pane); say("Added     " + tab.id + "  to " + (r ? nameOf(r.id) : pane.slotId)); } });
        this._tabs = tabs;
        // QUIET: a page seeding its own workspace is not answering anybody, so nothing is brought forward and
        // nothing takes the keys - the pane's own rule leaves the first of each pair showing, which is all that is
        // wanted. The plus and the control say otherwise, each in its own way.
        tabs.addTo(left, "books", "quiet");
        tabs.addTo(left, "shelves", "quiet");
        tabs.addTo(right, "plate", "quiet");
        tabs.addTo(right, "note", "quiet");
        desk.open({ id: "afloat", title: "Afloat", make: function (b, t) { return new PictureTab(b, { focus: t.focus, title: "A plate afloat" }); } },
                  desk.float({ x: 60, y: 210, w: 280, h: 190 }).host);

        this.root = el;
    }

    dispose() { this._offMenus(); this._offF6(); this._edge.dispose(); this._desk.dispose(); this._instruments.dispose();   // the desks first: a dock is disposed only empty
                if (this._ownMenus) this._ownMenus.dispose(); this._tabs.dispose();
                this._docks.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new DockingWidget(domOpsParty.createBranch("dockingPage"), params).root);
}
