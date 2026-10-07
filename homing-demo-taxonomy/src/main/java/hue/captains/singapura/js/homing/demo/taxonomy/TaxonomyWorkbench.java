package hue.captains.singapura.js.homing.demo.taxonomy;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.groups.core.models.ArrangedWidget;
import hue.captains.singapura.js.homing.workspace.groups.core.models.Arrangement;
import hue.captains.singapura.js.homing.workspace.groups.core.models.GroupedWorkspace;
import hue.captains.singapura.js.homing.workspace.groups.core.models.SplitGrid;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceArrangements;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceGroup;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceGroups;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceKind;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;
import hue.captains.singapura.js.homing.workspace.site.GroupedWorkspaces;
import hue.captains.singapura.js.homing.workspace.widgets.NoParams;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetDeclaration;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetQuery;
import hue.captains.singapura.js.homing.workspace.widgets.WorkspaceDeclaration;

import java.util.List;

/**
 * The taxonomy workbench: one workspace, {@code taxonomy}, filed in one group - the house's
 * components as a tree, their parts in a table, the picked node's details, and the role catalogue
 * with each role's uses - the four meeting in the {@code node-selection} party.
 */
public final class TaxonomyWorkbench {

    private TaxonomyWorkbench() {}

    /** A widget of the workbench: one of its kind, no params, the pick shared. */
    private interface Kind extends WidgetDeclaration<NoParams> {
        @Override default boolean single() { return true; }
        @Override default Class<NoParams> paramsType() { return NoParams.class; }
        @Override default WidgetQuery<NoParams> query() { return new NoParams.Query(); }
        @Override default List<PartyType<?>> parties() { return List.of(NodeSelection.TYPE); }
    }

    public record Tree() implements Kind {
        public static final Tree INSTANCE = new Tree();
        @Override public String kind() { return "taxonomy-tree"; }
        @Override public String title() { return "Taxonomy"; }
        @Override public ModuleImports<?> constructs() { return new ModuleImports<>(List.of(new TaxonomyTreeModule.TaxonomyTree()), TaxonomyTreeModule.INSTANCE); }
    }

    public record Parts() implements Kind {
        public static final Parts INSTANCE = new Parts();
        @Override public String kind() { return "taxonomy-parts"; }
        @Override public String title() { return "Parts"; }
        @Override public ModuleImports<?> constructs() { return new ModuleImports<>(List.of(new PartsTableModule.PartsTable()), PartsTableModule.INSTANCE); }
    }

    public record Roles() implements Kind {
        public static final Roles INSTANCE = new Roles();
        @Override public String kind() { return "taxonomy-roles"; }
        @Override public String title() { return "Roles"; }
        @Override public ModuleImports<?> constructs() { return new ModuleImports<>(List.of(new RoleTreeModule.RoleTree()), RoleTreeModule.INSTANCE); }
    }

    public record Details() implements Kind {
        public static final Details INSTANCE = new Details();
        @Override public String kind() { return "taxonomy-node"; }
        @Override public String title() { return "Node"; }
        @Override public ModuleImports<?> constructs() { return new ModuleImports<>(List.of(new NodeDetailsModule.NodeDetails()), NodeDetailsModule.INSTANCE); }
    }

    /** The workspace: {@code taxonomy}. */
    public record Workspace() implements WorkspaceDeclaration {
        public static final Workspace INSTANCE = new Workspace();
        @Override public String name() { return "taxonomy"; }
        @Override public List<WidgetDeclaration<?>> kinds() { return List.of(Tree.INSTANCE, Parts.INSTANCE, Details.INSTANCE, Roles.INSTANCE); }
    }

    /** The first time: the tree; beside it, the parts over the details; and the roles, beside them. */
    public static final WorkspaceArrangements<Workspace> ARRANGED = WorkspaceArrangements.of(Workspace.INSTANCE,
            Arrangement.of(Workspace.INSTANCE,
                    SplitGrid.of(SplitGrid.row(
                            SplitGrid.Part.of(SplitGrid.region("tree", "tree"), 3),
                            SplitGrid.Part.of(SplitGrid.column(SplitGrid.Part.of(SplitGrid.region("parts", "parts"), 2),
                                                               SplitGrid.Part.of(SplitGrid.region("node", "node"), 3)), 5),
                            SplitGrid.Part.of(SplitGrid.region("roles", "roles"), 3))),
                    ArrangedWidget.of("tree", Tree.INSTANCE.kind()),
                    ArrangedWidget.of("parts", Parts.INSTANCE.kind()),
                    ArrangedWidget.of("node", Details.INSTANCE.kind()),
                    ArrangedWidget.of("roles", Roles.INSTANCE.kind())));

    /** The one group. */
    public static final WorkspaceGroups GROUPS = WorkspaceGroups.of(
            WorkspaceGroup.of("components", "Components")
                    .section("The taxonomy", GroupedWorkspace.of(Workspace.INSTANCE.name(), "Taxonomy"))
                    .defaultTo(WorkspaceKind.of(Workspace.INSTANCE.name()))
                    .build());

    /** What the site serves: the workspace declared, filed, and arranged the first time. */
    public static final GroupedWorkspaces SITE = new GroupedWorkspaces(GROUPS, List.of(Workspace.INSTANCE)).arranged(ARRANGED);
}
