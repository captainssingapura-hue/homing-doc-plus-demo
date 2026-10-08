// =============================================================================
// ComponentLogView — what a component-log party says, newest first: a line for
// each note, numbered as the party numbers it; the last fifty shown. Joining,
// it asks for the lines kept, so a log opened late shows what came before it.
// The party's own Cleared - on the command of the widget that writes the log -
// empties it; its own Clear empties this view alone, and says nothing.
// It never writes: the panels do. Each log is a widget that extends it,
// handing it its party's type: ComponentDemoLog, ComponentControlLog.
//
//   new ComponentLogView(container, name, label, type)   type: the party type it reads
//   (the rest is a TaxonomyWidget's)
// =============================================================================

var _LINES_SHOWN = 50;

class ComponentLogView extends TaxonomyWidget {
    constructor(container, name, label, type) {
        super(container, name, label);
        var self = this;
        this._type = type;
        this._lines = [];
        this._logMember = null;
        var head = this.el("head", "div", tx_line, this.body);
        this.el("title", "h4", tx_title, head, label);
        var clear = new ButtonBuilder().label("Clear").plain().size(-1).onClick(function () { self._lines = []; self._draw(); });
        head.appendChild(clear.build(this.branch.createElement("clear", clear.tag)).el);
        this._box = this.el("lines", "div", tx_scroll, this.body);
        this._draw();
    }

    join(given) {
        var self = this, party = (given || {})[this._type.name];
        if (party) {
            this._logMember = party.join(this._name, {
                Noted: function (m) { self._lines.unshift({ seq: m.seq, words: m.words }); self._lines.length = Math.min(self._lines.length, _LINES_SHOWN); self._draw(); },
                Cleared: function () { self._lines = []; self._draw(); },
                History: function (m) { self._lines = m.lines.slice().reverse().slice(0, _LINES_SHOWN); self._draw(); }
            });
            this._logMember.tell({ kind: "HistoryRequested" });
        }
        super.join(given);
    }

    leave() {
        if (this._logMember) { this._logMember.leave(); this._logMember = null; }
        super.leave();
    }

    /** The lines, newest first, on a view of their own: the last one dissolved. */
    _draw() {
        var self = this, v = this.fresh();
        if (!this._lines.length) { this.mint(v, "empty", "p", tx_hint, this._box, "Nothing yet."); return; }
        this._lines.forEach(function (l, i) { self.mint(v, "line-" + l.seq, "p", i ? tx_hint : tx_code, self._box, l.seq + ". " + l.words); });
    }
}
