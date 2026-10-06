package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.ParamCodec;
import hue.captains.singapura.js.homing.workspace.site.GroupedWorkspacePageModule;

import java.util.List;

/** The workbench's page: the grouped workspace page over {@link TaxonomyWorkbench#SITE}, its states kept by the server. */
public record TaxonomyWorkbenchApp() implements AppModule<GroupedWorkspacePageModule.Params, TaxonomyWorkbenchApp> {

    public static final TaxonomyWorkbenchApp INSTANCE = new TaxonomyWorkbenchApp();

    record appMain() implements AppModule._AppMain<GroupedWorkspacePageModule.Params, TaxonomyWorkbenchApp> {}

    @Override public String title()      { return "Taxonomy"; }
    @Override public String simpleName() { return "taxonomy"; }
    @Override public Class<GroupedWorkspacePageModule.Params> paramsType() { return GroupedWorkspacePageModule.Params.class; }
    @Override public ParamCodec<GroupedWorkspacePageModule.Params> paramCodec() { return GroupedWorkspacePageModule.CODEC; }

    @Override
    public ImportsFor<TaxonomyWorkbenchApp> imports() {
        return ImportsFor.<TaxonomyWorkbenchApp>builder()
                .add(new ModuleImports<>(List.of(new GroupedWorkspacePageModule.GroupedWorkspacePage()), GroupedWorkspacePageModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyWorkspacesModule.TAXONOMY_WORKSPACES()), TaxonomyWorkspacesModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TaxonomyLayoutModule.TAXONOMY_GROUPS(), new TaxonomyLayoutModule.TAXONOMY_ARRANGEMENTS()), TaxonomyLayoutModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<TaxonomyWorkbenchApp> exports() { return new ExportsOf<>(INSTANCE, List.of(new appMain())); }
}
