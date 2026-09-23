// =============================================================================
// SheetsApp — the panel as a SHEET, and the three registers it can sit at.
// Nothing here is a workspace: one ground, four sheets laid on it, and the
// controls that say where one of them sits. The point is to be able to read
// the three against each other at once — sunken, flat, elevated — because a
// register only means anything beside the others.
//
// The page says NOTHING about what a register means. It sets them because it
// is a demo of registers; an app links its own states to one, in its own code.
// The fourth sheet is the one the controls drive: a slider for where it sits
// and a toggle for whether it is marked, which are two axes and not one.
//
// The stack below is the part a flat design cannot fake: a sheet lying on a
// sheet. Where the cast is a colour mixed from the ink rather than a painted
// grey, the same cast over a sheet and over the ground are two colours by
// construction, because a cast IS what lies beneath it, shaded.
// =============================================================================

const _sheetsOwner = Object.freeze({ toString: () => "sheetsPage" });

const REGISTERS = [
    { id: "sunken",   sits: "sunken",   note: "cut into the ground" },
    { id: "flat",     sits: null,       note: "lying on it" },
    { id: "elevated", sits: "elevated", note: "lifted off it" }
];

/**
 * The sheets page: a ground, the three registers side by side, one sheet you
 * drive, and a sheet resting on a sheet.
 */
class SheetsWidget {

    constructor(branch, params) {
        branch.activate(_sheetsOwner);
        var el = branch.createElement("root", "div");

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-ui-elements";
        el.appendChild(kicker);
        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Sheets";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "A panel is a sheet, and a sheet sits somewhere: cut into the ground, lying on it, or lifted off it. The design says what each of "
            + "those looks like, in whatever plane it honestly uses for depth — a cast shadow, a face a shade darker, two shadows swapping sides — and a "
            + "design with no idiom for depth says nothing and leaves the sheet where it is. Nothing here links a register to a state: this page sets them "
            + "because it is a page about registers. The fourth sheet is yours, on two controls that are two because the axes are — where it sits, and "
            + "whether it is marked as the current one.";
        el.appendChild(lede);

        // ── the ground, and the three registers read against each other ──
        var ground = branch.createElement("ground", "div");
        css.addClass(ground, ga_sheet_ground);
        el.appendChild(ground);
        this._panels = [];
        var self = this;
        REGISTERS.forEach(function (r) { self._lay(branch, ground, r.id, r.id, r.note).elevation(r.sits); });
        var yours = this._lay(branch, ground, "yours", "yours", "on the controls below");
        this._yours = yours;
        yours.elevation("elevated");

        // ── the controls: the two axes, on two controls ──────────────────
        var controls = branch.createElement("controls", "div");
        css.addClass(controls, ga_buttons);
        el.appendChild(controls);
        var SITS = [null, "sunken", "elevated"];
        controls.appendChild(new SliderBuilder().keyboard(params.keyboard, "sheets/sits").label("the fourth sheet sits")
            .range(-1, 1, 1).detent(-1, 0, 1).value(1).icon("level").labelWidth("11em")
            .format(function (v) { return v < 0 ? "sunken" : v > 0 ? "elevated" : "flat"; })
            .onInput(function (v) { yours.elevation(SITS[Math.abs(v) + (v > 0 ? 1 : 0)]); })
            .build(branch.createBranch("sits")).root);
        var mark = new ButtonBuilder().label("marked").size(-0.4).colour("plain")
            .onClick(function () { yours.highlight(!yours.isHighlighted()); mark.colour(yours.isHighlighted() ? "primary" : "plain"); });
        var markEl = branch.createElement("mark", mark.tag);
        mark = mark.build(markEl);
        controls.appendChild(markEl);

        // ── a sheet on a sheet: the cast falls on paper, not on the table ─
        var stackNote = branch.createElement("stack-note", "p");
        css.addClass(stackNote, ga_lede);
        stackNote.textContent = "And a sheet may lie on a sheet. The cast below falls on paper rather than on the table — the same word, a different colour, "
            + "because a cast is what lies beneath it in shadow and nothing has to be told which that is.";
        el.appendChild(stackNote);
        var stackGround = branch.createElement("stack-ground", "div");
        css.addClass(stackGround, ga_sheet_ground);
        el.appendChild(stackGround);
        var outer = new PanelBuilder().title("on the ground").host(stackGround).build(branch.createBranch("panel-outer"));
        outer.elevation("elevated");
        this._panels.push(outer);
        var inner = new PanelBuilder().title("on the sheet").host(outer.body).build(branch.createBranch("panel-inner"));
        inner.elevation("elevated");
        this._panels.push(inner);
        this._lines(branch, "inner", inner.body, ["the same register", "a different ground"]);

        this.root = el;
    }

    /** One sheet in its room on the ground, with a caption under it saying what register it is at. */
    _lay(branch, ground, name, label, note) {
        var slot = branch.createElement("slot-" + name, "div");
        css.addClass(slot, ga_sheet_slot);
        ground.appendChild(slot);
        var panel = new PanelBuilder().title(label).host(slot).build(branch.createBranch("panel-" + name));
        this._panels.push(panel);
        this._lines(branch, name, panel.body, ["a sheet of card", "cut to its content"]);
        var caption = branch.createElement("note-" + name, "div");
        css.addClass(caption, ga_sheet_note);
        caption.textContent = note;
        slot.appendChild(caption);
        return panel;
    }

    /** What a sheet holds here: a couple of quiet lines, so it is a sheet of something. */
    _lines(branch, name, into, lines) {
        var box = branch.createElement("lines-" + name, "div");
        css.addClass(box, ga_sheet_lines);
        lines.forEach(function (text, i) {
            var line = branch.createElement("line-" + name + "-" + i, "div");
            line.textContent = text;
            box.appendChild(line);
        });
        into.appendChild(box);
        return box;
    }

    dispose() { this._panels.forEach(function (p) { p.dispose(); }); }
}

function appMain(el, params) {
    el.appendChild(new SheetsWidget(domOpsParty.createBranch("sheetsPage"), params).root);
}
