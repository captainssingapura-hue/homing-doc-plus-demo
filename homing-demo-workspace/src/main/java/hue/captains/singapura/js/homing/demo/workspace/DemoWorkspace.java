package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.demo.workspacewidgets.DemoWorkspaceWidgetsCrate;
import hue.captains.singapura.js.homing.workspace.monitors.WorkspaceMonitorsCrate;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetDeclaration;
import hue.captains.singapura.js.homing.workspace.widgets.WorkspaceDeclaration;

import java.util.List;
import java.util.stream.Stream;

/**
 * The demo's workspace on the new core, declared: {@code demo} - its log's kind
 * too - where the demo's ported widgets can be opened, and the monitors beside
 * them, watching the page's parties. The ported widgets come a few at a time,
 * each driving what the workspace still lacks; the first is the video playlist.
 */
public record DemoWorkspace() implements WorkspaceDeclaration {

    public static final DemoWorkspace INSTANCE = new DemoWorkspace();

    @Override public String name() { return "demo"; }

    @Override
    public List<WidgetDeclaration<?>> kinds() {
        return Stream.concat(DemoWorkspaceWidgetsCrate.KINDS.stream(), WorkspaceMonitorsCrate.KINDS.stream()).toList();
    }
}
