// =============================================================================
// JumpPhysics — the platformer's vertical: a jump's push up, gravity's pull
// down, frame by frame - counted in frames, never in time, so the same pushes
// at the same frames fall the same way.
//
//   new JumpPhysics(gravity, strength)
//   physics.jump()          up by strength - unless in the air already
//   physics.fall()          off an edge: in the air, from rest
//   physics.gravity(g)      the pull per frame, from now on
//   physics.step(y, groundY) → y, a frame on: pulled, and stopped at the ground
//   physics.inAir()   physics.vy()
//
// Pure: no DOM.
// =============================================================================

class JumpPhysics {
    constructor(gravity, strength) {
        this._g = gravity;
        this._strength = strength;
        this._vy = 0;
        this._air = false;
    }

    jump() {
        if (this._air) return;
        this._vy = -this._strength;
        this._air = true;
    }

    fall() {
        if (this._air) return;
        this._vy = 0;
        this._air = true;
    }

    gravity(g) { this._g = g; }

    step(y, groundY) {
        if (!this._air) return y;
        this._vy += this._g;
        y += this._vy;
        if (y >= groundY) { y = groundY; this._vy = 0; this._air = false; }
        return y;
    }

    inAir() { return this._air; }

    vy() { return this._vy; }
}
