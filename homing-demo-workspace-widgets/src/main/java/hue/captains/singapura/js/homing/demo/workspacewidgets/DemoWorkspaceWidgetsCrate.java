package hue.captains.singapura.js.homing.demo.workspacewidgets;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.demo.workspacewidgets.video.EmbeddedVideoDeclaration;
import hue.captains.singapura.js.homing.demo.workspacewidgets.video.EmbeddedVideoModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.video.EmbeddedVideoStyles;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetDeclaration;
import hue.captains.singapura.js.homing.workspace.widgets.WorkspaceWidgetsCrate;

import java.util.List;

/**
 * The demo's widgets on the workspace's core, and nothing else: each
 * self-contained, each worn in the design's words. Any workspace may declare
 * them among its kinds ({@link #KINDS}); nothing here knows a shell, a page or
 * the studio's old workspace. The first is the video playlist.
 */
public final class DemoWorkspaceWidgetsCrate implements Crate {

    public static final DemoWorkspaceWidgetsCrate INSTANCE = new DemoWorkspaceWidgetsCrate();

    /** The kinds, for a workspace that offers them. */
    public static final List<WidgetDeclaration<?>> KINDS = List.of(EmbeddedVideoDeclaration.INSTANCE);

    private DemoWorkspaceWidgetsCrate() {}

    @Override public String name() { return "homing-demo-workspace-widgets"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the DomOpsParty a widget mints its root from
                CoreJsCrate.INSTANCE,
                // the focus party, the keys' convention, the css manager
                ServerCrate.INSTANCE,
                // the design words the sheets wear
                DesignCrate.INSTANCE,
                // what a widget is: the sheet it fills its container by
                WorkspaceWidgetsCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                // the video playlist: a player over a strip of takes
                CrateEntry.of(EmbeddedVideoStyles.INSTANCE),
                CrateEntry.of(EmbeddedVideoModule.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
