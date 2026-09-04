package hue.captains.singapura.js.homing.demo.es.grid;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.List;

/**
 * Table Workbench specimen: 400 rows, header shown but NOT frozen.
 *
 * <p>The control for the frozen band. Here the header scrolls away with the
 * body and occludes nothing, so the follow must claim the whole scrollport —
 * an inset applied unconditionally would leave a permanent 24-pixel dead strip
 * at the top of a table that has no band to hide behind. Put side by side with
 * the Tall specimen, the two answer the question the inset exists to ask.</p>
 */
public final class UnfrozenHeaderTableWidget
        extends WorkspaceWidget<WorkspaceWidget._None, UnfrozenHeaderTableWidget> {

    public static final UnfrozenHeaderTableWidget INSTANCE = new UnfrozenHeaderTableWidget();

    private UnfrozenHeaderTableWidget() {}

    private record construct()
            implements WorkspaceWidget._Construct<_None, UnfrozenHeaderTableWidget> {}

    @Override protected _Construct<_None, UnfrozenHeaderTableWidget> construct() { return new construct(); }
    @Override public Class<_None> paramsType() { return _None.class; }
    @Override public String title() { return "Unfrozen header"; }
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.MULTI; }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() { return TableSpecimen.imports(); }

    @Override
    protected List<String> constructBodyJs() {
        return TableSpecimen.bodyJs("Unfrozen header", 400, 4, TableSpecimen.PLAIN,
                "the band scrolls away, so the inset must not apply");
    }
}
