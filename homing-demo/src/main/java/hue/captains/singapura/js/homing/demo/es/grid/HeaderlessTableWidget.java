package hue.captains.singapura.js.homing.demo.es.grid;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.List;

/**
 * Table Workbench specimen: 400 rows, no header band at all.
 *
 * <p>The Minesweeper shape — {@code header.show = false} builds no thead, so
 * there is nothing to measure and nothing to inset around. It is the case
 * where the follow's header arithmetic has to cope with the band being absent
 * rather than merely unfrozen, which is a different branch and a different way
 * to get a null wrong.</p>
 */
public final class HeaderlessTableWidget
        extends WorkspaceWidget<WorkspaceWidget._None, HeaderlessTableWidget> {

    public static final HeaderlessTableWidget INSTANCE = new HeaderlessTableWidget();

    private HeaderlessTableWidget() {}

    private record construct()
            implements WorkspaceWidget._Construct<_None, HeaderlessTableWidget> {}

    @Override protected _Construct<_None, HeaderlessTableWidget> construct() { return new construct(); }
    @Override public Class<_None> paramsType() { return _None.class; }
    @Override public String title() { return "Headerless table"; }
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.MULTI; }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() { return TableSpecimen.imports(); }

    @Override
    protected List<String> constructBodyJs() {
        return TableSpecimen.bodyJs("Headerless", 400, 4, TableSpecimen.NONE,
                "no band to measure — the Minesweeper shape");
    }
}
