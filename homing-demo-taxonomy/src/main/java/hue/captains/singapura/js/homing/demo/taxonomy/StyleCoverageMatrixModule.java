package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.relgrid.RelGridModule;
import hue.captains.singapura.js.homing.relgrid.protocol.RelGridProtocolModule;
import hue.captains.singapura.js.homing.relgrid.house.HouseGridModule;
import hue.captains.singapura.js.homing.relgrid.cells.RelGridStockCellsModule;
import hue.captains.singapura.js.homing.relgrid.cells.RelGridLabelsModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/**
 * {@code StyleCoverageMatrix}: how each house style draws each casting, a row a casting and a column a style, Base
 * first, in a relation grid - the matrix read from the style manager on the Java side ({@link StyleCoverageModule}); a
 * view of the pick, never a picker.
 */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 4,
        reason = "It wears classes of its own, writes text of its own and touches components' elements: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record StyleCoverageMatrixModule() implements DomModule<StyleCoverageMatrixModule> {

    public static final StyleCoverageMatrixModule INSTANCE = new StyleCoverageMatrixModule();

    public record StyleCoverageMatrix() implements SelfContainedWidget<StyleCoverageMatrixModule>, NeedKeyboard {
        @Override public String summary() {
            return "How each house style draws each casting - its own CSS, as another casting of the component, or left to Base -"
                 + " and whether the component wears the new sheet. The root shows every casting, a branch its components', a component those beside it. It picks nothing.";
        }
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.ESCAPE, "the keys given back, when the grid did not take it")); }
    }

    @Override
    public ImportsFor<StyleCoverageMatrixModule> imports() {
        return ImportsFor.<StyleCoverageMatrixModule>builder()
                .add(new ModuleImports<>(List.of(new TaxonomyWidgetModule.TaxonomyWidget()), TaxonomyWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StyleCoverageModule.STYLE_COVERAGE()), StyleCoverageModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridModule.RelGrid()), RelGridModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new HouseGridModule.HouseGrid()), HouseGridModule.INSTANCE))   // the design its grid is made with
                .add(new ModuleImports<>(List.of(new RelGridStockCellsModule.RelGridTextCell()), RelGridStockCellsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridLabelsModule.RelGridLabels()), RelGridLabelsModule.INSTANCE))   // its header: label header cells
                .add(new ModuleImports<>(List.of(new RelGridProtocolModule.RelGridViewChanged()), RelGridProtocolModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyStyles.tx_hint(), new TaxonomyStyles.tx_frame(), new TaxonomyStyles.tx_port()), TaxonomyStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<StyleCoverageMatrixModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new StyleCoverageMatrix())); }
}
