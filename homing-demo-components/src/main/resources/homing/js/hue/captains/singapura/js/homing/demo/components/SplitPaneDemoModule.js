// =============================================================================
// SplitPaneDemo — the house's split pane in action: a list beside a detail
// over its notes, sharing their room by ratio; a divider dragged gives one
// region more, and the shares are said when the hand lets go. Its options:
// even-out - the outer split shared evenly; reset-layout - one to two again,
// as it began. Its layout is its owner's, fixed: asked to split a region or
// remove one, it says so, and does neither.
//
//   new SplitPaneDemo(container, { leaf })   leaf: "split-pane"
//   (the rest is a ComponentDemo's)
// =============================================================================

class SplitPaneDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "split-pane-demo", params);
        var self = this;
        var host = this.el("host", "div", dm_host, this.stage);
        var shares = function (rs) { return rs.map(function (r) { return Math.round(r * 100) + "%"; }).join(" : "); };
        this._split = new SplitPane(this.branch.createBranch("split"), {
            host: host,
            layout: { kind: "split", orientation: "horizontal", children: [
                { pane: { kind: "leaf", slotId: "list" }, ratio: 1 },
                { pane: { kind: "split", orientation: "vertical", children: [
                    { pane: { kind: "leaf", slotId: "detail" } },
                    { pane: { kind: "leaf", slotId: "notes" } }] }, ratio: 2 }] },
            onEvent: function (ev) {
                if (ev.kind === "RatioChanged") self.say((ev.path.length ? "the inner split" : "the outer split") + " re-shared: " + shares(ev.ratios));
            }
        });
        var words = { list: "The list", detail: "The detail", notes: "Its notes" };
        this._split.slots().forEach(function (id) { self.el("slot-" + id, "p", dm_text, self._split.slot(id), words[id]); });
        this.intro = "drag a divider to give one region more room; the shares are said when the hand lets go";
    }

    split() { this.say("its layout is its owner's: a split pane re-shares its regions, and never splits one"); }

    remove() { this.say("its layout is its owner's: a split pane re-shares its regions, and never removes one"); }

    evenOut() { this._split.setRatios("", [1, 1]); }

    resetLayout() { this._split.setRatios("", [1, 2]); }

    disposed() { try { this._split.dispose(); } catch (e) {} }
}
