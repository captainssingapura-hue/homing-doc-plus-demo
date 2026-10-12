// =============================================================================
// ButtonsApp — the button, exercised. Every button leaf of the house, in a
// row, with one extent slider over all of them: 1 is the word as the design
// binds it, 0 the design's neutral, −1 the meaning turned the other way. A
// live one under it: a danger button whose extent follows how many rows an
// action would touch — safe at none, dangerous at all. A size slider over all
// of them: 0 regular, 1 the biggest, −1 the smallest, every length the design
// gives a button growing by its own ratio — and one word at five sizes, so the
// curve is visible. And off, for all.
// =============================================================================

const _owner = Object.freeze({ toString: () => "buttonsPage" });

/** Each colour word, and the leaf that wears it: the rank is the class. */
var WORDS = ["plain", "primary", "secondary", "danger", "warning", "success"];
var KINDS = { plain: PlainButton, primary: PrimaryButton, secondary: SecondaryButton, danger: DangerButton, warning: WarningButton, success: SuccessButton };
/** Each word's companion: what makes the element its button takes. */
var MAKERS = { plain: PlainButtonFactory, primary: PrimaryButtonFactory, secondary: SecondaryButtonFactory, danger: DangerButtonFactory,
               warning: WarningButtonFactory, success: SuccessButtonFactory };

/** A press on one of the page's buttons: the page told which, and what about. */
class ButtonsPress {
    constructor(page, act, about) { this._page = page; this._act = act; this._about = about; }
    canHandle(type) { return type === EVENT.CLICK; }
    handle() { this._page._pressed(this._act, this._about); }
}

class ButtonsWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");
        // the keys, through the party: the page's steward, made by the chrome and handed in the params;
        // the members' ids qualified by the page, since the shell's one party has every page's members in it
        var kb = params && params.keyboard;
        if (!kb) throw new Error("[gallery] the page's keyboard steward is required: params.keyboard");

        var kicker = new Kicker(KickerFactory.create(branch, "kicker"), { text: "homing-ui-elements" });
        css.addClass(kicker.el, ga_kicker);
        el.appendChild(kicker.el);
        var title = new Heading1(Heading1Factory.create(branch, "title"), { text: "Buttons" });
        css.addClass(title.el, ga_title);
        el.appendChild(title.el);
        var lede = new Lede(LedeFactory.create(branch, "lede"), { text: "A button is one of six leaves, each its own class: the class says which element to mint, the caller "
            + "mints it on its own branch and hands it in with the properties, and the leaf dresses it. A colour word is a "
            + "semantic surface complete — the surface, the ink on it, the edge — and one number, the extent, moves them all "
            + "together along the anchors each design gives them. A second number, the size, grows every length the design "
            + "gives a button — the inset, the gap, the least width, the type — each by its own ratio: 0 is regular, 1 the "
            + "biggest, −1 the smallest, and the way between is exponential, so both ends are the same step." });
        css.addClass(lede.el, ga_lede);
        el.appendChild(lede.el);

        var log = branch.createElement("log", "div");
        css.addClass(log, ga_status);
        new Look(Object.freeze({ LEAF: "code-block" }), ROLE.STANDALONE).wear(log);   // the house's block of code: a console's lines
        el.appendChild(log);
        var clicks = 0;
        function say(what) { clicks++; log.textContent = clicks + ": " + what; }
        this._say = say;
        say("nothing pressed yet");

        // ── every word, one extent ────────────────────────────────────────
        var words = new Kicker(KickerFactory.create(branch, "words"), { text: "the colour words" });
        css.addClass(words.el, ga_kicker);
        el.appendChild(words.el);
        var row = branch.createElement("row", "div");
        css.addClass(row, ga_buttons);
        this._buttons = WORDS.map(function (word) {
            var Kind = KINDS[word];
            var btn = new Kind(MAKERS[word].create(branch, "btn-" + word), { label: word });
            btn.setOnClick(new ButtonsPress(self, "word", word));
            row.appendChild(btn.el);
            return btn;
        });
        el.appendChild(row);

        this._extent = 1;
        var extent = new Slider(branch.createBranch("extent"), {
            keyboard: kb, keyboardId: "buttons/extent", label: "extent", icon: "extent", labelWidth: "12em",
            min: -1, max: 1, step: 0.05, detents: [0], value: 1,
            onInput: function (v) { self._extent = v; self._buttons.forEach(function (b) { if ("extent" in b) b.extent = v; }); },
            format: function (v) { return v.toFixed(2) + (v === 1 ? "  the word" : v === 0 ? "  neutral" : v === -1 ? "  the other meaning" : ""); }
        }).root;
        el.appendChild(extent);

        // ── one size for all, and one word at five ────────────────────────
        var sizes = new Kicker(KickerFactory.create(branch, "sizes"), { text: "the size" });
        css.addClass(sizes.el, ga_kicker);
        el.appendChild(sizes.el);
        var sizeRow = branch.createElement("sizeRow", "div");
        css.addClass(sizeRow, ga_buttons);
        this._sized = [-1, -0.5, 0, 0.5, 1].map(function (s) {
            var btn = new PrimaryButton(PrimaryButtonFactory.create(branch, "size-" + String(s).replace("-", "m").replace(".", "_")), {
                label: s === 0 ? "regular" : (s > 0 ? "+" : "−") + Math.abs(s), size: s });
            btn.setOnClick(new ButtonsPress(self, "size", s));
            sizeRow.appendChild(btn.el);
            return btn;
        });
        el.appendChild(sizeRow);
        this._size = 0;
        var size = new Slider(branch.createBranch("size"), {
            keyboard: kb, keyboardId: "buttons/size", label: "size, for all of them", icon: "size", labelWidth: "12em",
            min: -1, max: 1, step: 0.05, detents: [0], value: 0,
            onInput: function (v) { self._size = v; self._buttons.forEach(function (b) { b.size = v; }); if (self._delete) self._delete.size = v; },
            format: function (v) { return v.toFixed(2) + (v === 0 ? "  regular" : v === 1 ? "  the biggest" : v === -1 ? "  the smallest" : ""); }
        }).root;
        el.appendChild(size);

        // ── a live one ────────────────────────────────────────────────────
        var live = new Kicker(KickerFactory.create(branch, "live"), { text: "live: the extent follows the data" });
        css.addClass(live.el, ga_kicker);
        el.appendChild(live.el);
        var liveRow = branch.createElement("liveRow", "div");
        css.addClass(liveRow, ga_buttons);
        this._delete = new DangerButton(DangerButtonFactory.create(branch, "delete"), { extent: -1 });
        this._delete.setOnClick(new ButtonsPress(self, "delete"));
        liveRow.appendChild(this._delete.el);
        el.appendChild(liveRow);
        this._delete.size = this._size;
        this._rows = 0;
        var rows = new Slider(branch.createBranch("rows"), {
            keyboard: kb, keyboardId: "buttons/rows", label: "rows the action touches", icon: "level", labelWidth: "12em",
            min: 0, max: 100, step: 1, detents: [50], value: 0,
            onInput: function (n) {
                self._rows = n;
                var t = (n - 50) / 50;                    // none: safe (−1); half: neutral (0); all: danger (1)
                self._delete.label = n === 0 ? "Nothing to delete" : "Delete " + n + " row" + (n === 1 ? "" : "s");
                self._delete.extent = t;
            },
            format: function (n) { return n + "  →  extent " + ((n - 50) / 50).toFixed(2); }
        }).root;
        el.appendChild(rows);

        // ── off ───────────────────────────────────────────────────────────
        var offRow = branch.createElement("offRow", "div");
        css.addClass(offRow, ga_buttons);
        this._allOn = true;
        this._switchAll = new PlainButton(PlainButtonFactory.create(branch, "toggle"), { label: "Switch them all off" });
        this._switchAll.setOnClick(new ButtonsPress(self, "all"));
        offRow.appendChild(this._switchAll.el);
        el.appendChild(offRow);

        this.root = el;
    }

    /** One of its buttons pressed: said, or every button switched off or on again. */
    _pressed(act, about) {
        if (act === "word") this._say(about + " pressed at extent " + this._extent.toFixed(2));
        else if (act === "size") this._say("size " + about + " pressed");
        else if (act === "delete") this._say("deleted " + this._rows + " rows");
        else if (act === "all") {
            var on = this._allOn = !this._allOn;
            this._buttons.forEach(function (b) { b.enabled = on; });
            this._sized.forEach(function (b) { b.enabled = on; });
            this._delete.enabled = on;
            this._switchAll.label = on ? "Switch them all off" : "Switch them all on";
        }
    }

    dispose() {}
}

function appMain(el, params) {
    el.appendChild(new ButtonsWidget(domOpsParty.createBranch("buttonsPage"), params).root);
}
