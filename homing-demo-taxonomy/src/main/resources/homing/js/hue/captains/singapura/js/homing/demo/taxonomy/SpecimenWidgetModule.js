// =============================================================================
// SpecimenWidget — the picked component in action: built live by its specimen,
// one of the house's (HOUSE_SPECIMENS), its behaviour exercised and every thing
// it does said, newest first; and a slider for each axis its leaf varies along
// - colour, size, aspect - from −1 to 1, at the axis's rest until moved, the
// number set on the live component as it moves. A leaf only: a branch or a part
// shows how to pick one. A leaf the page around it shows in action - the
// workspace, the page's bar, its preferences - says where to look; one nothing
// realizes yet says so, with what it means.
//
// TWO PARTS, TOP AND BOTTOM. Above, the demo area: the leaf's name and what it
// means, then the stage the component stands on - every bit of room the deck
// leaves, and scrolling on its own whatever the component grows to. Below, the
// deck: the controls, then the log - of a fixed height, made once and never
// remade, so nothing on it moves however the component or the widget changes
// size; a pick changes what is on the deck, never where.
//
// THE KEYS. Its own, as a member of the keyboard's party: a press in it claims
// them, and they go on to what was last pressed in it - the stage, or the
// controls - each handing them to what it holds: a slider, a card. Escape that
// neither takes gives them back.
//
//   new SpecimenWidget(container, params)   params: none
//   (the rest is a TaxonomyWidget's)
// =============================================================================

var _SAID = 12;

class SpecimenWidget extends TaxonomyWidget {
    constructor(container, params) {
        super(container, "specimen", "In action");
        var self = this;
        // the demo area: a head, then the stage
        var top = this.el("top", "div", tx_demo, this.body);
        this._head = this.el("head", "div", tx_head, top);
        this._stage = this.el("stage", "div", tx_stage, top);
        // the deck: the controls, then the log - made once, at a fixed height
        var deck = this.el("deck", "div", tx_deck, this.body);
        this._controls = this.el("controls", "section", tx_panel, deck);
        this._log = this.el("log", "section", tx_log, deck);
        this.el("log-title", "h4", tx_title, this._log, "What it did");
        this._lines = [];
        this._stage.addEventListener("pointerdown", function () { self._keysAt = "specimen"; }, true);
        this._controls.addEventListener("pointerdown", function () { self._keysAt = "axes"; }, true);
        this._specimen = null;
        this._axes = null;
        this._keysAt = null;
        this._said = [];
        this._says = 0;
        this.selected(this.picked());
    }

    selected(id) {
        this._drop();
        var self = this, t = this.taxonomy, n = id ? t.node(id) : null, v = this.fresh();
        // a new pick, a new log: its lines are the pick's, and go with it
        this._said = [];
        this._says = 0;
        this._lines = [];
        for (var i = 0; i < _SAID; i++) this._lines.push(this.mint(v, "said-" + i, "p", i ? tx_hint : tx_code, this._log));
        if (!n || n.is !== "component") {
            this.mint(v, "none", "p", tx_hint, this._stage, n
                ? "A " + n.is + " is not seen in action: pick a component - a leaf of the tree - to see it built and used."
                : "Pick a component - a leaf of the tree - to see it built live, its behaviour exercised and said, and its axes set by number.");
            this._nothingToSet(v);
            return;
        }
        this.mint(v, "title", "h3", tx_title, this._head, n.name);
        var first = String(n.meaning || "").split(/\n\s*\n/)[0].replace(/\s+/g, " ").trim();
        if (first) this.mint(v, "means", "p", tx_prose, this._head, first);
        var Specimen = HOUSE_SPECIMENS[id];
        if (!Specimen) {
            this.mint(v, "none", "p", tx_hint, this._stage, HOUSE_AROUND[id]
                ? "Shown in action by the page around this one: " + HOUSE_AROUND[id] + "."
                : HOUSE_UNREALIZED.indexOf(id) >= 0
                ? "No realization yet: nothing implements it - an owner mints it, or nothing does - so what it means is all there is to show."
                : "Realized, and its specimen is still to come.");
            this._nothingToSet(v);
            return;
        }

        // the component, live, on the stage
        this._specimen = new Specimen(v.createBranch("specimen"), { leaf: id, say: function (words) { self._say(words); } });
        this._stage.appendChild(this._specimen.root);

        // its axes, on the deck
        if (!n.extents.length) {
            this.mint(v, "no-axes", "p", tx_hint, this._controls, "It varies by no degree: nothing to set.");
            return;
        }
        this._axes = new SliderGroupBuilder().title("Varies by degree").build(v.createBranch("axes"));
        n.extents.forEach(function (axis) {
            var rest = t.rest(axis);
            self._axes.add(axis, new SliderBuilder().label(axis).icon(axis === "colour" ? "extent" : axis)
                .range(-1, 1, 0.05).detent(rest).value(rest)
                .format(function (x) { return x.toFixed(2) + (x === rest ? "  at rest" : ""); })
                .onInput(function (x) { if (self._specimen) self._specimen.extent(axis, x); })
                .onChange(function (x) { self._say(axis + " set to " + x.toFixed(2)); }));
        });
        this._controls.appendChild(this._axes.root);
    }

    /** The deck's controls, when there is nothing to set. */
    _nothingToSet(v) {
        this.mint(v, "nothing", "p", tx_hint, this._controls, "Nothing to set: a component with a specimen has its axes here.");
    }

    /** What the component did, said: the newest first, the last few kept. */
    _say(words) {
        this._said.unshift((++this._says) + ". " + words);
        if (this._said.length > _SAID) this._said.length = _SAID;
        for (var i = 0; i < this._lines.length; i++) this._lines[i].textContent = this._said[i] || "";
    }

    /** The specimen and its axes gone, with what they listen to. */
    _drop() {
        if (this._specimen) { try { this._specimen.dispose(); } catch (e) {} this._specimen = null; }
        if (this._axes) { try { this._axes.dispose(); } catch (e) {} this._axes = null; }
        this._keysAt = null;
    }

    /** The keys, to what was last pressed in it; Escape neither takes, given back. */
    keyDown(ev) {
        var to = this._keysAt === "axes" ? this._axes : this._specimen;
        if (to && to.key(ev)) return true;
        return super.keyDown(ev);
    }

    disposed() { this._drop(); }
}
