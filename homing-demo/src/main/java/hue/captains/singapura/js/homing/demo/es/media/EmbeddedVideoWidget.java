package hue.captains.singapura.js.homing.demo.es.media;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.css.VideoStyles;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.List;

/**
 * A YouTube video in a workspace pane.
 *
 * <p><b>It pauses when it is not the active tab.</b> That is the whole reason
 * this widget is more than an {@code <iframe>} in a div. A workspace shows
 * several panes at once, and a video that kept playing behind another tab would
 * be audio coming from somewhere the user is not — the widget contract's
 * {@code setActive(false)} exists for exactly this, and the contract says a
 * widget with no timers and no audio may no-op it. This one has audio.</p>
 *
 * <p>Pausing goes over {@code postMessage} to the player, which is why the src
 * carries {@code enablejsapi=1}. The message is addressed to the player's own
 * origin rather than {@code "*"}, so it is not broadcast to whatever else might
 * be listening.</p>
 *
 * <p>Two deliberate departures from the embed code YouTube hands out:
 * <b>{@code youtube-nocookie.com}</b>, which sets no cookies until the viewer
 * actually presses play; and <b>no {@code autoplay}</b> in the permission list,
 * because a pane that starts talking when you open it is hostile in a workspace
 * that may have four of them. The {@code si=} share token is dropped too — it
 * identifies the share link, not the video.</p>
 */
public final class EmbeddedVideoWidget
        extends WorkspaceWidget<WorkspaceWidget._None, EmbeddedVideoWidget> {

    public static final EmbeddedVideoWidget INSTANCE = new EmbeddedVideoWidget();
    private EmbeddedVideoWidget() {}

    /** The player's origin — the postMessage target, and the src host. */
    private static final String ORIGIN = "https://www.youtube-nocookie.com";

    /** The example video. enablejsapi is what makes setActive able to pause it. */
    private static final String SRC = ORIGIN + "/embed/dLl4PZtxia8?enablejsapi=1&rel=0";

    private record construct() implements WorkspaceWidget._Construct<_None, EmbeddedVideoWidget> {}

    @Override protected _Construct<_None, EmbeddedVideoWidget> construct() { return new construct(); }
    @Override public Class<_None> paramsType() { return _None.class; }
    @Override public String title() { return "Video"; }

    /** MULTI — several videos can be tiled, and each pauses on its own. */
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.MULTI; }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        return List.of(
                new ModuleImports<>(List.of(
                        new VideoStyles.vid_root(),
                        new VideoStyles.vid_title(),
                        new VideoStyles.vid_note(),
                        new VideoStyles.vid_stage(),
                        new VideoStyles.vid_frame()),
                        VideoStyles.INSTANCE)
        );
    }

    @Override
    protected List<String> constructBodyJs() {
        return List.of(
                "    var root = branch.createElement('root', 'div');",
                "    css.setClass(root, vid_root);",
                "",
                "    var h = branch.createElement('title', 'h2');",
                "    css.setClass(h, vid_title);",
                "    h.textContent = 'Embedded video';",
                "    root.appendChild(h);",
                "",
                "    var note = branch.createElement('note', 'p');",
                "    css.setClass(note, vid_note);",
                "    note.textContent = 'Plays inline. Pauses itself when another tab becomes active, "
                        + "so two panes never talk over each other.';",
                "    root.appendChild(note);",
                "",
                "    var stage = branch.createElement('stage', 'div');",
                "    css.setClass(stage, vid_stage);",
                "    root.appendChild(stage);",
                "",
                "    var frame = branch.createElement('frame', 'iframe');",
                "    css.setClass(frame, vid_frame);",
                "    frame.src = " + js(SRC) + ";",
                "    frame.title = 'YouTube video player';",
                "    // No autoplay: a pane that starts talking when you open it is hostile",
                "    // in a workspace that may have four of them.",
                "    frame.allow = 'accelerometer; clipboard-write; encrypted-media; gyroscope; "
                        + "picture-in-picture; web-share; fullscreen';",
                "    frame.referrerPolicy = 'strict-origin-when-cross-origin';",
                "    frame.allowFullscreen = true;",
                "    stage.appendChild(frame);",
                "",
                "    // The player is a control plane we do not own: we cannot read its state,",
                "    // only command it. So setActive PAUSES and never resumes - coming back to",
                "    // a tab must not start audio the user did not ask for.",
                "    function pause() {",
                "        try {",
                "            if (!frame.contentWindow) return;",
                "            frame.contentWindow.postMessage(",
                "                '{\"event\":\"command\",\"func\":\"pauseVideo\",\"args\":\"\"}', "
                        + js(ORIGIN) + ");",
                "        } catch (e) { /* not loaded yet, or navigated away - nothing to pause */ }",
                "    }",
                "",
                "    return {",
                "        root: root,",
                "        setActive: function (active) { if (!active) pause(); }",
                "    };"
        );
    }

    /** A JS string literal, quoted and escaped — the src is never concatenated raw. */
    private static String js(String s) {
        return "'" + s.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }
}
