// =============================================================================
// SliderDemo — the house's slider in action: a volume, 0 to 100, with ticks,
// figures and a detent at 50, read out as it moves - dragged, pressed on its
// rail, or moved by the keys while the demo holds them. Where it moves to and
// where it settles are said. Its option: enabled - off, it is inert and says so.
//
//   new SliderDemo(container, { leaf })   leaf: "slider"
//   (the rest is a ComponentDemo's)
// =============================================================================

class SliderDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "slider-demo", params);
        var self = this;
        this._slider = new SliderBuilder()
            .label("Volume")
            .range(0, 100, 1).value(40).detent(50)
            .ticks([{ at: 0, label: "0" }, { at: 25, label: "25" }, { at: 50, label: "50" }, { at: 75, label: "75" }, { at: 100, label: "100" }])
            .format(function (v) { return v + "%"; })
            .onChange(function (v) { self.say("set to " + v + "%" + (v === 50 ? " - rested at the detent" : "")); })
            .build(this.branch.createBranch("slider"));
        this.stage.appendChild(this._slider.root);
        this._on = true;
        this.intro = "drag the knob, press the rail anywhere, or press it and use the arrows, Home, End, PageUp and PageDown";
    }

    /** enabled: off, inert, and saying so. */
    setOn(on) {
        if (on === this._on) return;
        this._on = on;
        this._slider.setOn(on);
        this.say(on ? "on again" : "off: inert, and it says so");
    }

    key(ev) { return this._slider.key(ev); }

    disposed() { this._slider.dispose(); }
}
