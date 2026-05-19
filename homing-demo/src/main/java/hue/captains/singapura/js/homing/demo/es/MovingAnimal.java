package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.libs.ToneJs;
import hue.captains.singapura.js.homing.demo.css.PlaygroundStyles;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.base.ui.StudioElements;

import java.util.List;

public record MovingAnimal() implements AppModule<AppModule._None, MovingAnimal> {

    record appMain() implements AppModule._AppMain<AppModule._None, MovingAnimal> {}

    public record link() implements AppLink<MovingAnimal> {}

    public static final MovingAnimal INSTANCE = new MovingAnimal();

    @Override
    public String title() {
        return "Moving Animal";
    }

    @Override
    public ImportsFor<MovingAnimal> imports() {
        return ImportsFor.<MovingAnimal>builder()
                .add(new ModuleImports<>(List.of(new AnimalCell.createAnimalCell(), new AnimalCell.createAnimalSelector()), AnimalCell.INSTANCE))
                .add(new ModuleImports<>(List.of(new JumpPhysics.createJumpPhysics()), JumpPhysics.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformEngine.createPlatformEngine()), PlatformEngine.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new ToneJs.Synth(),
                        new ToneJs.MembraneSynth(),
                        new ToneJs.start()
                ), ToneJs.INSTANCE))
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
                        new PlaygroundStyles.pg_final_score()
                ), PlaygroundStyles.INSTANCE))
                // Studio chrome — Header + brand + breadcrumb wrapper and the
                // st_root / st_main / st_loading layout classes the chrome
                // expects. Lets the platformer sit inside the standard studio
                // shell rather than at root document level.
                .add(new ModuleImports<>(List.of(new StudioElements.Header()), StudioElements.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(),
                        new StudioStyles.st_main(),
                        new StudioStyles.st_loading()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<MovingAnimal> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
