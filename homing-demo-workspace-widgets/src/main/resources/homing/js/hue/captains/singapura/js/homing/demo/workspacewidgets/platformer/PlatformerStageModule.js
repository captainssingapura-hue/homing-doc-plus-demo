// =============================================================================
// PlatformerStage — the platformer, drawn: a stage of the game's own size
// (PlatformerGame.WIDTH by HEIGHT, in its units), scaled to the box it is
// lent, whichever side runs out first; the sky, the world sliding left under
// the camera, the platforms, the lava, the animal, and a card when the run is
// over. A branch component: its caller makes the sub-branch and hands it in.
//
// IT DRAWS WHAT IT IS HANDED, and decides nothing: every place, size and the
// scale is a custom property a typed class reads (PlatformerStyles) — never a
// style of its own. The platforms are a fixed set of places, each shown with
// the platform it holds now, or hidden: the world makes and lets go platforms
// all the time, and a page's elements are the branch's, made once.
//
//   new PlatformerStage(branch, { onAgain })   onAgain: the card's Play again, pressed
//   stage.root              the view: the stage centred and scaled in it
//   stage.draw(state)       PlatformerGame's state, drawn
//   stage.animal(id)        its picture (Animals)
//   stage.over(score)       the card, with the score; over(null) takes it away
//   stage.dispose()
// =============================================================================

const _stageOwner = Object.freeze({ toString: () => "platformerStage" });

class PlatformerStage {

    /** How many platforms the stage can show at once: more than the world ever keeps. */
    static PLACES = 24;

    constructor(branch, opts) {
        if (!branch) throw new Error("[PlatformerStage] the sub-branch its caller made for it is required");
        var o = opts || {}, self = this, b = this._b = branch;
        b.activate(_stageOwner);
        var view = PlatformerStage._el(b, "view", "div", pf_view, null);
        var stage = this._stage = PlatformerStage._el(b, "stage", "div", pf_stage, view);
        stage.setAttribute("role", "img");
        stage.setAttribute("aria-label", "The platformer's stage");
        PlatformerStage._el(b, "sky", "div", pf_sky, stage);
        var world = this._world = PlatformerStage._el(b, "world", "div", pf_world, stage);
        this._places = [];
        this._held = [];
        for (var i = 0; i < PlatformerStage.PLACES; i++) {
            var p = PlatformerStage._el(b, "platform" + i, "div", pf_platform, world);
            css.addClass(p, pf_gone);
            this._places.push(p);
            this._held.push(null);
        }
        this._animal = PlatformerStage._el(b, "animal", "div", pf_animal, world);
        PlatformerStage._el(b, "lava", "div", pf_lava, stage);
        var card = this._card = PlatformerStage._el(b, "over", "div", pf_over, stage);
        PlatformerStage._el(b, "overTitle", "h3", pf_over_title, card).textContent = "Game over";
        this._final = PlatformerStage._el(b, "overScore", "p", pf_hint, card);
        var again = PlatformerStage._el(b, "again", "button", el_button, card);
        css.addClass(again, el_button_primary);
        again.type = "button";
        again.textContent = "Play again";
        again.addEventListener("click", function () { again.blur(); if (typeof o.onAgain === "function") o.onAgain(); });
        css.addClass(card, pf_gone);
        this.root = view;
        this._fits = typeof ResizeObserver === "function" ? new ResizeObserver(function () { self._fit(); }) : null;
        if (this._fits) this._fits.observe(view);
    }

    draw(s) {
        this._world.style.setProperty("--pf-camera", String(s.cameraX));
        this._animal.style.setProperty("--pf-x", String(s.x));
        this._animal.style.setProperty("--pf-y", String(s.y));
        css.toggleClass(this._animal, pf_left, !s.facingRight);
        for (var i = 0; i < this._places.length; i++) {
            var p = i < s.platforms.length ? s.platforms[i] : null, el = this._places[i];
            if (p !== this._held[i]) {
                this._held[i] = p;
                css.toggleClass(el, pf_gone, p === null);
                if (p !== null) {
                    el.style.setProperty("--pf-x", String(p.x));
                    el.style.setProperty("--pf-y", String(p.y));
                    el.style.setProperty("--pf-w", String(p.w));
                }
            }
            css.toggleClass(el, pf_platform_under, p !== null && p === s.active);
        }
    }

    animal(id) { this._animal.style.setProperty("--pf-art", Animals.art(id)); }

    over(score) {
        if (score !== null) this._final.textContent = "Score: " + score;
        css.toggleClass(this._card, pf_gone, score === null);
    }

    /** The stage scaled to the view, whichever side runs out first. */
    _fit() {
        var w = this.root.clientWidth, h = this.root.clientHeight;
        var s = Math.min(w / PlatformerGame.WIDTH, h / PlatformerGame.HEIGHT);
        this._stage.style.setProperty("--pf-scale", s > 0 ? s.toFixed(4) : "1");
    }

    static _el(b, name, tag, cls, parent) {
        var el = b.createElement(name, tag);
        css.addClass(el, cls);
        if (parent) parent.appendChild(el);
        return el;
    }

    dispose() {
        if (this._fits) { this._fits.disconnect(); this._fits = null; }
    }
}
