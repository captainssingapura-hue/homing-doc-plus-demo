package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.preferences.ListMasterWidgetModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.ArrayList;
import java.util.List;

/** {@code ListMasterWidgetDemo}: the house's list master in action - settings listed by where they belong, chosen by a press or the keys. */
public record ListMasterWidgetDemoModule() implements DomModule<ListMasterWidgetDemoModule> {

    public static final ListMasterWidgetDemoModule INSTANCE = new ListMasterWidgetDemoModule();

    public record ListMasterWidgetDemo() implements SelfContainedWidget<ListMasterWidgetDemoModule>, NeedKeyboard {
        @Override public String summary() { return "The house's list master in action: a site's settings, listed by where they belong, depth said as indentation; a row chosen by a press, or moved and chosen by the arrows, Home and End."; }
        @Override public List<KeyBinding> keys() {
            var keys = new ArrayList<KeyBinding>(ListMasterWidgetModule.ListMasterWidget.KEYS);
            if (keys.stream().noneMatch(k -> k.key() == Key.ESCAPE)) keys.add(KeyBinding.of(Key.ESCAPE, "the keys given back"));
            return List.copyOf(keys);
        }
    }

    @Override
    public ImportsFor<ListMasterWidgetDemoModule> imports() {
        return ImportsFor.<ListMasterWidgetDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ListMasterWidgetModule.ListMasterWidget()), ListMasterWidgetModule.INSTANCE))
                .build();
    }

    @Override public ExportsOf<ListMasterWidgetDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ListMasterWidgetDemo())); }
}
