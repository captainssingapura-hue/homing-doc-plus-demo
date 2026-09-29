package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.component.keyboard.focusParties;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParties;
import hue.captains.singapura.js.homing.demo.workspacewidgets.animals.AnimalChoiceModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.animals.AnimalsModule;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/**
 * The animal platformer, played: {@code new PlatformerPlay(container, params)} -
 * the authority of its run, telling every action, every platform made and a
 * Tick a frame to the platformer party, in order; its animal the animal choice
 * party's; its keys through the keyboard's party.
 */
public record PlatformerPlayModule() implements DomModule<PlatformerPlayModule> {

    public record PlatformerPlay() implements SelfContainedWidget<PlatformerPlayModule>, NeedKeyboard {
        @Override public String summary() { return "The animal platformer, played - the authority of its run, telling every action, platform and frame to the platformer party; its animal the animal choice party's."; }
        @Override public List<KeyBinding> keys() {
            return List.of(
                    KeyBinding.of(Key.ARROW_LEFT, "run left, while held"),
                    KeyBinding.of(Key.ARROW_RIGHT, "run right, while held"),
                    KeyBinding.of(Key.SPACE, "jump"),
                    KeyBinding.of(Key.ESCAPE, "the keys given back"));
        }
    }

    public static final PlatformerPlayModule INSTANCE = new PlatformerPlayModule();

    @Override
    public ImportsFor<PlatformerPlayModule> imports() {
        return ImportsFor.<PlatformerPlayModule>builder()
                .add(new ModuleImports<>(List.of(new domOpsParties()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParties()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.SliderBuilder()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformerGameModule.PlatformerGame()), PlatformerGameModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformerSoundModule.PlatformerSound()), PlatformerSoundModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformerStageModule.PlatformerStage()), PlatformerStageModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new AnimalsModule.Animals()), AnimalsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformerModule.PLATFORMER()), PlatformerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new AnimalChoiceModule.ANIMAL_CHOICE()), AnimalChoiceModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_fill()), WidgetStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformerStyles.pf_root(), new PlatformerStyles.pf_head(), new PlatformerStyles.pf_title(),
                        new PlatformerStyles.pf_score(), new PlatformerStyles.pf_hint(), new PlatformerStyles.pf_controls()), PlatformerStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlatformerPlayModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlatformerPlay())); }
}
