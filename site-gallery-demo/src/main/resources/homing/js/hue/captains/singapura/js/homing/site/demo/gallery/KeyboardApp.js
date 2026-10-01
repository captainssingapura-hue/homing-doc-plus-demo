// =============================================================================
// KeyboardApp — who has the keys. Five members of the page's keyboard party
// on one page, and a strip that shows the holder live: a group of sliders,
// a card with an action, a strip of tabs, a dialog, and a platformer that
// holds keys down — the arrows run the animal while pressed, Space jumps —
// so keyup travels through the party as keydown does. Each claims by the
// convention (a press, or the focus arriving) or by call (Play; the dialog
// on open), and the last claim holds: the one before is told, and the strip
// moves. One button shows the bug the design names: a modal open, and the
// platformer claiming by call from behind it — the party is blind, so it
// gets the keys, and the dialog, no longer the holder, stops hearing Escape.
// =============================================================================

const _owner = Object.freeze({ toString: () => "keyboardPage" });
var _MEMBERS = ["keyboard/sliders", "keyboard/card", "keyboard/strip", "keyboard/dialog", "keyboard/platformer"];
var _STEP = 3, _JUMP = 9, _GRAVITY = 0.6;

/** The platformer: an animal on a stage; arrows held run it, Space jumps; a press on the stage claims, Play claims by call. */
class Platformer {
    constructor(branch, host, kb, id, say) {
        var self = this;
        branch.activate(_owner);
        this._kb = kb; this._id = id; this._say = say;
        var stage = branch.createElement("stage", "div");
        css.addClass(stage, ga_stage);
        stage.setAttribute("tabindex", "0");
        stage.setAttribute("role", "application");
        stage.setAttribute("aria-label", "Platformer");
        var sprite = branch.createElement("sprite", "div");
        css.addClass(sprite, ga_sprite);
        sprite.textContent = "🦊";
        stage.appendChild(sprite);
        host.appendChild(stage);
        this.root = stage; this._sprite = sprite;
        this._x = 24; this._y = 0; this._vy = 0; this._dir = 0; this._face = 1; this._frame = null;
        this._draw();
        kb.join(id, {
            keyDown: function (ev) { return self.keyDown(ev); },
            keyUp: function (ev) { return self.keyUp(ev); },
            taken: function () { self._dir = 0; }   // evicted mid-run: the animal stops, since its keyup will not come
        });
        this._offKeys = Keys.claimOn(stage, kb, id);
    }
    keyDown(ev) {
        if (ev.key === "ArrowLeft") { this._dir = -1; this._face = -1; this._run(); return true; }
        if (ev.key === "ArrowRight") { this._dir = 1; this._face = 1; this._run(); return true; }
        if (ev.key === " " || ev.key === "ArrowUp") { if (this._y === 0) { this._vy = _JUMP; this._run(); } return true; }
        return false;
    }
    keyUp(ev) {
        if ((ev.key === "ArrowLeft" && this._dir < 0) || (ev.key === "ArrowRight" && this._dir > 0)) { this._dir = 0; return true; }
        return false;
    }
    /** Claim by call: Play, or the button that shows the bug. */
    play() { this._kb.claim(this._id); }
    _run() {
        var self = this;
        if (this._frame !== null) return;
        function tick() {
            var w = self.root.clientWidth || 560;
            self._x = Math.max(0, Math.min(w - 44, self._x + self._dir * _STEP));
            if (self._y > 0 || self._vy > 0) { self._y = Math.max(0, self._y + self._vy); self._vy -= _GRAVITY; if (self._y === 0) self._vy = 0; }
            self._draw();
            self._frame = (self._dir !== 0 || self._y > 0) ? requestAnimationFrame(tick) : null;
        }
        this._frame = requestAnimationFrame(tick);
    }
    _draw() {
        this._sprite.style.setProperty("--ga-x", this._x + "px");
        this._sprite.style.setProperty("--ga-y", this._y + "px");
        this._sprite.style.setProperty("--ga-face", String(this._face));
    }
    dispose() { if (this._frame !== null) cancelAnimationFrame(this._frame); this._offKeys(); this._kb.leave(this._id); }
}

class KeyboardWidget {
    constructor(branch, params) {
        var self = this;
        branch.activate(_owner);
        var el = branch.createElement("root", "div");
        // the keys, through the party: the page's steward, made by the chrome and handed in the params
        var kb = params && params.keyboard;
        if (!kb) throw new Error("[gallery] the page's keyboard steward is required: params.keyboard");

        var kicker = branch.createElement("kicker", "div");
        css.addClass(kicker, ga_kicker);
        kicker.textContent = "homing-component-base";
        el.appendChild(kicker);
        var title = branch.createElement("title", "h1");
        css.addClass(title, ga_title);
        title.textContent = "Who has the keys";
        el.appendChild(title);
        var lede = branch.createElement("lede", "p");
        css.addClass(lede, ga_lede);
        lede.textContent = "One keyboard party per page, one holder or none. Every component that takes keys is a member; a press in it, "
            + "or the focus arriving, claims — and a claim evicts whoever held. The strip below shows the holder live, and the "
            + "log says who took the keys from whom. The platformer holds keys down: keyup travels through the party as keydown does.";
        el.appendChild(lede);

        // the strip: one chip per member, the holder lit
        var holders = branch.createElement("holders", "div");
        css.addClass(holders, ga_holders);
        var chips = {};
        _MEMBERS.forEach(function (id) {
            var c = branch.createElement("holder-" + id.replace(/[^A-Za-z0-9_-]/g, "_"), "span");
            css.addClass(c, ga_holder);
            c.textContent = id;
            holders.appendChild(c);
            chips[id] = c;
        });
        var none = branch.createElement("holder-none", "span");
        css.addClass(none, ga_holder, ga_holder_on);
        none.textContent = "no one";
        holders.appendChild(none);
        el.appendChild(holders);

        var log = branch.createElement("log", "div");
        css.addClass(log, ga_log);
        log.setAttribute("aria-live", "polite");
        var lines = 0;
        function say(line) {
            lines++;
            log.textContent += (lines > 1 ? "\n" : "") + lines + "  " + line;
            log.scrollTop = log.scrollHeight;
        }
        function light(id) {
            _MEMBERS.forEach(function (m) { css.removeClass(chips[m], ga_holder_on); });
            css.removeClass(none, ga_holder_on);
            css.addClass(id && chips[id] ? chips[id] : none, ga_holder_on);
        }
        this._offKb = kb.on(function (ev) {
            if (ev.kind === "Granted") { light(ev.id); say("Granted   " + ev.id); }
            else if (ev.kind === "Taken") { say("Taken     " + ev.id + "  by " + ev.by); }
            else { light(null); say("Released  " + ev.id); }
        });

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

        section("sliders", "a group of sliders — one member for the three; Tab walks them, the arrows move the current one", function (box, own) {
            var g = new SliderGroupBuilder().title("Sliders").keyboard(kb, "keyboard/sliders").build(own.createBranch("group"));
            ["size", "aspect", "extent"].forEach(function (axis) { g.add(axis, new SliderBuilder().label(axis).axis().icon(axis).labelWidth("5em").format(function (v) { return v.toFixed(1); })); });
            self._group = g;
            box.appendChild(g.root);
        });
        section("card", "a card with an action — Tab to it, or click it; Enter or Space is the action", function (box, own) {
            self._card = new CardBuilder().title("A card with an action").badge("PRESS").text("Enter or Space, through the party.").aspect(0.6)
                .onClick(function () { say("the card's action"); }).keyboard(kb, "keyboard/card").build(own.createBranch("card"));
            box.appendChild(self._card.root);
        });
        section("strip", "a strip of tabs — the Tab key walks the chips (the browser's), Enter or Space selects (the party's)", function (box, own) {
            var strip = new TabStrip(own.createBranch("strip"), { keyboard: kb, keyboardId: "keyboard/strip" });
            var order = [];
            ["Inbox", "Drafts", "Sent"].forEach(function (name) {
                var chip = strip.chip({ id: name.toLowerCase(), title: name }, {
                    onSelect: function () { strip.select(order, chip); say("Selected  " + name); },
                    onClose: function () { order.splice(order.indexOf(chip), 1); strip.remove(chip); strip.arrange(order); }
                });
                order.push(chip);
            });
            strip.arrange(order);
            strip.select(order, order[0]);
            self._strip = strip;
            box.appendChild(strip.el);
        });
        section("platformer", "the platformer — press the stage, or Play; hold an arrow to run, Space to jump; keyup stops the run", function (box, own) {
            var stageHost = own.createElement("stageHost", "div");
            box.appendChild(stageHost);
            self._game = new Platformer(own.createBranch("game"), stageHost, kb, "keyboard/platformer", say);
            var row = own.createElement("row", "div");
            css.addClass(row, ga_buttons);
            row.appendChild(new Button(own.createElement("play", Button.TAG), { label: "Play", onClick: function () { self._game.play(); } }).el);
            row.appendChild(new Button(own.createElement("bug", Button.TAG), { label: "A modal, then the platformer claims by call — the bug, shown", kind: "plain", onClick: function () {
                openDialog("A modal is up");
                setTimeout(function () { self._game.play(); say("the platformer claimed by call from behind the modal: the party is blind, so it holds — and the dialog no longer hears Escape; close it by its cross"); }, 600);
            } }).el);
            box.appendChild(row);
        });
        var dialogs = 0;
        function openDialog(t) {
            new Dialog(branch.createBranch("dialog" + (++dialogs)), {
                title: t, keyboard: kb, keyboardId: "keyboard/dialog", size: { w: 420, h: 200 },
                content: function (b, body) {
                    var p = b.createElement("text", "p");
                    css.addClass(p, ga_lede);
                    p.textContent = "The dialog claimed the keys on open, by call; whoever held was told. Escape closes it and gives the keys back.";
                    body.appendChild(p);
                    return {};
                },
                actions: [{ id: "ok", label: "OK", primary: true, onClick: function (h) { h.close(); } }]
            });
        }
        section("dialog", "a dialog — claims by call on open, evicting the holder; on close the keys go back to who held before", function (box, own) {
            var row = own.createElement("row", "div");
            css.addClass(row, ga_buttons);
            row.appendChild(new Button(own.createElement("open", Button.TAG), { label: "Open a modal", onClick: function () { openDialog("A modal dialog"); } }).el);
            box.appendChild(row);
        });
        say("five members; no one holds the keys yet");
        this.root = el;
    }

    dispose() { this._offKb(); this._game.dispose(); this._strip.dispose(); this._card.dispose(); this._group.dispose(); }
}

function appMain(el, params) {
    el.appendChild(new KeyboardWidget(domOpsParty.createBranch("keyboardPage"), params).root);
}
