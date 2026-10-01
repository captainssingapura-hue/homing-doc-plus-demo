package hue.captains.singapura.js.homing.demo.workspacewidgets.video;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.NoParams;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetDeclaration;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetQuery;

import java.util.List;

/**
 * The video playlist, declared: {@code video}, titled Video, with no params and
 * joining no party. Which take is on the stage lives for the session: a param
 * for it would put a box asking for a video's id in front of whoever opens one,
 * which is worse than starting at the top of the list.
 */
public record EmbeddedVideoDeclaration() implements WidgetDeclaration<NoParams> {

    public static final EmbeddedVideoDeclaration INSTANCE = new EmbeddedVideoDeclaration();

    @Override public String kind() { return "video"; }
    @Override public String title() { return "Video"; }
    @Override public Class<NoParams> paramsType() { return NoParams.class; }
    @Override public WidgetQuery<NoParams> query() { return new NoParams.Query(); }

    @Override
    public ModuleImports<?> constructs() {
        return new ModuleImports<>(List.of(new EmbeddedVideoModule.EmbeddedVideo()), EmbeddedVideoModule.INSTANCE);
    }
}
