// =============================================================================
// SummaryViewDemo — the house's summary view in action, shown on a clickable
// card: a title, a tag, a line of summary - the card pressed with the pointer,
// or with Enter or Space while the demo holds the keys. Every opening is said.
// Its options are a card's: its size, the view's with it, and its aspect -
// square at 0, the widest the design allows at 1, the tallest at −1.
//
//   new SummaryViewDemo(container, { leaf })   leaf: "summary-view"
//   (the rest is a ComponentDemo's)
// =============================================================================

class SummaryViewDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "summary-view-demo", params);
        var self = this, opened = 0;
        var row = this.el("row", "div", dm_row, this.stage);
        this._card = new ClickableCard(this.branch.createBranch("card"), {});
        this._card.setOnClick({
            canHandle: function (type) { return type === EVENT.CLICK; },
            handle: function () { opened++; self.say("opened - " + opened + (opened === 1 ? " time" : " times") + ", by the pointer or by Enter or Space"); }
        });
        this._view = new SummaryView(this.branch.createBranch("summary"), {
            title: "The quarter, in brief",
            tag: "new",
            text: "Revenue up a tenth on the last quarter, costs held, and three markets opening in the spring."
        });
        this._card.el.appendChild(this._view.root);
        row.appendChild(this._card.root);
        this.intro = "press the card, or press in the demo and use Enter or Space; size and shape it in the controls";
    }

    size(v) { this._card.size = v; this._view.size(v); }

    aspect(v) { this._card.aspect = v; }

    /** Enter or Space is the card's click. */
    key(ev) { return this._card.key(ev); }

    disposed() { this._view.dispose(); this._card.dispose(); }
}
