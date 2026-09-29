package hue.captains.singapura.js.homing.demo.workspace;

import hue.captains.singapura.js.homing.demo.workspacewidgets.DemoWorkspaceWidgetsCrate;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetDeclaration;
import hue.captains.singapura.js.homing.workspace.widgets.WorkspaceDeclaration;

import java.util.List;

/**
 * The platformer's workspace, declared: {@code platformer} - its log's kind too -
 * where the game is played, one at a time (its kind is single), and the animal it
 * runs as chosen. Its root parties are resolved from its kinds: the platformer
 * party the game tells its run to, and the animal choice. Filed by the group
 * under Games.
 */
public record PlatformerWorkspace() implements WorkspaceDeclaration {

    public static final PlatformerWorkspace INSTANCE = new PlatformerWorkspace();

    @Override public String name() { return "platformer"; }

    @Override
    public List<WidgetDeclaration<?>> kinds() { return DemoWorkspaceWidgetsCrate.GAMES; }
}
