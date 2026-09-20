// =============================================================================
// ButtonsApp — the button, exercised. Every colour word the builder knows, in
// a row, with one extent slider over all of them: 1 is the word as the design
// binds it, 0 the design's neutral, −1 the meaning turned the other way. A
// live one under it: a danger button whose extent follows how many rows an
// action would touch — safe at none, dangerous at all. And off, for all.
// =============================================================================

const _owner = Object.freeze({ toString: () => "buttonsPage" });

var WORDS = ["plain", "primary", "secondary", "danger", "warning", "success"];

class ButtonsWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");

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
            + "together along the anchors each design gives them.";
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
        var extent = this._slider(branch, "extent", "extent", -1, 1, 0.05, 1, function (v) {
            self._extent = v;
            self._buttons.forEach(function (b) { b.extent(v); });
            return v.toFixed(2) + (v === 1 ? "  the word" : v === 0 ? "  neutral" : v === -1 ? "  the other meaning" : "");
        });
        el.appendChild(extent);

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
        this._rows = 0;
        var rows = this._slider(branch, "rows", "rows the action touches", 0, 100, 1, 0, function (n) {
            self._rows = n;
            var t = (n - 50) / 50;                    // none: safe (−1); half: neutral (0); all: danger (1)
            self._delete.label(n === 0 ? "Nothing to delete" : "Delete " + n + " row" + (n === 1 ? "" : "s"));
            self._delete.extent(t);
            return n + "  →  extent " + t.toFixed(2);
        });
        el.appendChild(rows);

        // ── off ───────────────────────────────────────────────────────────
        var offRow = branch.createElement("offRow", "div");
        css.addClass(offRow, ga_buttons);
        var on = true, toggleBtn = null;
        var toggle = new ButtonBuilder().label("Switch them all off").plain().onClick(function () {
            on = !on;
            self._buttons.forEach(function (b) { b.setOn(on); });
            self._delete.setOn(on);
            toggleBtn.label(on ? "Switch them all off" : "Switch them all on");
        });
        toggleBtn = toggle.build(branch.createElement("toggle", toggle.tag));
        offRow.appendChild(toggleBtn.el);
        el.appendChild(offRow);

        this.root = el;
    }

    /** A labelled range with a readout; onValue draws the readout and does the work. */
    _slider(branch, name, label, min, max, step, value, onValue) {
        var wrap = branch.createElement(name + "-wrap", "div");
        css.addClass(wrap, ga_control);
        var lab = branch.createElement(name + "-label", "span");
        css.addClass(lab, ga_control_label);
        lab.textContent = label;
        var range = branch.createElement(name + "-range", "input");
        range.type = "range";
        css.addClass(range, pv_range);
        range.min = min; range.max = max; range.step = step; range.value = value;
        range.setAttribute("aria-label", label);
        var out = branch.createElement(name + "-out", "span");
        css.addClass(out, ga_control_readout);
        function draw() { out.textContent = onValue(Number(range.value)); }
        range.addEventListener("input", draw);
        wrap.appendChild(lab);
        wrap.appendChild(range);
        wrap.appendChild(out);
        draw();
        return wrap;
    }

    dispose() {}
}

function appMain(el, params) {
    el.appendChild(new ButtonsWidget(domOpsParty.createBranch("buttonsPage"), params).root);
}
