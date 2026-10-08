package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.components.ComponentControlLogModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code ComponentControlLog}: what was set, done and asked, newest first - a module of its own, as the demo log's is. */
public record ControlLogViewModule() implements DomModule<ControlLogViewModule> {

    public static final ControlLogViewModule INSTANCE = new ControlLogViewModule();

    public record ComponentControlLog() implements SelfContainedWidget<ControlLogViewModule>, NeedKeyboard {
        @Override public String summary() { return "What was set, done and asked, newest first: the control log, cleared by the control panel as the control type changes, and here by a Clear of its own that empties this view alone."; }
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.ESCAPE, "the keys given back")); }
    }

    @Override
    public ImportsFor<ControlLogViewModule> imports() {
        return ImportsFor.<ControlLogViewModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentLogViewModule.ComponentLogView()), ComponentLogViewModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ComponentControlLogModule.COMPONENT_CONTROL_LOG()), ComponentControlLogModule.INSTANCE))
                .build();
    }

    @Override public ExportsOf<ControlLogViewModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ComponentControlLog())); }
}
