// =============================================================================
// TearLabApp — tearing a tab off its strip, as a lab to play in. A strip of
// chips on a stage: drag one along the row and it stays on its rail; drag it
// past a dashed line and it tears — a window would be made there, at the
// breach. The hand is usually fast at that moment, so the window waits while
// the hand keeps the breach's velocity; a material change, held for a moment,
// settles it at the hand. TabTear decides all of it, headless; the page only
// sets its options, and draws what it says: the phase, the speed against the
// breach's, the departure, and a chart of the speed a frame. No window or
// floating pane is made here: a torn chip stands for one.
// =============================================================================

const _owner = Object.freeze({ toString: () => "tearLabPage" });
var _NAMES = ["Inbox", "Drafts", "Sent", "Archive", "Spam"];
var _SAYS = { rail: "on the rail: along the row only", flight: "torn: the window waits at the breach", follow: "settled: the window is at the hand, and follows it", done: "let go" };

class TearLabWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var kb = params && params.keyboard;
        if (!kb) throw new Error("[gallery] the page's keyboard steward is required: params.keyboard");
        var el = this.root = branch.createElement("root", "div");
        [["kicker", "div", ga_kicker, "homing-ui-panes · TabTear"], ["title", "h1", ga_title, "Tearing a tab off"],
         ["lede", "p", ga_lede, "Drag a chip. The ring is its centre, and the lines measure the ring, not your hand. While the "
            + "ring is between the dashed lines the chip stays on its rail, sliding along the row however far your hand wanders "
            + "sideways. Past a dashed line it tears: the chip is the window, made where you crossed — the breach — and it follows "
            + "your hand from there. Turn the flight on for the other model: the faded window waits at the breach while your hand "
            + "keeps the speed it had there; slow down, stop or turn (as the measure says) and keep it up for the hold, and it "
            + "settles at your hand and follows it; a tear slower than the floor settles at once. "
            + "Bring the ring back between the dotted lines and the chip is captured: in the row again at once, still in your "
            + "hand. The gap between the two pairs of lines is what keeps a chip on the edge from tearing and landing by turns. "
            + "Let go anywhere else and it stays; dragged again, it is a window from the start, captured the same way. The dots "
            + "are your hand's path, in the phase's colour; B is the breach, S the settle, C the capture."]].forEach(function (d) {
            var e = branch.createElement(d[0], d[1]);
            css.addClass(e, d[2]);
            e.textContent = d[3];
            el.appendChild(e);
        });

        var tear = this._tear = new TabTear();
        var controls = branch.createElement("controls", "div");
        css.addClass(controls, ga_buttons);
        el.appendChild(controls);
        function slider(id, label, min, max, step, value, unit, to) {
            controls.appendChild(new SliderBuilder().keyboard(kb, "tear-lab/" + id).label(label).range(min, max, step).value(value).icon(null).labelWidth("7em")
                .format(function (v) { return (step < 1 ? v.toFixed(2) : v) + unit; })
                .onInput(function (v) { var o = {}; o[id] = to ? to(v) : v; tear.set(o); if (id === "escape" || id === "capture") self._stage.edges(); })
                .build(branch.createBranch(id)).root);
        }
        var d = TabTear.DEFAULTS;
        slider("escape", "the escape", 0, 80, 2, d.escape, " px");
        slider("capture", "the capture", 0, 80, 2, d.capture, " px");
        slider("change", "the change", 10, 150, 5, d.change * 100, " %", function (v) { return v / 100; });
        slider("hold", "the hold", 0, 200, 10, d.hold, " ms");
        slider("span", "measured over", 20, 200, 10, d.span, " ms");
        slider("idle", "at rest after", 0, 200, 10, d.idle, " ms");
        slider("floor", "the floor", 0, 1, 0.05, d.floor, " px/ms");
        controls.appendChild(this._pick(branch, "flight", "the flight", "what the window does at the breach",
            [["off", "none: it follows from the breach"], ["on", "it waits until the hand changes"]], tear.options().flight ? "on" : "off",
            function (v) { tear.set({ flight: v === "on" }); }));
        controls.appendChild(this._pick(branch, "measure", "the measure", "what counts as a change",
            [["speed", "the speed, either way"], ["slowdown", "the speed, falling only"], ["velocity", "the vector: a turn counts"]], tear.options().measure,
            function (v) { tear.set({ measure: v }); }));
        var reset = new ButtonBuilder().label("Put them back").colour("secondary").onClick(function () { self._stage.reset(); say("Reset     every chip back on the strip"); });
        controls.appendChild(reset.build(branch.createElement("reset", reset.tag)).el);

        var readout = branch.createElement("readout", "div");
        css.addClass(readout, tl_readout);
        var log = branch.createElement("log", "div");
        css.addClass(log, ga_log);
        log.setAttribute("aria-live", "polite");
        var lines = 0;
        function say(line) { lines++; log.textContent += (lines > 1 ? "\n" : "") + lines + "  " + line; log.scrollTop = log.scrollHeight; }

        var chart = null, charted = null;
        this._stage = new TearStage(branch.createBranch("stage"), { host: el, names: _NAMES, tear: tear,
            onStep: function (s, p, kind) {
                if (kind === "press") chart.start();
                if (kind === "frame") chart.push(s.speed, s.phase);
                if (s.breach && s.breach !== charted) { charted = s.breach; chart.levels(s.breach.speed, tear.options().change); }
                readout.textContent = self._read(s, p);
            },
            onDone: function (r) { say(self._story(r)); } });
        chart = new TearChart(branch.createBranch("chart"), { host: el });
        el.appendChild(readout);
        el.appendChild(log);
        readout.textContent = "Press a chip and drag it.";
        say("five chips on the strip; the escape " + d.escape + " px either side of it, the capture " + d.capture);
    }

    /** A choice among a few, labelled: the flight, and what counts as a change in it. */
    _pick(branch, id, text, aria, choices, value, onPick) {
        var row = branch.createElement(id, "div");
        css.addClass(row, ga_control);
        var label = branch.createElement(id + "-label", "span");
        css.addClass(label, ga_control_label);
        label.textContent = text;
        row.appendChild(label);
        var pick = branch.createElement(id + "-pick", "select");
        css.addClass(pick, mtp_new_pick);
        pick.setAttribute("aria-label", aria);
        choices.forEach(function (m) {
            var opt = branch.createElement(id + "-" + m[0], "option");
            opt.value = m[0];
            opt.textContent = m[1];
            pick.appendChild(opt);
        });
        pick.value = value;
        pick.addEventListener("change", function () { onPick(pick.value); });
        row.appendChild(pick);
        return row;
    }

    /** The gesture as it stands, in numbers. */
    _read(s, p) {
        var o = this._tear.options(), b = s.breach, f = function (v) { return v == null ? "—" : v.toFixed(2); };
        return "phase    " + s.phase + "  —  " + _SAYS[s.phase]
            + "\nspeed    " + f(s.speed) + " px/ms" + (b ? "    at the breach " + f(b.speed) + " px/ms" : "")
            + "\nchange   " + f(s.ratio) + "    material above " + f(o.change) + ", held " + o.hold + " ms, by " + o.measure
            + "\nhand     " + Math.round(p.x) + ", " + Math.round(p.y)
            + (b ? "    breach " + Math.round(b.x) + ", " + Math.round(b.y) : "")
            + (s.settle ? "    settled " + Math.round(s.settle.x) + ", " + Math.round(s.settle.y) + " (" + s.settle.why
                          + (b ? ", " + Math.round(s.settle.t - b.t) + " ms after" : "") + ")" : "")
            + (s.captured ? "\ncaptured " + Math.round(s.captured.x) + ", " + Math.round(s.captured.y) + "    within " + Math.min(o.capture, o.escape)
                            + " px of the strip, the escape at " + o.escape : "");
    }

    /** A gesture over, as a line on the log. */
    _story(r) {
        var name = (r.name + "        ").slice(0, 8);
        var times = (r.tears ? ", torn " + r.tears + "×" : "") + (r.captures ? ", captured " + r.captures + "×" : "");
        if (!r.torn) return (r.afloat ? "Landed    " + name + "a window, captured onto the strip" : "Rail      " + name + "on the strip") + times;
        if (r.why === "afloat") return "Moved     " + name + "a window, dragged where it is" + times;
        return "Torn      " + name + "settled by " + r.why + " " + Math.round(r.flightMs) + " ms after the breach, " + Math.round(r.jump) + " px on — left as a window" + times;
    }

    dispose() {}
}

function appMain(el, params) {
    el.appendChild(new TearLabWidget(domOpsParty.createBranch("tearLabPage"), params).root);
}
