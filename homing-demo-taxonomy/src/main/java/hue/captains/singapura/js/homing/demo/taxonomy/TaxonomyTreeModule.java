package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.relgrid.protocol.RelGridProtocolModule;
import hue.captains.singapura.js.homing.reltree.RelTreeModule;
import hue.captains.singapura.js.homing.reltree.RelTreeStockCellsModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code TaxonomyTree}: the taxonomy in a relation tree - the root, the kinds, the components at its leaves, each component's parts under it; the node the cursor is on is picked. */
public record TaxonomyTreeModule() implements DomModule<TaxonomyTreeModule> {

    public static final TaxonomyTreeModule INSTANCE = new TaxonomyTreeModule();

    public record TaxonomyTree() implements SelfContainedWidget<TaxonomyTreeModule>, NeedKeyboard {
        @Override public String summary() { return "The house's taxonomy as the tree it is: each kind under its parent up to the root, with how many components are under it; each component a leaf under its kind, its parts - the roles it names - under it. The node the cursor is on is picked, and the chain it falls back along is lit."; }
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.ESCAPE, "the keys given back, when the tree did not take it")); }
    }

    @Override
    public ImportsFor<TaxonomyTreeModule> imports() {
        return ImportsFor.<TaxonomyTreeModule>builder()
                .add(new ModuleImports<>(List.of(new TaxonomyWidgetModule.TaxonomyWidget()), TaxonomyWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelTreeModule.RelTree()), RelTreeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelTreeStockCellsModule.RelTreeTextCell()), RelTreeStockCellsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridProtocolModule.RelTreeView(), new RelGridProtocolModule.RelTreeUnfold(),
                        new RelGridProtocolModule.RelTreeFold(), new RelGridProtocolModule.RelTreeViewChanged()), RelGridProtocolModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyStyles.tx_hint(), new TaxonomyStyles.tx_port(), new TaxonomyStyles.tx_on()), TaxonomyStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<TaxonomyTreeModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new TaxonomyTree())); }
}
