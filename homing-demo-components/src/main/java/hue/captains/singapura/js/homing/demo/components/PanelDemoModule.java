package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.PanelModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code PanelDemo}: the house's panel in action - one concern, named, filling its box; made current and raised by its options. */
public record PanelDemoModule() implements DomModule<PanelDemoModule> {

    public static final PanelDemoModule INSTANCE = new PanelDemoModule();

    public record PanelDemo() implements SelfContainedWidget<PanelDemoModule> {
        @Override public String summary() { return "The house's panel in action: one concern, named, filling the box it is put in; made the current one, and raised off its plane, by its options."; }
    }

    @Override
    public ImportsFor<PanelDemoModule> imports() {
        return ImportsFor.<PanelDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PanelModule.PanelBuilder()), PanelModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_host(), new DemoStyles.dm_text()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<PanelDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PanelDemo())); }
}
