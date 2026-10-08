package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.EdgeStripModule;
import hue.captains.singapura.js.homing.ui.elements.Elements;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code EdgeStripDemo}: the house's edge strip in action - tools at a box's foot, shown by the hand, held shown by its option. */
public record EdgeStripDemoModule() implements DomModule<EdgeStripDemoModule> {

    public static final EdgeStripDemoModule INSTANCE = new EdgeStripDemoModule();

    public record EdgeStripDemo() implements SelfContainedWidget<EdgeStripDemoModule> {
        @Override public String summary() { return "The house's edge strip in action: a box whose tools wait at its foot, shown when the hand reaches the lip and gone after it leaves; held shown, and let go, by its option."; }
    }

    @Override
    public ImportsFor<EdgeStripDemoModule> imports() {
        return ImportsFor.<EdgeStripDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new EdgeStripModule.EdgeStripBuilder()), EdgeStripModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.ButtonBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_host(), new DemoStyles.dm_text()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<EdgeStripDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new EdgeStripDemo())); }
}
