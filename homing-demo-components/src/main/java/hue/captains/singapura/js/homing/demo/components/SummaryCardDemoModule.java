package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.Elements;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.ArrayList;
import java.util.List;

/** {@code SummaryCardDemo}: the house's summary card in action - opened by the pointer or the keys; sized and shaped by its options. */
public record SummaryCardDemoModule() implements DomModule<SummaryCardDemoModule> {

    public static final SummaryCardDemoModule INSTANCE = new SummaryCardDemoModule();

    public record SummaryCardDemo() implements SelfContainedWidget<SummaryCardDemoModule>, NeedKeyboard {
        @Override public String summary() { return "The house's summary card in action: a title, a tag, a summary and an action, opened by the pointer or by the keys; its size and aspect set by its options."; }
        @Override public List<KeyBinding> keys() {
            var keys = new ArrayList<>(KeyBinding.each("the card's action", Key.ENTER, Key.SPACE));
            keys.add(KeyBinding.of(Key.ESCAPE, "the keys given back"));
            return List.copyOf(keys);
        }
    }

    @Override
    public ImportsFor<SummaryCardDemoModule> imports() {
        return ImportsFor.<SummaryCardDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.CardBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<SummaryCardDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new SummaryCardDemo())); }
}
