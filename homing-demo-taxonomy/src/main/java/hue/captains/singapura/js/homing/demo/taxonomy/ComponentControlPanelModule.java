package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.components.ComponentControlModule;
import hue.captains.singapura.js.homing.demo.components.ComponentControlLogModule;
import hue.captains.singapura.js.homing.ui.controlpanel.ControlPanelModule;
import hue.captains.singapura.js.homing.ui.controlpanel.ControlsModule;
import hue.captains.singapura.js.homing.ui.elements.Elements;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.ArrayList;
import java.util.List;

/** {@code ComponentControlPanel}: what the picked component is controlled by - its control type found, its panel mounted, what is set kept by the party. */
public record ComponentControlPanelModule() implements DomModule<ComponentControlPanelModule> {

    public static final ComponentControlPanelModule INSTANCE = new ComponentControlPanelModule();

    public record ComponentControlPanel() implements SelfContainedWidget<ComponentControlPanelModule>, NeedKeyboard {
        @Override public String summary() { return "What the picked component is controlled by: its control type found, and a control for each option - a slider for each axis, a toggle for each switch, a button for each action or question - set through the party, which keeps what is set across components; what was set, logged."; }
        @Override public List<KeyBinding> keys() {
            var keys = new ArrayList<KeyBinding>(SliderModule.Slider.KEYS);
            keys.add(KeyBinding.of(Key.ESCAPE, "the keys given back"));
            return List.copyOf(keys);
        }
    }

    @Override
    public ImportsFor<ComponentControlPanelModule> imports() {
        return ImportsFor.<ComponentControlPanelModule>builder()
                .add(new ModuleImports<>(List.of(new TaxonomyWidgetModule.TaxonomyWidget()), TaxonomyWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ControlsModule.Controls()), ControlsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ControlPanelModule.ControlPanel()), ControlPanelModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.ButtonBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new ComponentControlModule.COMPONENT_CONTROL()), ComponentControlModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ComponentControlLogModule.COMPONENT_CONTROL_LOG()), ComponentControlLogModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyStyles.tx_title(), new TaxonomyStyles.tx_hint(), new TaxonomyStyles.tx_line(),
                        new TaxonomyStyles.tx_tag(), new TaxonomyStyles.tx_code(), new TaxonomyStyles.tx_scroll()), TaxonomyStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<ComponentControlPanelModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ComponentControlPanel())); }
}
