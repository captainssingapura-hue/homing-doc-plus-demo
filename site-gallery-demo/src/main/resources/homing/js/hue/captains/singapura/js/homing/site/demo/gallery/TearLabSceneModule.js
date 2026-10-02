// =============================================================================
// TearLabScene — the tear-off lab's stage and its chart. The stage is a
// sunken box with a strip of chips across its top, the escape's edges dashed
// above and below it and the capture's dotted inside them. Drag a chip: on the
// rail it slides along the row, the others stepping aside; once its centre is
// past the escape it is torn — lifted off the strip and left waiting at the
// breach, faded, while the hand flies on; when TabTear settles the flight, the
// chip jumps to the hand and follows it. Bring its centre back within the
// capture and it is in the row again at once, under the hand, still in the
// drag. Let go anywhere else and it stays, a window in all but name; dragged
// again, it starts afloat and is captured the same way. The hand and the
// chip's centre are drawn, the hand's path dotted in the phase's colour, the
// breach, the settle and the capture marked. Nothing here decides anything:
// TabTear does, and TabDrag places the chip along the rail.
//
//   new TearStage(branch, { host, names, tear, onStep?, onDone? })
//     tear     the page's TabTear; the page sets its options
//     onStep(step, hand, kind)   every step of a gesture: kind "press", "move", "frame" or "release"
//     onDone(summary)      { name, torn, afloat, tears, captures, why, flightMs, jump }
//   stage.edges()   the bands' edges drawn again: after the escape or the capture changes
//   stage.reset()   every chip back on the strip, in the first order; the marks gone
//   new TearChart(branch, { host })   the speed, a bar a frame
//     chart.start() .push(speed, phase) .levels(v0, change)
// =============================================================================

const _labOwner = Object.freeze({ toString: () => "tearLab" });
var _DOTS = 240, _BARS = 120, _CHART_H = 90;
var _PHASE = { rail: tl_rail, flight: tl_flight, follow: tl_follow, done: tl_follow };

function _place(el, x, y) { el.style.setProperty("--tl-x", x + "px"); el.style.setProperty("--tl-y", y + "px"); }
function _paint(el, phase) { [tl_rail, tl_flight, tl_follow].forEach(function (c) { css.toggleClass(el, c, c === _PHASE[phase]); }); }

class TearStage {
    constructor(branch, opts) {
        var self = this, o = opts || {};
        branch.activate(_labOwner);
        this._tear = o.tear;
        this._onStep = typeof o.onStep === "function" ? o.onStep : function () {};
        this._onDone = typeof o.onDone === "function" ? o.onDone : function () {};
        var stage = this.root = branch.createElement("stage", "div");
        css.addClass(stage, tl_stage);
        this._strip = branch.createElement("strip", "div");
        css.addClass(this._strip, mtp_strip, tl_strip);
        this._strip.setAttribute("role", "tablist");
        stage.appendChild(this._strip);
        this._edge = ["escape-top", "escape-bottom", "capture-top", "capture-bottom"].map(function (n, i) {
            var e = branch.createElement(n, "div");
            css.addClass(e, tl_band);
            if (i > 1) css.addClass(e, tl_band_inner);
            stage.appendChild(e);
            return e;
        });
        this._hand = branch.createElement("hand", "div");
        css.addClass(this._hand, tl_hand);
        this._centre = branch.createElement("centre", "div");
        css.addClass(this._centre, tl_centre);
        this._marks = { breach: branch.createElement("mark-breach", "div"), settle: branch.createElement("mark-settle", "div"), capture: branch.createElement("mark-capture", "div") };
        Object.keys(this._marks).forEach(function (k) { css.addClass(self._marks[k], tl_mark); });
        this._dots = [];
        for (var i = 0; i < _DOTS; i++) { var d = branch.createElement("dot-" + i, "div"); css.addClass(d, tl_dot); this._dots.push(d); }
        this._next = 0;
        var chips = branch.createBranch("chips");
        chips.activate(_labOwner);
        this._names = new Map();
        this._first = (o.names || []).map(function (name) {
            var id = name.toLowerCase();
            var c = TabChip.mint(chips, { id: id, title: name, closable: false }, { onSelect: function () { self._select(c); } }, "-" + id);
            self._names.set(c, name);
            return c;
        });
        this.reset();
        stage.addEventListener("pointerdown", function (ev) { self._down(ev); });
        stage.addEventListener("pointermove", function (ev) { if (self._g) self._move(ev); });
        stage.addEventListener("pointerup", function (ev) { if (self._g) self._up(ev); });
        stage.addEventListener("pointercancel", function (ev) { if (self._g) self._up(ev); });
        if (o.host) o.host.appendChild(stage);
        requestAnimationFrame(function () { self.edges(); });
    }

    reset() {
        var self = this;
        this._unshift();
        this._first.forEach(function (c) { self._seat(c); self._strip.appendChild(c); });
        this._clearTrail();
        this._select(this._first[0]);
    }

    edges() {
        var b = this._band(), o = this._tear.options(), e = o.escape, c = Math.min(o.capture, o.escape);
        [b.top - e, b.bottom + e, b.top - c, b.bottom + c].forEach(function (y, i) { this._edge[i].style.setProperty("--tl-y", y + "px"); }, this);
    }

    // ── where things are, on the stage ───────────────────────────────────
    _at(ev) { var r = this.root.getBoundingClientRect(); return { x: ev.clientX - r.left, y: ev.clientY - r.top }; }
    _rect(el) { var r = this.root.getBoundingClientRect(), e = el.getBoundingClientRect(); return { left: e.left - r.left, top: e.top - r.top, width: e.width, height: e.height }; }
    _band() { var s = this._rect(this._strip); return { top: s.top, bottom: s.top + s.height }; }
    _row() { return Array.prototype.filter.call(this._strip.children, function (c) { return c._chip; }); }
    _select(c) { this._row().concat(this._loose()).forEach(function (x) { x.setAttribute("aria-selected", x === c ? "true" : "false"); }); }
    _loose() { var self = this; return this._first.filter(function (c) { return c.parentNode === self.root; }); }

    // ── the gesture ──────────────────────────────────────────────────────
    _down(ev) {
        if (ev.button !== 0 || this._g) return;
        var c = null;
        for (var x = ev.target; x && x !== this.root; x = x.parentNode) if (x._chip) { c = x; break; }
        if (!c) return;
        var p = this._at(ev), r = this._rect(c), afloat = this._row().indexOf(c) < 0;
        // the centre from the hand: what both bands measure, wherever on the chip it was taken
        var centre = { dx: r.left + r.width / 2 - p.x, dy: r.top + r.height / 2 - p.y };
        this._g = { chip: c, grip: { x: p.x - r.left, y: p.y - r.top }, centre: centre, afloat: afloat, tears: 0, captures: 0 };
        this.root.setPointerCapture(ev.pointerId);
        ev.preventDefault();
        this._clearTrail();
        css.removeClass(c, mtp_chip_seated);
        if (!afloat) { this._measure(); css.addClass(c, mtp_chip_dragging); c.style.setProperty("--mtp-drag-x", "0px"); }
        this._step(this._tear.press(p.x, p.y, ev.timeStamp, this._band(), { centre: centre, afloat: afloat }), p, "press");
        this._frame();
    }

    /** The row as it lies, and the chip's slot in it: what the slide along the rail is measured against. */
    _measure() {
        var g = this._g, row = this._row();
        g.slots = row.map(this._rect, this);
        g.from = g.to = row.indexOf(g.chip);
        g.left0 = g.slots[g.from].left;
    }

    _frame() {
        var self = this;
        this._g.raf = requestAnimationFrame(function (ts) { if (!self._g) return; self._step(self._tear.tick(ts), self._g.hand, "frame"); self._frame(); });
    }

    _move(ev) {
        var p = this._at(ev);
        // every sample the browser gathered since the last event, each at its own time: a browser sends about one
        // move a frame, and the velocity wants the hand's path, not the frame's
        var all = typeof ev.getCoalescedEvents === "function" ? ev.getCoalescedEvents() : [], s = null, self = this;
        if (!all.length) all = [ev];
        all.forEach(function (e) {
            var q = self._at(e), d = self._dots[self._next++ % _DOTS];
            s = self._tear.move(q.x, q.y, e.timeStamp);
            _place(d, q.x, q.y);
            _paint(d, s.phase);
            self.root.appendChild(d);
        });
        this._step(s, p, "move");
    }

    /** What TabTear said, drawn: the chip on its rail — captured back into it, if it was off — torn off and waiting, or at the hand. */
    _step(s, p, kind) {
        var g = this._g, c = g.chip;
        g.hand = p;
        this._show(this._hand, p);
        this._show(this._centre, { x: p.x + g.centre.dx, y: p.y + g.centre.dy });
        if (s.phase === "rail") {
            if (c.parentNode === this.root) this._capture(s, p);
            this._slide(p);
        } else {
            if (c.parentNode !== this.root) this._lift(s);
            css.toggleClass(c, tl_waiting, s.phase === "flight");
            _place(c, s.anchor.x - g.grip.x, s.anchor.y - g.grip.y);
            if (s.settle && !g.settled && s.settle.why !== "afloat") { g.settled = true; this._mark("settle", s.settle, "S  " + s.settle.why); }
        }
        this._onStep(s, p, kind);
        return s;
    }

    _slide(p) {
        var g = this._g;
        var left = TabDrag.clamp(p.x - g.grip.x, g.slots, 0), to = TabDrag.dest(left, g.slots, 0), pitch = TabDrag.pitch(g.slots);
        g.chip.style.setProperty("--mtp-drag-x", (left - g.left0) + "px");
        g.to = to;
        this._row().forEach(function (c, j) {
            var k = TabDrag.shift(j, g.from, to);
            css.toggleClass(c, mtp_chip_shifted, j !== g.from && k !== 0);
            if (j !== g.from) c.style.setProperty("--mtp-shift-x", (k * pitch) + "px");
        });
    }

    /** Torn: off the strip — the others close up — and onto the stage, at the breach. */
    _lift(s) {
        var c = this._g.chip;
        this._unshift();
        css.removeClass(c, mtp_chip_dragging);
        css.addClass(c, tl_free);
        this.root.appendChild(c);
        this._g.tears++;
        this._g.settled = false;
        this._hide(this._marks.settle);
        this._mark("breach", s.breach, "B  " + s.breach.speed.toFixed(2) + " px/ms");
    }

    /** Captured: back into the row at once, where the chip's centre is along it, and the slide goes on from there. */
    _capture(s, p) {
        var g = this._g, c = g.chip, x = p.x + g.centre.dx;
        var at = this._row().filter(function (o) { var r = this._rect(o); return r.left + r.width / 2 < x; }, this).length;
        css.removeClass(c, tl_free, tl_waiting);
        this._strip.insertBefore(c, this._row()[at] || null);
        css.addClass(c, mtp_chip_dragging);
        c.style.setProperty("--mtp-drag-x", "0px");
        this._measure();
        g.captures++;
        this._mark("capture", s.captured, "C");
    }

    _up(ev) {
        var g = this._g, p = this._at(ev), c = g.chip;
        if (g.raf) cancelAnimationFrame(g.raf);
        this._g = null;
        this._hide(this._hand);
        this._hide(this._centre);
        css.removeClass(c, tl_waiting);
        var s = this._tear.release(p.x, p.y, ev.timeStamp), torn = this._tear.torn(), b = s.breach, st = s.settle;
        if (!torn) this._dock(c, g.to);
        else { this._seat(c, true); _place(c, p.x - g.grip.x, p.y - g.grip.y); }
        if (st && !g.settled && st.why !== "afloat") this._mark("settle", st, "S  " + st.why);
        this._onStep(s, p, "release");
        this._onDone({ name: this._names.get(c), torn: torn, afloat: g.afloat, tears: g.tears, captures: g.captures,
                       why: st ? st.why : null,
                       flightMs: b && st ? st.t - b.t : null,
                       jump: b && st ? Math.hypot(st.x - b.x, st.y - b.y) : null });
    }

    _dock(c, index) {
        this._unshift();
        this._seat(c);
        var row = this._row().filter(function (x) { return x !== c; });
        this._strip.insertBefore(c, row[index] || null);
    }

    /** At rest: seated, out of the hand; on the stage still, when it is loose. */
    _seat(c, loose) {
        css.removeClass(c, mtp_chip_dragging, tl_waiting);
        css.addClass(c, mtp_chip_seated);
        c.style.removeProperty("--mtp-drag-x");
        if (!loose) css.removeClass(c, tl_free);
    }

    _unshift() { this._row().forEach(function (x) { css.removeClass(x, mtp_chip_shifted); x.style.removeProperty("--mtp-shift-x"); }); }
    _mark(k, at, text) { var m = this._marks[k]; m.textContent = text; this._show(m, at); }
    _show(el, p) { _place(el, p.x, p.y); if (el.parentNode !== this.root) this.root.appendChild(el); }
    _hide(el) { if (el.parentNode) el.parentNode.removeChild(el); }
    _clearTrail() {
        var self = this;
        this._dots.forEach(function (d) { self._hide(d); });
        this._next = 0;
        this._hide(this._marks.breach);
        this._hide(this._marks.settle);
        this._hide(this._marks.capture);
    }
}

class TearChart {
    constructor(branch, opts) {
        var o = opts || {};
        branch.activate(_labOwner);
        var root = this.root = branch.createElement("chart", "div");
        css.addClass(root, tl_chart);
        this._bars = [];
        for (var i = 0; i < _BARS; i++) { var b = branch.createElement("bar-" + i, "div"); css.addClass(b, tl_bar); root.appendChild(b); this._bars.push(b); }
        this._lines = ["low", "v0", "high"].map(function (n) { var l = branch.createElement("level-" + n, "div"); css.addClass(l, tl_level); return l; });
        this.start();
        if (o.host) o.host.appendChild(root);
    }
    start() { this._vals = []; this._v0 = null; this._draw(); }
    push(speed, phase) { this._vals.push({ v: speed, phase: phase }); if (this._vals.length > _BARS) this._vals.shift(); this._draw(); }
    levels(v0, change) { this._v0 = v0; this._change = change; this._draw(); }
    _draw() {
        var vals = this._vals, peak = 0.5, root = this.root, self = this;
        vals.forEach(function (x) { peak = Math.max(peak, x.v); });
        if (this._v0 != null) peak = Math.max(peak, this._v0 * (1 + this._change));
        var k = _CHART_H / (peak * 1.1), off = _BARS - vals.length;
        this._bars.forEach(function (b, i) {
            var x = vals[i - off];
            b.style.setProperty("--tl-h", (x ? x.v * k : 0) + "px");
            _paint(b, x ? x.phase : null);
        });
        [this._v0 == null ? null : this._v0 * (1 - this._change), this._v0, this._v0 == null ? null : this._v0 * (1 + this._change)].forEach(function (v, i) {
            var l = self._lines[i];
            if (v == null) { if (l.parentNode) root.removeChild(l); return; }
            l.style.setProperty("--tl-y", (v * k) + "px");
            root.appendChild(l);
        });
    }
}
