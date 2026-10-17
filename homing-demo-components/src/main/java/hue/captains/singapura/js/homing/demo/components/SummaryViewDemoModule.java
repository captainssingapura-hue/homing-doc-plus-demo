package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.ClickableCardModule;
import hue.captains.singapura.js.homing.ui.elements.SummaryViewModule;
import hue.captains.singapura.js.homing.ui.look.StatesAndEventsModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.ArrayList;
import java.util.List;

/** {@code SummaryViewDemo}: the house's summary view in action, shown on a clickable card - opened by the pointer or the keys; sized and shaped by its options. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 3,
        reason = "It places elements and touches components' elements - the summary view set on the card's own, as a card holds what is in it: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record SummaryViewDemoModule() implements DomModule<SummaryViewDemoModule> {

    public static final SummaryViewDemoModule INSTANCE = new SummaryViewDemoModule();

    public record SummaryViewDemo() implements SelfContainedWidget<SummaryViewDemoModule>, NeedKeyboard {
        @Override public String summary() { return "The house's summary view in action, shown on a clickable card: a title, a tag and a summary, the card opened by the pointer or by the keys; its size and aspect set by its options."; }
        @Override public List<KeyBinding> keys() {
            var keys = new ArrayList<>(KeyBinding.each("the card's action", Key.ENTER, Key.SPACE));
            keys.add(KeyBinding.of(Key.ESCAPE, "the keys given back"));
            return List.copyOf(keys);
        }
    }

    @Override
    public ImportsFor<SummaryViewDemoModule> imports() {
        return ImportsFor.<SummaryViewDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ClickableCardModule.ClickableCard()), ClickableCardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SummaryViewModule.SummaryView()), SummaryViewModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StatesAndEventsModule.EVENT()), StatesAndEventsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<SummaryViewDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new SummaryViewDemo())); }
}
