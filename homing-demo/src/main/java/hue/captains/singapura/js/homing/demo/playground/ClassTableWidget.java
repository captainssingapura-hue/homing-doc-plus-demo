package hue.captains.singapura.js.homing.demo.playground;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.List;

/**
 * Plain tables whose rows and cells lift <b>by class</b>: the component
 * decides the look itself and applies its own lifted class beside the base
 * — {@code tdm_row_lifted} beside {@code st_tr}, {@code tdm_cell_lifted}
 * beside {@code tdm_cell} — wearing the {@code Selected} pairs. The sheet's
 * precedence is what makes "beside" win. Twin of {@link StateTableWidget}.
 */
public final class ClassTableWidget extends WorkspaceWidget<ClassTableWidget.Params, ClassTableWidget> {

    public static final ClassTableWidget INSTANCE = new ClassTableWidget();

    private ClassTableWidget() {}

    public record Params() implements WorkspaceWidget._Param {}

    private record construct() implements WorkspaceWidget._Construct<Params, ClassTableWidget> {}

    @Override protected _Construct<Params, ClassTableWidget> construct() { return new construct(); }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title() { return "Table — by class"; }
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.SINGLETON; }

    @Override protected List<ModuleImports<? extends Importable>> bodyImports() { return TableDemoModule.widgetImports(); }
    @Override protected List<String> constructBodyJs() { return TableDemoModule.widgetBody("class"); }
}
