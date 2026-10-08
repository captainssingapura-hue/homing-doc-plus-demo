package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.splitgrid.SplitGridMirrorModule;
import hue.captains.singapura.js.homing.ui.splitgrid.SplitGridModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.ArrayList;
import java.util.List;

/** {@code SplitGridMirrorDemo}: the house's split-grid mirror in action - a grid drawn small, its cursor moved by press or keys; the grid arranged by its options at the cursor. */
public record SplitGridMirrorDemoModule() implements DomModule<SplitGridMirrorDemoModule> {

    public static final SplitGridMirrorDemoModule INSTANCE = new SplitGridMirrorDemoModule();

    public record SplitGridMirrorDemo() implements SelfContainedWidget<SplitGridMirrorDemoModule>, NeedKeyboard {
        @Override public String summary() { return "The house's split-grid mirror in action: a grid drawn small beside it, its cursor moved by a press or by the arrows; the cursor's room split or removed, the rooms evened out, and put back, by its options - every change reflected."; }
        @Override public List<KeyBinding> keys() {
            var keys = new ArrayList<KeyBinding>(SplitGridMirrorModule.SplitGridMirror.KEYS);
            if (keys.stream().noneMatch(k -> k.key() == Key.ESCAPE)) keys.add(KeyBinding.of(Key.ESCAPE, "the keys given back"));
            return List.copyOf(keys);
        }
    }

    @Override
    public ImportsFor<SplitGridMirrorDemoModule> imports() {
        return ImportsFor.<SplitGridMirrorDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SplitGridMirrorModule.SplitGridMirror()), SplitGridMirrorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SplitGridModule.SplitGrid()), SplitGridModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row(), new DemoStyles.dm_host(), new DemoStyles.dm_text()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<SplitGridMirrorDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new SplitGridMirrorDemo())); }
}
