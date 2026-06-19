package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
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
public final class MovingAnimalWidget extends WorkspaceWidget<MovingAnimalWidget.Params, MovingAnimalWidget> {

    /**
     * Persistable state per the {@code Widgets Are State Functions} doctrine —
     * the widget's whole identity, exposed at the framework boundary so
     * save/restore round-trips faithfully.
     *
     * <p>Position ({@code worldX/Y, cameraX, facingRight}) places the
     * animal exactly where it was; {@code score} continues from where it
     * stopped; {@code gravity / bgmVolume} preserve the slider settings;
     * {@code gameOver} captures whether the run already ended (so a refresh
     * doesn't silently resurrect a dead game). Platform layout is
     * <i>not</i> in Params — the engine regenerates platforms ahead of
     * {@code cameraX} deterministically, so they re-derive on construct.
     * The discipline: state in Params, derived state in the DOM cache.</p>
     */
    public record Params(
            double  worldX,
            double  worldY,
            double  cameraX,
            boolean facingRight,
            int     score,
            double  gravity,
            int     bgmVolume,
            boolean gameOver,
            // Physics — vy/isJumping needed for the next frame to behave
            // identically to the frame after capture. Without these, a saved
            // mid-jump animal lands wrong on restore.
            double  vy,
            boolean isJumping) implements WorkspaceWidget._Param {

        /**
         * Fresh-game defaults — what construct() reads when params is empty.
         * The presence of {@code DEFAULTS} signals to the widget picker that
         * the framework knows how to start this widget fresh without a form;
         * see {@code WidgetEntriesJson.appendParamsFields}. {@code platforms}
         * (the engine's terrain layout) is not on this record — the array
         * shape doesn't fit the picker's reflection-driven form layer; the
         * JS-side codec carries it as a side-channel field.
         */
        public static final Params DEFAULTS =
                new Params(100, 0, 0, true, 0, 0.6, 40, false, 0, false);
    }

    public static final MovingAnimalWidget INSTANCE = new MovingAnimalWidget();

    private MovingAnimalWidget() {}

    private record construct() implements WorkspaceWidget._Construct<Params, MovingAnimalWidget> {}

    @Override protected _Construct<Params, MovingAnimalWidget> construct() { return new construct(); }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title() { return "Moving Animal"; }
    // SINGLETON — there is only ever one live (authoritative) game; opening it
    // again focuses the existing instance. Spectators are MovingAnimalReplayWidget.
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.SINGLETON; }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        // The whole game body now lives in the reusable MovingAnimalGame
        // backbone module; this widget only constructs it and wires the
        // AnimalsParty. All of the engine/audio/style imports the game needs
        // are declared on MovingAnimalGame itself and ride in transitively.
        return List.of(
                new ModuleImports<>(List.of(new MovingAnimalGame.createMovingAnimalGame()),
                        MovingAnimalGame.INSTANCE)
        );
    }


    @Override
    protected List<String> constructBodyJs() {
        // The PLAY widget — the authoritative game. It constructs the backbone
        // in 'play' mode, wires the workspace AnimalsParty, and:
        //   * forwards the game's live event stream out as GameActionRequested
        //     (onEvent → tellFrom), which the Secretary broadcasts as GameAction
        //     to every replay spectator;
        //   * answers a replay's late-join GameSnapshotRequested with a private
        //     Snapshot (currentParams + tick) addressed to the asker;
        //   * keeps the existing AnimalChanged / CurrentAnimalRequested wiring.
        return List.of(
                "    var __actorId = null;",
                "    var __party = (workspaceCtx && workspaceCtx.animalsParty) ? workspaceCtx.animalsParty : null;",
                "    var game = createMovingAnimalGame({",
                "        branch: branch,",
                "        params: params,",
                "        mode:   'play',",
                "        // Live stream sink — read __actorId lazily; the first frame",
                "        // fires after joinActor() below has set it.",
                "        onEvent: function (ev) {",
                "            if (__party && __actorId) {",
                "                __party.tellFrom(__actorId, { kind: 'GameActionRequested', event: ev });",
                "            }",
                "        }",
                "    });",
                "",
                "    // RFC 0028 — join AnimalsParty if the workspace provided one.",
                "    if (__party) {",
                "        __actorId = 'animals/moving-' + Math.random().toString(36).slice(2, 8);",
                "        __party.joinActor({",
                "            id: __actorId,",
                "            parentSecretary: 'animals',",
                "            reactors: {",
                "                AnimalChanged: function (msg) { game.setAnimal(msg.animal); },",
                "                // A spectator joined and asked for the current game — reply",
                "                // privately with a full snapshot it can restore from.",
                "                GameSnapshotRequested: function (msg) {",
                "                    if (msg && msg.asker) {",
                "                        __party.tellFrom(__actorId, {",
                "                            kind: 'GameActionRequested',",
                "                            to:   msg.asker,",
                "                            event: { kind: 'Snapshot', state: game.snapshot() }",
                "                        });",
                "                    }",
                "                }",
                "            }",
                "        });",
                "        // Late-join sync — see SpinningAnimalsWidget for the full comment.",
                "        __party.tellFrom(__actorId, { kind: 'CurrentAnimalRequested' });",
                "    }",
                "    return {",
                "        root:            game.root,",
                "        currentParams:   game.currentParams,",
                "        setActive:       game.setActive,",
                "        partyDeregister: function () {",
                "            if (__actorId && __party) {",
                "                try { __party.leave(__actorId); } catch (e) {}",
                "            }",
                "        }",
                "    };"
        );
    }
}
