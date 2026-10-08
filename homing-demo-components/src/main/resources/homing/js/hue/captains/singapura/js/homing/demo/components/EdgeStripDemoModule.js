// =============================================================================
// EdgeStripDemo — the house's edge strip in action: a box whose tools wait
// out of the way at its foot. The hand on the thin lip at the foot shows the
// strip over the bottom of the box; the hand off both hides it again, a little
// after. What is pressed on the strip is said. Its option: held - shown until
// let go, for something on it a person must know.
//
//   new EdgeStripDemo(container, { leaf })   leaf: "edge-strip"
//   (the rest is a ComponentDemo's)
// =============================================================================

class EdgeStripDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "edge-strip-demo", params);
        var self = this;
        var host = this.el("host", "div", dm_host, this.stage);
        this.el("text", "p", dm_text, host, "Bring the pointer to the foot of this box: its tools lie there, out of the way until asked for.");
        this._strip = new EdgeStripBuilder().label("The box's tools").host(host).build(this.branch.createBranch("strip"));
        ["Copy", "Share", "Archive"].forEach(function (name) {
            var b = new ButtonBuilder().label(name).plain().size(-1).onClick(function () { self.say(name + " pressed, on the strip"); });
            self._strip.root.appendChild(b.build(self.branch.createElement("tool-" + name.toLowerCase(), b.tag)).el);
        });
        this._held = false;
        this.intro = "bring the pointer to the box's foot, or hold the strip shown in the controls";
    }

    /** held: shown until let go. */
    hold(on) {
        if (on === this._held) return;
        this._held = on;
        this._strip.hold(on);
        this.say(on ? "held: shown until let go - something on it a person must know" : "let go: it goes once the hand and the keys have");
    }

    disposed() { this._strip.dispose(); }
}
