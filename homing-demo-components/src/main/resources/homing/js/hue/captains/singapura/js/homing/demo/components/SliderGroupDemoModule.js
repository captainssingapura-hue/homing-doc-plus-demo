// =============================================================================
// SliderGroupDemo — the house's slider group in action: three faders that set
// related values together, under one heading, holding the keys as one. The
// keys the demo holds go to the current fader; Tab and Shift+Tab make the next
// or the previous one current, wrapping. Every value set is said, with whose
// it is. It is controlled by nothing more than its own faders.
//
//   new SliderGroupDemo(container, { leaf })   leaf: "slider-group"
//   (the rest is a ComponentDemo's)
// =============================================================================

class SliderGroupDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "slider-group-demo", params);
        var self = this;
        this._group = new SliderGroupBuilder().title("Equaliser").across().build(this.branch.createBranch("group"));
        ["Bass", "Middle", "Treble"].forEach(function (name) {
            self._group.add(name.toLowerCase(), new SliderBuilder().label(name).vertical().axis()
                .format(function (v) { return (v > 0 ? "+" : "") + v.toFixed(1); })
                .onChange(function (v) { self.say(name + " set to " + (v > 0 ? "+" : "") + v.toFixed(1)); }));
        });
        this.stage.appendChild(this._group.root);
        this.intro = "three faders, holding the keys as one: Tab moves between them, the arrows move the current one";
    }

    key(ev) { return this._group.key(ev); }

    disposed() { this._group.dispose(); }
}
