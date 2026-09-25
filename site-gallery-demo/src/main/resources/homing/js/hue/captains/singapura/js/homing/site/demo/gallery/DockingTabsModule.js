// =============================================================================
// DockingTabs — where the docking page's tabs come from, and the control that
// puts one somewhere. One object for the whole business, so the page that runs
// a workspace is about the workspace.
//
//   new DockingTabs(branch, { host, panes, store, domain, note, register, place, onAdded? })
//     host:   where the new-tab control goes
//     panes:  () → [pane], asked afresh: the workspace is split and merged
//     store, domain, note: what the KINDS need — each closes over its own, so
//             the source hands a maker only the branch and the dock's branch
//     register, place: the page's desk — every tab is a TAB-PANE opened in its
//             register, and put in a pane by place, the desk's move, so the desk
//             says it arrived (RFC 0066 E3, appendix "tab-panes")
//
//   tabs.addTo(pane, kindId, how?)  a tab of that kind, there, arriving the way
//                              the caller says — the four the page opens with are
//                              asked for exactly this way, and "quiet", so the
//                              pane's own rule leaves the first of them showing
//   tabs.opener(pane)          a tab holding the CHOOSER, WITH THE KEYS: what the
//                              strip's plus asks for. The asking happened in the
//                              strip, so the answering may take the keys; a plus
//                              that opens a chooser you then have to go and find
//                              has asked you a question and walked off
//   tabs.keys                  KeysPicker: which scheme the docks answer, swapped live
//   tabs.release(tabId)        a tab gone for good frees its branch
//   tabs.refresh()             the control says again what can be done
//   tabs.dispose()
//
// EVERY TAB ON THE PAGE COMES THROUGH THE ONE SOURCE — the four it opens with,
// the ones the control adds, the ones the opener becomes. One counter, so no
// two tabs are ever the same name; and the page keeps no second way of making
// a tab that could drift from this one. The first shape of this page had one,
// and the ids collided the day a second "books" was asked for.
// =============================================================================

const _dockingTabsOwner = Object.freeze({ toString: () => "dockingTabs" });

class DockingTabs {
    constructor(branch, opts) {
        if (!branch) throw new Error("[DockingTabs] a branch of its own is required");
        var o = opts || {};
        branch.activate(_dockingTabsOwner);
        this.branch = branch;
        var store = o.store, domain = o.domain, note = o.note == null ? "" : String(o.note);
        // The page's answer to "what can be mounted". Each kind closes over whatever IT needs, so the only things
        // handed to a maker are the branch to build on, the dock's focus branch, and the pane it is going into.
        var source = new TabSource(branch.createBranch("source"), { register: o.register, place: o.place, kinds: [
            { id: "note",    label: "A note",      title: "Notes",   make: function (b, p) { return new NoteTab(b, { focus: p.focus, text: note }); } },
            { id: "plate",   label: "A picture",   title: "Plate",   make: function (b, p) { return new PictureTab(b, { focus: p.focus, title: "A plate" }); } },
            { id: "books",   label: "The books",   title: "Books",   make: function (b, p) { return new BooksTab(b, { focus: p.focus, store: store, domain: domain }); } },
            { id: "shelves", label: "The shelves", title: "Shelves", make: function (b, p) { return new ShelvesTab(b, { focus: p.focus, store: store, domain: domain }); } },
            // LISTED:FALSE — it is what the plus opens, never one of the things you open with it. It is handed the
            // pane and its own tab id because it gives that tab up to whatever is chosen in it.
            { id: "opener",  label: "Open…",      title: "Open",    listed: false,
              make: function (b, p) { return new TabOpener(b, { focus: p.focus, pane: p.pane, tabId: p.id, source: source }); } } ] });
        this.source = source;
        // The control: a picture of the panes to say WHERE, a list to say WHAT. It is handed the docks and nothing
        // about the grid — it measures where they are — so a region minted by a split is in the picture at once, and
        // the instruments' float, which is not of this workspace, is not.
        // MODES ON: this page exists to be tried, so every way a tab can arrive is reachable without an edit and a
        // rebuild - quietly, in front, or in front with the keys. An app that had already made up its mind would
        // leave them off and name one.
        this.adder = new AddTab(branch.createBranch("adder"), { host: o.host, source: source, width: o.width == null ? "148px" : o.width,
                                                                panes: o.panes, onAdded: o.onAdded, modes: true });
        // WHICH KEYS THE DOCKS ANSWER, swapped while you stand in the workspace: a pane's schemes are a list it is
        // given, so changing them is one call per dock and nothing else at all.
        this.keys = new KeysPicker(branch.createBranch("keys"), { host: o.host, panes: o.panes });
    }

    /** A tab of that kind in that pane: the index it landed at, or −1 if the pane had no room. */
    addTo(pane, kindId, how) { return this.source.addTo(pane, kindId, how); }

    /** A tab holding the chooser, holding the keys: the strip's plus, and anything else that asks in the strip. */
    opener(pane) { return this.source.addTo(pane, "opener", "focus"); }

    /** Removed for good, not detached: its branch dissolves, which is the only thing that frees the name. */
    release(tabId) { this.source.release(tabId); return this; }

    /** How full each dock is, where the panes are, and what a dock minted since answers: said again. */
    refresh() { this.adder.refresh(); this.keys.refresh(); return this; }

    dispose() {
        this.keys.dispose();
        this.adder.dispose();
        this.source.dispose();
        try { this.branch.dissolve(); } catch (e) {}
    }
}
