package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.LinkModule;
import hue.captains.singapura.js.homing.ui.look.StatesAndEventsModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code LinkDemo}: the house's link in action - its click told, the way not followed; sized by its options. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 3,
        reason = "It makes elements by a companion, places elements and touches components' elements: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record LinkDemoModule() implements DomModule<LinkDemoModule> {

    public static final LinkDemoModule INSTANCE = new LinkDemoModule();

    public record LinkDemo() implements SelfContainedWidget<LinkDemoModule> {
        @Override public String summary() { return "The house's link in action: its words and where it goes, in the look the style gives it; a click told, and the way not followed - its handler prevents the raw event; sized by its options."; }
    }

    @Override
    public ImportsFor<LinkDemoModule> imports() {
        return ImportsFor.<LinkDemoModule>builder()
                .add(new ModuleImports<>(List.of(new StatesAndEventsModule.EVENT()), StatesAndEventsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new LinkModule.Link(), new LinkModule.LinkFactory()), LinkModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<LinkDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new LinkDemo())); }
}
