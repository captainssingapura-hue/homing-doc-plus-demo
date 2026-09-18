package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.demo.playground.AnimalPlaygroundSpec;
import hue.captains.singapura.js.homing.demo.playground.TableWorkbenchSpec;
import hue.captains.singapura.js.homing.demo.playground.TablesPlaygroundSpec;
import hue.captains.singapura.js.homing.demo.playground.VideoRoomSpec;
import hue.captains.singapura.js.homing.studio.workspace.StudioWorkspaceSpec;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceGroup;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceGroupRegistry;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceSpecRegistry;

import java.util.List;

/**
 * RFC 0058 — the demo deployment's two workspace groups, each placed once:
 * <ul>
 *   <li><b>{@code studio}</b> — the Studio Workspace (Navigator / Summary /
 *       Document / Party Monitor over this deployment's catalogue), placed on the
 *       umbrella's home so it browses the whole composed tree;</li>
 *   <li><b>{@code apps}</b> — the demo's own kinds, placed on the Demo studio:
 *       the Animals Playground (<i>Games</i>), the Video Room (<i>Media</i>) and
 *       the Table Workbench (<i>Data</i>) — the last of which had no address at
 *       all before this.</li>
 * </ul>
 *
 * <p>Registered from {@link DemoFixtures}, after the starter has registered the
 * {@code studio} spec and the demo specs have self-registered: a group validates
 * that every kind it holds is in {@code WorkspaceSpecRegistry}, so order matters.
 * Idempotent, like the spec registrations it follows.</p>
 */
public final class DemoWorkspaceGroups {

    private DemoWorkspaceGroups() {}

    public static final String STUDIO = "studio";
    public static final String APPS   = "apps";

    public static void register() {
        var registry = WorkspaceGroupRegistry.INSTANCE;
        if (registry.get(STUDIO).isEmpty()) {
            registry.register(WorkspaceGroup.of(STUDIO, "Studio",
                    "The Studio Workspace — Navigator, Summary and Document over this deployment's catalogue, "
                  + "plus the DomOpsParty monitor.",
                    List.of(spec(StudioWorkspaceSpec.INSTANCE.kind()))));
        }
        if (registry.get(APPS).isEmpty()) {
            registry.register(WorkspaceGroup.of(APPS, "Apps",
                    "The demo's own workspaces — games, media and data — switched in place from the title.",
                    List.of(AnimalPlaygroundSpec.INSTANCE, VideoRoomSpec.INSTANCE, TableWorkbenchSpec.INSTANCE, TablesPlaygroundSpec.INSTANCE)));
        }
    }

    /** The registered spec for a kind — the starter registers {@code studio}, never this class. */
    private static hue.captains.singapura.js.homing.workspace.shell.WorkspaceSpec spec(String kind) {
        return WorkspaceSpecRegistry.INSTANCE.get(kind).orElseThrow(() ->
                new IllegalStateException("kind '" + kind + "' is not registered yet — register the groups after the starter"));
    }
}
