// =============================================================================
// FloatLayerDemo — the house's float layer in action: notes over a box that
// cascade and stack - press one behind to raise it - each moved by its head
// and sized by its grip. Its options: open - a note opened; close - the one in
// front closed. The keys the demo holds go to the layer: Escape closes the one
// in front. Every opening, raise, move, resize and close is said.
//
//   new FloatLayerDemo(container, { leaf })   leaf: "float-layer"
//   (the rest is a ComponentDemo's)
// =============================================================================

const _floatLayerDemo = Object.freeze({ toString: () => "floatLayerDemo" });

/** What a pane on the layer shows: a note, by the base's contract for a widget. */
class _FloatNote {
    constructor(branch, params) {
        branch.activate(_floatLayerDemo);
        this.root = branch.createElement("note", "p");
        css.addClass(this.root, dm_text);
        this.root.textContent = "Note " + params.n + ": press a note behind to raise it; Escape closes the one in front.";
    }
    dispose() {}
}

class FloatLayerDemo extends ComponentDemo {
    constructor(container, params) {
        super(container, "float-layer-demo", params);
        var self = this, titles = {};
        var host = this.el("host", "div", dm_host, this.stage);
        this._opened = 0;
        this._layer = new FloatLayer(this.branch.createBranch("layer"), {
            host: host,
            onEvent: function (ev) {
                if (ev.kind === "Opened") { titles[ev.id] = ev.title; self.say(ev.title + " opened"); }
                else if (ev.kind === "Raised") self.say((titles[ev.id] || ev.id) + " raised: in front");
                else if (ev.kind === "Moved") self.say((titles[ev.id] || ev.id) + " moved to " + ev.x + ", " + ev.y);
                else if (ev.kind === "Resized") self.say((titles[ev.id] || ev.id) + " resized to " + ev.w + " by " + ev.h);
                else if (ev.kind === "Closed") self.say((titles[ev.id] || ev.id) + " closed");
            }
        });
        this._note();
        this._note();
        this.intro = "notes cascade and stack: press one behind to raise it, open more or close the one in front from the controls, Escape closes it too";
    }

    _note() {
        var n = ++this._opened;
        this._layer.open({ title: "Note " + n, w: 200, h: 100, widget: _FloatNote, params: { n: n } });
    }

    /** open: a note opened. */
    open() { this._note(); }

    /** close: the one in front closed. */
    close() {
        var front = this._layer.active();
        if (front == null) this.say("nothing is open");
        else this._layer.close(front);
    }

    key(ev) { return this._layer.key(ev); }

    disposed() { try { this._layer.dispose(); } catch (e) {} }
}
