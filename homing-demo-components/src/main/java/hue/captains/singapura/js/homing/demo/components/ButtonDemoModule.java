package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.StatesAndEventsModule;
import hue.captains.singapura.js.homing.ui.elements.CloseButtonModule;
import hue.captains.singapura.js.homing.ui.elements.DangerButtonModule;
import hue.captains.singapura.js.homing.ui.elements.IconButtonModule;
import hue.captains.singapura.js.homing.ui.elements.PlainButtonModule;
import hue.captains.singapura.js.homing.ui.elements.PrimaryButtonModule;
import hue.captains.singapura.js.homing.ui.elements.SecondaryButtonModule;
import hue.captains.singapura.js.homing.ui.elements.SuccessButtonModule;
import hue.captains.singapura.js.homing.ui.elements.ToggleButtonModule;
import hue.captains.singapura.js.homing.ui.elements.WarningButtonModule;
import hue.captains.singapura.js.homing.ui.icons.IconModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code ButtonDemo}: one of the house's nine buttons in action - pressed and counted, or turned over; switched, sized and coloured by its options. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class,
        reason = "It makes its own elements, places elements and touches components' elements: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record ButtonDemoModule() implements DomModule<ButtonDemoModule> {

    public static final ButtonDemoModule INSTANCE = new ButtonDemoModule();

    public record ButtonDemo() implements SelfContainedWidget<ButtonDemoModule> {
        @Override public String summary() { return "One of the house's nine buttons, as its leaf says, in action: pressed and counted, or a toggle turned over; switched off and inert, sized, and a coloured one's colour set, by its options."; }
    }

    @Override
    public ImportsFor<ButtonDemoModule> imports() {
        return ImportsFor.<ButtonDemoModule>builder()
                .add(new ModuleImports<>(List.of(new StatesAndEventsModule.STATE(), new StatesAndEventsModule.EVENT()), StatesAndEventsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlainButtonModule.PlainButton(), new PlainButtonModule.PlainButtonFactory()), PlainButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PrimaryButtonModule.PrimaryButton(), new PrimaryButtonModule.PrimaryButtonFactory()), PrimaryButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SecondaryButtonModule.SecondaryButton(), new SecondaryButtonModule.SecondaryButtonFactory()), SecondaryButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DangerButtonModule.DangerButton(), new DangerButtonModule.DangerButtonFactory()), DangerButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WarningButtonModule.WarningButton(), new WarningButtonModule.WarningButtonFactory()), WarningButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SuccessButtonModule.SuccessButton(), new SuccessButtonModule.SuccessButtonFactory()), SuccessButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ToggleButtonModule.ToggleButton(), new ToggleButtonModule.ToggleButtonFactory()), ToggleButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CloseButtonModule.CloseButton(), new CloseButtonModule.CloseButtonFactory()), CloseButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new IconButtonModule.IconButton(), new IconButtonModule.IconButtonFactory()), IconButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new IconModule.Icon()), IconModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<ButtonDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ButtonDemo())); }
}
