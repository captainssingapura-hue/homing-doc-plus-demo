package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.demo.workspacewidgets.DemoWorkspaceWidgetsCrate;
import hue.captains.singapura.js.homing.workspace.monitors.WorkspaceMonitorsCrate;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceShellCrate;

import java.util.List;

/**
 * The demo workspace, runnable: its manifest, generated from its declaration,
 * and its page. The widgets are the widget sets' own crates; the page is the
 * workspace shell's.
 */
public final class DemoWorkspaceCrate implements Crate {

    public static final DemoWorkspaceCrate INSTANCE = new DemoWorkspaceCrate();

    private DemoWorkspaceCrate() {}

    @Override public String name() { return "homing-demo-workspace"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the page a workspace is: the shell's, handed the manifest
                WorkspaceShellCrate.INSTANCE,
                // the widget sets it puts together: the demo's ported widgets, and the monitors
                DemoWorkspaceWidgetsCrate.INSTANCE,
                WorkspaceMonitorsCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(DemoWorkspaceModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(DemoWorkspaceApp.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
