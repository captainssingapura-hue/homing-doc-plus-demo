package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.relgrid.RelGridModule;
import hue.captains.singapura.js.homing.relgrid.RelGridStockCellsModule;
import hue.captains.singapura.js.homing.relgrid.RelGridStyles;
import hue.captains.singapura.js.homing.relgrid.protocol.RelGridProtocolModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code PartsTable}: the parts of the house's components, a row each, in a relation grid - what it shows follows the pick; the row the cursor is on is picked. */
public record PartsTableModule() implements DomModule<PartsTableModule> {

    public static final PartsTableModule INSTANCE = new PartsTableModule();

    public record PartsTable() implements SelfContainedWidget<PartsTableModule>, NeedKeyboard {
        @Override public String summary() { return "The parts of the house's components, a row each: the component that names it, the role, the component that plays it, its token. The root shows every part, a kind those its components name, a component its own; the row the cursor is on is picked."; }
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.ESCAPE, "the keys given back, when the grid did not take it")); }
    }

    @Override
    public ImportsFor<PartsTableModule> imports() {
        return ImportsFor.<PartsTableModule>builder()
                .add(new ModuleImports<>(List.of(new TaxonomyWidgetModule.TaxonomyWidget()), TaxonomyWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridModule.RelGrid()), RelGridModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridStockCellsModule.RelGridTextCell()), RelGridStockCellsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridProtocolModule.RelGridViewChanged()), RelGridProtocolModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridStyles.hrg_frame(), new RelGridStyles.hrg_lit()), RelGridStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyStyles.tx_hint(), new TaxonomyStyles.tx_frame(), new TaxonomyStyles.tx_port()), TaxonomyStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PartsTableModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PartsTable())); }
}
