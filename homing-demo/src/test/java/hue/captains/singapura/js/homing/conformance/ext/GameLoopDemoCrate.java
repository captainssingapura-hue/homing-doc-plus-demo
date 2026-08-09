package hue.captains.singapura.js.homing.conformance.ext;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.demo.es.MovingAnimalGame;

import java.util.List;

/**
 * RFC 0044 extension demo — a <b>hypothetical downstream {@link Crate}</b>. This
 * is where a module is <b>declared</b> as the library's own {@link
 * GameLoopModuleType#GAME_LOOP} type: {@code CrateEntry.of(module, declaredType)}
 * attaches the intended role, and {@code ModuleClassifier} then
 * honours it — the declaration wins over the structural inference that would
 * otherwise class this {@code DomModule} as a plain {@code CONSUMER}.
 *
 * <p>Here {@link MovingAnimalGame} — a real served demo game — is packed as a
 * {@code GAME_LOOP}. Running {@code ConformanceEngine.checkCrates(...)} with the
 * extended {@link GameLoopConformance#POLICY} would then hold it to the {@code
 * game-loop} rule set. (For a real crate this class lives in its own Maven module,
 * whose build output <i>is</i> the packed modules; here it just demonstrates the
 * declaration.)</p>
 */
public final class GameLoopDemoCrate implements Crate {

    public static final GameLoopDemoCrate INSTANCE = new GameLoopDemoCrate();

    private GameLoopDemoCrate() {}

    @Override public String name() { return "homing-demo-game-loops"; }

    @Override
    public List<CrateEntry> entries() {
        return List.of(CrateEntry.of(MovingAnimalGame.INSTANCE, GameLoopModuleType.GAME_LOOP));
    }
}
