package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.look.StatesAndEventsModule;
import hue.captains.singapura.js.homing.ui.elements.EdgeStripModule;
import hue.captains.singapura.js.homing.ui.elements.PlainButtonModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code EdgeStripDemo}: the house's edge strip in action - tools at a box's foot, shown by the hand, held shown by its option. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 4,
        reason = "It makes elements by a companion, places elements and touches components' elements: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record EdgeStripDemoModule() implements DomModule<EdgeStripDemoModule> {

    public static final EdgeStripDemoModule INSTANCE = new EdgeStripDemoModule();

    public record EdgeStripDemo() implements SelfContainedWidget<EdgeStripDemoModule> {
        @Override public String summary() { return "The house's edge strip in action: a box whose tools wait at its foot, shown when the hand reaches the lip and gone after it leaves; held shown, and let go, by its option."; }
    }

    @Override
    public ImportsFor<EdgeStripDemoModule> imports() {
        return ImportsFor.<EdgeStripDemoModule>builder()
                .add(new ModuleImports<>(List.of(new StatesAndEventsModule.EVENT()), StatesAndEventsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new EdgeStripModule.EdgeStrip()), EdgeStripModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlainButtonModule.PlainButton(), new PlainButtonModule.PlainButtonFactory()), PlainButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_host(), new DemoStyles.dm_text()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<EdgeStripDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new EdgeStripDemo())); }
}
