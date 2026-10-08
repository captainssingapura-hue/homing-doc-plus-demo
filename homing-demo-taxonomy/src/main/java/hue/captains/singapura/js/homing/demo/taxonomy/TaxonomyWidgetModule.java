package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.focusParties;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParties;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/** {@code TaxonomyWidget}: what every widget of the taxonomy workbench is to its host, said once - its parties, its keys, the pick it shares. */
public record TaxonomyWidgetModule() implements DomModule<TaxonomyWidgetModule> {

    public static final TaxonomyWidgetModule INSTANCE = new TaxonomyWidgetModule();

    public record TaxonomyWidget() implements Exportable._Class<TaxonomyWidgetModule> {}

    @Override
    public ImportsFor<TaxonomyWidgetModule> imports() {
        return ImportsFor.<TaxonomyWidgetModule>builder()
                .add(new ModuleImports<>(List.of(new domOpsParties()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParties()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new NodeSelectionModule.NODE_SELECTION()), NodeSelectionModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyIndexModule.TaxonomyIndex()), TaxonomyIndexModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_fill()), WidgetStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyStyles.tx_root(), new TaxonomyStyles.tx_link()), TaxonomyStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<TaxonomyWidgetModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new TaxonomyWidget())); }
}
