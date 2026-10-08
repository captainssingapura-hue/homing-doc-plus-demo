// =============================================================================
// IconDemo — the house's icon in action: one mark, its word written beside
// it. Its options: next - the next word of the vocabulary; clear - a blank,
// at its width, so what stands beside it stays where it was; missing - a word
// the vocabulary lacks, which it refuses, saying why.
//
//   new IconDemo(container, { leaf })   leaf: "icon"
//   (the rest is a ComponentDemo's)
// =============================================================================

class IconDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "icon-demo", params);
        var shown = this.el("shown", "div", dm_row, this.stage);
        this._icon = new Icon(this.branch.createElement("icon", Icon.TAG), { name: Icon.NAMES[0] });
        shown.appendChild(this._icon.el);
        this._word = this.el("word", "span", dm_text, shown);
        this._at = 0;
        this._tell();
        this.intro = Icon.NAMES.length + " words in the vocabulary: step through them, clear the mark, or ask for one it lacks - in the controls";
    }

    _tell() { this._word.textContent = this._icon.name() == null ? "(blank)" : this._icon.name(); }

    next() {
        this._at = (this._at + 1) % Icon.NAMES.length;
        this._icon.set(Icon.NAMES[this._at]);
        this._tell();
        this.say("the mark is \"" + Icon.NAMES[this._at] + "\" - word " + (this._at + 1) + " of " + Icon.NAMES.length);
    }

    clear() {
        this._icon.clear();
        this._tell();
        this.say("blank, at its width: what stands beside it stays where it was");
    }

    missing() {
        try { this._icon.set("no-such-word"); this.say("taken - which it should not have been"); }
        catch (e) { this.say("refused: " + e.message); }
    }
}
