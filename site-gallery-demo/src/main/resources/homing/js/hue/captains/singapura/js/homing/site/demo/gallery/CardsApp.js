// =============================================================================
// CardsApp — the card, exercised. A grid of cards at one size slider; one with
// text longer than its frame, so the body scrolls; one with a caller's own tree
// in the body; one with an action, so it is a button to the keyboard too; and
// the same card at −1, 0 and 1 in a row, and across the aspect from the tallest
// to the widest. The card is a hard frame: the design gives it its measure,
// grown by its size, and its aspect — square at 0 — and what is inside fits it.
// =============================================================================

const _owner = Object.freeze({ toString: () => "cardsPage" });

var LONG = "A card is a hard frame. Its inline size is the design's, grown by its size, and its block size follows its "
    + "aspect: square at 0, the widest the design allows at 1 — two to one for Editorial, wider for Neo-Futurism — and the tallest at −1. "
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
            + "behaves as an enlarged button would: it lifts on hover, presses, and rings on focus. What a press does is the caller's. "
            + "Two numbers shape it: the size, and the aspect — square at 0, the design's widest at 1, its tallest at −1.";
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
        var plain = new CardBuilder().title("A plain card").badge("BASE").text("Title, badge, a line of text, and a link in the foot.").link("/", "The gallery").aspect(0.6)
            .build(branch.createBranch("plain"));
        grid.appendChild(plain.root);
        this._cards.push(plain);

        var long = new CardBuilder().title("A card with too much to say").badge("SCROLLS").text(LONG).link("/buttons", "Buttons").aspect(0.6)
            .build(branch.createBranch("long"));
        grid.appendChild(long.root);
        this._cards.push(long);

        var own = new CardBuilder().title("A caller's own body").badge("BODY").aspect(0.6)
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
            .onClick(function () { say("the action card was pressed"); }).aspect(0.6)
            .build(branch.createBranch("action"));
        grid.appendChild(action.root);
        this._cards.push(action);
        el.appendChild(grid);

        this._size = 0;
        var size = new SliderBuilder().label("size, for the grid").labelWidth("10em").range(-1, 1, 0.05).detent(0).value(0)
            .onInput(function (v) { self._size = v; self._cards.forEach(function (c) { c.size(v); }); })
            .format(function (v) { return v.toFixed(2) + (v === 0 ? "  regular" : v === 1 ? "  the biggest" : v === -1 ? "  the smallest" : ""); })
            .build(branch.createBranch("size")).root;
        el.appendChild(size);
        var aspect = new SliderBuilder().label("aspect, for the grid").labelWidth("10em").range(-1, 1, 0.05).detent(0).value(0.6)
            .onInput(function (v) { self._cards.forEach(function (c) { c.aspect(v); }); })
            .format(function (v) { return v.toFixed(2) + (v === 0 ? "  square" : v === 1 ? "  the widest" : v === -1 ? "  the tallest" : ""); })
            .build(branch.createBranch("aspect")).root;
        el.appendChild(aspect);

        // ── one card across the aspect ────────────────────────────────────
        var across = branch.createElement("across", "div");
        css.addClass(across, ga_kicker);
        across.textContent = "the same card at aspect −1, −½, 0, ½ and 1, at size −½";
        el.appendChild(across);
        var aspects = branch.createElement("aspects", "div");
        css.addClass(aspects, ga_cards);
        [-1, -0.5, 0, 0.5, 1].forEach(function (a) {
            var c = new CardBuilder().title(a === 0 ? "square" : (a > 0 ? "+" : "−") + Math.abs(a)).badge("ASPECT").text("The design's widest to the power of the aspect.").size(-0.5).aspect(a)
                .build(branch.createBranch("aspect-" + String(a).replace("-", "m").replace(".", "_")));
            aspects.appendChild(c.root);
        });
        el.appendChild(aspects);

        // ── one card at three sizes ───────────────────────────────────────
        var three = branch.createElement("three", "div");
        css.addClass(three, ga_kicker);
        three.textContent = "the same card at −1, 0 and 1";
        el.appendChild(three);
        var row = branch.createElement("row", "div");
        css.addClass(row, ga_cards);
        [-1, 0, 1].map(function (s) {
            var c = new CardBuilder().title(s === 0 ? "regular" : s > 0 ? "+1" : "−1").badge("SIZE").text("The measure, the inset, the gap and the type grow by the design's ratios; the aspect holds.").size(s).aspect(0.6)
                .build(branch.createBranch("sized-" + (s < 0 ? "m1" : s)));
            row.appendChild(c.root);
            return c;
        });
        el.appendChild(row);

        this.root = el;
    }

    dispose() {}
}

function appMain(el, params) {
    el.appendChild(new CardsWidget(domOpsParty.createBranch("cardsPage"), params).root);
}
