package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.css.PlaygroundStyles;          // platformer game styles
import hue.captains.singapura.js.homing.demo.playground.AnimalsPlaygroundStyles;  // workspace chrome styles
import hue.captains.singapura.js.homing.libs.ToneJs;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.List;

/**
 * WorkspaceWidget port of the legacy {@link MovingAnimal} platformer.
 * Chrome stripped — the workspace tab supplies it; only the game body
 * (title, controls, playground, sky, world, lava, animal, score,
 * game-over overlay) survives, along with Tone.js audio + BGM +
 * landing-chord harmonisation, JumpPhysics, and PlatformEngine.
 *
 * <p>Returns a controller per the b.2d contract:
 * {@code { root, setActive(boolean) }}.</p>
 *
 * <ul>
 *   <li>{@code setActive(true)}: attaches keydown/keyup listeners on
 *       {@code document}, resumes BGM if audio is ready and the game
 *       isn't over.</li>
 *   <li>{@code setActive(false)}: detaches keyboard listeners, stops
 *       BGM, clears held-key state so re-activation doesn't see stuck
 *       input.</li>
 * </ul>
 *
 * <p>Multiple instances coexist cleanly — only the workspace-active
 * one consumes keys and plays music. Mouse on controls is gated by
 * the framework overlay; no widget code needed.</p>
 *
 * <p>Known V0 quirks (deferred to a later sweep):</p>
 * <ul>
 *   <li><b>No viewport resize.</b> The legacy attached
 *       {@code window.addEventListener('resize', …)}. Stripped here —
 *       removing the listener on dissolve needs extra plumbing. The
 *       widget measures once at construct time.</li>
 *   <li><b>Dynamic platforms use document.createElement.</b> Platforms
 *       come and go as the player advances; the framework's
 *       branch.createElement requires unique names per element which
 *       doesn't fit a dynamic collection. The platforms are appended to
 *       {@code world} (branch-owned) so DOM cascade on dissolve still
 *       cleans them up — no leak.</li>
 * </ul>
 *
 * @since RFC 0025 Ext1b b.2b — third WorkspaceWidget port
 *        (controller contract + active gating added b.2d).
 */
public final class MovingAnimalWidget extends WorkspaceWidget<WorkspaceWidget._None, MovingAnimalWidget> {

    public static final MovingAnimalWidget INSTANCE = new MovingAnimalWidget();

    private MovingAnimalWidget() {}

    private record construct() implements WorkspaceWidget._Construct<_None, MovingAnimalWidget> {}

    @Override protected _Construct<_None, MovingAnimalWidget> construct() { return new construct(); }
    @Override public Class<_None> paramsType() { return _None.class; }
    @Override public String title() { return "Moving Animal"; }
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.MULTI; }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        return List.of(
                new ModuleImports<>(List.of(
                        new AnimalCell.createAnimalCell(),
                        new AnimalCell.createAnimalSelector(),
                        new AnimalCell.createAnimalScope(),
                        new AnimalCell.setScopeAnimal()),
                        AnimalCell.INSTANCE),
                new ModuleImports<>(List.of(new JumpPhysics.createJumpPhysics()), JumpPhysics.INSTANCE),
                new ModuleImports<>(List.of(new PlatformEngine.createPlatformEngine()), PlatformEngine.INSTANCE),
                new ModuleImports<>(List.of(
                        new ToneJs.Synth(),
                        new ToneJs.MembraneSynth(),
                        new ToneJs.start()),
                        ToneJs.INSTANCE),
                new ModuleImports<>(List.of(new PlatformerBgm.getBgm()), PlatformerBgm.INSTANCE),
                new ModuleImports<>(List.of(
                        new PlaygroundStyles.pg_title(),
                        new PlaygroundStyles.pg_hint(),
                        new PlaygroundStyles.pg_controls(),
                        new PlaygroundStyles.pg_size_display(),
                        new PlaygroundStyles.pg_playground(),
                        new PlaygroundStyles.pg_sky(),
                        new PlaygroundStyles.pg_world(),
                        new PlaygroundStyles.pg_animal(),
                        new PlaygroundStyles.pg_platform(),
                        new PlaygroundStyles.pg_platform_active(),
                        new PlaygroundStyles.pg_lava(),
                        new PlaygroundStyles.pg_score(),
                        new PlaygroundStyles.pg_gameover(),
                        new PlaygroundStyles.pg_final_score()),
                        PlaygroundStyles.INSTANCE),
                new ModuleImports<>(List.of(new AnimalsPlaygroundStyles.pg_widget_root()),
                        AnimalsPlaygroundStyles.INSTANCE)
        );
    }

    @Override
    protected List<String> constructBodyJs() {
        // Ported from legacy MovingAnimal.js / buildGame(). Mechanical
        // transcription: document.createElement('foo') → branch.createElement
        // ('name', 'foo'); chrome stripped; resize listener dropped; root
        // wrapper added so the game body sits in the pane cleanly.
        return List.of(
                "    var ANIMAL_SIZE = 50, STEP = 5, SKY_H = 120, VIEWPORT_H = 500,",
                "        LAVA_H = 40, PLATFORM_H = 16, DEFAULT_GRAVITY = 0.6, JUMP_STRENGTH = 12;",
                "    var root = branch.createElement('root', 'div');",
                "    css.setClass(root, pg_widget_root);",
                "",
                "    // ─── Title + hint ──────────────────────────────────────────",
                "    var h1 = branch.createElement('title', 'h1');",
                "    css.setClass(h1, pg_title);",
                "    h1.textContent = 'Animal Platformer';",
                "    root.appendChild(h1);",
                "    var hint = branch.createElement('hint', 'p');",
                "    css.setClass(hint, pg_hint);",
                "    hint.textContent = 'Arrow keys to move. Space to jump. Don\\u2019t fall into the lava!';",
                "    root.appendChild(hint);",
                "",
                "    // ─── Audio (lazy-init on first gesture) ───────────────────",
                "    var audioReady = false;",
                "    var moveSynth  = new Synth({ oscillator: { type: 'square'   }, envelope: { attack: 0.005, decay: 0.06, sustain: 0,   release: 0.05 }, volume: -26 }).toDestination();",
                "    var jumpSynth  = new MembraneSynth({ pitchDecay: 0.08, octaves: 3, envelope: { attack: 0.005, decay: 0.15, sustain: 0,   release: 0.1 }, volume: -12 }).toDestination();",
                "    var deathSynth = new Synth({ oscillator: { type: 'triangle' }, envelope: { attack: 0.01,  decay: 0.2,  sustain: 0.03, release: 0.15 }, volume: -12 }).toDestination();",
                "    function ensureAudio() {",
                "        if (!audioReady) return start().then(function () { audioReady = true; startBgm(); });",
                "        return Promise.resolve();",
                "    }",
                "    var moveNoteIndex = 0;",
                "    var moveNotes = ['C5', 'D5', 'E5', 'D5'];",
                "    function playMoveSound() { moveSynth.triggerAttackRelease(moveNotes[moveNoteIndex], '32n'); moveNoteIndex = (moveNoteIndex + 1) % moveNotes.length; }",
                "    function playJumpSound() { jumpSynth.triggerAttackRelease('C3', '8n'); }",
                "    function playDeathJingle() {",
                "        var notes = ['B4', 'F5', 'F5', 'F5', 'E5', 'D5', 'C5', 'E4', 'E4', 'C4'];",
                "        var timing = [0, 120, 240, 400, 520, 640, 760, 920, 1040, 1200];",
                "        for (var di = 0; di < notes.length; di++) {",
                "            (function (note, t) { setTimeout(function () { deathSynth.triggerAttackRelease(note, '16n'); }, t); })(notes[di], timing[di]);",
                "        }",
                "    }",
                "",
                "    // ─── BGM loop ─────────────────────────────────────────────",
                "    var bgmSynth = new Synth({ oscillator: { type: 'triangle' }, envelope: { attack: 0.02, decay: 0.15, sustain: 0.1, release: 0.2 }, volume: -24 }).toDestination();",
                "    var bgmData = getBgm();",
                "    var bgmIndex = 0, bgmTimer = null, bgmPlaying = false;",
                "    function durationToMs(dur, bpm) {",
                "        var beat = 60000 / bpm;",
                "        switch (dur) { case '2n': return beat * 2; case '4n': return beat; case '8n': return beat / 2;",
                "                       case '16n': return beat / 4; case '32n': return beat / 8; default: return beat; }",
                "    }",
                "    function bgmStep() {",
                "        if (!bgmPlaying || gameOver || !root.isConnected) return;",
                "        var note = bgmData.notes[bgmIndex];",
                "        var dur  = bgmData.durations[bgmIndex];",
                "        if (note !== null) bgmSynth.triggerAttackRelease(note, dur);",
                "        bgmIndex = (bgmIndex + 1) % bgmData.notes.length;",
                "        bgmTimer = setTimeout(bgmStep, durationToMs(dur, bgmData.bpm));",
                "    }",
                "    function startBgm() { if (!bgmPlaying) { bgmPlaying = true; bgmIndex = 0; bgmStep(); } }",
                "    function stopBgm()  { bgmPlaying = false; if (bgmTimer !== null) { clearTimeout(bgmTimer); bgmTimer = null; } }",
                "",
                "    // ─── Landing chord (root + third + fifth, theme by platform Y) ─",
                "    var landSynthRoot  = new Synth({ oscillator: { type: 'triangle' }, envelope: { attack: 0.01, decay: 0.2, sustain: 0.03, release: 0.15 }, volume: -16 }).toDestination();",
                "    var landSynthThird = new Synth({ oscillator: { type: 'triangle' }, envelope: { attack: 0.01, decay: 0.2, sustain: 0.03, release: 0.15 }, volume: -18 }).toDestination();",
                "    var landSynthFifth = new Synth({ oscillator: { type: 'triangle' }, envelope: { attack: 0.01, decay: 0.2, sustain: 0.03, release: 0.15 }, volume: -18 }).toDestination();",
                "    var NOTE_NAMES = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B'];",
                "    var LAND_BASE_OCTAVE = 4, LAND_SEMITONE_RANGE = 24;",
                "    var lastLandedPlatY = null;",
                "    function noteFromIndex(i) { return NOTE_NAMES[i % 12] + (LAND_BASE_OCTAVE + Math.floor(i / 12)); }",
                "    function playLandingChord(platformY) {",
                "        var range = engine.getYRange();",
                "        var t = 1 - (platformY - range.minY) / (range.maxY - range.minY);",
                "        t = Math.max(0, Math.min(1, t));",
                "        var rootIndex = Math.round(t * LAND_SEMITONE_RANGE);",
                "        var thirdOffset = (lastLandedPlatY !== null && platformY > lastLandedPlatY) ? 3 : 4;",
                "        landSynthRoot.triggerAttackRelease(noteFromIndex(rootIndex), '8n');",
                "        landSynthThird.triggerAttackRelease(noteFromIndex(rootIndex + thirdOffset), '8n');",
                "        landSynthFifth.triggerAttackRelease(noteFromIndex(rootIndex + 7), '8n');",
                "        lastLandedPlatY = platformY;",
                "    }",
                "",
                "    // ─── Controls ──────────────────────────────────────────────",
                "    var controls = branch.createElement('controls', 'div');",
                "    css.setClass(controls, pg_controls);",
                "    var gravityLabel = branch.createElement('gravityLabel', 'label');",
                "    gravityLabel.textContent = 'Gravity ';",
                "    var gravitySlider = branch.createElement('gravitySlider', 'input');",
                "    gravitySlider.type = 'range'; gravitySlider.min = '1'; gravitySlider.max = '20'; gravitySlider.step = '1';",
                "    gravitySlider.value = String(DEFAULT_GRAVITY * 10);",
                "    gravityLabel.appendChild(gravitySlider);",
                "    var gravityDisplay = branch.createElement('gravityDisplay', 'span');",
                "    css.setClass(gravityDisplay, pg_size_display);",
                "    gravityDisplay.textContent = DEFAULT_GRAVITY.toFixed(1);",
                "    var bgmLabel = branch.createElement('bgmLabel', 'label');",
                "    bgmLabel.textContent = 'BGM ';",
                "    var bgmSlider = branch.createElement('bgmSlider', 'input');",
                "    bgmSlider.type = 'range'; bgmSlider.min = '0'; bgmSlider.max = '100'; bgmSlider.step = '1'; bgmSlider.value = '40';",
                "    bgmLabel.appendChild(bgmSlider);",
                "    var bgmDisplay = branch.createElement('bgmDisplay', 'span');",
                "    css.setClass(bgmDisplay, pg_size_display);",
                "    bgmDisplay.textContent = '40%';",
                "    var animalScope = createAnimalScope();   // per-instance — no cross-widget bleed",
                "    var selector = createAnimalSelector(animalScope);",
                "    controls.appendChild(gravityLabel); controls.appendChild(gravityDisplay);",
                "    controls.appendChild(bgmLabel);     controls.appendChild(bgmDisplay);",
                "    controls.appendChild(selector);",
                "    root.appendChild(controls);",
                "",
                "    // ─── World DOM (playground, sky, world, lava, animal, score, overlay) ─",
                "    var viewportW = root.clientWidth || 700;",
                "    var physics = createJumpPhysics(DEFAULT_GRAVITY, JUMP_STRENGTH);",
                "    var engine  = createPlatformEngine({ viewportW: viewportW, viewportH: VIEWPORT_H, animalW: ANIMAL_SIZE, animalH: ANIMAL_SIZE, platformH: PLATFORM_H, lavaH: LAVA_H, skyH: SKY_H });",
                "    var worldX = 100, worldY = 0, cameraX = 0, facingRight = true, gameOver = false, score = 0, animFrameId = 0;",
                "",
                "    var playground = branch.createElement('playground', 'div');",
                "    css.setClass(playground, pg_playground);",
                "    playground.style.height = VIEWPORT_H + 'px';",
                "    root.appendChild(playground);",
                "    var sky = branch.createElement('sky', 'div');",
                "    css.setClass(sky, pg_sky); sky.style.height = SKY_H + 'px';",
                "    playground.appendChild(sky);",
                "    var world = branch.createElement('world', 'div');",
                "    css.setClass(world, pg_world);",
                "    playground.appendChild(world);",
                "    var lava = branch.createElement('lava', 'div');",
                "    css.setClass(lava, pg_lava); lava.style.height = LAVA_H + 'px';",
                "    playground.appendChild(lava);",
                "    var animal = createAnimalCell(css.className(pg_animal), animalScope);",
                "    animal.style.width = ANIMAL_SIZE + 'px'; animal.style.height = ANIMAL_SIZE + 'px';",
                "    world.appendChild(animal);",
                "    var scoreEl = branch.createElement('score', 'div');",
                "    css.setClass(scoreEl, pg_score);",
                "    scoreEl.textContent = '0';",
                "    playground.appendChild(scoreEl);",
                "",
                "    var overlay = branch.createElement('overlay', 'div');",
                "    css.setClass(overlay, pg_gameover);",
                "    overlay.style.display = 'none';",
                "    var overTitle = branch.createElement('overTitle', 'h2');",
                "    overTitle.textContent = 'GAME OVER';",
                "    overlay.appendChild(overTitle);",
                "    var finalScore = branch.createElement('finalScore', 'p');",
                "    css.setClass(finalScore, pg_final_score);",
                "    overlay.appendChild(finalScore);",
                "    var restartBtn = branch.createElement('restartBtn', 'button');",
                "    restartBtn.textContent = 'Play Again';",
                "    overlay.appendChild(restartBtn);",
                "    playground.appendChild(overlay);",
                "",
                "    // ─── Dynamic platforms — document.createElement (collection escape hatch).",
                "    //     world is branch-owned; DOM cascade on dissolve cleans these up.",
                "    var platformEls = new Map();",
                "    var activePlatform = null;",
                "    function syncPlatformDom() {",
                "        var currentPlats = engine.getPlatforms();",
                "        var currentSet = new Set(currentPlats);",
                "        platformEls.forEach(function (el, plat) { if (!currentSet.has(plat)) { el.remove(); platformEls.delete(plat); } });",
                "        for (var i = 0; i < currentPlats.length; i++) {",
                "            var p = currentPlats[i];",
                "            var el = platformEls.get(p);",
                "            if (!el) {",
                "                el = document.createElement('div');",
                "                css.setClass(el, pg_platform);",
                "                el.style.height = PLATFORM_H + 'px';",
                "                world.appendChild(el);",
                "                platformEls.set(p, el);",
                "            }",
                "            el.style.left = p.x + 'px';",
                "            el.style.top  = p.y + 'px';",
                "            el.style.width = p.w + 'px';",
                "            css.toggleClass(el, pg_platform_active, p === activePlatform);",
                "        }",
                "    }",
                "",
                "    function initGame() {",
                "        worldX = 100; worldY = 0; cameraX = 0; facingRight = true;",
                "        gameOver = false; score = 0; scoreEl.textContent = '0';",
                "        overlay.style.display = 'none';",
                "        platformEls.forEach(function (el) { el.remove(); }); platformEls.clear();",
                "        lastLandedPlatY = null; activePlatform = null;",
                "        engine.init(worldX, worldY);",
                "        var hit = engine.findGround(worldX, worldY, 1);",
                "        if (hit !== null) { worldY = hit.groundY; activePlatform = hit.platform; }",
                "        engine.generateAhead(worldX + viewportW * 2);",
                "        updateFlip(); render();",
                "    }",
                "    function updateFlip() { animal.style.transform = facingRight ? 'scaleX(1)' : 'scaleX(-1)'; }",
                "    function render() {",
                "        world.style.transform = 'translateX(' + (-cameraX) + 'px)';",
                "        animal.style.left = worldX + 'px';",
                "        animal.style.top  = worldY + 'px';",
                "        syncPlatformDom();",
                "    }",
                "    function die() {",
                "        gameOver = true; stopBgm();",
                "        ensureAudio().then(function () { playDeathJingle(); });",
                "        finalScore.textContent = 'Score: ' + score;",
                "        overlay.style.display = '';",
                "    }",
                "",
                "    // ─── Input (document-scoped; gated on/off by setActive below).",
                "    var keys = {}, moveHeld = false;",
                "    var keyDown = function (e) {",
                "        if (gameOver) return;",
                "        keys[e.key] = true;",
                "        if (e.key === ' ') {",
                "            e.preventDefault();",
                "            ensureAudio().then(function () { if (!physics.isJumping()) playJumpSound(); physics.jump(); });",
                "        }",
                "        if (e.key === 'ArrowLeft' || e.key === 'ArrowRight') {",
                "            e.preventDefault();",
                "            if (!moveHeld) { moveHeld = true; ensureAudio(); }",
                "        }",
                "    };",
                "    var keyUp = function (e) {",
                "        keys[e.key] = false;",
                "        if (!keys['ArrowLeft'] && !keys['ArrowRight']) moveHeld = false;",
                "    };",
                "    gravitySlider.addEventListener('input', function (e) { var g = parseInt(e.target.value) / 10; physics.setGravity(g); gravityDisplay.textContent = g.toFixed(1); });",
                "    bgmSlider.addEventListener('input', function (e) {",
                "        var pct = parseInt(e.target.value); bgmDisplay.textContent = pct + '%';",
                "        if (pct === 0) bgmSynth.volume.value = -Infinity;",
                "        else           bgmSynth.volume.value = -40 + (pct / 100) * 32;",
                "    });",
                "    restartBtn.addEventListener('click', function () {",
                "        initGame(); if (audioReady) startBgm();",
                "        animFrameId = requestAnimationFrame(frame);",
                "    });",
                "",
                "    // ─── Game loop ─────────────────────────────────────────────",
                "    var moveFrameCount = 0;",
                "    function frame() {",
                "        if (!root.isConnected) return;",
                "        if (gameOver) return;",
                "        var moved = false;",
                "        if (keys['ArrowLeft'])  { worldX -= STEP; if ( facingRight) { facingRight = false; updateFlip(); } moved = true; }",
                "        if (keys['ArrowRight']) { worldX += STEP; if (!facingRight) { facingRight = true;  updateFlip(); } moved = true; }",
                "        var wasInAir = physics.isJumping();",
                "        var hit = engine.findGround(worldX, worldY, physics.getVy());",
                "        if (hit !== null) { worldY = physics.update(worldY, hit.groundY); activePlatform = hit.platform; }",
                "        else              { physics.fall(); worldY = physics.update(worldY, VIEWPORT_H + 100); activePlatform = null; }",
                "        if (wasInAir && !physics.isJumping() && audioReady && hit !== null) playLandingChord(hit.platform.y);",
                "        if (moved && audioReady && !physics.isJumping()) { moveFrameCount++; if (moveFrameCount % 4 === 0) playMoveSound(); } else { moveFrameCount = 0; }",
                "        if (engine.isInLava(worldY)) { die(); render(); return; }",
                "        cameraX = engine.updateCamera(cameraX, worldX);",
                "        var dist = Math.max(0, Math.floor(worldX / 10));",
                "        if (dist > score) { score = dist; scoreEl.textContent = String(score); }",
                "        engine.generateAhead(cameraX + viewportW * 2);",
                "        engine.pruneBehind(cameraX);",
                "        render();",
                "        animFrameId = requestAnimationFrame(frame);",
                "    }",
                "    initGame();",
                "    animFrameId = requestAnimationFrame(frame);",
                "    // setActive(true/false) attaches/detaches keyboard listeners and",
                "    // pauses/resumes BGM. Mouse is gated by the framework overlay.",
                "    // Multiple MovingAnimal instances coexist cleanly — only the active",
                "    // one consumes keys and plays music.",
                "    // RFC 0028 cycle 4 — join AnimalsParty if the workspace provided one.",
                "    var __actorId = null;",
                "    if (workspaceCtx && workspaceCtx.animalsParty) {",
                "        __actorId = 'animals/moving-' + Math.random().toString(36).slice(2, 8);",
                "        workspaceCtx.animalsParty.joinActor({",
                "            id: __actorId,",
                "            parentSecretary: 'animals',",
                "            reactors: {",
                "                AnimalChanged: function (msg) { setScopeAnimal(animalScope, msg.animal); }",
                "            }",
                "        });",
                "        // Late-join sync — see SpinningAnimalsWidget for the full comment.",
                "        workspaceCtx.animalsParty.tellFrom(__actorId, { kind: 'CurrentAnimalRequested' });",
                "    }",
                "    return {",
                "        root: root,",
                "        setActive: function (active) {",
                "            if (active) {",
                "                document.addEventListener('keydown', keyDown);",
                "                document.addEventListener('keyup',   keyUp);",
                "                if (audioReady && !bgmPlaying && !gameOver) startBgm();",
                "            } else {",
                "                document.removeEventListener('keydown', keyDown);",
                "                document.removeEventListener('keyup',   keyUp);",
                "                stopBgm();",
                "                // Forget held keys so re-activation doesn't see stuck input.",
                "                keys = {}; moveHeld = false;",
                "            }",
                "        },",
                "        partyDeregister: function () {",
                "            if (__actorId && workspaceCtx && workspaceCtx.animalsParty) {",
                "                try { workspaceCtx.animalsParty.leave(__actorId); } catch (e) {}",
                "            }",
                "        }",
                "    };"
        );
    }
}
