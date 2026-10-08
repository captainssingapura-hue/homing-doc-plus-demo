package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.keyboard.KeyboardStewardModule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.menu.ContextMenuStewardModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code ContextMenuStewardDemo}: the house's context-menu steward in action - one menu active at a time; asked what is open by its option. */
public record ContextMenuStewardDemoModule() implements DomModule<ContextMenuStewardDemoModule> {

    public static final ContextMenuStewardDemoModule INSTANCE = new ContextMenuStewardDemoModule();

    public record ContextMenuStewardDemo() implements SelfContainedWidget<ContextMenuStewardDemoModule> {
        @Override public String summary() { return "The house's context-menu steward in action: the page's one, keeping every menu of the page, one active at a time; asked what is open by its option. On a page that has its steward already, it says so, and where to see that one at work."; }
    }

    @Override
    public ImportsFor<ContextMenuStewardDemoModule> imports() {
        return ImportsFor.<ContextMenuStewardDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ContextMenuStewardModule.ContextMenuSteward()), ContextMenuStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeyboardStewardModule.KeyboardStewardInstance()), KeyboardStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row(), new DemoStyles.dm_host(), new DemoStyles.dm_text()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<ContextMenuStewardDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ContextMenuStewardDemo())); }
}
