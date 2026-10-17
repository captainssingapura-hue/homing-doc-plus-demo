package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.ui.icons.IconRoomModule;
import hue.captains.singapura.js.homing.ui.look.IconsModule;
import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.icons.IconModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code IconDemo}: an icon of the house's in action (D52) - mounted in a room as an owner mounts it, another mounted in its place, cleared, and one the instance lacks refused, by its options. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 4,
        reason = "It makes its own elements, places elements and writes text of its own - its icon mounted by a room, as an owner mounts one: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record IconDemoModule() implements DomModule<IconDemoModule> {

    public static final IconDemoModule INSTANCE = new IconDemoModule();

    public record IconDemo() implements SelfContainedWidget<IconDemoModule> {
        @Override public String summary() { return "An icon of the house's in action: mounted as an owner mounts it, its picture the style's; another mounted in its place, cleared, and one the instance lacks refused."; }
    }

    @Override
    public ImportsFor<IconDemoModule> imports() {
        return ImportsFor.<IconDemoModule>builder()
                .add(new ModuleImports<>(List.of(new IconsModule.ICON()), IconsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new IconRoomModule.IconRoom()), IconRoomModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new IconModule.Icon()), IconModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row(), new DemoStyles.dm_text()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<IconDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new IconDemo())); }
}
