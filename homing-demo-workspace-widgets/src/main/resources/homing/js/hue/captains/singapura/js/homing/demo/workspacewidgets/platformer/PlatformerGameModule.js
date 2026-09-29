// =============================================================================
// PlatformerGame — the platformer's world and its rules, apart from any page:
// where the animal is and which way it faces, the camera following it, the
// score (how far it has come), the terrain (PlatformEngine) and the jump
// (JumpPhysics); a run over when it falls into the lava.
//
// THE SAME ACTIONS AT THE SAME FRAMES MAKE THE SAME WORLD. It steps a frame at
// a time - no clock - so a watcher applying the player's actions frame for
// frame re-simulates the run exactly. Chance makes only the terrain, and only
// the player makes it (ahead); a watcher is handed each platform (place).
//
//   new PlatformerGame({ random? })       random: the terrain's, Math.random unless said
//   PlatformerGame.WIDTH, HEIGHT, ANIMAL, SKY, LAVA, PLATFORM, GRAVITY    the world's measures
//   game.reset(ahead?) → [platform]   a fresh run: the animal on the start, the terrain ahead
//                                made - the new platforms; reset(false), a watcher's: none made
//   game.apply(action)           { kind: "MoveStarted" | "MoveStopped", dir: "left" | "right" },
//                                { kind: "Jumped" }, { kind: "GravityChanged", value }
//   game.held() → ["left", …]    the directions held      game.inAir()   in the air, from a jump or an edge
//   game.step() → { alive, moved, landed }   a frame: moved along the ground, landed on a platform
//                                (or null), alive unless it fell in - then the run is over
//   game.ahead() → [platform]    the terrain ahead of the camera, made: the new platforms
//   game.place(platform)         one made elsewhere - a watcher's
//   game.prune()                 the terrain far behind, let go
//   game.state() → { x, y, cameraX, facingRight, score, over, tick, gravity, active, platforms }
//   game.heights() → { min, max }   where the platforms may be: a landing's pitch reads it
//
// Pure: no DOM.
// =============================================================================

class PlatformerGame {

    static WIDTH = 700;
    static HEIGHT = 500;
    static ANIMAL = 50;
    static SKY = 120;
    static LAVA = 40;
    static PLATFORM = 16;
    static GRAVITY = 0.6;
    static JUMP = 12;
    static STEP = 5;
    static START_X = 100;

    constructor(o) {
        var opts = o || {};
        this._engine = new PlatformEngine({ width: PlatformerGame.WIDTH, height: PlatformerGame.HEIGHT, animal: PlatformerGame.ANIMAL,
                                            platformHeight: PlatformerGame.PLATFORM, lava: PlatformerGame.LAVA, random: opts.random });
        this._gravity = PlatformerGame.GRAVITY;
        this._physics = new JumpPhysics(this._gravity, PlatformerGame.JUMP);
        this._keys = { left: false, right: false };
        this._begin();
    }

    reset(ahead) {
        this._begin();
        this._physics = new JumpPhysics(this._gravity, PlatformerGame.JUMP);
        this._engine.start(this.x, this.y);
        var hit = this._engine.ground(this.x, this.y, 1);
        if (hit !== null) { this.y = hit.groundY; this.active = hit.platform; }
        return ahead === false ? [] : this.ahead();
    }

    apply(a) {
        if (a.kind === "MoveStarted" || a.kind === "MoveStopped") {
            if (a.dir === "left" || a.dir === "right") this._keys[a.dir] = a.kind === "MoveStarted";
        } else if (a.kind === "Jumped") {
            this._physics.jump();
        } else if (a.kind === "GravityChanged") {
            this._gravity = a.value;
            this._physics.gravity(a.value);
        }
    }

    held() { return ["left", "right"].filter(function (d) { return this._keys[d]; }, this); }

    inAir() { return this._physics.inAir(); }

    step() {
        if (this.over) return { alive: false, moved: false, landed: null };
        this.tick++;
        var moved = false;
        if (this._keys.left) { this.x -= PlatformerGame.STEP; this.facingRight = false; moved = true; }
        if (this._keys.right) { this.x += PlatformerGame.STEP; this.facingRight = true; moved = true; }
        var wasInAir = this._physics.inAir();
        var hit = this._engine.ground(this.x, this.y, this._physics.vy());
        if (hit !== null) { this.y = this._physics.step(this.y, hit.groundY); this.active = hit.platform; }
        else { this._physics.fall(); this.y = this._physics.step(this.y, PlatformerGame.HEIGHT + 100); this.active = null; }
        var landed = wasInAir && !this._physics.inAir() && hit !== null ? hit.platform : null;
        if (this._engine.inLava(this.y)) { this.over = true; return { alive: false, moved: moved, landed: landed }; }
        this.cameraX = this._engine.camera(this.cameraX, this.x);
        this.score = Math.max(this.score, Math.floor(this.x / 10));
        return { alive: true, moved: moved && !this._physics.inAir(), landed: landed };
    }

    ahead() { return this._engine.ahead(this.cameraX + PlatformerGame.WIDTH * 2); }

    place(p) { this._engine.place(p); }

    prune() { this._engine.behind(this.cameraX); }

    state() {
        return { x: this.x, y: this.y, cameraX: this.cameraX, facingRight: this.facingRight, score: this.score, over: this.over,
                 tick: this.tick, gravity: this._gravity, active: this.active, platforms: this._engine.platforms() };
    }

    heights() { return this._engine.heights(); }

    _begin() {
        this.x = PlatformerGame.START_X;
        this.y = 0;
        this.cameraX = 0;
        this.facingRight = true;
        this.score = 0;
        this.over = false;
        this.tick = 0;
        this.active = null;
        this._keys = { left: false, right: false };
    }
}
