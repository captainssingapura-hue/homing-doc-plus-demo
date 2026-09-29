package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.workspace.groups.core.models.ArrangedWidget;
import hue.captains.singapura.js.homing.workspace.groups.core.models.Arrangement;
import hue.captains.singapura.js.homing.workspace.groups.core.models.SplitGrid;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceArrangements;

/**
 * How the demo workspace is arranged the first time, engine by engine - the
 * launcher's decision, as the declaration is: the workspace knows neither.
 */
public final class DemoArrangements {

    private DemoArrangements() {}

    /** In the split grid: two videos side by side, each half of the room. */
    public static final Arrangement<DemoWorkspace, SplitGrid> SPLIT_GRID = Arrangement.of(DemoWorkspace.INSTANCE,
            SplitGrid.of(SplitGrid.row(SplitGrid.region("left", "left-video"), SplitGrid.region("right", "right-video"))),
            ArrangedWidget.of("left-video", "video"),
            ArrangedWidget.of("right-video", "video"));

    /** Every engine's it has: the split grid's. */
    public static final WorkspaceArrangements<DemoWorkspace> ALL = WorkspaceArrangements.of(DemoWorkspace.INSTANCE, SPLIT_GRID);
}
