package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.menu.ContextMenuModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.ArrayList;
import java.util.List;

/** {@code ContextMenuDemo}: the house's context menu in action - shown where the user asks, walked by the keys; opened and closed by its options. */
public record ContextMenuDemoModule() implements DomModule<ContextMenuDemoModule> {

    public static final ContextMenuDemoModule INSTANCE = new ContextMenuDemoModule();

    public record ContextMenuDemo() implements SelfContainedWidget<ContextMenuDemoModule>, NeedKeyboard {
        @Override public String summary() { return "The house's context menu in action: the choices that apply to a document, shown where the user right-presses, or opened from the keyboard by its option, its first row lit; walked by the keys; picked; hidden by Escape or closed by call. Beside it, the same menu shown still."; }
        @Override public List<KeyBinding> keys() {
            var keys = new ArrayList<KeyBinding>();
            keys.addAll(KeyBinding.each("the cursor along the rows", Key.ARROW_UP, Key.ARROW_DOWN, Key.HOME, Key.END));
            keys.addAll(KeyBinding.each("into a row's rows, or the row picked", Key.ARROW_RIGHT, Key.ENTER, Key.SPACE));
            keys.add(KeyBinding.of(Key.ARROW_LEFT, "a level back"));
            keys.add(KeyBinding.of(Key.ESCAPE, "the menu hidden; with none shown, the keys given back"));
            return List.copyOf(keys);
        }
    }

    @Override
    public ImportsFor<ContextMenuDemoModule> imports() {
        return ImportsFor.<ContextMenuDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ContextMenuModule.ContextMenu()), ContextMenuModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_host(), new DemoStyles.dm_text(), new DemoStyles.dm_layer(), new DemoStyles.dm_frames()),
                        DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<ContextMenuDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ContextMenuDemo())); }
}
