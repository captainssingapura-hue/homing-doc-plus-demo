package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.SliderGroupModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.ArrayList;
import java.util.List;

/** {@code SliderGroupDemo}: the house's slider group in action - three faders holding the keys as one. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 1,
        reason = "It places elements: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record SliderGroupDemoModule() implements DomModule<SliderGroupDemoModule> {

    public static final SliderGroupDemoModule INSTANCE = new SliderGroupDemoModule();

    public record SliderGroupDemo() implements SelfContainedWidget<SliderGroupDemoModule>, NeedKeyboard {
        @Override public String summary() { return "The house's slider group in action: three faders under one heading, holding the keys as one - Tab between them, the arrows on the current one - each value set said."; }
        @Override public List<KeyBinding> keys() {
            var keys = new ArrayList<>(new SliderGroupModule.SliderGroup().keys());
            if (keys.stream().noneMatch(k -> k.key() == Key.ESCAPE)) keys.add(KeyBinding.of(Key.ESCAPE, "the keys given back"));
            return List.copyOf(keys);
        }
    }

    @Override
    public ImportsFor<SliderGroupDemoModule> imports() {
        return ImportsFor.<SliderGroupDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderGroupModule.SliderGroup()), SliderGroupModule.INSTANCE))
                .build();
    }

    @Override public ExportsOf<SliderGroupDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new SliderGroupDemo())); }
}
