package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.split.SplitPaneModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code SplitPaneDemo}: the house's split pane in action - regions re-shared by the hand; evened out and put back by its options. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class,
        reason = "It touches components' elements: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record SplitPaneDemoModule() implements DomModule<SplitPaneDemoModule> {

    public static final SplitPaneDemoModule INSTANCE = new SplitPaneDemoModule();

    public record SplitPaneDemo() implements SelfContainedWidget<SplitPaneDemoModule> {
        @Override public String summary() { return "The house's split pane in action: regions sharing their room by ratio, re-shared by dragging a divider; evened out and put back as they began by its options. Its layout is its owner's: it never splits a region, nor removes one."; }
    }

    @Override
    public ImportsFor<SplitPaneDemoModule> imports() {
        return ImportsFor.<SplitPaneDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SplitPaneModule.SplitPane()), SplitPaneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_host(), new DemoStyles.dm_text()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<SplitPaneDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new SplitPaneDemo())); }
}
