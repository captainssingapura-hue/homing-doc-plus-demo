// =============================================================================
// SummaryCardDemo — the house's summary card in action: a title, a tag, a
// line of summary, and an action - pressed with the pointer, or with Enter or
// Space while the demo holds the keys. Every opening is said. Its options are
// every card's: its size, and its aspect - square at 0, the widest the design
// allows at 1, the tallest at −1.
//
//   new SummaryCardDemo(container, { leaf })   leaf: "summary-card"
//   (the rest is a ComponentDemo's)
// =============================================================================

class SummaryCardDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "summary-card-demo", params);
        var self = this, opened = 0;
        var row = this.el("row", "div", dm_row, this.stage);
        this._card = new CardBuilder()
            .title("The quarter, in brief")
            .badge("new")
            .text("Revenue up a tenth on the last quarter, costs held, and three markets opening in the spring.")
            .onClick(function () { opened++; self.say("opened - " + opened + (opened === 1 ? " time" : " times") + ", by the pointer or by Enter or Space"); })
            .build(this.branch.createBranch("card"));
        row.appendChild(this._card.root);
        this.intro = "press the card, or press in the demo and use Enter or Space; size and shape it in the controls";
    }

    size(v) { this._card.size(v); }

    aspect(v) { this._card.aspect(v); }

    /** Enter or Space is the card's action. */
    key(ev) { return this._card.key(ev); }

    disposed() { this._card.dispose(); }
}
