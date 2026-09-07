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
 * room is however many playlists you split the panes into. What it demonstrates
 * is not the embed — that is four attributes — but that a widget owning a
 * <i>third-party player</i> still answers the workspace's contract: each pane
 * pauses when it stops being the active tab, so four videos in four panes are
 * four things you can hear one at a time.</p>
 *
 * <p>The widget carries a playlist of its own, which makes the room a nest of
 * control planes: the workspace decides which pane is active, and each pane
 * decides which take is on its stage. Neither plane may lie about the other.</p>
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
                                "Five cooks on one dish. Arrow along the strip to browse, Enter "
                              + "to put a take on the stage. Pauses itself when its tab stops "
                              + "being the active one — split the pane and add a second to hear "
                              + "the rule work."))
        );
    }
}
