package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.demo.workspacewidgets.DemoWorkspaceWidgetsCrate;
import hue.captains.singapura.js.homing.workspace.monitors.WorkspaceMonitorsCrate;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetDeclaration;
import hue.captains.singapura.js.homing.workspace.widgets.WorkspaceDeclaration;

import java.util.List;
import java.util.stream.Stream;

/**
 * The demo's media workspace, declared: {@code demo} - its log's kind too - where
 * the video playlist can be opened, and the monitors beside it, watching the
 * page's parties. Filed by the group as the Video room.
 */
public record DemoWorkspace() implements WorkspaceDeclaration {

    public static final DemoWorkspace INSTANCE = new DemoWorkspace();

    @Override public String name() { return "demo"; }

    @Override
    public List<WidgetDeclaration<?>> kinds() {
        return Stream.concat(DemoWorkspaceWidgetsCrate.MEDIA.stream(), WorkspaceMonitorsCrate.KINDS.stream()).toList();
    }
}
