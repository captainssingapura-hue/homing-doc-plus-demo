package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.components.ComponentDemoLogModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code ComponentDemoLog}: what the demos did, newest first - a module of its own, so a page can take it apart from the control log. */
public record DemoLogViewModule() implements DomModule<DemoLogViewModule> {

    public static final DemoLogViewModule INSTANCE = new DemoLogViewModule();

    public record ComponentDemoLog() implements SelfContainedWidget<DemoLogViewModule>, NeedKeyboard {
        @Override public String summary() { return "What the demos did, newest first: the demo log, cleared by the demo panel as it mounts a demo, and here by a Clear of its own that empties this view alone."; }
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.ESCAPE, "the keys given back")); }
    }

    @Override
    public ImportsFor<DemoLogViewModule> imports() {
        return ImportsFor.<DemoLogViewModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentLogViewModule.ComponentLogView()), ComponentLogViewModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ComponentDemoLogModule.COMPONENT_DEMO_LOG()), ComponentDemoLogModule.INSTANCE))
                .build();
    }

    @Override public ExportsOf<DemoLogViewModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ComponentDemoLog())); }
}
