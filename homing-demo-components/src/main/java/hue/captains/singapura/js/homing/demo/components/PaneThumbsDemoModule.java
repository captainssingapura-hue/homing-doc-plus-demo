package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.panes.PaneThumbsModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code PaneThumbsDemo}: the house's pane thumbnails in action - the regions pictured small, pointed at; the regions arranged by its options, the picture following. */
public record PaneThumbsDemoModule() implements DomModule<PaneThumbsDemoModule> {

    public static final PaneThumbsDemoModule INSTANCE = new PaneThumbsDemoModule();

    public record PaneThumbsDemo() implements SelfContainedWidget<PaneThumbsDemoModule> {
        @Override public String summary() { return "The house's pane thumbnails in action: the regions of a room pictured small, one chosen by pointing at it, one shown and refused; a region split or removed, and the room put back, by its options - the picture following."; }
    }

    @Override
    public ImportsFor<PaneThumbsDemoModule> imports() {
        return ImportsFor.<PaneThumbsDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoDocksModule.DemoDocks()), DemoDocksModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PaneThumbsModule.PaneThumbs()), PaneThumbsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row(), new DemoStyles.dm_host()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<PaneThumbsDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PaneThumbsDemo())); }
}
