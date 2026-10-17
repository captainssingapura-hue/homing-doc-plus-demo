package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.panes.TabOpenerModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code TabOpenerDemo}: the house's tab opener in action - a chooser in a tab that becomes what is chosen; opened and closed by its options. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 1,
        reason = "It touches components' elements: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record TabOpenerDemoModule() implements DomModule<TabOpenerDemoModule> {

    public static final TabOpenerDemoModule INSTANCE = new TabOpenerDemoModule();

    public record TabOpenerDemo() implements SelfContainedWidget<TabOpenerDemoModule> {
        @Override public String summary() { return "The house's tab opener in action: a chooser in a tab of its own, which becomes what is chosen - the same chip, in the same place; another opened, and the one in front closed, by its options."; }
    }

    @Override
    public ImportsFor<TabOpenerDemoModule> imports() {
        return ImportsFor.<TabOpenerDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoDocksModule.DemoDocks()), DemoDocksModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TabOpenerModule.TabOpener()), TabOpenerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_host()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<TabOpenerDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new TabOpenerDemo())); }
}
