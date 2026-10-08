package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.focus.StewardMonitorModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code StewardMonitorDemo}: the house's steward monitor in action - this page's keys as one lamp; asked what the lamp says by its option. */
public record StewardMonitorDemoModule() implements DomModule<StewardMonitorDemoModule> {

    public static final StewardMonitorDemoModule INSTANCE = new StewardMonitorDemoModule();

    public record StewardMonitorDemo() implements SelfContainedWidget<StewardMonitorDemoModule> {
        @Override public String summary() { return "The house's steward monitor in action: this page's keys as one lamp, changing as the page is pressed; asked what the lamp says, and whether an invariant is broken, by its option."; }
    }

    @Override
    public ImportsFor<StewardMonitorDemoModule> imports() {
        return ImportsFor.<StewardMonitorDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StewardMonitorModule.StewardMonitor()), StewardMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<StewardMonitorDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new StewardMonitorDemo())); }
}
