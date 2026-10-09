// =============================================================================
// LinkDemo — the house's link in action: its words and where it goes, in the
// look the style gives it. A click is told, with how many - and the way is not
// followed: the demo's handler prevents the raw event the link hands it, as any
// owner may. Its options: its size.
//
//   new LinkDemo(container, { leaf })   leaf: "link"
//   (the rest is a ComponentDemo's)
// =============================================================================

/** What the demo's link tells it: a click, which it keeps from going anywhere. */
class LinkDemoTold {
    constructor(demo) { this._demo = demo; }
    canHandle(type) { return type === EVENT.CLICK; }
    handle(event) { if (event.raw) event.raw.preventDefault(); this._demo._told(); }
}

class LinkDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "link-demo", params);
        var row = this.el("row", "div", dm_row, this.stage);
        this._clicks = 0;
        this._link = new Link(LinkFactory.create(this.branch, "link"), { text: "Read the quarterly report", to: "/docs" });
        this._link.setOnClick(new LinkDemoTold(this));
        row.appendChild(this._link.el);
        this.intro = "a link: a way to somewhere else - click it, and the demo is told, and keeps it from going there";
    }

    /** A click on its link, counted. */
    _told() {
        this._clicks++;
        this.say("clicked - " + this._clicks + (this._clicks === 1 ? " time" : " times") + "; it would go to " + this._link.to);
    }

    size(v) { this._link.size = v; }
}
