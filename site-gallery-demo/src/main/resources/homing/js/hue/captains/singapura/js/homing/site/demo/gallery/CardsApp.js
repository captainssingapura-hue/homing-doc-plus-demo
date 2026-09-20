// =============================================================================
// CardsApp — the card, exercised. A grid of cards at one size slider; one with
// text longer than its frame, so the body scrolls; one with a caller's own tree
// in the body; one with an action, so it is a button to the keyboard too; and
// the same card at −1, 0 and 1 in a row. The card is a hard frame: the design
// gives it its measure, grown by its size, and what is inside fits it.
// =============================================================================

const _owner = Object.freeze({ toString: () => "cardsPage" });

var LONG = "A card is a hard frame. Its inline size is the design's, grown by its size, and its block size follows the "
    + "proportion the design gives a card: three by two for Editorial, squarer for Neo-Brutalism, wide for Neo-Futurism. "
    + "What is inside fits the card, not the other way round: the head and the foot are fixed, and the body takes what is "
    + "left and scrolls beyond it. This paragraph is longer than the frame on purpose, so the body has something to scroll. "
    + "Hover lifts it, a press sets it down, and with an action it can be reached by the keyboard and rings on focus.";

class CardsWidget {
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
        title.textContent = "Cards";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "A card is built through its builder on a sub-branch of the caller's: title, badge, text, link, size, and an "
            + "action if it has one. It is Container.Card.Base to the design — a raised box whose measure is its own — and it "
            + "behaves as an enlarged button would: it lifts on hover, presses, and rings on focus. What a press does is the caller's.";
        el.appendChild(lede);

        var log = branch.createElement("log", "div");
        css.addClass(log, ga_status);
        el.appendChild(log);
        var presses = 0;
        function say(what) { presses++; log.textContent = presses + ": " + what; }
        say("nothing pressed yet");

        // ── the grid ──────────────────────────────────────────────────────
        var grid = branch.createElement("grid", "div");
        css.addClass(grid, ga_cards);
        this._cards = [];
        var plain = new CardBuilder().title("A plain card").badge("BASE").text("Title, badge, a line of text, and a link in the foot.").link("/", "The gallery")
            .build(branch.createBranch("plain"));
        grid.appendChild(plain.root);
        this._cards.push(plain);

        var long = new CardBuilder().title("A card with too much to say").badge("SCROLLS").text(LONG).link("/buttons", "Buttons")
            .build(branch.createBranch("long"));
        grid.appendChild(long.root);
        this._cards.push(long);

        var own = new CardBuilder().title("A caller's own body").badge("BODY")
            .build(branch.createBranch("own"));
        var list = branch.createElement("own-list", "ul");
        css.addClass(list, ga_card_list);
        ["The card gives the body", "the caller mints in it", "on the card's branch", "and the frame still bounds it", "however long the list", "gets to be"].forEach(function (t, i) {
            var li = branch.createElement("own-li-" + i, "li");
            li.textContent = t;
            list.appendChild(li);
        });
        own.body.appendChild(list);
        grid.appendChild(own.root);
        this._cards.push(own);

        var action = new CardBuilder().title("A card with an action").badge("PRESS").text("Click it, or Tab to it and press Enter or Space. It has a role and a ring; the plain ones do not.")
            .onClick(function () { say("the action card was pressed"); })
            .build(branch.createBranch("action"));
        grid.appendChild(action.root);
        this._cards.push(action);
        el.appendChild(grid);

        this._size = 0;
        var size = this._slider(branch, "size", "size, for the grid", -1, 1, 0.05, 0, function (v) {
            self._size = v;
            self._cards.forEach(function (c) { c.size(v); });
            return v.toFixed(2) + (v === 0 ? "  regular" : v === 1 ? "  the biggest" : v === -1 ? "  the smallest" : "");
        });
        el.appendChild(size);

        // ── one card at three sizes ───────────────────────────────────────
        var three = branch.createElement("three", "div");
        css.addClass(three, ga_kicker);
        three.textContent = "the same card at −1, 0 and 1";
        el.appendChild(three);
        var row = branch.createElement("row", "div");
        css.addClass(row, ga_cards);
        [-1, 0, 1].map(function (s) {
            var c = new CardBuilder().title(s === 0 ? "regular" : s > 0 ? "+1" : "−1").badge("SIZE").text("The measure, the inset, the gap and the type grow by the design's ratios; the proportion holds.").size(s)
                .build(branch.createBranch("sized-" + (s < 0 ? "m1" : s)));
            row.appendChild(c.root);
            return c;
        });
        el.appendChild(row);

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
    el.appendChild(new CardsWidget(domOpsParty.createBranch("cardsPage"), params).root);
}
