// =============================================================================
// TabStripApp — the strip alone, no pane: five chips in a row and the drag
// that is a browser's. Press a chip and it is selected; drag it and it goes
// where the hand goes, the press remembered as an offset within it, the
// others stepping aside live as it passes them; let go and it lands on the
// slot it is nearest. Pull it down until two thirds of it is off the strip
// and it leaves — here, to a shelf under the strip, with the grab it was
// held by, from where a button returns it. A slider sets the chips' size,
// so the arithmetic is seen to hold at any pitch. Every step is a line on
// the log.
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
            + "leaves, to the shelf; the button returns the shelf.";
        el.appendChild(lede);

        // ── controls ──────────────────────────────────────────────────────
        var controls = branch.createElement("controls", "div");
        css.addClass(controls, ga_buttons);
        el.appendChild(controls);

        // ── the box: the strip on top, the shelf under it ─────────────────
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

        var order = [], names = new Map(), shelved = [];
        function nameOf(chip) { return names.get(chip); }
        function draw() {
            self._strip.arrange(order);
            self._strip.count(order.length, _NAMES.length, false);
            shelf.textContent = shelved.length ? "on the shelf: " + shelved.map(nameOf).join(", ") : "the shelf is empty";
        }

        this._strip = new TabStrip(branch.createBranch("strip"), {
            onDrop: function (chip, dest) {
                var from = order.indexOf(chip);
                order.splice(from, 1);
                order.splice(dest, 0, chip);
                draw();
                say("Landed    " + nameOf(chip) + "  " + from + " → " + dest);
            },
            onDragOut: function (chip, e, grab) {
                order.splice(order.indexOf(chip), 1);
                self._strip.remove(chip);
                shelved.push(chip);
                draw();
                say("Left      " + nameOf(chip) + "  at " + Math.round(e.clientX) + "," + Math.round(e.clientY) + "  held " + Math.round(grab.x) + "," + Math.round(grab.y) + " in");
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
        var back = new ButtonBuilder().label("Return the shelf").plain().onClick(function () {
            if (!shelved.length) { say("Nothing   on the shelf"); return; }
            shelved.forEach(function (chip) { order.push(chip); });
            say("Returned  " + shelved.map(nameOf).join(", "));
            shelved = [];
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
