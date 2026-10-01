package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.demo.workspacewidgets.DemoWorkspaceWidgetsCrate;
import hue.captains.singapura.js.homing.workspace.monitors.WorkspaceMonitorsCrate;
import hue.captains.singapura.js.homing.workspace.site.WorkspaceSiteCrate;

import java.util.List;

/**
 * The demo's workspaces, runnable: their manifests, generated from their
 * declarations; their groups; their first states; and their page. The widgets
 * are the widget sets' own crates; the page is the grouped site's, on the
 * shell's.
 */
public final class DemoWorkspaceCrate implements Crate {

    public static final DemoWorkspaceCrate INSTANCE = new DemoWorkspaceCrate();

    private DemoWorkspaceCrate() {}

    @Override public String name() { return "homing-demo-workspace"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the page a group is: the shell's, the workspace its anchor names - and the switcher it summons
                WorkspaceSiteCrate.INSTANCE,
                // the widget sets it puts together: the demo's ported widgets, and the monitors
                DemoWorkspaceWidgetsCrate.INSTANCE,
                WorkspaceMonitorsCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(DemoWorkspaceModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(DemoGroupsModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(DemoArrangementModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(DemoWorkspaceApp.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
