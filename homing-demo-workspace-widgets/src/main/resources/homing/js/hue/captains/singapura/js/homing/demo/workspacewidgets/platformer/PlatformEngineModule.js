// =============================================================================
// PlatformEngine — the platformer's terrain: platforms ahead of the camera,
// each a gap past the last and a step up or down from it, kept between the
// sky and the lava; let go once far behind. Chance makes the terrain — and
// only here: a watcher is handed each platform the player's engine made
// (place), and never makes one, so both see one world.
//
//   new PlatformEngine({ width, height, animal, platformHeight, lava, random? })
//     random     () → [0, 1): Math.random unless said - a test's is its own
//   engine.start(x, y)            one platform under a start at x - the same every run
//   engine.ahead(untilX) → [platform]   made, until the last reaches untilX: the new ones
//   engine.place(platform)        one made elsewhere, taken as it is
//   engine.restore([platform])    the terrain as a world has it, whole - copies
//   engine.behind(cameraX)        the ones far behind the camera, let go
//   engine.platforms()            [{ x, y, w, vehicle }], left to right: the engine's own list
//   engine.ground(x, y, vy) → { groundY, platform } | null   what an animal at x, y, falling by vy, lands on
//   engine.camera(cameraX, x) → the camera, following an animal at x - never back
//   engine.inLava(y)   engine.heights() → { min, max }: where platforms may be
//
// Pure: no DOM.
// =============================================================================

class PlatformEngine {

    static MIN_GAP = 60;
    static MAX_GAP = 140;
    /** Each look's width: a platform's footprint says which it is. */
    static WIDTHS = Object.freeze({ 1: Object.freeze({ min: 140, max: 175 }), 2: Object.freeze({ min: 105, max: 135 }), 3: Object.freeze({ min: 75, max: 100 }) });
    static UP = -60;
    static DOWN = 50;
    static KEPT_BEHIND = 400;

    constructor(o) {
        this._w = o.width;
        this._h = o.height;
        this._animal = o.animal;
        this._platformH = o.platformHeight;
        this._lava = o.lava;
        this._random = typeof o.random === "function" ? o.random : Math.random;
        var ground = this._h - this._lava;
        this._minY = Math.floor(ground * 0.55);
        this._maxY = ground - this._platformH - 20;
        this._lead = this._w / 3;
        this._list = [];
        this._right = 0;
        this._lastY = 0;
    }

    start(x, y) {
        this._list.length = 0;
        this._take({ x: x - 40, y: Math.floor((this._minY + this._maxY) / 2), w: 200, vehicle: 1 });
    }

    ahead(untilX) {
        var made = [];
        while (this._right < untilX) {
            var gap = PlatformEngine.MIN_GAP + this._random() * (PlatformEngine.MAX_GAP - PlatformEngine.MIN_GAP);
            var dy = PlatformEngine.UP + this._random() * (PlatformEngine.DOWN - PlatformEngine.UP);
            var y = Math.max(this._minY, Math.min(this._maxY, this._lastY + dy));
            var vehicle = 1 + Math.floor(this._random() * 3), band = PlatformEngine.WIDTHS[vehicle];
            var w = band.min + this._random() * (band.max - band.min);
            made.push(this._take({ x: this._right + gap, y: y, w: w, vehicle: vehicle }));
        }
        return made;
    }

    place(p) { this._take({ x: p.x, y: p.y, w: p.w, vehicle: p.vehicle }); }

    /** The terrain as a world has it, whole - its own copies, left to right: the next is made past the last. */
    restore(list) {
        this._list = list.map(function (p) { return { x: p.x, y: p.y, w: p.w, vehicle: p.vehicle }; });
        var last = this._list.length ? this._list[this._list.length - 1] : null;
        this._right = last ? last.x + last.w : 0;
        this._lastY = last ? last.y : 0;
    }

    behind(cameraX) {
        var cutoff = cameraX - PlatformEngine.KEPT_BEHIND;
        while (this._list.length > 0 && this._list[0].x + this._list[0].w < cutoff) this._list.shift();
    }

    platforms() { return this._list; }

    ground(x, y, vy) {
        var bottom = y + this._animal, right = x + this._animal, best = null;
        for (var i = 0; i < this._list.length; i++) {
            var p = this._list[i];
            if (right <= p.x || x >= p.x + p.w) continue;
            if (vy >= 0 && bottom <= p.y + 8 && bottom >= p.y - this._animal) {
                var groundY = p.y - this._animal;
                if (best === null || groundY < best.groundY) best = { groundY: groundY, platform: p };
            }
        }
        return best;
    }

    camera(cameraX, x) { return Math.max(cameraX, x - this._lead); }

    inLava(y) { return y + this._animal >= this._h - this._lava; }

    heights() { return { min: this._minY, max: this._maxY }; }

    _take(p) {
        this._list.push(p);
        this._right = p.x + p.w;
        this._lastY = p.y;
        return p;
    }
}
