package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.panes.AddTabModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code AddTabDemo}: the house's add-tab control in action - where, what and how, then one button. */
public record AddTabDemoModule() implements DomModule<AddTabDemoModule> {

    public static final AddTabDemoModule INSTANCE = new AddTabDemoModule();

    public record AddTabDemo() implements SelfContainedWidget<AddTabDemoModule> {
        @Override public String summary() { return "The house's add-tab control in action: where - a picture of the panes, pointed at - what, and how it arrives, then one button; it asks whether a pane will take another, and never decides for you."; }
    }

    @Override
    public ImportsFor<AddTabDemoModule> imports() {
        return ImportsFor.<AddTabDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoDocksModule.DemoDocks()), DemoDocksModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new AddTabModule.AddTab()), AddTabModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row(), new DemoStyles.dm_host()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<AddTabDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new AddTabDemo())); }
}
