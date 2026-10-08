package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.ArrayList;
import java.util.List;

/** {@code SliderDemo}: the house's slider in action - a value on a scale, moved by the hand or the keys; switched off by its option. */
public record SliderDemoModule() implements DomModule<SliderDemoModule> {

    public static final SliderDemoModule INSTANCE = new SliderDemoModule();

    public record SliderDemo() implements SelfContainedWidget<SliderDemoModule>, NeedKeyboard {
        @Override public String summary() { return "The house's slider in action: a value on a scale with ticks, figures and a detent, read out as it moves - dragged, pressed on its rail, moved by the keys - and switched off by its option."; }
        @Override public List<KeyBinding> keys() {
            var keys = new ArrayList<>(SliderModule.Slider.KEYS);
            keys.add(KeyBinding.of(Key.ESCAPE, "the keys given back"));
            return List.copyOf(keys);
        }
    }

    @Override
    public ImportsFor<SliderDemoModule> imports() {
        return ImportsFor.<SliderDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.SliderBuilder()), SliderModule.INSTANCE))
                .build();
    }

    @Override public ExportsOf<SliderDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new SliderDemo())); }
}
