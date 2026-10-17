// =============================================================================
// ButtonDemo — a house button in action: one of the nine, as its leaf says -
// plain, primary, secondary, danger, warning, success, toggle, close, icon -
// made as the leaf's own class and labelled with what such a button does; an
// icon button holds the icon the demo makes and hands it, as any owner would.
// A press is said, with how many. Its options: enabled - off, it is inert and says so,
// and a press does nothing; its size; and a coloured one's colour - how much
// of its word's meaning it wears, −1 the meaning turned the other way.
//
//   new ButtonDemo(container, { leaf })   leaf: "danger-button", …
//   (the rest is a ComponentDemo's)
// =============================================================================

// each leaf, the class that is it - the rank is the class - and its word and label
var _BUTTON_KINDS = Object.freeze({
    "plain-button":     Object.freeze({ Leaf: PlainButton,     Factory: PlainButtonFactory,     word: "plain",     label: "Open the report" }),
    "primary-button":   Object.freeze({ Leaf: PrimaryButton,   Factory: PrimaryButtonFactory,   word: "primary",   label: "Save" }),
    "secondary-button": Object.freeze({ Leaf: SecondaryButton, Factory: SecondaryButtonFactory, word: "secondary", label: "Preview" }),
    "danger-button":    Object.freeze({ Leaf: DangerButton,    Factory: DangerButtonFactory,    word: "danger",    label: "Delete the project" }),
    "warning-button":   Object.freeze({ Leaf: WarningButton,   Factory: WarningButtonFactory,   word: "warning",   label: "Overwrite the draft" }),
    "success-button":   Object.freeze({ Leaf: SuccessButton,   Factory: SuccessButtonFactory,   word: "success",   label: "Finish" }),
    "toggle-button":    Object.freeze({ Leaf: ToggleButton,    Factory: ToggleButtonFactory,    word: "toggle",    label: "Bold", toggles: true }),
    "close-button":     Object.freeze({ Leaf: CloseButton,     Factory: CloseButtonFactory,     word: "close",     label: "Close the note" }),
    "icon-button":      Object.freeze({ Leaf: IconButton,      Factory: IconButtonFactory,      word: "icon",      label: "Add a note", icon: ICON.ADD_ICON })
});

/** What the demo's button tells it: a press, or a toggle turned over - each said. */
class ButtonDemoTold {
    constructor(demo) { this._demo = demo; }
    canHandle(type) { return type === EVENT.CLICK || type === STATE.PRESSED.changed; }
    handle(event) { this._demo._told(event); }
}

class ButtonDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "button-demo", params);
        var kind = _BUTTON_KINDS[this.leaf];
        if (!kind) throw new Error("[ButtonDemo] no button for the leaf '" + this.leaf + "'; one of " + Object.keys(_BUTTON_KINDS).join(", "));
        var row = this.el("row", "div", dm_row, this.stage);
        var props = { label: kind.label };
        if (kind.icon) props.icon = new Icon(this.branch.createElement("icon", Icon.TAG), kind.icon, ROLE.FACE);   // its face, made here and handed in
        if (kind.Leaf === CloseButton) props.face = this.branch.elementMaker("face");   // the room its face, the close icon, is made on
        this._kind = kind;
        this._presses = 0;
        this._button = new kind.Leaf(kind.Factory.create(this.branch, "button"), props);
        if (kind.toggles) this._button.setOnPressedChanged(new ButtonDemoTold(this));
        else this._button.setOnClick(new ButtonDemoTold(this));
        row.appendChild(this._button.el);
        this._on = true;
        this.intro = kind.toggles ? "a toggle button: press it to turn it on, again to turn it off; switch it off in the controls and it does nothing"
            : kind.icon ? "an icon button: its face is the icon it is handed, its name is for whoever cannot see it; press it, or switch it off in the controls"
            : "a " + kind.word + " button: press it, or switch it off in the controls and press it again";
    }

    /** What its button told it: a press counted, or the toggle turned over - by a press, or by nothing else a toggle has. */
    _told(event) {
        var label = "\"" + this._kind.label + "\"";
        if (event.type === STATE.PRESSED.changed) {
            this.say(label + " turned " + (this._button.pressed ? "on: it stays pressed" : "off") + " - it was " + (event.previous ? "on" : "off"));
            return;
        }
        this._presses++;
        this.say(label + " pressed - " + this._presses + (this._presses === 1 ? " time" : " times"));
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
