package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.ParamCodec;
import hue.captains.singapura.js.homing.workspace.site.GroupedWorkspacePageModule;

import java.util.List;

/**
 * The demo's workspaces as a page of any standard MPA: the grouped workspace
 * page, handed the demo's manifests, groups and first states. Its params are a
 * grouped page's - the group its route names, which workspace of the anchor's
 * kind, and whether the server keeps its states.
 */
public record DemoWorkspaceApp() implements AppModule<GroupedWorkspacePageModule.Params, DemoWorkspaceApp> {

    public static final DemoWorkspaceApp INSTANCE = new DemoWorkspaceApp();

    record appMain() implements AppModule._AppMain<GroupedWorkspacePageModule.Params, DemoWorkspaceApp> {}

    @Override public String title()      { return "Workspace"; }
    @Override public String simpleName() { return "demo-workspace"; }
    @Override public Class<GroupedWorkspacePageModule.Params> paramsType() { return GroupedWorkspacePageModule.Params.class; }
    @Override public ParamCodec<GroupedWorkspacePageModule.Params> paramCodec() { return GroupedWorkspacePageModule.CODEC; }

    @Override
    public ImportsFor<DemoWorkspaceApp> imports() {
        return ImportsFor.<DemoWorkspaceApp>builder()
                .add(new ModuleImports<>(List.of(new GroupedWorkspacePageModule.GroupedWorkspacePage()), GroupedWorkspacePageModule.INSTANCE))
                // what it serves: each workspace's manifest by its kind, and the groups they are filed in
                .add(new ModuleImports<>(List.of(new DemoWorkspaceModule.DEMO_WORKSPACES()), DemoWorkspaceModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoGroupsModule.DEMO_GROUPS()), DemoGroupsModule.INSTANCE))
                // and how each is arranged the first time, in the split grid
                .add(new ModuleImports<>(List.of(new DemoArrangementModule.DEMO_ARRANGEMENTS()), DemoArrangementModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DemoWorkspaceApp> exports() { return new ExportsOf<>(INSTANCE, List.of(new appMain())); }
}
