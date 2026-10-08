package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.focusParties;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParties;
import hue.captains.singapura.js.homing.ui.controlpanel.ControlsModule;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/** {@code ComponentDemo}: what every demo is to the panel that holds it, said once - its parties, its keys, the options it answers. */
public record ComponentDemoModule() implements DomModule<ComponentDemoModule> {

    public static final ComponentDemoModule INSTANCE = new ComponentDemoModule();

    public record ComponentDemo() implements Exportable._Class<ComponentDemoModule> {}

    @Override
    public ImportsFor<ComponentDemoModule> imports() {
        return ImportsFor.<ComponentDemoModule>builder()
                .add(new ModuleImports<>(List.of(new domOpsParties()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParties()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ControlsModule.Controls()), ControlsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ComponentControlModule.COMPONENT_CONTROL()), ComponentControlModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ComponentDemoLogModule.COMPONENT_DEMO_LOG()), ComponentDemoLogModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_fill()), WidgetStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_stage()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<ComponentDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ComponentDemo())); }
}
