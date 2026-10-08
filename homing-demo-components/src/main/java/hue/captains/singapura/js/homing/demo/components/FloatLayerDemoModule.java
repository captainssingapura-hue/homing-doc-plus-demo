package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.floating.FloatLayerModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code FloatLayerDemo}: the house's float layer in action - notes cascading and stacking; opened, and the one in front closed, by its options. */
public record FloatLayerDemoModule() implements DomModule<FloatLayerDemoModule> {

    public static final FloatLayerDemoModule INSTANCE = new FloatLayerDemoModule();

    public record FloatLayerDemo() implements SelfContainedWidget<FloatLayerDemoModule>, NeedKeyboard {
        @Override public String summary() { return "The house's float layer in action: notes over a box that cascade and stack, one raised by a press; a note opened, and the one in front closed, by its options; Escape closes the one in front."; }
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.ESCAPE, "the note in front closed, handed on to the layer; with none open, the keys given back")); }
    }

    @Override
    public ImportsFor<FloatLayerDemoModule> imports() {
        return ImportsFor.<FloatLayerDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new FloatLayerModule.FloatLayer()), FloatLayerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_host(), new DemoStyles.dm_text()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<FloatLayerDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new FloatLayerDemo())); }
}
