// =============================================================================
// ContextMenuStewardDemo — the house's context-menu steward in action: the
// page's one, keeping every menu of the page by kind, one active at a time -
// right-press the file, then the folder, and the first gives way. Its option:
// ask - what is open, and for what. Every opening, pick and close is said.
//
// One per page: a page that has its steward already - the workspace's, which
// keeps the menus of its tabs and its regions - refuses a second, and the demo
// says so, and where to see that one at work; asked, it says the same.
//
//   new ContextMenuStewardDemo(container, { leaf })   leaf: "context-menu-steward"
//   (the rest is a ComponentDemo's)
// =============================================================================

class ContextMenuStewardDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "context-menu-steward-demo", params);
        var self = this;
        this._menus = null;
        this._refused = null;
        try {
            this._menus = new ContextMenuSteward(this.branch.createBranch("menus"), {
                keyboard: KeyboardStewardInstance, keyboardId: "demo-context-menu-steward",
                onEvent: function (ev) {
                    if (ev.kind === "Opened") self.say("the " + ev.menuKind + "'s menu opened - the one active");
                    else if (ev.kind === "Picked") self.say("picked on the " + ev.menuKind + ": " + ev.itemId);
                    else if (ev.kind === "Closed") self.say("the " + ev.menuKind + "'s menu closed: " + ev.reason);
                }
            });
        } catch (e) {
            this._refused = e.message;
            this.el("refused", "p", dm_text, this.stage, "This page has its steward already - the workspace's, which keeps the menus of its tabs and its regions. "
                + "Right-press a tab of this workspace, or the ground between its panes, to see that one at work.");
            this.intro = "asked to keep this page's menus: refused - " + e.message;
            return;
        }
        this._menus.define("file").row("open", "Open").row("rename", "Rename", { hint: "F2" }).divider().row("delete", "Delete", { icon: "remove" }).done();
        this._menus.define("folder").row("open", "Open").row("new-file", "New file", { icon: "add" }).divider().row("delete", "Delete", { icon: "remove" }).done();
        this._menus.handle("file", { pick: function () {} });
        this._menus.handle("folder", { pick: function () {} });
        var row = this.el("targets", "div", dm_row, this.stage);
        ["file", "folder"].forEach(function (kind) {
            var box = self.el("target-" + kind, "div", dm_host, row);
            self.el("target-" + kind + "-text", "p", dm_text, box, "A " + kind + ": right-press here.");
            box.addEventListener("contextmenu", function (e) {
                if (self._menus.open(kind, { name: "the " + kind }, { x: e.clientX, y: e.clientY }, { anchor: box })) e.preventDefault();
            });
        });
        this.intro = "the page's steward: right-press the file, then the folder - one menu is active at a time";
    }

    /** ask: what is open, and for what. */
    ask() {
        if (this._refused) return "this page has its steward already, and it was not this one: " + this._refused;
        var kind = this._menus.active();
        return kind ? "the " + kind + "'s menu, for " + this._menus.bound().name : "nothing is open; it knows " + this._menus.kinds().join(" and ");
    }

    disposed() {
        if (this._menus) { try { this._menus.dispose(); } catch (e) {} this._menus = null; }
    }
}
