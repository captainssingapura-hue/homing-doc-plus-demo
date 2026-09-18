package hue.captains.singapura.js.homing.demo.playground;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.List;

/**
 * Plain tables whose rows and cells lift <b>by state</b>: the component flips
 * {@code aria-selected}, {@code aria-current} and {@code data-highlighted},
 * and the design's word at that slot is the look. One class per element.
 * Its twin, {@link ClassTableWidget}, lifts by class; the workspace keys tabs
 * by widget class, so the two routes are two widgets.
 */
public final class StateTableWidget extends WorkspaceWidget<StateTableWidget.Params, StateTableWidget> {

    public static final StateTableWidget INSTANCE = new StateTableWidget();

    private StateTableWidget() {}

    public record Params() implements WorkspaceWidget._Param {}

    private record construct() implements WorkspaceWidget._Construct<Params, StateTableWidget> {}

    @Override protected _Construct<Params, StateTableWidget> construct() { return new construct(); }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title() { return "Table — by state"; }
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.SINGLETON; }

    @Override protected List<ModuleImports<? extends Importable>> bodyImports() { return TableDemoModule.widgetImports(); }
    @Override protected List<String> constructBodyJs() { return TableDemoModule.widgetBody("state"); }
}
