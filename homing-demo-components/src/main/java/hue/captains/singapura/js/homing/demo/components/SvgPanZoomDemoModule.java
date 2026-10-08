package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.panzoom.SvgPanZoomModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.ArrayList;
import java.util.List;

/** {@code SvgPanZoomDemo}: the house's pan-zoom view in action - a drawing zoomed by the wheel, a pinch or the keys; zoomed and fitted by its options. */
public record SvgPanZoomDemoModule() implements DomModule<SvgPanZoomDemoModule> {

    public static final SvgPanZoomDemoModule INSTANCE = new SvgPanZoomDemoModule();

    public record SvgPanZoomDemo() implements SelfContainedWidget<SvgPanZoomDemoModule>, NeedKeyboard {
        @Override public String summary() { return "The house's pan-zoom view in action: a drawing fitted to its box, zoomed about the pointer by the wheel with Ctrl or ⌘ or by a pinch, panned once zoomed in, + and − and 0 and the arrows by the keys; zoomed in, out and fitted by its options."; }
        @Override public List<KeyBinding> keys() {
            var keys = new ArrayList<KeyBinding>(SvgPanZoomModule.SvgPanZoom.KEYS);
            if (keys.stream().noneMatch(k -> k.key() == Key.ESCAPE)) keys.add(KeyBinding.of(Key.ESCAPE, "the keys given back"));
            return List.copyOf(keys);
        }
    }

    @Override
    public ImportsFor<SvgPanZoomDemoModule> imports() {
        return ImportsFor.<SvgPanZoomDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SvgPanZoomModule.SvgPanZoom()), SvgPanZoomModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoDrawingsModule.DEMO_DRAWINGS()), DemoDrawingsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_host(), new DemoStyles.dm_fill()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<SvgPanZoomDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new SvgPanZoomDemo())); }
}
