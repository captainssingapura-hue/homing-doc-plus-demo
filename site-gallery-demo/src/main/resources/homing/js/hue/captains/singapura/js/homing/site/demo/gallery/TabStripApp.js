// =============================================================================
// TabStripApp — the strip alone, no pane: five chips in a row and the drag
// that is a browser's, along a rail. Press a chip and it is selected, and
// lifts; drag it and it goes where the hand goes along the row, the press
// remembered as an offset within it, the others stepping aside live as it
// passes them; let go and it settles onto the slot it is nearest. The hand
// may wander up or down; the chip stays on its rail. A slider sets the
// chips' size, so the arithmetic is seen to hold at any pitch. Every step is
// a line on the log.
// =============================================================================

const _owner = Object.freeze({ toString: () => "tabStripPage" });
var _NAMES = ["Inbox", "Drafts", "Sent", "Archive", "Spam"];

class TabStripWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");
        // the keys, through the party: the page's steward, made by the chrome and handed in the params;
        // the members' ids qualified by the page, since the shell's one party has every page's members in it
        var kb = params && params.keyboard;
        if (!kb) throw new Error("[gallery] the page's keyboard steward is required: params.keyboard");

        var kicker = new Kicker(KickerFactory.create(branch, "kicker"), { text: "homing-ui-panes · TabStrip" });
        css.addClass(kicker.el, ga_kicker);
        el.appendChild(kicker.el);
        var title = new Heading1(Heading1Factory.create(branch, "title"), { text: "Tab strip" });
        css.addClass(title.el, ga_title);
        el.appendChild(title.el);
        var lede = new Lede(LedeFactory.create(branch, "lede"), { text: "The strip alone. Press a chip: selected, and lifted. Drag it along the row: it follows your hand, held where "
            + "you pressed it, and the others step aside as it passes; let go and it settles onto the nearest slot. Wander up or "
            + "down as you like — the chip stays on its rail." });
        css.addClass(lede.el, ga_lede);
        el.appendChild(lede.el);

        // ── controls ──────────────────────────────────────────────────────
        var controls = branch.createElement("controls", "div");
        css.addClass(controls, ga_buttons);
        el.appendChild(controls);

        // ── the box: the strip on top, the floor under it ─────────────────
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
        function draw() {
            self._strip.arrange(order);
            self._strip.count(order.length, _NAMES.length, false);
            shelf.textContent = "the row: " + order.map(nameOf).join(" · ");
        }

        this._strip = new TabStrip(branch.createBranch("strip"), {
            keyboard: kb, keyboardId: "tab-strip/strip",
            onDrop: function (chip, dest) {
                var from = order.indexOf(chip);
                order.splice(from, 1);
                order.splice(dest, 0, chip);
                draw();
                say("Landed    " + nameOf(chip) + "  " + from + " → " + dest);
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

        controls.appendChild(new Slider(branch.createBranch("size"), { keyboard: kb, keyboardId: "tab-strip/size", label: "the chips' size", axis: true, icon: "size",
            onInput: function (v) { self._strip.size(v); }, format: function (v) { return v.toFixed(1); } }).root);
        say("five chips, Inbox selected");
        this.root = el;
    }

    dispose() { this._strip.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new TabStripWidget(domOpsParty.createBranch("tabStripPage"), params).root);
}
