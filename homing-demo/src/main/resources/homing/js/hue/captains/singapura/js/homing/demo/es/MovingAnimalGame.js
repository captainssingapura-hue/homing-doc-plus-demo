// =============================================================================
// MovingAnimalGame — the reusable, object-oriented backbone of the Animal
// Platformer. Drives two host widgets for the RFC 0028 broadcast feature:
//
//   - "play"   : a live, keyboard-driven instance (MovingAnimalPlayWidget,
//                SINGLETON). It is the authority: it emits a single ordered
//                stream of semantic events through the host (config.onEvent).
//   - "replay" : a passive spectator (MovingAnimalReplayWidget, MULTI). It
//                never reads the keyboard and never generates terrain; it
//                re-simulates the play stream via applyEvent().
//
// Determinism contract (why the replay is exact, not approximate):
//   * The physics is frame-count based (one STEP / one gravity tick per
//     frame, no wall-clock delta), so identical inputs at identical frames
//     produce identical motion.
//   * Both widgets run in the SAME browser JS engine, so IEEE-754 float ops
//     are bit-identical.
//   * The ONLY non-deterministic input — PlatformEngine's Math.random terrain
//     — is removed from the replay's path entirely: the play side emits each
//     generated platform as a PlatformGenerated event and the replay just
//     applies it. The replay never calls generateAhead.
//   * Event delivery over the in-process AnimalsParty is ordered, and a
//     per-frame Tick event is the shared clock: the replay executes exactly
//     one stepFrame() per Tick, in receipt order. No tick-stamping or
//     catch-up arithmetic is needed — ordering + determinism suffice.
//
// Late-join: a replay that opens mid-game asks for a Snapshot (host wiring);
// the play side answers with currentParams() (which already carries the exact
// platform layout). The replay BUFFERS any stream events that arrive before
// the snapshot, restores, then flushes the buffer — so it converges exactly
// regardless of whether party delivery is synchronous or queued.
//
// `css` and the pg_* tokens are in scope via MovingAnimalGame.java; `branch`
// and `params` ride in on config.
// =============================================================================

function createMovingAnimalGame(config) {
    var branch  = config.branch;
    var params  = config.params;
    var mode    = config.mode || 'play';
    var isReplay = (mode === 'replay');
    // Host-supplied sink for the live event stream (play only). Read lazily so
    // the first frame (a future animation frame) sees the host's party wiring.
    var onEvent = config.onEvent || function () {};
    function emit(ev) { if (!isReplay) onEvent(ev); }

    var ANIMAL_SIZE = 50, STEP = 5, SKY_H = 120, VIEWPORT_H = 500,
        LAVA_H = 40, PLATFORM_H = 16, DEFAULT_GRAVITY = 0.6, JUMP_STRENGTH = 12;

    // Logical frame counter — the replay's clock advances one per Tick event.
    var tick = 0;

    // ─── Params → initial state (Widgets Are State Functions doctrine) ─
    //     Read every persistable field from params with the DEFAULTS
    //     literal as fallback. Same Params ⇒ same construct output.
    var P = params || {};
    var initialWorldX      = (typeof P.worldX      === 'number') ? P.worldX      : 100;
    var initialWorldY      = (typeof P.worldY      === 'number') ? P.worldY      : 0;
    var initialCameraX     = (typeof P.cameraX     === 'number') ? P.cameraX     : 0;
    var initialFacingRight = (typeof P.facingRight === 'boolean') ? P.facingRight : true;
    var initialScore       = (typeof P.score       === 'number') ? P.score       : 0;
    var initialGravity     = (typeof P.gravity     === 'number') ? P.gravity     : DEFAULT_GRAVITY;
    var initialBgmVolume   = (typeof P.bgmVolume   === 'number') ? P.bgmVolume   : 40;
    var initialGameOver    = (typeof P.gameOver    === 'boolean') ? P.gameOver    : false;
    var initialVy          = (typeof P.vy          === 'number')  ? P.vy          : 0;
    var initialIsJumping   = (typeof P.isJumping   === 'boolean') ? P.isJumping   : false;
    var initialPlatforms   = Array.isArray(P.platforms)            ? P.platforms   : null;
    var root = branch.createElement('root', 'div');
    css.setClass(root, pg_widget_root);

    // ─── Title + hint ──────────────────────────────────────────
    var h1 = branch.createElement('title', 'h1');
    css.setClass(h1, pg_title);
    h1.textContent = isReplay ? 'Animal Platformer — Replay' : 'Animal Platformer';
    root.appendChild(h1);
    var hint = branch.createElement('hint', 'p');
    css.setClass(hint, pg_hint);
    hint.textContent = isReplay
        ? 'Passive replay — mirroring the live game via broadcast events.'
        : 'Arrow keys to move. Space to jump. Don’t fall into the lava!';
    root.appendChild(hint);

    // ─── Audio (lazy-init on first gesture; fully silent in replay) ───
    var audioReady = false;
    var moveSynth  = new Synth({ oscillator: { type: 'square'   }, envelope: { attack: 0.005, decay: 0.06, sustain: 0,   release: 0.05 }, volume: -26 }).toDestination();
    var jumpSynth  = new MembraneSynth({ pitchDecay: 0.08, octaves: 3, envelope: { attack: 0.005, decay: 0.15, sustain: 0,   release: 0.1 }, volume: -12 }).toDestination();
    var deathSynth = new Synth({ oscillator: { type: 'triangle' }, envelope: { attack: 0.01,  decay: 0.2,  sustain: 0.03, release: 0.15 }, volume: -12 }).toDestination();
    function ensureAudio() {
        // Replay is a silent spectator — never start the audio context, so
        // audioReady stays false and every guarded sound call below no-ops.
        if (isReplay) return Promise.resolve();
        if (!audioReady) return start().then(function () { audioReady = true; startBgm(); });
        return Promise.resolve();
    }
    var moveNoteIndex = 0;
    var moveNotes = ['C5', 'D5', 'E5', 'D5'];
    function playMoveSound() { moveSynth.triggerAttackRelease(moveNotes[moveNoteIndex], '32n'); moveNoteIndex = (moveNoteIndex + 1) % moveNotes.length; }
    function playJumpSound() { jumpSynth.triggerAttackRelease('C3', '8n'); }
    function playDeathJingle() {
        var notes = ['B4', 'F5', 'F5', 'F5', 'E5', 'D5', 'C5', 'E4', 'E4', 'C4'];
        var timing = [0, 120, 240, 400, 520, 640, 760, 920, 1040, 1200];
        for (var di = 0; di < notes.length; di++) {
            (function (note, t) { setTimeout(function () { deathSynth.triggerAttackRelease(note, '16n'); }, t); })(notes[di], timing[di]);
        }
    }

    // ─── BGM loop ─────────────────────────────────────────────
    var bgmSynth = new Synth({ oscillator: { type: 'triangle' }, envelope: { attack: 0.02, decay: 0.15, sustain: 0.1, release: 0.2 }, volume: -24 }).toDestination();
    var bgmData = getBgm();
    var bgmIndex = 0, bgmTimer = null, bgmPlaying = false;
    function durationToMs(dur, bpm) {
        var beat = 60000 / bpm;
        switch (dur) { case '2n': return beat * 2; case '4n': return beat; case '8n': return beat / 2;
                       case '16n': return beat / 4; case '32n': return beat / 8; default: return beat; }
    }
    function bgmStep() {
        if (!bgmPlaying || gameOver || !root.isConnected) return;
        var note = bgmData.notes[bgmIndex];
        var dur  = bgmData.durations[bgmIndex];
        if (note !== null) bgmSynth.triggerAttackRelease(note, dur);
        bgmIndex = (bgmIndex + 1) % bgmData.notes.length;
        bgmTimer = setTimeout(bgmStep, durationToMs(dur, bgmData.bpm));
    }
    function startBgm() { if (!bgmPlaying) { bgmPlaying = true; bgmIndex = 0; bgmStep(); } }
    function stopBgm()  { bgmPlaying = false; if (bgmTimer !== null) { clearTimeout(bgmTimer); bgmTimer = null; } }

    // ─── Landing chord (root + third + fifth, theme by platform Y) ─
    var landSynthRoot  = new Synth({ oscillator: { type: 'triangle' }, envelope: { attack: 0.01, decay: 0.2, sustain: 0.03, release: 0.15 }, volume: -16 }).toDestination();
    var landSynthThird = new Synth({ oscillator: { type: 'triangle' }, envelope: { attack: 0.01, decay: 0.2, sustain: 0.03, release: 0.15 }, volume: -18 }).toDestination();
    var landSynthFifth = new Synth({ oscillator: { type: 'triangle' }, envelope: { attack: 0.01, decay: 0.2, sustain: 0.03, release: 0.15 }, volume: -18 }).toDestination();
    var NOTE_NAMES = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B'];
    var LAND_BASE_OCTAVE = 4, LAND_SEMITONE_RANGE = 24;
    var lastLandedPlatY = null;
    function noteFromIndex(i) { return NOTE_NAMES[i % 12] + (LAND_BASE_OCTAVE + Math.floor(i / 12)); }
    function playLandingChord(platformY) {
        var range = engine.getYRange();
        var t = 1 - (platformY - range.minY) / (range.maxY - range.minY);
        t = Math.max(0, Math.min(1, t));
        var rootIndex = Math.round(t * LAND_SEMITONE_RANGE);
        var thirdOffset = (lastLandedPlatY !== null && platformY > lastLandedPlatY) ? 3 : 4;
        landSynthRoot.triggerAttackRelease(noteFromIndex(rootIndex), '8n');
        landSynthThird.triggerAttackRelease(noteFromIndex(rootIndex + thirdOffset), '8n');
        landSynthFifth.triggerAttackRelease(noteFromIndex(rootIndex + 7), '8n');
        lastLandedPlatY = platformY;
    }

    // ─── Controls ──────────────────────────────────────────────
    var controls = branch.createElement('controls', 'div');
    css.setClass(controls, pg_controls);
    var gravityLabel = branch.createElement('gravityLabel', 'label');
    gravityLabel.textContent = 'Gravity ';
    var gravitySlider = branch.createElement('gravitySlider', 'input');
    gravitySlider.type = 'range'; gravitySlider.min = '1'; gravitySlider.max = '20'; gravitySlider.step = '1';
    gravitySlider.value = String(Math.round(initialGravity * 10));
    gravityLabel.appendChild(gravitySlider);
    var gravityDisplay = branch.createElement('gravityDisplay', 'span');
    css.setClass(gravityDisplay, pg_size_display);
    gravityDisplay.textContent = initialGravity.toFixed(1);
    var bgmLabel = branch.createElement('bgmLabel', 'label');
    bgmLabel.textContent = 'BGM ';
    var bgmSlider = branch.createElement('bgmSlider', 'input');
    bgmSlider.type = 'range'; bgmSlider.min = '0'; bgmSlider.max = '100'; bgmSlider.step = '1'; bgmSlider.value = String(initialBgmVolume);
    bgmLabel.appendChild(bgmSlider);
    var bgmDisplay = branch.createElement('bgmDisplay', 'span');
    css.setClass(bgmDisplay, pg_size_display);
    bgmDisplay.textContent = initialBgmVolume + '%';
    var animalScope = createAnimalScope();   // per-instance — no cross-widget bleed
    var selector = createAnimalSelector(animalScope);
    controls.appendChild(gravityLabel); controls.appendChild(gravityDisplay);
    controls.appendChild(bgmLabel);     controls.appendChild(bgmDisplay);
    controls.appendChild(selector);
    root.appendChild(controls);

    // ─── World DOM (playground, sky, world, lava, animal, score, overlay) ─
    var viewportW = root.clientWidth || 700;
    var physics = createJumpPhysics(initialGravity, JUMP_STRENGTH);
    var engine  = createPlatformEngine({ viewportW: viewportW, viewportH: VIEWPORT_H, animalW: ANIMAL_SIZE, animalH: ANIMAL_SIZE, platformH: PLATFORM_H, lavaH: LAVA_H, skyH: SKY_H });
    var worldX = initialWorldX, worldY = initialWorldY, cameraX = initialCameraX,
        facingRight = initialFacingRight, gameOver = initialGameOver, score = initialScore, animFrameId = 0;
    // BGM volume → synth dB, mirroring slider 'input' handler arithmetic.
    bgmSynth.volume.value = (initialBgmVolume === 0) ? -Infinity : (-40 + (initialBgmVolume / 100) * 32);

    var playground = branch.createElement('playground', 'div');
    css.setClass(playground, pg_playground);
    playground.style.height = VIEWPORT_H + 'px';
    root.appendChild(playground);
    var sky = branch.createElement('sky', 'div');
    css.setClass(sky, pg_sky); sky.style.height = SKY_H + 'px';
    playground.appendChild(sky);
    var world = branch.createElement('world', 'div');
    css.setClass(world, pg_world);
    playground.appendChild(world);
    var lava = branch.createElement('lava', 'div');
    css.setClass(lava, pg_lava); lava.style.height = LAVA_H + 'px';
    playground.appendChild(lava);
    var animal = createAnimalCell(css.className(pg_animal), animalScope);
    animal.style.width = ANIMAL_SIZE + 'px'; animal.style.height = ANIMAL_SIZE + 'px';
    world.appendChild(animal);
    var scoreEl = branch.createElement('score', 'div');
    css.setClass(scoreEl, pg_score);
    scoreEl.textContent = '0';
    playground.appendChild(scoreEl);

    var overlay = branch.createElement('overlay', 'div');
    css.setClass(overlay, pg_gameover);
    overlay.style.display = 'none';
    var overTitle = branch.createElement('overTitle', 'h2');
    overTitle.textContent = 'GAME OVER';
    overlay.appendChild(overTitle);
    var finalScore = branch.createElement('finalScore', 'p');
    css.setClass(finalScore, pg_final_score);
    overlay.appendChild(finalScore);
    var restartBtn = branch.createElement('restartBtn', 'button');
    restartBtn.textContent = 'Play Again';
    overlay.appendChild(restartBtn);
    playground.appendChild(overlay);

    // A replay's controls are driven by the broadcast stream, not the user —
    // disable them so a spectator can't perturb its own simulation.
    if (isReplay) { gravitySlider.disabled = true; bgmSlider.disabled = true; restartBtn.disabled = true; }

    // ─── Dynamic platforms — document.createElement (collection escape hatch).
    //     world is branch-owned; DOM cascade on dissolve cleans these up.
    var platformEls = new Map();
    var activePlatform = null;
    function syncPlatformDom() {
        var currentPlats = engine.getPlatforms();
        var currentSet = new Set(currentPlats);
        platformEls.forEach(function (el, plat) { if (!currentSet.has(plat)) { el.remove(); platformEls.delete(plat); } });
        for (var i = 0; i < currentPlats.length; i++) {
            var p = currentPlats[i];
            var el = platformEls.get(p);
            if (!el) {
                el = document.createElement('div');
                css.setClass(el, pg_platform);
                el.style.height = PLATFORM_H + 'px';
                world.appendChild(el);
                platformEls.set(p, el);
            }
            el.style.left = p.x + 'px';
            el.style.top  = p.y + 'px';
            el.style.width = p.w + 'px';
            css.toggleClass(el, pg_platform_active, p === activePlatform);
        }
    }

    // initGame(fresh): when fresh, reset to the doctrine's DEFAULTS;
    //   when not fresh (boot), honour the restored worldX/Y/cameraX/score
    //   already set above from params. Same Params ⇒ same DOM rendering.
    //   When initialPlatforms was supplied (restore path), the engine seats
    //   those exact platforms instead of generating a fresh PRNG layout —
    //   physical state survives the round-trip.
    function initGame(fresh) {
        if (fresh) {
            worldX = 100; worldY = 0; cameraX = 0; facingRight = true;
            gameOver = false; score = 0;
        }
        scoreEl.textContent = String(score);
        overlay.style.display = gameOver ? '' : 'none';
        if (gameOver) finalScore.textContent = 'Score: ' + score;
        platformEls.forEach(function (el) { el.remove(); }); platformEls.clear();
        lastLandedPlatY = null; activePlatform = null;
        if (!fresh && initialPlatforms && initialPlatforms.length > 0) {
            // Restore path — re-seat exact saved platforms + physics state.
            engine.restore(initialPlatforms);
            physics.restore(initialVy, initialIsJumping);
            var hitR = engine.findGround(worldX, worldY, Math.max(0.001, initialVy));
            if (hitR !== null) activePlatform = hitR.platform;
            // Extend the world ahead of the saved camera so the player can
            // keep running. Existing platforms aren't disturbed by this.
            engine.generateAhead(Math.max(worldX, cameraX) + viewportW * 2);
        } else {
            // Fresh path — engine seeds its own first platform; player
            // snaps onto it.
            engine.init(worldX, worldY);
            var hit = engine.findGround(worldX, worldY, 1);
            if (hit !== null) { worldY = hit.groundY; activePlatform = hit.platform; }
            engine.generateAhead(Math.max(worldX, cameraX) + viewportW * 2);
        }
        // After restore, drop the one-shot platforms reference so a future
        // Play-Again (which calls initGame(true)) goes through the fresh path.
        initialPlatforms = null;
        updateFlip(); render();
    }
    function updateFlip() { animal.style.transform = facingRight ? 'scaleX(1)' : 'scaleX(-1)'; }
    function render() {
        world.style.transform = 'translateX(' + (-cameraX) + 'px)';
        animal.style.left = worldX + 'px';
        animal.style.top  = worldY + 'px';
        syncPlatformDom();
    }
    function die() {
        gameOver = true; stopBgm();
        if (!isReplay) ensureAudio().then(function () { playDeathJingle(); });
        finalScore.textContent = 'Score: ' + score;
        overlay.style.display = '';
    }

    // ─── Input funnel ─────────────────────────────────────────
    //   applyAction is the single semantic entry point for BOTH the play
    //   keyboard and the replay event stream — the structural reason the
    //   spectator stays in lockstep. It applies pure state effects only;
    //   audio and emission live in the play-side wrappers below.
    var keys = {}, moveHeld = false;
    function applyAction(a) {
        if (a.kind === 'MoveStarted')      { keys[a.dir] = true; }
        else if (a.kind === 'MoveStopped') { keys[a.dir] = false; if (!keys['ArrowLeft'] && !keys['ArrowRight']) moveHeld = false; }
        else if (a.kind === 'Jumped')      { physics.jump(); }
        else if (a.kind === 'GravityChanged') {
            physics.setGravity(a.value);
            gravityDisplay.textContent = a.value.toFixed(1);
            gravitySlider.value = String(Math.round(a.value * 10));
        }
    }

    // Play-side keyboard → state + audio + emit. Edge-triggered so auto-repeat
    // doesn't spam duplicate MoveStarted events.
    var keyDown = function (e) {
        if (gameOver) return;
        if (e.key === ' ') {
            e.preventDefault();
            var wasJumping = physics.isJumping();
            applyAction({ kind: 'Jumped' });        // physics applied synchronously (deterministic)
            emit({ kind: 'Jumped' });
            ensureAudio().then(function () { if (!wasJumping) playJumpSound(); });
        } else if (e.key === 'ArrowLeft' || e.key === 'ArrowRight') {
            e.preventDefault();
            if (!keys[e.key]) {
                applyAction({ kind: 'MoveStarted', dir: e.key });
                if (!moveHeld) { moveHeld = true; ensureAudio(); }
                emit({ kind: 'MoveStarted', dir: e.key });
            }
        }
    };
    var keyUp = function (e) {
        if (e.key === 'ArrowLeft' || e.key === 'ArrowRight') {
            if (keys[e.key]) {
                applyAction({ kind: 'MoveStopped', dir: e.key });
                emit({ kind: 'MoveStopped', dir: e.key });
            }
        }
    };
    gravitySlider.addEventListener('input', function (e) {
        var g = parseInt(e.target.value) / 10;
        applyAction({ kind: 'GravityChanged', value: g });
        emit({ kind: 'GravityChanged', value: g });
    });
    bgmSlider.addEventListener('input', function (e) {
        // BGM is cosmetic + play-local; not part of the deterministic stream.
        var pct = parseInt(e.target.value); bgmDisplay.textContent = pct + '%';
        if (pct === 0) bgmSynth.volume.value = -Infinity;
        else           bgmSynth.volume.value = -40 + (pct / 100) * 32;
    });
    restartBtn.addEventListener('click', function () {
        doRestart();
        // A restart re-seeds the PRNG terrain; rather than stream the new
        // layout platform-by-platform, hand the spectator a fresh Snapshot.
        emit({ kind: 'Snapshot', state: snapshotState() });
    });

    function doRestart() {
        initGame(true);
        if (!isReplay) { if (audioReady) startBgm(); animFrameId = requestAnimationFrame(frame); }
    }

    // ─── Simulation step — the deterministic core shared by play & replay.
    //     Mutates position/camera/score; returns false on death. Terrain
    //     generation is NOT here — it is a play-only authority (see frame()).
    var moveFrameCount = 0;
    function stepCore() {
        var moved = false;
        if (keys['ArrowLeft'])  { worldX -= STEP; if ( facingRight) { facingRight = false; updateFlip(); } moved = true; }
        if (keys['ArrowRight']) { worldX += STEP; if (!facingRight) { facingRight = true;  updateFlip(); } moved = true; }
        var wasInAir = physics.isJumping();
        var hit = engine.findGround(worldX, worldY, physics.getVy());
        if (hit !== null) { worldY = physics.update(worldY, hit.groundY); activePlatform = hit.platform; }
        else              { physics.fall(); worldY = physics.update(worldY, VIEWPORT_H + 100); activePlatform = null; }
        if (wasInAir && !physics.isJumping() && audioReady && hit !== null) playLandingChord(hit.platform.y);
        if (moved && audioReady && !physics.isJumping()) { moveFrameCount++; if (moveFrameCount % 4 === 0) playMoveSound(); } else { moveFrameCount = 0; }
        if (engine.isInLava(worldY)) { die(); render(); return false; }
        cameraX = engine.updateCamera(cameraX, worldX);
        var dist = Math.max(0, Math.floor(worldX / 10));
        if (dist > score) { score = dist; scoreEl.textContent = String(score); }
        return true;
    }

    // ─── Play loop (authority) — drives the clock and the terrain stream.
    function frame() {
        if (!root.isConnected) return;
        if (gameOver) return;
        tick++;
        var alive = stepCore();
        if (alive) {
            // Terrain authority: generate ahead, emit each NEW platform, prune.
            var before = engine.getPlatforms().length;
            engine.generateAhead(cameraX + viewportW * 2);
            var plats = engine.getPlatforms();
            for (var gi = before; gi < plats.length; gi++) {
                var np = plats[gi];
                emit({ kind: 'PlatformGenerated', x: np.x, y: np.y, w: np.w, vehicle: np.vehicle });
            }
            engine.pruneBehind(cameraX);
            render();
        }
        // Emit Tick last — even on the death frame — so the spectator steps
        // into (and renders) the same lava death.
        emit({ kind: 'Tick' });
        if (alive) animFrameId = requestAnimationFrame(frame);
    }

    // ─── Replay step — one frame, driven by a received Tick. No terrain
    //     generation; platforms arrive as PlatformGenerated events.
    function replayStep() {
        if (!root.isConnected) return;
        if (gameOver) return;
        tick++;
        var alive = stepCore();
        if (alive) { engine.pruneBehind(cameraX); render(); }
    }

    // ─── Snapshot / restore (late-join) ───────────────────────
    function snapshotState() {
        var s = snapshotParams();
        s.tick = tick;
        return s;
    }
    function restoreState(s) {
        if (!s) return;
        worldX = (typeof s.worldX === 'number') ? s.worldX : worldX;
        worldY = (typeof s.worldY === 'number') ? s.worldY : worldY;
        cameraX = (typeof s.cameraX === 'number') ? s.cameraX : cameraX;
        facingRight = !!s.facingRight;
        score = s.score | 0;
        gameOver = !!s.gameOver;
        physics.setGravity((typeof s.gravity === 'number') ? s.gravity : DEFAULT_GRAVITY);
        physics.restore(s.vy, s.isJumping);
        gravitySlider.value = String(Math.round(((typeof s.gravity === 'number') ? s.gravity : DEFAULT_GRAVITY) * 10));
        gravityDisplay.textContent = ((typeof s.gravity === 'number') ? s.gravity : DEFAULT_GRAVITY).toFixed(1);
        scoreEl.textContent = String(score);
        platformEls.forEach(function (el) { el.remove(); }); platformEls.clear();
        engine.restore(Array.isArray(s.platforms) ? s.platforms : []);
        activePlatform = null; lastLandedPlatY = null;
        keys = {}; moveHeld = false;
        overlay.style.display = gameOver ? '' : 'none';
        if (gameOver) finalScore.textContent = 'Score: ' + score;
        if (typeof s.tick === 'number') tick = s.tick;
        updateFlip(); render();
    }

    // ─── Replay event intake ──────────────────────────────────
    //   Until the first Snapshot lands, buffer the stream so the spectator
    //   converges exactly whether party delivery is synchronous or queued.
    var haveSnapshot = false;
    var preSnapshotBuffer = [];
    function applyEventInternal(ev) {
        switch (ev.kind) {
            case 'MoveStarted':
            case 'MoveStopped':
            case 'Jumped':
            case 'GravityChanged':
                applyAction(ev); break;
            case 'PlatformGenerated':
                engine.getPlatforms().push({ x: ev.x, y: ev.y, w: ev.w, vehicle: ev.vehicle }); break;
            case 'Tick':
                replayStep(); break;
            default: break;
        }
    }
    function applyEvent(ev) {
        if (!isReplay || !ev) return;
        if (ev.kind === 'Snapshot') {
            restoreState(ev.state);
            haveSnapshot = true;
            for (var i = 0; i < preSnapshotBuffer.length; i++) applyEventInternal(preSnapshotBuffer[i]);
            preSnapshotBuffer = [];
            return;
        }
        if (!haveSnapshot) { preSnapshotBuffer.push(ev); return; }
        applyEventInternal(ev);
    }

    // ─── Persistable snapshot (Widgets Are State Functions) ───
    function snapshotParams() {
        var gv = parseInt(gravitySlider.value) / 10;
        var bv = parseInt(bgmSlider.value);
        // Snapshot platforms — shallow copy of { x, y, w, vehicle } each.
        // The PRNG-driven engine isn't reproducible from worldX alone, so
        // the actual layout has to ride along with the rest of Params.
        var livePlatforms = engine.getPlatforms();
        var platformsCopy = [];
        for (var pi = 0; pi < livePlatforms.length; pi++) {
            var lp = livePlatforms[pi];
            platformsCopy.push({ x: lp.x, y: lp.y, w: lp.w, vehicle: lp.vehicle });
        }
        return {
            worldX:      worldX,
            worldY:      worldY,
            cameraX:     cameraX,
            facingRight: facingRight,
            score:       score,
            gravity:     gv,
            bgmVolume:   bv,
            gameOver:    gameOver,
            vy:          physics.getVy(),
            isJumping:   physics.isJumping(),
            platforms:   platformsCopy
        };
    }

    initGame(false);    // honour restored params on first construct
    // Only the play authority free-runs on rAF; the replay advances on Ticks.
    if (!isReplay && !gameOver) animFrameId = requestAnimationFrame(frame);

    // ─── Controller — the game object's public surface. The host widget
    //     wraps this (adding party wiring) and returns it from construct().
    return {
        root: root,
        currentParams: snapshotParams,
        setActive: function (active) {
            // The replay has no keyboard or audio to gate; its lifecycle is
            // entirely driven by the broadcast stream.
            if (isReplay) return;
            if (active) {
                document.addEventListener('keydown', keyDown);
                document.addEventListener('keyup',   keyUp);
                if (audioReady && !bgmPlaying && !gameOver) startBgm();
            } else {
                document.removeEventListener('keydown', keyDown);
                document.removeEventListener('keyup',   keyUp);
                stopBgm();
                // Forget held keys so re-activation doesn't see stuck input.
                keys = {}; moveHeld = false;
            }
        },
        // setAnimal(index) — apply an AnimalsParty AnimalChanged broadcast to
        // this game's per-instance animal scope. Works in both modes.
        setAnimal: function (index) { setScopeAnimal(animalScope, index); },
        // applyEvent(ev) — replay intake for the broadcast game stream.
        applyEvent: applyEvent,
        // snapshot() — play-side answer to a late-join snapshot request.
        snapshot: snapshotState
    };
}
