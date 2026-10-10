package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.WidgetSlotModule;
import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.monitors.FocusMonitorModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code FocusMonitorDemo}: the house's focus monitor in action, on this very page - its focus tree, the holder lit; asked who holds the keys by its option. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 3,
        reason = "It writes text of its own and touches components' elements: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record FocusMonitorDemoModule() implements DomModule<FocusMonitorDemoModule> {

    public static final FocusMonitorDemoModule INSTANCE = new FocusMonitorDemoModule();

    public record FocusMonitorDemo() implements SelfContainedWidget<FocusMonitorDemoModule> {
        @Override public String summary() { return "The house's focus monitor in action, on this very page: its logical-focus tree as a tree view, the row of the member that holds the keys lit, following every press; asked who holds the keys by its option."; }
    }

    @Override
    public ImportsFor<FocusMonitorDemoModule> imports() {
        return ImportsFor.<FocusMonitorDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new FocusMonitorModule.FocusMonitor()), FocusMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetSlotModule.WidgetSlot()), WidgetSlotModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_scroll()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<FocusMonitorDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new FocusMonitorDemo())); }
}
