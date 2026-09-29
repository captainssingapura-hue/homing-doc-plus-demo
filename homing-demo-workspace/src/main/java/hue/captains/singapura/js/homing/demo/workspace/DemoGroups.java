package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.workspace.groups.core.models.GroupedWorkspace;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceGroup;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceGroups;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceKind;
import hue.captains.singapura.js.homing.workspace.site.GroupedWorkspaces;

import java.util.List;

/**
 * The demo's workspaces, grouped: one group, the page {@code /demo}, and each
 * workspace where the group files it, {@code #ws/<section>/<kind>} - the media,
 * the video room, its default; the games, the animal platformer. A workspace
 * does not know where it is filed: the group decides.
 */
public final class DemoGroups {

    private DemoGroups() {}

    public static final WorkspaceGroups GROUPS = WorkspaceGroups.of(
            WorkspaceGroup.of("demo", "Demo workspaces")
                    .section("Media", GroupedWorkspace.of(DemoWorkspace.INSTANCE.name(), "Video room"))
                    .section("Games", GroupedWorkspace.of(PlatformerWorkspace.INSTANCE.name(), "Animal platformer"))
                    .defaultTo(WorkspaceKind.of(DemoWorkspace.INSTANCE.name()))
                    .build());

    /** What the site serves: each workspace declared, each filed, each arranged the first time. */
    public static final GroupedWorkspaces SITE = new GroupedWorkspaces(GROUPS, List.of(DemoWorkspace.INSTANCE, PlatformerWorkspace.INSTANCE))
            .arranged(DemoArrangements.ALL, DemoArrangements.PLATFORMER);
}
