package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.components.ComponentDemoLogModule;
import hue.captains.singapura.js.homing.demo.components.HouseDemosModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code ComponentDemoPanel}: the picked component in action - its demo, a widget of its own, mounted for each pick and handed the parties. */
public record ComponentDemoPanelModule() implements DomModule<ComponentDemoPanelModule> {

    public static final ComponentDemoPanelModule INSTANCE = new ComponentDemoPanelModule();

    public record ComponentDemoPanel() implements SelfContainedWidget<ComponentDemoPanelModule>, NeedKeyboard {
        @Override public String summary() { return "The picked component in action: its demo - a widget of its own, the component built live - mounted for each pick, grafted, and handed the parties that control it and log it. A leaf with no demo says where it is shown, or what it means."; }
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.ESCAPE, "the keys given back, when the demo gave them up")); }
    }

    @Override
    public ImportsFor<ComponentDemoPanelModule> imports() {
        return ImportsFor.<ComponentDemoPanelModule>builder()
                .add(new ModuleImports<>(List.of(new TaxonomyWidgetModule.TaxonomyWidget()), TaxonomyWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new HouseDemosModule.HOUSE_DEMOS(), new HouseDemosModule.HOUSE_AROUND(),
                        new HouseDemosModule.HOUSE_PENDING(), new HouseDemosModule.HOUSE_UNREALIZED()), HouseDemosModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ComponentDemoLogModule.COMPONENT_DEMO_LOG()), ComponentDemoLogModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyStyles.tx_title(), new TaxonomyStyles.tx_hint(), new TaxonomyStyles.tx_slot()),
                        TaxonomyStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<ComponentDemoPanelModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ComponentDemoPanel())); }
}
