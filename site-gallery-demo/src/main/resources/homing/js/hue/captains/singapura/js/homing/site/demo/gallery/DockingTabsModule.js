// =============================================================================
// DockingTabs — where the docking page's tabs come from, and the picker of
// which keys the docks answer. One object for the whole business, so the page
// that runs a workspace is about the workspace.
//
//   new DockingTabs(branch, { host, panes, store, domain, note, desk })
//     host:   where the keys picker goes
//     panes:  () → [pane], asked afresh: the workspace is split and merged
//     store, domain, note: what the KINDS need — each closes over its own, so
//             the source hands a maker only the branch and the dock's branch
//     desk:   the page's Desk — every tab is a TAB-PANE opened in its register
//             and put in a pane by its move, so the desk says it arrived
//             (RFC 0066 E3, appendix "tab-panes")
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
//   tabs.refresh()             the picker says again which docks there are
//   tabs.dispose()
//
// EVERY TAB ON THE PAGE COMES THROUGH THE ONE SOURCE — the four it opens with,
// the ones the opener becomes. One counter, so no
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
        var source = new TabSource(branch.createBranch("source"), { desk: o.desk, kinds: [
            { id: "note",    label: "A note",      title: "Notes",   make: function (b, p) { return new NoteTab(b, { focus: p.focus, text: note }); } },
            { id: "plate",   label: "A picture",   title: "Plate",   make: function (b, p) { return new PictureTab(b, { focus: p.focus, title: "A plate" }); } },
            { id: "books",   label: "The books",   title: "Books",   make: function (b, p) { return new BooksTab(b, { focus: p.focus, store: store, domain: domain }); } },
            { id: "shelves", label: "The shelves", title: "Shelves", make: function (b, p) { return new ShelvesTab(b, { focus: p.focus, store: store, domain: domain }); } },
            // LISTED:FALSE — it is what the plus opens, never one of the things you open with it. It is handed its
            // own tab, which becomes whatever is chosen in it: the same tab, holding something else.
            { id: "opener",  label: "Open…",      title: "Open",    listed: false,
              make: function (b, p) { return new TabOpener(b, { focus: p.focus, tab: p.tab, source: source }); } } ] });
        this.source = source;
        // WHICH KEYS THE DOCKS ANSWER, swapped while you stand in the workspace: a pane's schemes are a list it is
        // given, so changing them is one call per dock and nothing else at all.
        this.keys = new KeysPicker(branch.createBranch("keys"), { host: o.host, panes: o.panes });
    }

    /** A tab of that kind in that pane: the index it landed at, or −1 if the pane had no room. */
    addTo(pane, kindId, how) { return this.source.addTo(pane, kindId, how); }

    /** A tab holding the chooser, holding the keys: the strip's plus, and anything else that asks in the strip. */
    opener(pane) { return this.source.addTo(pane, "opener", "focus"); }

    /** Which docks there are, a dock minted since among them: said again. */
    refresh() { this.keys.refresh(); return this; }

    dispose() {
        this.keys.dispose();
        this.source.dispose();
        try { this.branch.dissolve(); } catch (e) {}
    }
}
