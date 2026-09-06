package hue.captains.singapura.js.homing.demo.playground;

import hue.captains.singapura.js.homing.demo.es.media.EmbeddedVideoWidget;
import hue.captains.singapura.js.homing.workspace.WidgetDescription;
import hue.captains.singapura.js.homing.workspace.WidgetEntry;
import hue.captains.singapura.js.homing.workspace.WidgetGroup;
import hue.captains.singapura.js.homing.workspace.WidgetIcon;
import hue.captains.singapura.js.homing.workspace.WidgetLabel;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceSpec;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceSpecRegistry;

import java.util.List;

/**
 * Video Room — a workspace whose widgets are embedded videos.
 *
 * <p>The smallest interesting workspace: one widget kind, {@code MULTI}, so the
 * room is however many videos you split the panes into. What it demonstrates is
 * not the embed — that is four attributes — but that a widget owning a
 * <i>third-party player</i> still answers the workspace's contract: each pane
 * pauses when it stops being the active tab, so four videos in four panes are
 * four things you can hear one at a time.</p>
 *
 * <p>It also carries a {@code group()} — RFC 0057 — so it lands under Media
 * rather than in the default section when the switcher draws its tree.</p>
 */
public final class VideoRoomSpec implements WorkspaceSpec {

    public static final VideoRoomSpec INSTANCE;
    static {
        INSTANCE = new VideoRoomSpec();
        WorkspaceSpecRegistry.INSTANCE.register(INSTANCE);
    }

    private VideoRoomSpec() {}

    @Override public String kind()  { return "videoRoom"; }
    @Override public String title() { return "Video Room"; }
    @Override public String group() { return "Media"; }

    @Override
    public List<WidgetEntry> widgetEntries() {
        return List.of(
                WidgetEntry.of(EmbeddedVideoWidget.class, WidgetLabel.of("Video"))
                        .withIcon(new WidgetIcon.Emoji("📺"))
                        .withGroup(WidgetGroup.of("Media"))
                        .withDescription(WidgetDescription.of(
                                "A YouTube embed that pauses itself when its tab stops being "
                              + "the active one. Split the pane and add a second to hear the "
                              + "rule work."))
        );
    }
}
