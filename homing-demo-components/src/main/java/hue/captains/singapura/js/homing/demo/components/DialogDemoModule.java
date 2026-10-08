package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.keyboard.KeyboardStewardModule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.dialog.DialogModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code DialogDemo}: the house's dialog in action - opened modal or not, and closed, by its options; settled by its actions or its keys. */
public record DialogDemoModule() implements DomModule<DialogDemoModule> {

    public static final DialogDemoModule INSTANCE = new DialogDemoModule();

    public record DialogDemo() implements SelfContainedWidget<DialogDemoModule> {
        @Override public String summary() { return "The house's dialog in action: one matter put above the page, opened modal - the page held back - or not, by its options; settled by an action, by Enter for the primary one, or by Escape; closed by call."; }
    }

    @Override
    public ImportsFor<DialogDemoModule> imports() {
        return ImportsFor.<DialogDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DialogModule.Dialog()), DialogModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeyboardStewardModule.KeyboardStewardInstance()), KeyboardStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_text()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<DialogDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DialogDemo())); }
}
