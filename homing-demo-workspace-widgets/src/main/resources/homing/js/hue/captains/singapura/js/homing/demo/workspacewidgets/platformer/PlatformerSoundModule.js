// =============================================================================
// PlatformerSound — the platformer's sound, on Tone.js (served from the
// classpath, never a CDN): a tick for every few steps along the ground, a
// thump for a jump, a chord for a landing - pitched by how high the platform
// is, minor when it is a step down - a jingle for falling in, and a tune
// looping under it all.
//
// NOTHING SOUNDS UNTIL A PERSON HAS ASKED FOR IT: the browser's rule, and a
// widget's own - it never starts audio you did not. ready() starts the audio
// from a key or a press; until it has, every sound is nothing. The tune plays
// only while it is on (tune(true)) - the game holds the keys, and the run is
// not over - and stops the moment it is not.
//
//   new PlatformerSound()
//   sound.ready() → Promise, the audio started - call it from a person's key or press
//   sound.started()             whether it has
//   sound.step(moving)   sound.jump()   sound.land(y, heights)   sound.fall()
//   sound.tune(on)              the loop, on or off
//   sound.volume(pct)           the tune's, 0 … 100; 0 is silence
//   sound.dispose()             everything stopped, the synths let go
//
// No DOM.
// =============================================================================

var _PLATFORMER_TUNE = Object.freeze({
    bpm: 120,
    notes: Object.freeze(["C4", "E4", "G4", "E4", "F4", "A4", "G4", "E4", "D4", "F4", "A4", "G4", "E4", "D4", "C4", null,
                          "C4", "G4", "F4", "E4", "D4", "E4", "F4", "D4", "C4", "E4", "G4", "A4", "G4", "F4", "E4", null]),
    beats: Object.freeze([0.5, 0.5, 1, 0.5, 0.5, 1, 0.5, 0.5, 0.5, 0.5, 1, 0.5, 0.5, 0.5, 1, 1,
                          0.5, 0.5, 1, 0.5, 0.5, 0.5, 0.5, 1, 0.5, 0.5, 1, 0.5, 0.5, 0.5, 1, 1])
});
var _PLATFORMER_NAMES = Object.freeze(["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"]);
var _PLATFORMER_STEPS = Object.freeze(["C5", "D5", "E5", "D5"]);
var _PLATFORMER_JINGLE = Object.freeze([["B4", 0], ["F5", 120], ["F5", 240], ["F5", 400], ["E5", 520], ["D5", 640], ["C5", 760], ["E4", 920], ["E4", 1040], ["C4", 1200]]);

class PlatformerSound {
    constructor() {
        this._synths = null;
        this._starting = null;
        this._on = false;
        this._timer = null;
        this._note = 0;
        this._steps = 0;
        this._lastY = null;
        this._volume = 40;
    }

    ready() {
        var self = this;
        if (!this._starting) this._starting = start().then(function () { self._make(); if (self._on) self._play(); });
        return this._starting;
    }

    started() { return this._synths !== null; }

    /** A frame along the ground, or not: every fourth moving frame ticks, the next of the four notes. */
    step(moving) {
        if (!moving) { this._steps = 0; return; }
        if (!this._synths) return;
        this._steps = (this._steps + 1) % 4;
        if (this._steps !== 0) return;
        this._synths.move.triggerAttackRelease(_PLATFORMER_STEPS[this._note], "32n");
        this._note = (this._note + 1) % _PLATFORMER_STEPS.length;
    }

    jump() { if (this._synths) this._synths.jump.triggerAttackRelease("C3", "8n"); }

    /** A landing: a chord on the platform's height - higher, higher - its third minor when it is a step down. */
    land(y, heights) {
        if (!this._synths) return;
        var t = Math.max(0, Math.min(1, 1 - (y - heights.min) / (heights.max - heights.min)));
        var root = Math.round(t * 24), third = this._lastY !== null && y > this._lastY ? 3 : 4;
        this._synths.root.triggerAttackRelease(PlatformerSound._note(root), "8n");
        this._synths.third.triggerAttackRelease(PlatformerSound._note(root + third), "8n");
        this._synths.fifth.triggerAttackRelease(PlatformerSound._note(root + 7), "8n");
        this._lastY = y;
    }

    fall() {
        if (!this._synths) return;
        var s = this._synths.death;
        _PLATFORMER_JINGLE.forEach(function (n) { setTimeout(function () { s.triggerAttackRelease(n[0], "16n"); }, n[1]); });
        this._lastY = null;
    }

    tune(on) {
        if (on === this._on) return;
        this._on = on;
        if (on && this._synths) this._play();
        if (!on && this._timer !== null) { clearTimeout(this._timer); this._timer = null; }
    }

    volume(pct) {
        this._volume = pct;
        if (this._synths) this._synths.tune.volume.value = PlatformerSound._db(pct);
    }

    dispose() {
        this.tune(false);
        if (this._synths) Object.keys(this._synths).forEach(function (k) { this._synths[k].dispose(); }, this);
        this._synths = null;
    }

    _make() {
        if (this._synths) return;
        function tone(type, attack, decay, sustain, release, volume) {
            return new Synth({ oscillator: { type: type }, envelope: { attack: attack, decay: decay, sustain: sustain, release: release }, volume: volume }).toDestination();
        }
        this._synths = {
            move: tone("square", 0.005, 0.06, 0, 0.05, -26),
            jump: new MembraneSynth({ pitchDecay: 0.08, octaves: 3, envelope: { attack: 0.005, decay: 0.15, sustain: 0, release: 0.1 }, volume: -12 }).toDestination(),
            death: tone("triangle", 0.01, 0.2, 0.03, 0.15, -12),
            tune: tone("triangle", 0.02, 0.15, 0.1, 0.2, -24),
            root: tone("triangle", 0.01, 0.2, 0.03, 0.15, -16),
            third: tone("triangle", 0.01, 0.2, 0.03, 0.15, -18),
            fifth: tone("triangle", 0.01, 0.2, 0.03, 0.15, -18)
        };
        this._synths.tune.volume.value = PlatformerSound._db(this._volume);
    }

    /** The tune from its top, a note at a time, while it is on. */
    _play() {
        var self = this, i = 0, beat = 60000 / _PLATFORMER_TUNE.bpm;
        if (this._timer !== null) clearTimeout(this._timer);
        function next() {
            if (!self._on || !self._synths) { self._timer = null; return; }
            var note = _PLATFORMER_TUNE.notes[i], beats = _PLATFORMER_TUNE.beats[i];
            if (note !== null) self._synths.tune.triggerAttackRelease(note, beats >= 1 ? "4n" : "8n");
            i = (i + 1) % _PLATFORMER_TUNE.notes.length;
            self._timer = setTimeout(next, beats * beat);
        }
        next();
    }

    static _note(i) { return _PLATFORMER_NAMES[i % 12] + (4 + Math.floor(i / 12)); }

    static _db(pct) { return pct <= 0 ? -Infinity : -40 + (pct / 100) * 32; }
}
