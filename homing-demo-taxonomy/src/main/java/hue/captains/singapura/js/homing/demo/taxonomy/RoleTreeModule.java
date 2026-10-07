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

/** {@code RoleTree}: the house's role catalogue in a relation tree, and the uses of the role the cursor is on; a view, not a picker. */
public record RoleTreeModule() implements DomModule<RoleTreeModule> {

    public static final RoleTreeModule INSTANCE = new RoleTreeModule();

    public record RoleTree() implements SelfContainedWidget<RoleTreeModule>, NeedKeyboard {
        @Override public String summary() { return "The house's role catalogue, studied on its own: the root, the branches by what their roles do for their owner, the roles at the leaves; under the tree, the uses of the role the cursor is on - every component that names it, what plays it there, how many. The cursor picks nothing; a part picked anywhere brings it to its role."; }
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.ESCAPE, "the keys given back, when the tree did not take it")); }
    }

    @Override
    public ImportsFor<RoleTreeModule> imports() {
        return ImportsFor.<RoleTreeModule>builder()
                .add(new ModuleImports<>(List.of(new TaxonomyWidgetModule.TaxonomyWidget()), TaxonomyWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelTreeModule.RelTree()), RelTreeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelTreeStockCellsModule.RelTreeTextCell()), RelTreeStockCellsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridProtocolModule.RelTreeView(), new RelGridProtocolModule.RelTreeUnfold(),
                        new RelGridProtocolModule.RelTreeFold()), RelGridProtocolModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyStyles.tx_hint(), new TaxonomyStyles.tx_port(), new TaxonomyStyles.tx_panel(),
                        new TaxonomyStyles.tx_title(), new TaxonomyStyles.tx_tag(), new TaxonomyStyles.tx_line(), new TaxonomyStyles.tx_code()),
                        TaxonomyStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<RoleTreeModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new RoleTree())); }
}
