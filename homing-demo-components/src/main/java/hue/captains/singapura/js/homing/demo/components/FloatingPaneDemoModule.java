package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.floating.FloatingPaneModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code FloatingPaneDemo}: the house's floating pane in action - moved and sized by the hand; moved, sized, ringed and opened again by its options. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class,
        reason = "It makes its own elements, places elements, wears classes of its own, writes text of its own and touches components' elements: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record FloatingPaneDemoModule() implements DomModule<FloatingPaneDemoModule> {

    public static final FloatingPaneDemoModule INSTANCE = new FloatingPaneDemoModule();

    public record FloatingPaneDemo() implements SelfContainedWidget<FloatingPaneDemoModule> {
        @Override public String summary() { return "The house's floating pane in action: content in a window of its own over a box, moved by its head and sized by its grip, clamped to the box; moved, sized, ringed and opened again by its options; closed by its cross, which asks its holder."; }
    }

    @Override
    public ImportsFor<FloatingPaneDemoModule> imports() {
        return ImportsFor.<FloatingPaneDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new FloatingPaneModule.FloatingPane()), FloatingPaneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_host(), new DemoStyles.dm_text()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<FloatingPaneDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new FloatingPaneDemo())); }
}
