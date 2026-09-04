package hue.captains.singapura.js.homing.demo.es.grid;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.List;

/**
 * Table Workbench specimen: 400 rows, four columns, frozen header band.
 *
 * <p>The defect's home ground. Hold ArrowDown and the cursor used to walk off
 * the bottom of the scrollport while the viewport stayed put — the grid read
 * as frozen even though the selection was perfectly correct the whole time.
 * The frozen band is the interesting part of the way back UP: the follow has
 * to stop at the band's lower edge, not at the scrollport's, or the cursor
 * parks underneath the header and looks like it vanished.</p>
 */
public final class TallTableWidget extends WorkspaceWidget<WorkspaceWidget._None, TallTableWidget> {

    public static final TallTableWidget INSTANCE = new TallTableWidget();

    private TallTableWidget() {}

    private record construct() implements WorkspaceWidget._Construct<_None, TallTableWidget> {}

    @Override protected _Construct<_None, TallTableWidget> construct() { return new construct(); }
    @Override public Class<_None> paramsType() { return _None.class; }
    @Override public String title() { return "Tall table"; }
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.MULTI; }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() { return TableSpecimen.imports(); }

    @Override
    protected List<String> constructBodyJs() {
        return TableSpecimen.bodyJs("Tall", 400, 4, TableSpecimen.STICKY,
                "the vertical follow under a frozen band");
    }
}
