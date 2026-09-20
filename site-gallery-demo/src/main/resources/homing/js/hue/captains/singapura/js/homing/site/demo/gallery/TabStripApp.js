// =============================================================================
// TabStripApp — the strip alone, no pane: five chips in a row and the drag
// that is a browser's. Press a chip and it is selected; drag it and it goes
// where the hand goes, the press remembered as an offset within it, the
// others stepping aside live as it passes them; let go and it lands on the
// slot it is nearest. Pull it down until two thirds of it is off the strip
// and it leaves the row — the same chip, now afloat: free under the hand,
// left where the hand lets go, the row closed behind it. Press it again and
// bring it back onto the strip and it is seated; a button seats every chip
// afloat. A slider sets the chips' size, so the arithmetic is seen to hold
// at any pitch. Every step is a line on the log.
// =============================================================================

const _owner = Object.freeze({ toString: () => "tabStripPage" });
var _NAMES = ["Inbox", "Drafts", "Sent", "Archive", "Spam"];

class TabStripWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-ui-panes · TabStrip";
        el.appendChild(kicker);
        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Tab strip";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "The strip alone. Press a chip: selected. Drag it: it follows your hand, held where you pressed it, and the "
            + "others step aside as it passes; let go and it lands. Pull it down until two thirds of it is off the strip and it "
            + "floats — the same chip, free — and stays where you let go; bring it back onto the strip and it is seated again.";
        el.appendChild(lede);

        // ── controls ──────────────────────────────────────────────────────
        var controls = branch.createElement("controls", "div");
        css.addClass(controls, ga_buttons);
        el.appendChild(controls);

        // ── the box: the strip on top, the floor under it for chips afloat ─
        var box = branch.createElement("box", "div");
        css.addClass(box, ga_strip_box);
        el.appendChild(box);
        var shelf = branch.createElement("shelf", "div");
        css.addClass(shelf, ga_shelf);

        var log = branch.createElement("log", "div");
        css.addClass(log, ga_log);
        log.setAttribute("aria-live", "polite");
        el.appendChild(log);
        var lines = 0;
        function say(line) {
            lines++;
            log.textContent += (lines > 1 ? "\n" : "") + lines + "  " + line;
            log.scrollTop = log.scrollHeight;
        }

        var order = [], names = new Map();
        function nameOf(chip) { return names.get(chip); }
        function afloat() { return order.filter(function (c) { return self._strip.floating(c); }); }
        function draw() {
            self._strip.arrange(order);
            self._strip.count(order.length - afloat().length, _NAMES.length, false);
            var f = afloat();
            shelf.textContent = f.length ? "afloat: " + f.map(nameOf).join(", ") : "nothing afloat";
        }

        this._strip = new TabStrip(branch.createBranch("strip"), {
            floating: true,
            // dest counts the seated chips: the order is the seated ones, then the ones afloat
            onDrop: function (chip, dest) {
                var seated = order.filter(function (c) { return !self._strip.floating(c); });
                var from = seated.indexOf(chip);
                seated.splice(from, 1);
                seated.splice(dest, 0, chip);
                order = seated.concat(afloat());
                draw();
                say("Landed    " + nameOf(chip) + "  " + from + " → " + dest);
            },
            onFloat: function (chip, e, grab) {
                draw();
                say("Afloat    " + nameOf(chip) + "  held " + Math.round(grab.x) + "," + Math.round(grab.y) + " in");
            },
            onLand: function (chip, at) {
                say("Let go    " + nameOf(chip) + "  at " + Math.round(at.x) + "," + Math.round(at.y) + " in the strip's frame");
            }
        });
        box.appendChild(this._strip.el);
        box.appendChild(shelf);

        _NAMES.forEach(function (name) {
            var id = name.toLowerCase();
            var chip = self._strip.chip({ id: id, title: name }, {
                onSelect: function () { self._strip.select(order, chip); say("Selected  " + name); },
                onClose:  function () { order.splice(order.indexOf(chip), 1); self._strip.remove(chip); draw(); say("Closed    " + name); }
            });
            names.set(chip, name);
            order.push(chip);
        });
        draw();
        this._strip.select(order, order[0]);

        controls.appendChild(this._slider(branch, "size", "the chips' size", -1, 1, 0.1, 0, function (v) { self._strip.size(v); return v.toFixed(1); }));
        var back = new ButtonBuilder().label("Seat the chips afloat").plain().onClick(function () {
            var f = afloat();
            if (!f.length) { say("Nothing   afloat"); return; }
            f.forEach(function (chip) { self._strip.seat(chip); });
            say("Seated    " + f.map(nameOf).join(", "));
            draw();
        });
        controls.appendChild(back.build(branch.createElement("back", back.tag)).el);
        say("five chips, Inbox selected");
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

    dispose() { this._strip.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new TabStripWidget(domOpsParty.createBranch("tabStripPage"), params).root);
}
