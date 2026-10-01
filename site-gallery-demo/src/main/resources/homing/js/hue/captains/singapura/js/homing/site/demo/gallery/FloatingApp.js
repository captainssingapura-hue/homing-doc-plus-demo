// =============================================================================
// FloatingApp — the desk, exercised. A desk fills the box; buttons open panes
// holding widgets by the base's contract — a card, a counter, a note — on a
// cascade; the hand moves one by its head, sizes it by its corner, raises the
// one it presses, closes with the cross or Escape. Every mutation is one line
// on the log below, as the desk reported it.
// =============================================================================

const _owner = Object.freeze({ toString: () => "floatingPage" });

// Three kinds of widget, each a class by the base's contract.
class CardWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        this.root = new CardBuilder().title(params.title).badge(params.badge).text(params.text).aspect(0.6).build(branch.createBranch("card")).root;
    }
}

class CounterWidget {
    constructor(branch, params) {
        var self = this;
        this._value = params.start || 0;
        branch.activate(_owner);
        var root = branch.createElement("counter", "div");
        css.addClass(root, ga_buttons);
        var count = branch.createElement("count", "div");
        css.addClass(count, ga_count);
        var b = new ButtonBuilder().label("Count").onClick(function () { self._value++; draw(); });
        var btn = b.build(branch.createElement("btn", b.tag));
        function draw() { count.textContent = String(self._value); }
        draw();
        root.appendChild(count);
        root.appendChild(btn.el);
        this.root = root;
    }
    setActive(on) { this.root.setAttribute("data-active", on ? "true" : "false"); }
}

class NoteWidget {
    constructor(branch, params) {
        branch.activate(_owner);
        var root = branch.createElement("note", "p");
        css.addClass(root, ga_lede);
        root.textContent = params.text;
        this.root = root;
    }
}

var _NOTE = "A floating pane is Container.Pane.Floating: a container's corner, rule and ring, the overlay's shadow, "
    + "the pane's air on its head. Its place and its measure are its user's — drag the head, drag the corner — and "
    + "the desk keeps the stack: a press raises, the cross or Escape closes. The widget inside is held by the base's "
    + "contract, as a tab holds one. This note is longer than the pane on purpose, so the body has something to scroll. "
    + "Resize it and see the frame bound the text, not the other way round.";

class FloatingWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");
        // the keys, through the party: the page's steward, made by the chrome and handed in the params
        var kb = params && params.keyboard;
        if (!kb) throw new Error("[gallery] the page's keyboard steward is required: params.keyboard");

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-ui-floating";
        el.appendChild(kicker);
        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Floating panes";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "A desk, and the panes that float on it: open a few, drag them by the head, size them by the corner, "
            + "press one to raise it, close with the cross or Escape. Every mutation the desk reports is a line on the log.";
        el.appendChild(lede);

        var row = branch.createElement("row", "div");
        css.addClass(row, ga_buttons);
        var n = 0;
        function opener(label, spec) {
            var b = new ButtonBuilder().label(label).plain().onClick(function () { n++; self._desk.open(spec(n)); });
            row.appendChild(b.build(branch.createElement("open-" + label.replace(/\W+/g, "-"), b.tag)).el);
        }
        opener("Open a card", function (i) { return { title: "Card " + i, widget: CardWidget, params: { title: "A card in a pane", badge: "CARD", text: "The card keeps its own measure inside the pane's body; the pane bounds it." } }; });
        opener("Open a counter", function (i) { return { title: "Counter " + i, w: 240, h: 180, widget: CounterWidget, params: { start: i * 10 } }; });
        opener("Open a note", function (i) { return { title: "Note " + i + " — a long title to cut with an ellipsis", w: 300, h: 160, widget: NoteWidget, params: { text: _NOTE } }; });
        opener("One that cannot close", function (i) { return { title: "Pinned " + i, w: 220, h: 120, closable: false, widget: NoteWidget, params: { text: "No cross, and Escape does nothing here." } }; });
        el.appendChild(row);

        var host = branch.createElement("host", "div");
        css.addClass(host, ga_pane_host);
        el.appendChild(host);

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

        this._desk = new FloatLayer(branch.createBranch("desk"), { host: host, keyboard: kb, keyboardId: "floating/desk", onEvent: function (ev) {
            switch (ev.kind) {
                case "Opened":  say("Opened   " + ev.id + "  \"" + ev.title + "\"  at " + ev.x + "," + ev.y + "  " + ev.w + "×" + ev.h); break;
                case "Moved":   say("Moved    " + ev.id + "  to " + ev.x + "," + ev.y); break;
                case "Resized": say("Resized  " + ev.id + "  to " + ev.w + "×" + ev.h); break;
                case "Raised":  say("Raised   " + ev.id); break;
                case "Closed":  say("Closed   " + ev.id); break;
                default: say(ev.kind);
            }
        } });
        this._desk.open({ title: "Note 0", w: 300, h: 160, widget: NoteWidget, params: { text: _NOTE } });
        this._desk.open({ title: "Counter 0", w: 240, h: 180, widget: CounterWidget, params: { start: 0 } });

        this.root = el;
    }

    dispose() { this._desk.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new FloatingWidget(domOpsParty.createBranch("floatingPage"), params).root);
}
