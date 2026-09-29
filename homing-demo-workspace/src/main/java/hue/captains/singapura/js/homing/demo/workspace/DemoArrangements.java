package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.workspace.groups.core.models.ArrangedWidget;
import hue.captains.singapura.js.homing.workspace.groups.core.models.Arrangement;
import hue.captains.singapura.js.homing.workspace.groups.core.models.SplitGrid;
import hue.captains.singapura.js.homing.workspace.groups.core.models.SplitGrid.Part;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceArrangements;

/**
 * How the demo's workspaces are arranged the first time, engine by engine - the
 * launcher's decision, as the declarations and the groups are: the workspaces
 * know neither.
 */
public final class DemoArrangements {

    private DemoArrangements() {}

    /** The video room, in the split grid: two videos side by side, each half of the room. */
    public static final Arrangement<DemoWorkspace, SplitGrid> SPLIT_GRID = Arrangement.of(DemoWorkspace.INSTANCE,
            SplitGrid.of(SplitGrid.row(SplitGrid.region("left", "left-video"), SplitGrid.region("right", "right-video"))),
            ArrangedWidget.of("left-video", "video"),
            ArrangedWidget.of("right-video", "video"));

    /** Every engine's the video room has: the split grid's. */
    public static final WorkspaceArrangements<DemoWorkspace> ALL = WorkspaceArrangements.of(DemoWorkspace.INSTANCE, SPLIT_GRID);

    /** The platformer, in the split grid: the animals on the left; three times as wide beside them, the game above its replay. */
    public static final Arrangement<PlatformerWorkspace, SplitGrid> PLATFORMER_GRID = Arrangement.of(PlatformerWorkspace.INSTANCE,
            SplitGrid.of(SplitGrid.row(Part.of(SplitGrid.region("animals", "selector"), 1),
                    Part.of(SplitGrid.column(SplitGrid.region("game", "play"), SplitGrid.region("watch", "replay")), 3))),
            ArrangedWidget.of("selector", "animal-selector"),
            ArrangedWidget.of("play", "platformer"),
            ArrangedWidget.of("replay", "platformer-replay"));

    /** Every engine's the platformer has: the split grid's. */
    public static final WorkspaceArrangements<PlatformerWorkspace> PLATFORMER = WorkspaceArrangements.of(PlatformerWorkspace.INSTANCE, PLATFORMER_GRID);
}
