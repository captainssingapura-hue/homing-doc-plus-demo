// =============================================================================
// FocusMonitorDemo — the house's focus monitor in action, on this very page:
// its logical-focus tree as a tree view, a row per node, indented by depth,
// the row of the member that holds the keys lit. Press anywhere on the page -
// the tree, a widget, this one - and the lit row follows. Its option: ask -
// who holds the keys now, the lit row read.
//
//   new FocusMonitorDemo(container, { leaf })   leaf: "focus-monitor"
//   (the rest is a ComponentDemo's)
// =============================================================================

class FocusMonitorDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "focus-monitor-demo", params);
        var host = this.el("host", "div", dm_scroll, this.stage);
        this._monitor = new FocusMonitor(this.branch.createBranch("monitor"), { host: host });
        this.intro = "this page's focus tree: press anywhere on the page and the lit row follows; ask who holds the keys from the controls";
    }

    /** ask: who holds the keys - the lit row, drawn now, so the row read is the row shown. */
    ask() {
        var lit = this._monitor.refresh().holderRow();
        var parts = lit ? Array.prototype.map.call(lit.children, function (c) { return c.textContent.trim(); }).filter(Boolean) : [];
        return lit ? "the lit row: " + (parts.length ? parts.join(" · ") : lit.textContent.trim()) : "nobody holds the keys";
    }

    disposed() { try { this._monitor.dispose(); } catch (e) {} }
}
