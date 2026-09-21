// =============================================================================
// SlidersApp — the slider on view. The three axes the demos set — size,
// aspect, extent — each resting at nought on its detent; a plain range with
// a unit and a coarser step; one that is off; and the slider at its three
// sizes, the smallest, the design's, the biggest — the track, the knob and
// the notch all growing by the design's ratio. Every value is a line on the
// log, live as the hand moves and once more on release, so the two events
// are seen apart. Press anywhere on a rail; the knob takes the keys.
// =============================================================================

const _owner = Object.freeze({ toString: () => "slidersPage" });

class SlidersWidget {
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
        title.textContent = "Sliders";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "A number set by a knob on a track. Every part is the design's: the track sunk, the fill from the detent, "
            + "the knob raised and ringed when it has the focus, the notch where the knob rests. Press anywhere on a rail, or "
            + "take the knob with the keys: arrows by a step, with Shift by ten, Home and End. The same slider sets the size, "
            + "the aspect and the extent on the other pages.";
        el.appendChild(lede);

        var log = branch.createElement("log", "div");
        css.addClass(log, ga_log);
        log.setAttribute("aria-live", "polite");
        var lines = 0;
        function say(line) {
            lines++;
            log.textContent += (lines > 1 ? "\n" : "") + lines + "  " + line;
            log.scrollTop = log.scrollHeight;
        }
        var specimens = branch.createElement("specimens", "div");
        css.addClass(specimens, ga_specimens);
        el.appendChild(specimens);
        el.appendChild(log);

        function section(name, caption, fill) {
            var box = branch.createElement("s-" + name, "div");
            var cap = branch.createElement("s-" + name + "-cap", "div");
            css.addClass(cap, ga_specimen_name);
            cap.textContent = caption;
            box.appendChild(cap);
            var own = branch.createBranch("s-" + name);
            own.activate(_owner);
            fill(box, own);
            specimens.appendChild(box);
        }
        function reporting(b, name) {
            return b.onInput(function (v) { say(name + "  " + v + "  (live)"); }).onChange(function (v) { say(name + "  " + v + "  changed"); });
        }
        var words = { size: function (v) { return v === 0 ? "regular" : v === 1 ? "the biggest" : v === -1 ? "the smallest" : ""; },
                      aspect: function (v) { return v === 0 ? "the design's" : v > 0 ? "wider" : "narrower"; },
                      extent: function (v) { return v === 1 ? "the word" : v === 0 ? "neutral" : v === -1 ? "the other meaning" : ""; } };
        this._sliders = [];

        section("axes", "the three axes — each rests at nought, on its detent", function (box, own) {
            ["size", "aspect", "extent"].forEach(function (axis) {
                var s = reporting(new SliderBuilder().label(axis).axis().icon(axis).labelWidth("5em").value(axis === "extent" ? 1 : 0)
                    .format(function (v) { return v.toFixed(1) + "  " + words[axis](v); }), axis).build(own.createBranch(axis));
                self._sliders.push(s);
                box.appendChild(s.root);
            });
        });
        section("plain", "a plain range — 0 to 100 by 5, a unit in the readout, no detent", function (box, own) {
            var s = reporting(new SliderBuilder().label("volume").range(0, 100, 5).icon("level").value(40).format(function (v) { return v + " %"; }), "volume").build(own.createBranch("volume"));
            self._sliders.push(s);
            box.appendChild(s.root);
        });
        section("off", "off — inert, and says so; its knob keeps the grip", function (box, own) {
            var s = new SliderBuilder().label("gain").range(-12, 12, 1).detent(0).value(3).format(function (v) { return (v > 0 ? "+" : "") + v + " dB"; }).build(own.createBranch("gain"));
            s.setOn(false);
            self._sliders.push(s);
            box.appendChild(s.root);
        });
        section("sizes", "the slider at its three sizes: the track, the knob and the notch grow by the design's ratio", function (box, own) {
            [[-1, "the smallest"], [0, "the design's"], [1, "the biggest"]].forEach(function (pair) {
                var s = reporting(new SliderBuilder().label(pair[1]).axis().icon("size").labelWidth("8em").size(pair[0]).format(function (v) { return v.toFixed(1); }), "size " + pair[0]).build(own.createBranch("size" + (pair[0] + 1)));
                self._sliders.push(s);
                box.appendChild(s.root);
            });
        });
        section("mixer", "stood up, as a mixer's faders: -60 to +10 dB by one, unity on the detent, a scale beside each track", function (box, own) {
            var strip = own.createElement("strip", "div");
            css.addClass(strip, ga_mixer);
            var scale = [{ at: 10, label: "+10" }, { at: 5, label: "+5" }, { at: 0, label: "0" }, { at: -5, label: "-5" }, { at: -10, label: "-10" }, { at: -20, label: "-20" }, { at: -30, label: "-30" }, { at: -40, label: "-40" }, { at: -60, label: "-∞" }];
            function dB(v) { return v <= -60 ? "-∞ dB" : (v > 0 ? "+" : "") + v + " dB"; }
            [["vocals", 0], ["bass", -6], ["drums", -3], ["keys", -12]].forEach(function (ch) {
                var s = reporting(new SliderBuilder().label(ch[0]).vertical().range(-60, 10, 1).detent(0).value(ch[1]).icon("level").ticks(scale).format(dB), ch[0]).build(own.createBranch(ch[0]));
                self._sliders.push(s);
                strip.appendChild(s.root);
            });
            box.appendChild(strip);
        });
        say("twelve sliders; press a rail, or focus a knob and use the arrows");
        this.root = el;
    }

    dispose() { this._sliders.forEach(function (s) { s.dispose(); }); }
}

function appMain(el, params) {
    el.appendChild(new SlidersWidget(domOpsParty.createBranch("slidersPage"), params).root);
}
