package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.icons.IconModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code IconDemo}: the house's icon in action - stepped through its vocabulary, cleared, and refusing a word it lacks, by its options. */
public record IconDemoModule() implements DomModule<IconDemoModule> {

    public static final IconDemoModule INSTANCE = new IconDemoModule();

    public record IconDemo() implements SelfContainedWidget<IconDemoModule> {
        @Override public String summary() { return "The house's icon in action: one mark stepped through the whole vocabulary, cleared to a blank that keeps its width, and refusing a word the vocabulary lacks."; }
    }

    @Override
    public ImportsFor<IconDemoModule> imports() {
        return ImportsFor.<IconDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new IconModule.Icon()), IconModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row(), new DemoStyles.dm_text()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<IconDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new IconDemo())); }
}
