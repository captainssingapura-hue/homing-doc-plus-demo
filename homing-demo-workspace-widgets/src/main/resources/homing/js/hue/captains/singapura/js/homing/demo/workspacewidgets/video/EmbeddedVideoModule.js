// =============================================================================
// EmbeddedVideo — a short playlist: five cooks, one dish. A self-contained
// widget (the Workspace & Widgets doctrines): made with the container it is
// lent and its params, and nothing else; its DomOps and focus parties its own,
// offered as roots for its host to graft.
//
// WHICH TAKE IS ON THE STAGE is said in four places at once — the strip marks
// it, the counter numbers it, the line under the heading names it, the player
// shows it — and one method moves all four, so they never disagree.
//
// SWITCHING DESTROYS THE PLAYER rather than commanding it. The player is a
// control plane this widget does not own: it is told, and it says what it is
// doing, never more. So a switch dissolves the branch that owns the frame —
// the element goes, and the player with it — and mints a fresh one on a branch
// of its own: the outgoing video stops by construction, and a src reassigned
// would have walked the browser's history. No autoplay: a widget never starts
// audio you did not ask for, so a take put on the stage is a paused player.
//
// IT PAUSES WHEN IT IS NOT SEEN — its tab hidden behind another, its float
// closed over — by watching its own root, since a widget respects its
// container by itself. And it PLAYS AGAIN WHEN IT IS SEEN AGAIN, if it was
// the one that paused it: the player says its state (YouTube's listening
// channel), so a video playing when it went behind plays when it comes back,
// and one you paused stays paused. A player whose state was never said is
// paused all the same, and never played again: no audio you did not ask for.
//
// THE KEYS. The strip is a tablist with MANUAL activation: arrows move the
// focus among the takes, Enter or Space puts one on the stage — one player per
// choice, not one per keystroke. The takes are native buttons, the native
// world's; the widget is a logical member of the keyboard's party. Holding the
// keys with nothing natively focused, an arrow takes you into the strip, Enter
// or Space plays the take the strip is on, and Escape gives the keys back.
//
//   new EmbeddedVideo(container, params)   params: none
//   video.root   video.roots { dom, focus }   video.focus
//   video.shown() → the index of the take on the stage
//   video.show(i)   take i on the stage: a fresh player, paused
//   video.activate()   video.keyDown(ev)   the keyboard's member
//   video.dispose()
// =============================================================================

const _videoOwner = Object.freeze({ toString: () => "embeddedVideo" });
var _videos = 0;

/** The player's origin: the frame's host, and the only one a command is posted to. youtube-nocookie sets no cookie until play is pressed. */
var _VIDEO_ORIGIN = "https://www.youtube-nocookie.com";

var _VIDEO_DISH = "宫保鸡丁 · Kung Pao Chicken";

/** Five takes on one dish: the same recipe by different hands. The label tells apart the two from one channel. */
var _VIDEO_TAKES = Object.freeze([
    Object.freeze({ id: "c6WRS8xSA-4", label: "老饭骨 · 传承版", note: "老饭骨 — the state-banquet master's version, a dish with centuries behind it" }),
    Object.freeze({ id: "yqwE6zO-hUA", label: "老饭骨 · 宗师版", note: "老饭骨 — the same masters again, on the Sichuan grandmaster's line" }),
    Object.freeze({ id: "wEkVkT6IU9M", label: "美食作家王刚", note: "美食作家王刚 — a head chef's Sichuan method, start to finish" }),
    Object.freeze({ id: "KFZX7VRN_oY", label: "特厨隋卞", note: "特厨隋卞 — the take that sold out a Beijing dining room" }),
    Object.freeze({ id: "-AZ87qyHQ88", label: "大师的菜", note: "大师的菜 — where the name 宫保 comes from, and what makes it authentic" })
]);

class EmbeddedVideo {
    constructor(container, params) {
        if (!container || typeof container.appendChild !== "function") throw new Error("[EmbeddedVideo] a container is required: the one its page lends it");
        var name = this._name = "embeddedVideo-" + (++_videos), self = this, d;
        this._dom = d = domOpsParties.mobile(name);
        d.activate(_videoOwner);
        var root = d.createElement("root", "div");
        css.addClass(root, wg_fill);
        css.addClass(root, vd_root);
        root.setAttribute("role", "region");
        root.setAttribute("aria-label", "Video: " + _VIDEO_DISH);
        var head = EmbeddedVideo._el(d, "head", "div", vd_head, root);
        EmbeddedVideo._el(d, "title", "h2", vd_title, head).textContent = _VIDEO_DISH;
        this._count = EmbeddedVideo._el(d, "count", "span", vd_count, head);
        this._note = EmbeddedVideo._el(d, "note", "p", vd_note, root);
        this._stage = EmbeddedVideo._el(d, "stage", "div", vd_stage, root);
        // A tablist of the takes. No aria-controls: several may be tiled, and an id would meet its twin;
        // the stage is labelled with the take instead.
        var rail = EmbeddedVideo._el(d, "rail", "div", vd_rail, root);
        rail.setAttribute("role", "tablist");
        rail.setAttribute("aria-label", "Takes");
        this._tabs = _VIDEO_TAKES.map(function (t, i) { return self._tab(rail, t, i); });
        rail.addEventListener("keydown", function (ev) { if (self._railKey(ev.key)) ev.preventDefault(); });
        container.appendChild(root);
        this.root = root;
        this._focusParty = focusParties.mobile(name);
        this.focus = this._focusParty.root.join("video", this);
        this._off = Keys.claimOn(root, this.focus);
        this.roots = Object.freeze({ dom: d, focus: this._focusParty });
        this._shown = -1;
        this._focused = 0;
        this._players = 0;
        this._player = null;
        this._frame = null;
        this._state = null;      // the player's state as it last said it: 1 playing, 2 paused, 3 buffering...; null, never said
        this._resume = false;    // paused by the widget while it was playing: played again when seen
        this._heard = function (ev) { self._said(ev); };
        window.addEventListener("message", this._heard);
        this._seen = typeof IntersectionObserver === "function"
            ? new IntersectionObserver(function (entries) { entries.forEach(function (e) { if (e.isIntersecting) self._seenAgain(); else self._hidden(); }); }) : null;
        if (this._seen) this._seen.observe(root);
        this.show(0);
    }

    shown() { return this._shown; }

    /** The switch: everything that says WHICH take moves here, together. */
    show(i) {
        if (i === this._shown) { this._paint(); return; }
        var take = _VIDEO_TAKES[i];
        this._shown = i;
        this._swap(take);
        this._note.textContent = take.note;
        this._count.textContent = (i + 1) + " / " + _VIDEO_TAKES.length;
        this._stage.setAttribute("aria-label", take.label);
        this._paint();
    }

    activate() { Keys.claim(this.focus); }

    /** Holding the keys, nothing natively focused: an arrow goes into the strip, Enter or Space plays, Escape gives the keys back. */
    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        if (ev.key === "Enter" || ev.key === " ") { this.show(this._focused); return true; }
        return this._railKey(ev.key);
    }

    /** A key on the strip: arrows, Home and End browse, Enter and Space choose. Escape and Tab are left to go on. */
    _railKey(key) {
        var n = _VIDEO_TAKES.length;
        if (key === "ArrowRight" || key === "ArrowDown") this._move((this._focused + 1) % n);
        else if (key === "ArrowLeft" || key === "ArrowUp") this._move((this._focused + n - 1) % n);
        else if (key === "Home") this._move(0);
        else if (key === "End") this._move(n - 1);
        else if (key === "Enter" || key === " ") this.show(this._focused);
        else return false;
        return true;
    }

    _move(i) { this._focused = i; this._paint(); this._tabs[i].focus(); }

    /** Roving tabindex: one stop for the strip. The selection follows the stage, the stop the focus - apart while you browse. */
    _paint() {
        for (var i = 0; i < this._tabs.length; i++) {
            this._tabs[i].setAttribute("aria-selected", i === this._shown ? "true" : "false");
            this._tabs[i].setAttribute("tabindex", i === this._focused ? "0" : "-1");
        }
    }

    _tab(rail, take, i) {
        var self = this, b = this._dom.createElement("take" + i, "button");
        b.type = "button";
        b.textContent = take.label;
        b.title = take.note;
        b.setAttribute("role", "tab");
        css.addClass(b, vd_take);
        // The pointer's way to choose; the keys' is the strip's own, so one choice comes from one place.
        b.addEventListener("click", function () { self._focused = i; self.show(i); });
        rail.appendChild(b);
        return b;
    }

    /** Dissolve, then mint: the old frame leaves the page, its player with it, and a fresh one comes on a branch of its own. */
    _swap(take) {
        if (this._player) { this._player.dissolve(); this._player = null; this._frame = null; }
        this._state = null;
        this._resume = false;
        var self = this, p = this._player = this._dom.createBranch("player" + (++this._players));
        p.activate(_videoOwner);
        var frame = this._frame = p.createElement("frame", "iframe");
        // loaded - and loaded again, when the frame is moved and the page reloads it - the player is asked to say its state
        frame.addEventListener("load", function () { if (self._frame === frame) self._post({ event: "listening", id: self._name, channel: "widget" }); });
        css.addClass(frame, vd_frame);
        frame.src = _VIDEO_ORIGIN + "/embed/" + take.id + "?enablejsapi=1&rel=0";
        frame.title = take.note + " — YouTube video player";
        frame.allow = "accelerometer; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share; fullscreen";
        frame.referrerPolicy = "strict-origin-when-cross-origin";
        frame.allowFullscreen = true;
        this._stage.appendChild(frame);
    }

    /** Not seen: paused - and, when it was playing by its own word, marked to play again when it is seen. */
    _hidden() {
        if (this._state === 1 || this._state === 3) this._resume = true;
        this._post({ event: "command", func: "pauseVideo", args: "" });
    }

    /** Seen again: played, if this widget paused it while it played; else left as it is. */
    _seenAgain() {
        if (!this._resume) return;
        this._resume = false;
        this._post({ event: "command", func: "playVideo", args: "" });
    }

    /** What the player says, from its own origin and its own frame alone: the state it is in. */
    _said(ev) {
        if (!this._frame || ev.origin !== _VIDEO_ORIGIN || ev.source !== this._frame.contentWindow) return;
        var m;
        try { m = typeof ev.data === "string" ? JSON.parse(ev.data) : ev.data; } catch (e) { return; }
        if (!m) return;
        if (m.event === "onStateChange" && typeof m.info === "number") this._state = m.info;
        else if (m.info && typeof m.info.playerState === "number") this._state = m.info.playerState;
    }

    /** Told: a message posted to the player's own origin. Not loaded yet, or gone - nothing to tell. */
    _post(message) {
        try {
            if (this._frame && this._frame.contentWindow) this._frame.contentWindow.postMessage(JSON.stringify(message), _VIDEO_ORIGIN);
        } catch (e) {}
    }

    static _el(d, name, tag, cls, parent) {
        var el = d.createElement(name, tag);
        css.addClass(el, cls);
        parent.appendChild(el);
        return el;
    }

    dispose() {
        if (this._seen) { this._seen.disconnect(); this._seen = null; }
        if (this._heard) { window.removeEventListener("message", this._heard); this._heard = null; }
        if (this._off) { this._off(); this._off = null; }
        this._focusParty.dissolve();
        this._dom.dissolve();
    }
}
