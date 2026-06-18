package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.css.PlaygroundStyles;
import hue.captains.singapura.js.homing.demo.playground.AnimalsPlaygroundStyles;
import hue.captains.singapura.js.homing.libs.ToneJs;

import java.util.List;

/**
 * The reusable, object-oriented backbone of the Animal Platformer —
 * {@code createMovingAnimalGame(config)}. Extracted from
 * {@link MovingAnimalWidget}'s inline body so the identical game can be
 * driven two ways for the RFC 0028 broadcast feature: a live keyboard
 * instance ({@code MovingAnimalPlayWidget}) and a passive spectator that
 * re-simulates broadcast events ({@code MovingAnimalReplayWidget}).
 *
 * <p>This module owns everything below the widget boundary — DOM build,
 * Tone.js audio + BGM, {@link JumpPhysics}, {@link PlatformEngine}, the
 * {@code requestAnimationFrame} loop, rendering, and persistable-state
 * snapshot/restore. It is created with a {@code config} bag
 * ({@code { branch, params }}) rather than relying on construct-scope
 * bindings, so it is host-agnostic and returns a controller object:</p>
 *
 * <pre>{@code
 * { root, currentParams(), setActive(boolean), setAnimal(index) }
 * }</pre>
 *
 * <p>This is a {@link DomModule} so {@code css} and the {@code pg_*} style
 * tokens are in scope in the generated JS; the imports below also bring the
 * factory functions, Tone.js synths, and BGM data into scope. {@code branch}
 * and {@code params} are construct-time handles and ride in on {@code config}.</p>
 *
 * @since RFC 0028 broadcast feature — step 1, backbone extraction.
 */
public record MovingAnimalGame() implements DomModule<MovingAnimalGame> {

    record createMovingAnimalGame() implements Exportable._Constant<MovingAnimalGame> {}

    public static final MovingAnimalGame INSTANCE = new MovingAnimalGame();

    @Override
    public ImportsFor<MovingAnimalGame> imports() {
        return ImportsFor.<MovingAnimalGame>builder()
                .add(new ModuleImports<>(List.of(
                        new AnimalCell.createAnimalCell(),
                        new AnimalCell.createAnimalSelector(),
                        new AnimalCell.createAnimalScope(),
                        new AnimalCell.setScopeAnimal()),
                        AnimalCell.INSTANCE))
                .add(new ModuleImports<>(List.of(new JumpPhysics.createJumpPhysics()), JumpPhysics.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformEngine.createPlatformEngine()), PlatformEngine.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new ToneJs.Synth(),
                        new ToneJs.MembraneSynth(),
                        new ToneJs.start()),
                        ToneJs.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformerBgm.getBgm()), PlatformerBgm.INSTANCE))
                .add(new ModuleImports<>(List.of(
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
                        PlaygroundStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new AnimalsPlaygroundStyles.pg_widget_root()),
                        AnimalsPlaygroundStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<MovingAnimalGame> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new createMovingAnimalGame()));
    }
}
