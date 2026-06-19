package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.List;

/**
 * The passive spectator twin of {@link MovingAnimalWidget} — RFC 0028
 * broadcast feature. It shares the exact same backbone ({@code
 * createMovingAnimalGame}) and the same {@link MovingAnimalWidget.Params}
 * shape, but constructs the game in {@code 'replay'} mode: no keyboard, no
 * audio, no terrain generation. It re-simulates the live game by applying the
 * authoritative event stream that the play widget broadcasts through the
 * workspace {@code AnimalsParty}.
 *
 * <h2>Wiring</h2>
 * <ul>
 *   <li>{@code AnimalChanged} → recolour this game's per-instance animal scope
 *       (workspace-wide animal selection, unchanged from the play side).</li>
 *   <li>{@code GameAction} → {@code game.applyEvent(event)} — the spectator
 *       intake. The backbone buffers events until the first {@code Snapshot}
 *       lands, then converges exactly.</li>
 *   <li>On join it sends {@code CurrentGameRequested}; the Secretary fans that
 *       out and the live play widget answers with a private {@code Snapshot}.</li>
 * </ul>
 *
 * <p>{@link LifecycleHint#MULTI} — any number of spectator panes may mirror the
 * single {@code SINGLETON} play game.</p>
 *
 * @since RFC 0028 broadcast feature — step 2, play/replay split.
 */
public final class MovingAnimalReplayWidget
        extends WorkspaceWidget<MovingAnimalWidget.Params, MovingAnimalReplayWidget> {

    public static final MovingAnimalReplayWidget INSTANCE = new MovingAnimalReplayWidget();

    private MovingAnimalReplayWidget() {}

    private record construct() implements WorkspaceWidget._Construct<MovingAnimalWidget.Params, MovingAnimalReplayWidget> {}

    @Override protected _Construct<MovingAnimalWidget.Params, MovingAnimalReplayWidget> construct() { return new construct(); }
    @Override public Class<MovingAnimalWidget.Params> paramsType() { return MovingAnimalWidget.Params.class; }
    @Override public String title() { return "Moving Animal (Replay)"; }
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.MULTI; }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        // Same backbone as the play widget — the engine/audio/style imports
        // ride in transitively via MovingAnimalGame.
        return List.of(
                new ModuleImports<>(List.of(new MovingAnimalGame.createMovingAnimalGame()),
                        MovingAnimalGame.INSTANCE)
        );
    }

    @Override
    protected List<String> constructBodyJs() {
        // The REPLAY widget — a passive spectator. It constructs the backbone
        // in 'replay' mode (no onEvent: it never emits), then wires the party:
        //   * GameAction → game.applyEvent(event) re-simulates the live stream;
        //   * on join it requests a Snapshot via CurrentGameRequested so a
        //     mid-game open converges exactly;
        //   * AnimalChanged keeps the workspace-wide animal selection in sync.
        return List.of(
                "    var __actorId = null;",
                "    var __party = (workspaceCtx && workspaceCtx.animalsParty) ? workspaceCtx.animalsParty : null;",
                "    var game = createMovingAnimalGame({ branch: branch, params: params, mode: 'replay' });",
                "",
                "    if (__party) {",
                "        __actorId = 'animals/replay-' + Math.random().toString(36).slice(2, 8);",
                "        __party.joinActor({",
                "            id: __actorId,",
                "            parentSecretary: 'animals',",
                "            reactors: {",
                "                AnimalChanged: function (msg) { game.setAnimal(msg.animal); },",
                "                GameAction:    function (msg) { game.applyEvent(msg.event); }",
                "            }",
                "        });",
                "        // Late-join: pull the current animal AND the current game state.",
                "        __party.tellFrom(__actorId, { kind: 'CurrentAnimalRequested' });",
                "        __party.tellFrom(__actorId, { kind: 'CurrentGameRequested' });",
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
