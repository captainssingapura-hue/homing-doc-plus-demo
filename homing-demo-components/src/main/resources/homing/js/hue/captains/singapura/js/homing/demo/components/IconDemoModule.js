// =============================================================================
// IconDemo — an icon of the house's in action (RFC 0067, D52): the leaf's icon,
// mounted in a room as an owner mounts it, its token beside it. An icon shows
// one picture - its look, the style's - and another shape is another icon. Its
// options: next - the next icon the instance registers, mounted in its place,
// as an owner mounts another; clear - none, the room left empty; missing - an
// icon the instance lacks, named by a token, refused, saying why.
//
//   new IconDemo(container, { leaf })   leaf: the icon's token - "close-icon"
//   (the rest is a ComponentDemo's)
// =============================================================================

class IconDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "icon-demo", params);
        var shown = this.el("shown", "div", dm_row, this.stage);
        this._all = Object.keys(ICON).map(function (k) { return ICON[k]; });
        this._room = new IconRoom(this.el("room", "span", null, shown), this.branch, "icon", null);
        this._word = this.el("word", "span", dm_text, shown);
        this._at = this._all.indexOf(Icon.of(this.leaf));
        this._room.show(this._all[this._at]);
        this._tell();
        this.intro = "the " + this.leaf + ", one of the " + this._all.length + " icons the instance registers, its picture the style's: "
            + "mount the others in its place, as an owner does, clear it, or ask for one there is not - in the controls";
    }

    _tell() { this._word.textContent = this._room.shown == null ? "(none)" : this._room.shown.LEAF; }

    next() {
        this._at = (this._at + 1) % this._all.length;
        this._room.show(this._all[this._at]);
        this._tell();
        this.say("mounted the " + this._all[this._at].LEAF + " - icon " + (this._at + 1) + " of " + this._all.length + ": another shape is another icon");
    }

    clear() {
        this._room.show(null);
        this._tell();
        this.say("none: the room left empty");
    }

    missing() {
        try { this._room.show(Icon.of("no-such-icon")); this.say("taken - which it should not have been"); }
        catch (e) { this.say("refused: " + e.message); }
    }
}
