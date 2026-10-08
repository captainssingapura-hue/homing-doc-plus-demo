package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.Elements;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code ButtonDemo}: one of the house's six buttons in action - pressed and counted; switched, sized and coloured by its options. */
public record ButtonDemoModule() implements DomModule<ButtonDemoModule> {

    public static final ButtonDemoModule INSTANCE = new ButtonDemoModule();

    public record ButtonDemo() implements SelfContainedWidget<ButtonDemoModule> {
        @Override public String summary() { return "One of the house's six buttons, as its leaf says, in action: pressed and counted; switched off and inert, sized, and a coloured one's colour set, by its options."; }
    }

    @Override
    public ImportsFor<ButtonDemoModule> imports() {
        return ImportsFor.<ButtonDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.ButtonBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<ButtonDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ButtonDemo())); }
}
