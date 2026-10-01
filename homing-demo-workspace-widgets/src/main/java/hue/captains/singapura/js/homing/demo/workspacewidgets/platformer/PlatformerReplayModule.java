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
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/**
 * The animal platformer, watched: {@code new PlatformerReplay(container, params)} -
 * the run the player tells the platformer party, re-simulated step for step;
 * joining mid-run, handed the world as it stands, a snapshot of records inside
 * records. It makes nothing of its own: no terrain, no keys, no sound.
 */
public record PlatformerReplayModule() implements DomModule<PlatformerReplayModule> {

    public record PlatformerReplay() implements SelfContainedWidget<PlatformerReplayModule>, NeedKeyboard {
        @Override public String summary() { return "The animal platformer, watched - the run the player tells the platformer party, re-simulated step for step; joining mid-run, handed the world as it stands."; }
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.ESCAPE, "the keys given back")); }
    }

    public static final PlatformerReplayModule INSTANCE = new PlatformerReplayModule();

    @Override
    public ImportsFor<PlatformerReplayModule> imports() {
        return ImportsFor.<PlatformerReplayModule>builder()
                .add(new ModuleImports<>(List.of(new domOpsParties()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParties()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformerGameModule.PlatformerGame()), PlatformerGameModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformerStageModule.PlatformerStage()), PlatformerStageModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new AnimalsModule.Animals()), AnimalsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformerModule.PLATFORMER()), PlatformerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new AnimalChoiceModule.ANIMAL_CHOICE()), AnimalChoiceModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_fill()), WidgetStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformerStyles.pf_root(), new PlatformerStyles.pf_head(), new PlatformerStyles.pf_title(),
                        new PlatformerStyles.pf_score(), new PlatformerStyles.pf_hint()), PlatformerStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlatformerReplayModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlatformerReplay())); }
}
