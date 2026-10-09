// =============================================================================
// ButtonDemo — a house button in action: one of the seven, as its leaf says -
// plain, primary, secondary, danger, warning, success, toggle - made as the leaf's
// own class and labelled with what such a button does. A press is
// said, with how many. Its options: enabled - off, it is inert and says so,
// and a press does nothing; its size; and a coloured one's colour - how much
// of its word's meaning it wears, −1 the meaning turned the other way.
//
//   new ButtonDemo(container, { leaf })   leaf: "danger-button", …
//   (the rest is a ComponentDemo's)
// =============================================================================

// each leaf, the class that is it - the rank is the class - and its word and label
var _BUTTON_KINDS = Object.freeze({
    "plain-button":     Object.freeze({ Leaf: PlainButton,     word: "plain",     label: "Open the report" }),
    "primary-button":   Object.freeze({ Leaf: PrimaryButton,   word: "primary",   label: "Save" }),
    "secondary-button": Object.freeze({ Leaf: SecondaryButton, word: "secondary", label: "Preview" }),
    "danger-button":    Object.freeze({ Leaf: DangerButton,    word: "danger",    label: "Delete the project" }),
    "warning-button":   Object.freeze({ Leaf: WarningButton,   word: "warning",   label: "Overwrite the draft" }),
    "success-button":   Object.freeze({ Leaf: SuccessButton,   word: "success",   label: "Finish" }),
    "toggle-button":    Object.freeze({ Leaf: ToggleButton,    word: "toggle",    label: "Bold", toggles: true })
});

class ButtonDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "button-demo", params);
        var self = this, kind = _BUTTON_KINDS[this.leaf];
        if (!kind) throw new Error("[ButtonDemo] no button for the leaf '" + this.leaf + "'; one of " + Object.keys(_BUTTON_KINDS).join(", "));
        var row = this.el("row", "div", dm_row, this.stage);
        var presses = 0, props = { label: kind.label };
        if (kind.toggles) props.onToggle = function (on) { self.say("\"" + kind.label + "\" turned " + (on ? "on: it stays pressed" : "off")); };
        else props.onClick = function () {
            presses++;
            self.say("\"" + kind.label + "\" pressed - " + presses + (presses === 1 ? " time" : " times"));
        };
        this._button = new kind.Leaf(this.branch.createElement("button", kind.Leaf.TAG), props);
        row.appendChild(this._button.el);
        this._on = true;
        this.intro = kind.toggles ? "a toggle button: press it to turn it on, again to turn it off; switch it off in the controls and it does nothing"
            : "a " + kind.word + " button: press it, or switch it off in the controls and press it again";
    }

    /** colour: how much of its word's meaning it wears. */
    extent(v) { this._button.extent = v; }

    size(v) { this._button.size = v; }

    /** enabled: off, inert, and saying so. */
    setOn(on) {
        if (on === this._on) return;
        this._on = on;
        this._button.enabled = on;
        this.say(on ? "on again: a press acts" : "off: inert, and it says so - a press does nothing");
    }
}
