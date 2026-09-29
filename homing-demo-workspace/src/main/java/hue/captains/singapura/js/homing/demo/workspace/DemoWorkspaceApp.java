package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.ParamCodec;
import hue.captains.singapura.js.homing.workspace.shell.WorkspacePageModule;

import java.util.List;

/**
 * The demo workspace as a page of any standard MPA: the shell's workspace page
 * handed the demo's manifest. Its params are a workspace page's - which
 * workspace of the kind, and whether the server keeps its states.
 */
public record DemoWorkspaceApp() implements AppModule<WorkspacePageModule.Params, DemoWorkspaceApp> {

    public static final DemoWorkspaceApp INSTANCE = new DemoWorkspaceApp();

    /** A page's way to a demo workspace: {@code nav.DemoWorkspaceApp({ ws_id, ws_server })} - its own log bar's, for a new one. */
    public record link() implements AppLink<DemoWorkspaceApp> {}

    record appMain() implements AppModule._AppMain<WorkspacePageModule.Params, DemoWorkspaceApp> {}

    @Override public String title()      { return "Workspace"; }
    @Override public String simpleName() { return "demo-workspace"; }
    @Override public Class<WorkspacePageModule.Params> paramsType() { return WorkspacePageModule.Params.class; }
    @Override public ParamCodec<WorkspacePageModule.Params> paramCodec() { return WorkspacePageModule.CODEC; }

    @Override
    public ImportsFor<DemoWorkspaceApp> imports() {
        return ImportsFor.<DemoWorkspaceApp>builder()
                .add(new ModuleImports<>(List.of(new WorkspacePageModule.WorkspacePage()), WorkspacePageModule.INSTANCE))
                // its own address, for the log bar's new workspace of the kind
                .add(new ModuleImports<>(List.of(new link()), INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoWorkspaceModule.DEMO_WORKSPACE()), DemoWorkspaceModule.INSTANCE))
                // its first state in the split grid: two videos side by side
                .add(new ModuleImports<>(List.of(new DemoArrangementModule.DEMO_ARRANGEMENT()), DemoArrangementModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DemoWorkspaceApp> exports() { return new ExportsOf<>(INSTANCE, List.of(new appMain())); }
}
