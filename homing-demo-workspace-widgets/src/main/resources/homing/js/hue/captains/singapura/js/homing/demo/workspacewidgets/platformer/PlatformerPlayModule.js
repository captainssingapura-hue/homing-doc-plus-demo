// =============================================================================
// PlatformerPlay — the animal platformer, played: run along the platforms, jump
// the gaps, and don't fall into the lava; the further you get, the higher the
// score. A workspace holds one (its kind is single): it is the AUTHORITY of the
// run, and tells it — every action, every platform the terrain makes, and a
// Tick every step of the world, sixty a second whatever the display's frames —
// to the platformer party, in the order it happened, so a
// watcher applying them re-simulates the run exactly (PlatformerGame). A
// watcher that joins mid-run is handed the world as it stands, alone - a
// Snapshot, records inside records; a run joined or begun again is one to
// every member. Which animal runs is what the animal choice party says; until
// it says, the first.
//
// A self-contained widget (the Workspace & Widgets doctrines): made with the
// container its page lends it and its params, and nothing else; its DomOps and
// focus parties its own, offered as roots for its host to graft; the parties
// it needs declared by type, and joined after it is made. Not joined, it plays
// alone.
//
// THE KEYS come through the keyboard's party, never the document: a press on
// the widget claims them; holding them, ← → move - held until let go - Space
// jumps, Escape gives them back. Taken away, every move held is let go, so
// nothing runs on unheld. The sliders are native controls of their own.
//
// THE SOUND starts only on a person's key or press, and the tune plays only
// while the widget holds the keys and the run is not over (PlatformerSound).
//
//   new PlatformerPlay(container, params)   params: none
//   play.root   play.roots { dom, focus }   play.focus
//   (its kind declares, in Java: single, and the types it joins - platformer, animal-choice)
//   play.join(given)   given: { [type name]: party }; a second join without a leave is refused
//   play.leave()
//   play.state()      the run, as PlatformerGame says it
//   play.again()      a fresh run
//   play.activate()   play.keyDown(ev)   play.keyUp(ev)   play.granted()   play.taken()
//   play.dispose()
// =============================================================================

const _platformerOwner = Object.freeze({ toString: () => "platformerPlay" });
var _platformers = 0;

class PlatformerPlay {

    /** The world's steps a second - each a Tick told - whatever the display's frames. */
    static STEPS = 60;

    /** The most steps a frame makes up for: after a pause, the run goes on, not a burst of it. */
    static MOST_OWED = 5;

    constructor(container, params) {
        if (!container || typeof container.appendChild !== "function") throw new Error("[PlatformerPlay] a container is required: the one its page lends it");
        var name = "platformerPlay-" + (++_platformers), self = this, d;
        this._dom = d = domOpsParties.mobile(name);
        d.activate(_platformerOwner);
        var root = d.createElement("root", "div");
        css.addClass(root, wg_fill);
        css.addClass(root, pf_root);
        root.setAttribute("role", "region");
        root.setAttribute("aria-label", "Animal platformer");
        var head = PlatformerPlay._el(d, "head", "div", pf_head, root);
        PlatformerPlay._el(d, "title", "h2", pf_title, head).textContent = "Animal platformer";
        this._score = PlatformerPlay._el(d, "score", "span", pf_score, head);
        PlatformerPlay._el(d, "hint", "p", pf_hint, root).textContent = "Press the stage, then ← → to run and Space to jump. Keep out of the lava!";
        var controls = PlatformerPlay._el(d, "controls", "div", pf_controls, root);
        this._gravity = new SliderBuilder().label("Gravity").range(0.2, 2, 0.1).value(PlatformerGame.GRAVITY)
            .format(function (v) { return v.toFixed(1); })
            .onInput(function (v) { self._act({ kind: "GravityChanged", value: Math.round(v * 10) / 10 }); })
            .build(d.createBranch("gravity"));
        this._music = new SliderBuilder().label("Music").range(0, 100, 5).value(40)
            .format(function (v) { return Math.round(v) + "%"; })
            .onInput(function (v) { self._sound.volume(v); })
            .build(d.createBranch("music"));
        controls.appendChild(this._gravity.root);
        controls.appendChild(this._music.root);
        this._stage = new PlatformerStage(d.createBranch("stage"), { onAgain: function () { self.again(); self.activate(); } });
        this._stage.root.addEventListener("pointerdown", function () { self._sound.ready(); });
        root.appendChild(this._stage.root);
        container.appendChild(root);
        this.root = root;
        this._focusParty = focusParties.mobile(name);
        this.focus = this._focusParty.root.join("platformer", this);
        this._off = Keys.claimOn(root, this.focus);
        this.roots = Object.freeze({ dom: d, focus: this._focusParty });
        this._game = new PlatformerGame();
        this._sound = new PlatformerSound();
        this._run = null;
        this._choice = null;
        this._joined = false;
        this._holding = false;
        this._frame = 0;
        this._last = null;
        this._owed = 0;
        this._next = function (now) { self._tick(now); };
        this._stage.animal(Animals.FIRST);
        this.again();
    }

    join(given) {
        if (this._joined) throw new Error("[PlatformerPlay] joined already: leave first");
        this._joined = true;
        var run = given && given[PLATFORMER.name], choice = given && given[ANIMAL_CHOICE.name], self = this;
        if (run) {
            // a watcher that joins mid-run is handed the world alone; the ones there already, the run it joins
            this._run = run.join("platformerPlay", { WorldWanted: function (m) { self._snapshot(m.asker); } });
            this._snapshot("");
        }
        if (choice) {
            this._choice = choice.join("platformerPlay", { Chosen: function (m) { if (Animals.byId(m.animal)) self._stage.animal(m.animal); } });
            this._choice.tell({ kind: "CurrentRequested" });
        }
    }

    leave() {
        if (this._run) { this._run.leave(); this._run = null; }
        if (this._choice) { this._choice.leave(); this._choice = null; }
        this._joined = false;
    }

    state() { return this._game.state(); }

    /** A fresh run: the world made again and told whole - its terrain is new - the steps going. */
    again() {
        if (this._frame) cancelAnimationFrame(this._frame);
        this._game.reset();
        this._snapshot("");
        this._stage.over(null);
        this._drawn();
        this._last = null;
        this._owed = 0;
        this._frame = requestAnimationFrame(this._next);
        this._tune();
    }

    activate() { Keys.claim(this.focus); }

    /** Holding the keys: ← → move, held until let go; Space jumps; Escape gives the keys back. */
    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        if (ev.key === " ") {
            this._sound.ready();
            if (this._game.over || this._game.inAir()) return true;
            this._act({ kind: "Jumped" });
            this._sound.jump();
            return true;
        }
        var dir = PlatformerPlay._dir(ev.key);
        if (!dir) return false;
        this._sound.ready();
        if (this._game.held().indexOf(dir) < 0) this._act({ kind: "MoveStarted", dir: dir });
        return true;
    }

    keyUp(ev) {
        var dir = PlatformerPlay._dir(ev.key);
        if (!dir || this._game.held().indexOf(dir) < 0) return false;
        this._act({ kind: "MoveStopped", dir: dir });
        return true;
    }

    granted() { this._holding = true; this._tune(); }

    /** The keys gone elsewhere: every move held let go - nothing runs on unheld - and the tune stopped. */
    taken() {
        var self = this;
        this._holding = false;
        this._game.held().forEach(function (dir) { self._act({ kind: "MoveStopped", dir: dir }); });
        this._tune();
    }

    /**
     * A frame of the page: the world stepped at its own rate - STEPS a second, whatever the display's -
     * as many steps as the time since owes, a few at most after a pause; then drawn once.
     */
    _tick(now) {
        var every = 1000 / PlatformerPlay.STEPS, alive = true;
        this._frame = 0;
        if (this._last === null) this._last = now - every;
        this._owed = Math.min(this._owed + (now - this._last), every * PlatformerPlay.MOST_OWED);
        this._last = now;
        while (alive && this._owed >= every) { this._owed -= every; alive = this._step(); }
        this._drawn();
        if (alive) { this._frame = requestAnimationFrame(this._next); return; }
        this._stage.over(this._game.score);
        this._sound.fall();
        this._tune();
    }

    /** A step: the world stepped, its sounds, the terrain ahead made and told, and the Tick told last - on the step the run ends too. */
    _step() {
        var self = this, r = this._game.step();
        this._sound.step(r.moved);
        if (r.landed) this._sound.land(r.landed.y, this._game.heights());
        if (r.alive) {
            this._game.ahead().forEach(function (p) { self._platform(p); });
            this._game.prune();
        }
        this._tell({ kind: "Tick" });
        return r.alive;
    }

    /** An action: applied to the world, and told. */
    _act(a) {
        this._game.apply(a);
        this._tell(a);
    }

    _platform(p) { this._tell({ kind: "PlatformGenerated", x: p.x, y: p.y, w: p.w, vehicle: p.vehicle }); }

    /** The world as it stands, told whole: to the member that asked - or, to "", to every member. */
    _snapshot(to) { this._tell({ kind: "Snapshot", to: to, world: this._game.world() }); }

    _tell(message) { if (this._run) this._run.tell(message); }

    _drawn() {
        this._stage.draw(this._game.state());
        this._score.textContent = String(this._game.score);
    }

    /** The tune: on while the keys are held and the run goes on. */
    _tune() { this._sound.tune(this._holding && !this._game.over); }

    static _dir(key) { return key === "ArrowLeft" ? "left" : key === "ArrowRight" ? "right" : null; }

    static _el(d, name, tag, cls, parent) {
        var el = d.createElement(name, tag);
        css.addClass(el, cls);
        parent.appendChild(el);
        return el;
    }

    dispose() {
        if (this._frame) { cancelAnimationFrame(this._frame); this._frame = 0; }
        this.leave();
        this._sound.dispose();
        this._stage.dispose();
        this._gravity.dispose();
        this._music.dispose();
        if (this._off) { this._off(); this._off = null; }
        this._focusParty.dissolve();
        this._dom.dissolve();
    }
}
