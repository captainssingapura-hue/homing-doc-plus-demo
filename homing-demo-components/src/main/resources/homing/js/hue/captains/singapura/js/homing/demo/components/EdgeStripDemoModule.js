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

/** A tool on the strip pressed: the demo says which. */
class EdgeStripDemoTool {
    constructor(demo, name) { this._demo = demo; this._name = name; }
    canHandle(type) { return type === EVENT.CLICK; }
    handle() { this._demo.say(this._name + " pressed, on the strip"); }
}

class EdgeStripDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "edge-strip-demo", params);
        var self = this;
        var host = this.el("host", "div", dm_host, this.stage);
        this.el("text", "p", dm_text, host, "Bring the pointer to the foot of this box: its tools lie there, out of the way until asked for.");
        this._strip = new EdgeStrip(this.branch.createBranch("strip"), { label: "The box's tools", host: host });
        ["Copy", "Share", "Archive"].forEach(function (name) {
            var tool = new PlainButton(PlainButtonFactory.create(self.branch, "tool-" + name.toLowerCase()), { label: name, size: -1 });
            tool.setOnClick(new EdgeStripDemoTool(self, name));
            self._strip.root.appendChild(tool.el);
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
