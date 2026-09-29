package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.workspacewidgets.animals.AnimalsModule;
import hue.captains.singapura.js.homing.ui.elements.ElementStyles;

import java.util.List;

/**
 * The platformer, drawn: {@code new PlatformerStage(branch, { onAgain })} - a
 * stage of the game's own size scaled to its box, the world, the platforms,
 * the lava, the animal, and a card when the run is over; every place a custom
 * property its class reads.
 */
public record PlatformerStageModule() implements DomModule<PlatformerStageModule> {

    public record PlatformerStage() implements BranchComponent<PlatformerStageModule> {
        @Override public String summary() { return "The platformer drawn: a stage of the game's size scaled to its box - sky, world, platforms, lava, the animal, and a card when the run is over."; }
    }

    public static final PlatformerStageModule INSTANCE = new PlatformerStageModule();

    @Override
    public ImportsFor<PlatformerStageModule> imports() {
        return ImportsFor.<PlatformerStageModule>builder()
                .add(new ModuleImports<>(List.of(new PlatformerGameModule.PlatformerGame()), PlatformerGameModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new AnimalsModule.Animals()), AnimalsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ElementStyles.el_button(), new ElementStyles.el_button_primary()), ElementStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformerStyles.pf_view(), new PlatformerStyles.pf_stage(), new PlatformerStyles.pf_sky(),
                        new PlatformerStyles.pf_world(), new PlatformerStyles.pf_platform(), new PlatformerStyles.pf_platform_under(),
                        new PlatformerStyles.pf_gone(), new PlatformerStyles.pf_animal(), new PlatformerStyles.pf_left(), new PlatformerStyles.pf_lava(),
                        new PlatformerStyles.pf_over(), new PlatformerStyles.pf_over_title(), new PlatformerStyles.pf_hint()), PlatformerStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlatformerStageModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlatformerStage())); }
}
