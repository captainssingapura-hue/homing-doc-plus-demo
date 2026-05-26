package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.demo.css.SpinningStyles;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.base.ui.StudioElements;

import java.util.List;

@LegacyAppMain(reason = "Simple gallery; opportunistic migration after RFC 0024 P3 lands.")
public record SpinningAnimals() implements AppModule<AppModule._None, SpinningAnimals> {

    record appMain() implements AppModule._AppMain<AppModule._None, SpinningAnimals> {}

    public record link() implements AppLink<SpinningAnimals> {}

    public static final SpinningAnimals INSTANCE = new SpinningAnimals();

    @Override
    public String title() {
        return "Spinning Animals";
    }

    @Override
    public ImportsFor<SpinningAnimals> imports() {
        return ImportsFor.<SpinningAnimals>builder()
                .add(new ModuleImports<>(List.of(new AnimalCell.createAnimalCell(), new AnimalCell.createAnimalSelector()), AnimalCell.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new SpinningStyles.spin_title(),
                        new SpinningStyles.spin_hint(),
                        new SpinningStyles.spin_controls(),
                        new SpinningStyles.spin_grid(),
                        new SpinningStyles.spin_cell(),
                        new SpinningStyles.paused()
                ), SpinningStyles.INSTANCE))
                // Studio chrome — Header + brand + breadcrumb plus the layout
                // classes the shell expects.
                .add(new ModuleImports<>(List.of(new StudioElements.Header()), StudioElements.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(),
                        new StudioStyles.st_main(),
                        new StudioStyles.st_loading()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<SpinningAnimals> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
