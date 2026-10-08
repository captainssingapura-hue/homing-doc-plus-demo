package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.splitgrid.SplitGridModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code SplitGridDemo}: the house's split grid in action - rooms re-shared by the hand; split, removed, evened out and put back by its options. */
public record SplitGridDemoModule() implements DomModule<SplitGridDemoModule> {

    public static final SplitGridDemoModule INSTANCE = new SplitGridDemoModule();

    public record SplitGridDemo() implements SelfContainedWidget<SplitGridDemoModule> {
        @Override public String summary() { return "The house's split grid in action: rooms in rows and columns, re-shared by dragging a line; a room split, a room removed - the last cannot go - the rooms evened out, and put back as they began, by its options."; }
    }

    @Override
    public ImportsFor<SplitGridDemoModule> imports() {
        return ImportsFor.<SplitGridDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SplitGridModule.SplitGrid()), SplitGridModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_host(), new DemoStyles.dm_text()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<SplitGridDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new SplitGridDemo())); }
}
