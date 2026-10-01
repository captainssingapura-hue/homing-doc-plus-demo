// =============================================================================
// PlatformerReplay — the animal platformer, watched: the run the player tells
// the platformer party, re-simulated here, step for step. It makes nothing of
// its own — no terrain, no keys, no sound — and a workspace may hold any number
// of them, each the same run.
//
// THE SAME EVENTS AT THE SAME STEPS MAKE THE SAME WORLD (PlatformerGame): the
// moves, jumps and pulls applied as they come, each platform placed as the
// player's terrain made it, one step for each Tick.
//
// JOINING MID-RUN, it asks for the world as it stands; the party asks the
// player, and the player hands it the world alone — a Snapshot, the World
// whole, its platforms with it. Until one comes it has no world, and what the
// stream says it passes over: all of it is in the snapshot already, for the
// party hands on what it is told in the order it is told. A run begun, joined
// or begun again is a snapshot to every member, and it takes that whole too.
// With no one playing, it waits, and says so.
//
// A self-contained widget (the Workspace & Widgets doctrines): made with the
// container its page lends it and its params, and nothing else; its DomOps and
// focus parties its own, offered as roots for its host to graft; the parties it
// needs declared by type, and joined after it is made. Its animal is what the
// animal choice party says.
//
//   new PlatformerReplay(container, params)   params: none
//   replay.root   replay.roots { dom, focus }   replay.focus
//   (its kind declares, in Java: many, and the types it joins - platformer, animal-choice)
//   replay.join(given)   given: { [type name]: party }; a second join without a leave is refused
//   replay.leave()
//   replay.watching()   whether it has a world - a snapshot has come
//   replay.state()      the run, as it has re-simulated it
//   replay.activate()   replay.keyDown(ev)   Escape gives the keys back
//   replay.dispose()
// =============================================================================

const _replayOwner = Object.freeze({ toString: () => "platformerReplay" });
var _replays = 0;

class PlatformerReplay {
    constructor(container, params) {
        if (!container || typeof container.appendChild !== "function") throw new Error("[PlatformerReplay] a container is required: the one its page lends it");
        var name = "platformerReplay-" + (++_replays), d;
        this._dom = d = domOpsParties.mobile(name);
        d.activate(_replayOwner);
        var root = d.createElement("root", "div");
        css.addClass(root, wg_fill);
        css.addClass(root, pf_root);
        root.setAttribute("role", "region");
        root.setAttribute("aria-label", "Platformer replay");
        var head = PlatformerReplay._el(d, "head", "div", pf_head, root);
        PlatformerReplay._el(d, "title", "h2", pf_title, head).textContent = "Animal platformer - replay";
        this._score = PlatformerReplay._el(d, "score", "span", pf_score, head);
        this._note = PlatformerReplay._el(d, "note", "p", pf_hint, root);
        this._stage = new PlatformerStage(d.createBranch("stage"), {});
        root.appendChild(this._stage.root);
        container.appendChild(root);
        this.root = root;
        this._focusParty = focusParties.mobile(name);
        this.focus = this._focusParty.root.join("platformerReplay", this);
        this._off = Keys.claimOn(root, this.focus);
        this.roots = Object.freeze({ dom: d, focus: this._focusParty });
        this._game = new PlatformerGame({ random: PlatformerReplay._never });
        this._watching = false;
        this._run = null;
        this._choice = null;
        this._joined = false;
        this._stage.animal(Animals.FIRST);
        this._said();
    }

    join(given) {
        if (this._joined) throw new Error("[PlatformerReplay] joined already: leave first");
        this._joined = true;
        var run = given && given[PLATFORMER.name], choice = given && given[ANIMAL_CHOICE.name], self = this;
        if (run) {
            var heard = function (m) { self._heard(m); }, ears = {};
            ["MoveStarted", "MoveStopped", "Jumped", "GravityChanged", "PlatformGenerated", "Tick", "Snapshot"].forEach(function (k) { ears[k] = heard; });
            this._run = run.join("platformerReplay", ears);
            this._run.tell({ kind: "WorldRequested" });
        }
        if (choice) {
            this._choice = choice.join("platformerReplay", { Chosen: function (m) { if (Animals.byId(m.animal)) self._stage.animal(m.animal); } });
            this._choice.tell({ kind: "CurrentRequested" });
        }
        this._said();
    }

    leave() {
        if (this._run) { this._run.leave(); this._run = null; }
        if (this._choice) { this._choice.leave(); this._choice = null; }
        this._joined = false;
    }

    watching() { return this._watching; }

    state() { return this._game.state(); }

    activate() { Keys.claim(this.focus); }

    /** It plays nothing: Escape gives the keys back, and the rest go on. */
    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        return false;
    }

    /** What the party says of the run: a snapshot taken whole; after one, the stream applied - a step for each Tick. */
    _heard(m) {
        if (m.kind === "Snapshot") {
            this._game.restore(m.world);
            this._watching = true;
            this._said();
            this._drawn();
            return;
        }
        if (!this._watching) return;   // told before the world came: in the world already
        if (m.kind === "Tick") {
            if (this._game.step().alive) this._game.prune();
            this._drawn();
        } else if (m.kind === "PlatformGenerated") {
            this._game.place(m);
        } else {
            this._game.apply(m);
        }
    }

    _drawn() {
        var s = this._game.state();
        this._stage.draw(s);
        this._stage.over(s.over ? s.score : null);
        this._score.textContent = String(s.score);
    }

    /** What it is doing, in words: waiting for a run, or watching one. */
    _said() {
        this._note.textContent = this._watching ? "Watching the run as the player tells it."
            : this._run ? "Waiting for a run - no one is playing yet." : "Not joined to a run.";
    }

    /** A watcher makes no terrain: chance is the player's alone. */
    static _never() { throw new Error("[PlatformerReplay] a watcher never makes a platform - it is handed each"); }

    static _el(d, name, tag, cls, parent) {
        var el = d.createElement(name, tag);
        css.addClass(el, cls);
        parent.appendChild(el);
        return el;
    }

    dispose() {
        this.leave();
        this._stage.dispose();
        if (this._off) { this._off(); this._off = null; }
        this._focusParty.dissolve();
        this._dom.dissolve();
    }
}
