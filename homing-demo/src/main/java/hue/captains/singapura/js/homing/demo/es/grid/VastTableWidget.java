package hue.captains.singapura.js.homing.demo.es.grid;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.List;

/**
 * Table Workbench specimen: 2000 rows by 30 columns, frozen header band.
 *
 * <p>Both axes at once, at a scale where a follow that overshoots is unmissable
 * and where the grid's own cost shows up honestly — 60,000 cells is past the
 * point where anyone would call this a small table. Ctrl+End territory: jump to
 * the last row and last column together and watch both offsets move.</p>
 */
public final class VastTableWidget extends WorkspaceWidget<WorkspaceWidget._None, VastTableWidget> {

    public static final VastTableWidget INSTANCE = new VastTableWidget();

    private VastTableWidget() {}

    private record construct() implements WorkspaceWidget._Construct<_None, VastTableWidget> {}

    @Override protected _Construct<_None, VastTableWidget> construct() { return new construct(); }
    @Override public Class<_None> paramsType() { return _None.class; }
    @Override public String title() { return "Vast table"; }
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.MULTI; }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() { return TableSpecimen.imports(); }

    @Override
    protected List<String> constructBodyJs() {
        return TableSpecimen.bodyJs("Vast", 2000, 30, TableSpecimen.STICKY,
                "both axes at stress scale");
    }
}
