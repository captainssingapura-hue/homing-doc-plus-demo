package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.demo.css.SubwayStyles;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.base.ui.StudioElements;

import java.util.List;

public record DancingAnimals() implements AppModule<AppModule._None, DancingAnimals> {

    record appMain() implements AppModule._AppMain<AppModule._None, DancingAnimals> {}

    public record link() implements AppLink<DancingAnimals> {}

    public static final DancingAnimals INSTANCE = new DancingAnimals();

    @Override
    public String title() {
        return "Dancing Animals";
    }

    @Override
    public ImportsFor<DancingAnimals> imports() {
        return ImportsFor.<DancingAnimals>builder()
                .add(new ModuleImports<>(List.of(new AnimalCell.createAnimalCell(), new AnimalCell.createAnimalSelector()), AnimalCell.INSTANCE))
                .add(new ModuleImports<>(List.of(new JumpPhysics.createJumpPhysics()), JumpPhysics.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new SubwayStyles.subway_title(),
                        new SubwayStyles.subway_hint(),
                        new SubwayStyles.subway_grid(),
                        new SubwayStyles.subway_cell()
                ), SubwayStyles.INSTANCE))
                // Studio chrome — Header + brand + breadcrumb plus the layout
                // classes the shell expects. Lets the dance grid sit inside
                // the standard studio shell.
                .add(new ModuleImports<>(List.of(new StudioElements.Header()), StudioElements.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(),
                        new StudioStyles.st_main(),
                        new StudioStyles.st_loading()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DancingAnimals> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
