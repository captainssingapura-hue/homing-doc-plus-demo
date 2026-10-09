package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.DangerButtonModule;
import hue.captains.singapura.js.homing.ui.elements.PlainButtonModule;
import hue.captains.singapura.js.homing.ui.elements.PrimaryButtonModule;
import hue.captains.singapura.js.homing.ui.elements.SecondaryButtonModule;
import hue.captains.singapura.js.homing.ui.elements.SuccessButtonModule;
import hue.captains.singapura.js.homing.ui.elements.ToggleButtonModule;
import hue.captains.singapura.js.homing.ui.elements.WarningButtonModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code ButtonDemo}: one of the house's seven buttons in action - pressed and counted, or turned over; switched, sized and coloured by its options. */
public record ButtonDemoModule() implements DomModule<ButtonDemoModule> {

    public static final ButtonDemoModule INSTANCE = new ButtonDemoModule();

    public record ButtonDemo() implements SelfContainedWidget<ButtonDemoModule> {
        @Override public String summary() { return "One of the house's seven buttons, as its leaf says, in action: pressed and counted, or a toggle turned over; switched off and inert, sized, and a coloured one's colour set, by its options."; }
    }

    @Override
    public ImportsFor<ButtonDemoModule> imports() {
        return ImportsFor.<ButtonDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlainButtonModule.PlainButton()), PlainButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PrimaryButtonModule.PrimaryButton()), PrimaryButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SecondaryButtonModule.SecondaryButton()), SecondaryButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DangerButtonModule.DangerButton()), DangerButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WarningButtonModule.WarningButton()), WarningButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SuccessButtonModule.SuccessButton()), SuccessButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ToggleButtonModule.ToggleButton()), ToggleButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<ButtonDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ButtonDemo())); }
}
