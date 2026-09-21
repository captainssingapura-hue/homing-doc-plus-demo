// =============================================================================
// ButtonsApp — the button, exercised. Every colour word the builder knows, in
// a row, with one extent slider over all of them: 1 is the word as the design
// binds it, 0 the design's neutral, −1 the meaning turned the other way. A
// live one under it: a danger button whose extent follows how many rows an
// action would touch — safe at none, dangerous at all. A size slider over all
// of them: 0 regular, 1 the biggest, −1 the smallest, every length the design
// gives a button growing by its own ratio — and one word at five sizes, so the
// curve is visible. And off, for all.
// =============================================================================

const _owner = Object.freeze({ toString: () => "buttonsPage" });

var WORDS = ["plain", "primary", "secondary", "danger", "warning", "success"];

class ButtonsWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");
        // the keys, through the party: the shell's steward when handed one, else the page's own
        var kb = params && params.keyboard ? params.keyboard : new KeyboardSteward(branch.createBranch("keyboard"), {});
        // the members' ids, qualified by the page: the shell's one party has every page's sliders in it
        this._ownKb = params && params.keyboard ? null : kb;

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-ui-elements";
        el.appendChild(kicker);
        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Buttons";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "A button is built through its builder: the builder says which element to mint, the caller mints it "
            + "on its own branch, the properties are set one by one, and build dresses the element. A colour word is a "
            + "semantic surface complete — the surface, the ink on it, the edge — and one number, the extent, moves them all "
            + "together along the anchors each design gives them. A second number, the size, grows every length the design "
            + "gives a button — the inset, the gap, the least width, the type — each by its own ratio: 0 is regular, 1 the "
            + "biggest, −1 the smallest, and the way between is exponential, so both ends are the same step.";
        el.appendChild(lede);

        var log = branch.createElement("log", "div");
        css.addClass(log, ga_status);
        el.appendChild(log);
        var clicks = 0;
        function say(what) { clicks++; log.textContent = clicks + ": " + what; }
        say("nothing pressed yet");

        // ── every word, one extent ────────────────────────────────────────
        var words = branch.createElement("words", "div");
        css.addClass(words, ga_kicker);
        words.textContent = "the colour words";
        el.appendChild(words);
        var row = branch.createElement("row", "div");
        css.addClass(row, ga_buttons);
        this._buttons = WORDS.map(function (word) {
            var b = new ButtonBuilder().label(word).colour(word).onClick(function () { say(word + " pressed at extent " + self._extent.toFixed(2)); });
            var btn = b.build(branch.createElement("btn-" + word, b.tag));
            row.appendChild(btn.el);
            return btn;
        });
        el.appendChild(row);

        this._extent = 1;
        var extent = new SliderBuilder().keyboard(kb, "buttons/extent").label("extent").icon("extent").labelWidth("12em").range(-1, 1, 0.05).detent(0).value(1)
            .onInput(function (v) { self._extent = v; self._buttons.forEach(function (b) { b.extent(v); }); })
            .format(function (v) { return v.toFixed(2) + (v === 1 ? "  the word" : v === 0 ? "  neutral" : v === -1 ? "  the other meaning" : ""); })
            .build(branch.createBranch("extent")).root;
        el.appendChild(extent);

        // ── one size for all, and one word at five ────────────────────────
        var sizes = branch.createElement("sizes", "div");
        css.addClass(sizes, ga_kicker);
        sizes.textContent = "the size";
        el.appendChild(sizes);
        var sizeRow = branch.createElement("sizeRow", "div");
        css.addClass(sizeRow, ga_buttons);
        this._sized = [-1, -0.5, 0, 0.5, 1].map(function (s) {
            var b = new ButtonBuilder().label(s === 0 ? "regular" : (s > 0 ? "+" : "−") + Math.abs(s)).colour("primary").size(s).onClick(function () { say("size " + s + " pressed"); });
            var btn = b.build(branch.createElement("size-" + String(s).replace("-", "m").replace(".", "_"), b.tag));
            sizeRow.appendChild(btn.el);
            return btn;
        });
        el.appendChild(sizeRow);
        this._size = 0;
        var size = new SliderBuilder().keyboard(kb, "buttons/size").label("size, for all of them").icon("size").labelWidth("12em").range(-1, 1, 0.05).detent(0).value(0)
            .onInput(function (v) { self._size = v; self._buttons.forEach(function (b) { b.size(v); }); if (self._delete) self._delete.size(v); })
            .format(function (v) { return v.toFixed(2) + (v === 0 ? "  regular" : v === 1 ? "  the biggest" : v === -1 ? "  the smallest" : ""); })
            .build(branch.createBranch("size")).root;
        el.appendChild(size);

        // ── a live one ────────────────────────────────────────────────────
        var live = branch.createElement("live", "div");
        css.addClass(live, ga_kicker);
        live.textContent = "live: the extent follows the data";
        el.appendChild(live);
        var liveRow = branch.createElement("liveRow", "div");
        css.addClass(liveRow, ga_buttons);
        var del = new ButtonBuilder().colour("danger", -1).onClick(function () { say("deleted " + self._rows + " rows"); });
        this._delete = del.build(branch.createElement("delete", del.tag));
        liveRow.appendChild(this._delete.el);
        el.appendChild(liveRow);
        this._delete.size(this._size);
        this._rows = 0;
        var rows = new SliderBuilder().keyboard(kb, "buttons/rows").label("rows the action touches").icon("level").labelWidth("12em").range(0, 100, 1).detent(50).value(0)
            .onInput(function (n) {
                self._rows = n;
                var t = (n - 50) / 50;                    // none: safe (−1); half: neutral (0); all: danger (1)
                self._delete.label(n === 0 ? "Nothing to delete" : "Delete " + n + " row" + (n === 1 ? "" : "s"));
                self._delete.extent(t);
            })
            .format(function (n) { return n + "  →  extent " + ((n - 50) / 50).toFixed(2); })
            .build(branch.createBranch("rows")).root;
        el.appendChild(rows);

        // ── off ───────────────────────────────────────────────────────────
        var offRow = branch.createElement("offRow", "div");
        css.addClass(offRow, ga_buttons);
        var on = true, toggleBtn = null;
        var toggle = new ButtonBuilder().label("Switch them all off").plain().onClick(function () {
            on = !on;
            self._buttons.forEach(function (b) { b.setOn(on); });
            self._sized.forEach(function (b) { b.setOn(on); });
            self._delete.setOn(on);
            toggleBtn.label(on ? "Switch them all off" : "Switch them all on");
        });
        toggleBtn = toggle.build(branch.createElement("toggle", toggle.tag));
        offRow.appendChild(toggleBtn.el);
        el.appendChild(offRow);

        this.root = el;
    }

    dispose() { if (this._ownKb) this._ownKb.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new ButtonsWidget(domOpsParty.createBranch("buttonsPage"), params).root);
}
