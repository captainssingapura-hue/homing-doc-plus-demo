// =============================================================================
// SpecimenWidget — the picked component in action: built live by its specimen,
// one of the house's (HOUSE_SPECIMENS), its behaviour exercised and every thing
// it does said, newest first; and a slider for each axis its leaf varies along
// - colour, size, aspect - from −1 to 1, at the axis's rest until moved, the
// number set on the live component as it moves. A leaf only: a branch or a part
// shows how to pick one. A leaf nothing realizes yet says so, with what it
// means; a realized one whose specimen is still to come says that.
//
// THE KEYS. Its own, as a member of the keyboard's party: a press in it claims
// them, and they go on to what was last pressed in it - the specimen, or its
// axes - each handing them to what it holds: a slider, a card. Escape that
// neither takes gives them back.
//
//   new SpecimenWidget(container, params)   params: none
//   (the rest is a TaxonomyWidget's)
// =============================================================================

var _SAID = 6;

class SpecimenWidget extends TaxonomyWidget {
    constructor(container, params) {
        super(container, "specimen", "In action");
        this._box = this.el("box", "div", tx_scroll, this.body);
        this._specimen = null;
        this._axes = null;
        this._keysAt = null;
        this._lines = [];
        this._said = [];
        this._says = 0;
        this.selected(this.picked());
    }

    selected(id) {
        this._drop();
        var self = this, t = this.taxonomy, n = id ? t.node(id) : null, v = this.fresh(), box = this._box;
        if (!n || n.is !== "component") {
            this.mint(v, "none", "p", tx_hint, box, n
                ? "A " + n.is + " is not seen in action: pick a component - a leaf of the tree - to see it built and used."
                : "Pick a component - a leaf of the tree - to see it built live, its behaviour exercised and said, and its axes set by number.");
            return;
        }
        this.mint(v, "title", "h3", tx_title, box, n.name);
        var first = String(n.meaning || "").split(/\n\s*\n/)[0].replace(/\s+/g, " ").trim();
        if (first) this.mint(v, "means", "p", tx_prose, box, first);
        var Specimen = HOUSE_SPECIMENS[id];
        if (!Specimen) {
            this.mint(v, "none", "p", tx_hint, box, HOUSE_UNREALIZED.indexOf(id) >= 0
                ? "No realization yet: nothing implements it - an owner mints it, or nothing does - so what it means is all there is to show."
                : "Realized, and its specimen is still to come.");
            return;
        }

        // what it did, newest first
        var said = this._panel(v, box, "said", "What it did");
        this._lines = [];
        for (var i = 0; i < _SAID; i++) this._lines.push(this.mint(v, "said-" + i, "p", i ? tx_hint : tx_code, said));
        this._said = [];
        this._says = 0;

        // the component, live
        var stage = this._panel(v, box, "stage", "Live");
        stage.addEventListener("pointerdown", function () { self._keysAt = "specimen"; }, true);
        box.insertBefore(stage, said);
        this._specimen = new Specimen(v.createBranch("specimen"), { leaf: id, say: function (words) { self._say(words); } });
        stage.appendChild(this._specimen.root);

        // its axes
        if (n.extents.length) {
            var axes = this.mint(v, "axes", "section", tx_panel);
            axes.addEventListener("pointerdown", function () { self._keysAt = "axes"; }, true);
            box.insertBefore(axes, said);
            this._axes = new SliderGroupBuilder().title("Varies by degree").build(v.createBranch("axes"));
            n.extents.forEach(function (axis) {
                var rest = t.rest(axis);
                self._axes.add(axis, new SliderBuilder().label(axis).icon(axis === "colour" ? "extent" : axis)
                    .range(-1, 1, 0.05).detent(rest).value(rest)
                    .format(function (x) { return x.toFixed(2) + (x === rest ? "  at rest" : ""); })
                    .onInput(function (x) { if (self._specimen) self._specimen.extent(axis, x); })
                    .onChange(function (x) { self._say(axis + " set to " + x.toFixed(2)); }));
            });
            axes.appendChild(this._axes.root);
            this.mint(v, "axes-hint", "p", tx_hint, axes,
                "Each a number from −1 to 1 on the live component, set as the knob moves - no component of its own, and no state.");
        }
    }

    _panel(v, box, key, title) {
        var panel = this.mint(v, key, "section", tx_panel, box);
        this.mint(v, key + "-title", "h4", tx_title, panel, title);
        return panel;
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
