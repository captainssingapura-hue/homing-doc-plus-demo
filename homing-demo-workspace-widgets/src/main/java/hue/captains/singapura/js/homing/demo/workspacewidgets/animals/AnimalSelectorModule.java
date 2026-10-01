package hue.captains.singapura.js.homing.demo.workspacewidgets.animals;

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
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/**
 * Which animal the widgets of a workspace show: {@code new AnimalSelector(container, params)}
 * - the six animals, one chosen, as the animal choice party says; a press asks the
 * party to choose, and every widget that meets there follows.
 */
public record AnimalSelectorModule() implements DomModule<AnimalSelectorModule> {

    public record AnimalSelector() implements SelfContainedWidget<AnimalSelectorModule>, NeedKeyboard {
        @Override public String summary() { return "Which animal the widgets of a workspace show - the six animals, one chosen, as the animal choice party says; a press asks the party to choose."; }
        @Override public List<KeyBinding> keys() {
            return List.of(
                    KeyBinding.of(Key.ARROW_RIGHT, "the next animal, browsed - into the list, when the widget holds the keys"),
                    KeyBinding.of(Key.ARROW_DOWN, "the next animal, browsed"),
                    KeyBinding.of(Key.ARROW_LEFT, "the previous animal, browsed"),
                    KeyBinding.of(Key.ARROW_UP, "the previous animal, browsed"),
                    KeyBinding.of(Key.HOME, "the first animal, browsed"),
                    KeyBinding.of(Key.END, "the last animal, browsed"),
                    KeyBinding.of(Key.ENTER, "the animal browsed, chosen"),
                    KeyBinding.of(Key.SPACE, "the animal browsed, chosen"),
                    KeyBinding.of(Key.ESCAPE, "the keys given back"));
        }
    }

    public static final AnimalSelectorModule INSTANCE = new AnimalSelectorModule();

    @Override
    public ImportsFor<AnimalSelectorModule> imports() {
        return ImportsFor.<AnimalSelectorModule>builder()
                .add(new ModuleImports<>(List.of(new domOpsParties()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParties()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new AnimalsModule.Animals()), AnimalsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new AnimalChoiceModule.ANIMAL_CHOICE()), AnimalChoiceModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_fill()), WidgetStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new AnimalStyles.an_root(), new AnimalStyles.an_title(), new AnimalStyles.an_note(),
                        new AnimalStyles.an_list(), new AnimalStyles.an_option(), new AnimalStyles.an_picture(), new AnimalStyles.an_name()), AnimalStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<AnimalSelectorModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new AnimalSelector())); }
}
