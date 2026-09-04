package hue.captains.singapura.js.homing.demo.es.grid;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.List;

/**
 * Table Workbench specimen: 15 rows, 30 columns — four named, the rest monthly
 * periods.
 *
 * <p>The other axis. Nothing here ever scrolls vertically, so a horizontal
 * follow that quietly did nothing would be invisible in the tall specimen and
 * obvious in this one. Tab and End are the keys that matter: End jumps to the
 * last column in one move, which is the largest single leap the follow has to
 * absorb.</p>
 */
public final class WideTableWidget extends WorkspaceWidget<WorkspaceWidget._None, WideTableWidget> {

    public static final WideTableWidget INSTANCE = new WideTableWidget();

    private WideTableWidget() {}

    private record construct() implements WorkspaceWidget._Construct<_None, WideTableWidget> {}

    @Override protected _Construct<_None, WideTableWidget> construct() { return new construct(); }
    @Override public Class<_None> paramsType() { return _None.class; }
    @Override public String title() { return "Wide table"; }
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.MULTI; }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() { return TableSpecimen.imports(); }

    @Override
    protected List<String> constructBodyJs() {
        return TableSpecimen.bodyJs("Wide", 15, 30, TableSpecimen.STICKY,
                "the horizontal follow: Tab and End past the right edge");
    }
}
