package hue.captains.singapura.js.homing.demo.es.media;

import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.demo.css.VideoStyles;
import hue.captains.singapura.js.homing.workspace.LifecycleHint;
import hue.captains.singapura.js.homing.workspace.WorkspaceWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * A short playlist in a workspace pane — five cooks, one dish.
 *
 * <p><b>It pauses when it is not the active tab.</b> That is the first reason
 * this widget is more than an {@code <iframe>} in a div. A workspace shows
 * several panes at once, and a video that kept playing behind another tab would
 * be audio coming from somewhere the user is not — the widget contract's
 * {@code setActive(false)} exists for exactly this, and the contract says a
 * widget with no timers and no audio may no-op it. This one has audio.</p>
 *
 * <p><b>It carries a list, and the list is a second control plane.</b> Which
 * take is on the stage is said in four places at once — the strip marks it, the
 * counter numbers it, the line under the heading names it, and the player is
 * showing it. One function moves all four, because a control plane that can
 * disagree with the thing it controls will eventually do so.</p>
 *
 * <h2>Switching destroys the player rather than commanding it</h2>
 *
 * <p>The player is a control plane we do not own: we can command it, never read
 * it. So a switch does not ask the old video to stop — it <i>dissolves</i> the
 * branch that owns the iframe, which removes the element and takes the player
 * with it. The outgoing video stops by construction rather than by a message we
 * cannot confirm arrived, and the incoming one is a fresh player rather than a
 * reused one carrying whatever state the last video left behind.</p>
 *
 * <p>A branch per player, rather than one iframe whose {@code src} is
 * reassigned, for two reasons. Reassigning {@code src} pushes a browser history
 * entry, so Back would walk the playlist instead of leaving the page. And
 * {@code DomOpsParty} refuses a duplicate element name, so a second iframe on
 * the same branch throws — the child-branch-per-render pattern the studio's own
 * dialogs use (RFC 0057) answers both.</p>
 *
 * <h2>Browse and activate are different acts</h2>
 *
 * <p>The strip is an ARIA tablist with <b>manual</b> activation: arrows move the
 * focus, Enter or Space or a click puts a take on the stage. Automatic
 * activation — the commoner tab pattern — would load a YouTube player per
 * keystroke, which is both expensive and loud. It is the same split the theme
 * picker makes for the same reason: arrowing browses, Enter chooses.</p>
 *
 * <p>The rail handles Enter itself rather than leaning on a button's native
 * "Enter is a click". Enter is also how the workspace enters a pane, so an
 * activation that arrives by default action is one a chrome layer can take away
 * without touching this file — and the arrows would go on working, which is the
 * shape of bug that is hardest to see.</p>
 *
 * <p>Two deliberate departures from the embed code YouTube hands out:
 * <b>{@code youtube-nocookie.com}</b>, which sets no cookies until the viewer
 * actually presses play; and <b>no {@code autoplay}</b> in the permission list,
 * because a pane that starts talking when you open it is hostile in a workspace
 * that may have four of them. The {@code si=} share token is dropped too — it
 * identifies the share link, not the video. Switching a take therefore hands you
 * a paused player: the widget never starts audio you did not ask for, and that
 * rule does not get an exception for the take you just picked.</p>
 *
 * <p><b>Not persisted.</b> Which take you are on lives for the session. The
 * params seam is where it would go, but a typed param would put a free-text box
 * in the widget picker asking for a video id, which is worse than starting at
 * the top of the list.</p>
 */
public final class EmbeddedVideoWidget
        extends WorkspaceWidget<WorkspaceWidget._None, EmbeddedVideoWidget> {

    public static final EmbeddedVideoWidget INSTANCE = new EmbeddedVideoWidget();
    private EmbeddedVideoWidget() {}

    /** The player's origin — the postMessage target, and the src host. */
    private static final String ORIGIN = "https://www.youtube-nocookie.com";

    /** The dish, as the heading says it. */
    private static final String DISH = "宫保鸡丁 · Kung Pao Chicken";

    /**
     * One video.
     *
     * @param videoId the YouTube id, without the {@code si=} share token
     * @param label   the strip's text — short, and distinct from its neighbours,
     *                which is why the two takes from one channel are told apart
     *                here rather than both reading as the channel name
     * @param note    the line under the heading while this take is on the stage,
     *                and the strip button's tooltip
     */
    private record Take(String videoId, String label, String note) {}

    /**
     * Five takes on one dish, which is what makes the list worth switching:
     * the same recipe by different hands, not five unrelated videos.
     */
    private static final List<Take> TAKES = List.of(
            new Take("c6WRS8xSA-4", "老饭骨 · 传承版",
                    "老饭骨 — the state-banquet master's version, a dish with centuries behind it"),
            new Take("yqwE6zO-hUA", "老饭骨 · 宗师版",
                    "老饭骨 — the same masters again, on the Sichuan grandmaster's line"),
            new Take("wEkVkT6IU9M", "美食作家王刚",
                    "美食作家王刚 — a head chef's Sichuan method, start to finish"),
            new Take("KFZX7VRN_oY", "特厨眼博",
                    "特厨眼博 — the take that sold out a Beijing dining room"),
            new Take("-AZ87qyHQ88", "大师的菜",
                    "大师的菜 — where the name 宫保 comes from, and what makes it authentic")
    );

    private record construct() implements WorkspaceWidget._Construct<_None, EmbeddedVideoWidget> {}

    @Override protected _Construct<_None, EmbeddedVideoWidget> construct() { return new construct(); }
    @Override public Class<_None> paramsType() { return _None.class; }
    @Override public String title() { return "Video"; }

    /** MULTI — several playlists can be tiled, and each pauses on its own. */
    @Override public LifecycleHint lifecycleHint() { return LifecycleHint.MULTI; }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        return List.of(
                new ModuleImports<>(List.of(
                        new VideoStyles.vid_root(),
                        new VideoStyles.vid_head(),
                        new VideoStyles.vid_title(),
                        new VideoStyles.vid_count(),
                        new VideoStyles.vid_note(),
                        new VideoStyles.vid_stage(),
                        new VideoStyles.vid_frame(),
                        new VideoStyles.vid_rail(),
                        new VideoStyles.vid_take(),
                        new VideoStyles.vid_take_on(),
                        new VideoStyles.vid_take_focus()),
                        VideoStyles.INSTANCE)
        );
    }

    @Override
    protected List<String> constructBodyJs() {
        var js = new ArrayList<String>();

        js.add("    // The playlist, emitted from TAKES so the data has one home.");
        js.add("    var takes = [");
        for (Take t : TAKES) {
            js.add("        { id: " + js(t.videoId()) + ", label: " + js(t.label())
                    + ", note: " + js(t.note()) + " },");
        }
        js.add("    ];");
        js.addAll(List.of(
                "",
                "    // Owner of every player branch. A construct-scope object, so it lives",
                "    // exactly as long as the closures that use it - the branch's WeakRef",
                "    // never outlives what it points at.",
                "    var owner = { toString: function () { return 'embeddedVideo'; } };",
                "    var shown = -1, focused = 0, seq = 0, player = null, frame = null;",
                "",
                "    var root = branch.createElement('root', 'div');",
                "    css.addClass(root, vid_root);",
                "",
                "    var head = branch.createElement('head', 'div');",
                "    css.addClass(head, vid_head);",
                "    root.appendChild(head);",
                "",
                "    var h = branch.createElement('title', 'h2');",
                "    css.addClass(h, vid_title);",
                "    h.textContent = " + js(DISH) + ";",
                "    head.appendChild(h);",
                "",
                "    var count = branch.createElement('count', 'span');",
                "    css.addClass(count, vid_count);",
                "    head.appendChild(count);",
                "",
                "    var note = branch.createElement('note', 'p');",
                "    css.addClass(note, vid_note);",
                "    root.appendChild(note);",
                "",
                "    var stage = branch.createElement('stage', 'div');",
                "    css.addClass(stage, vid_stage);",
                "    root.appendChild(stage);",
                "",
                "    // A tablist, and the takes are its tabs. No aria-controls: this widget is",
                "    // MULTI, so several can be tiled, and an id would collide with its twin.",
                "    // The stage carries an aria-label naming the take instead.",
                "    var rail = branch.createElement('rail', 'div');",
                "    css.addClass(rail, vid_rail);",
                "    rail.setAttribute('role', 'tablist');",
                "    rail.setAttribute('aria-label', 'Takes');",
                "    root.appendChild(rail);",
                "",
                "    var tabs = [];",
                "    for (var i = 0; i < takes.length; i++) { tabs.push(mkTab(i)); }",
                "",
                "    function mkTab(i) {",
                "        var b = branch.createElement('take' + i, 'button');",
                "        b.type = 'button';",
                "        b.textContent = takes[i].label;",
                "        b.title = takes[i].note;",
                "        b.setAttribute('role', 'tab');",
                "        css.addClass(b, vid_take);",
                "        css.addClass(b, vid_take_focus);",
                "        // The POINTER's path to activation. The keyboard's is the rail's own",
                "        // keydown, deliberately, rather than a button's default Enter-is-a-click:",
                "        // Enter is also how the workspace enters a pane, so a chrome layer that",
                "        // one day consumes it would kill activation while leaving the arrows",
                "        // working - a widget that owns its arrows should own its Enter.",
                "        b.addEventListener('click', function () { focused = i; show(i); });",
                "        rail.appendChild(b);",
                "        return b;",
                "    }",
                "",
                "    // Roving tabindex: one tab stop for the strip, arrows within it. Selection",
                "    // follows the STAGE and the tab stop follows the FOCUS - they are the same",
                "    // only until you start arrowing, which is the point of manual activation.",
                "    function paint() {",
                "        for (var i = 0; i < tabs.length; i++) {",
                "            css.toggleClass(tabs[i], vid_take_on, i === shown);",
                "            tabs[i].setAttribute('aria-selected', i === shown ? 'true' : 'false');",
                "            tabs[i].setAttribute('tabindex', i === focused ? '0' : '-1');",
                "        }",
                "    }",
                "",
                "    function move(i) { focused = i; paint(); tabs[i].focus(); }",
                "",
                "    rail.addEventListener('keydown', function (ev) {",
                "        var n = takes.length;",
                "        if (ev.key === 'ArrowRight' || ev.key === 'ArrowDown') move((focused + 1) % n);",
                "        else if (ev.key === 'ArrowLeft' || ev.key === 'ArrowUp') move((focused + n - 1) % n);",
                "        else if (ev.key === 'Home') move(0);",
                "        else if (ev.key === 'End') move(n - 1);",
                "        else if (ev.key === 'Enter' || ev.key === ' ') show(focused);",
                "        else return;",
                "        // Ours to consume: the arrows would otherwise scroll the strip we just",
                "        // moved through, and preventing Enter's default is what stops the button",
                "        // ALSO firing a click - one activation, from one place. Escape and Tab",
                "        // are deliberately left alone: they are how the workspace gets its pane",
                "        // back.",
                "        ev.preventDefault();",
                "    });",
                "",
                "    // The switch. Everything that says WHICH take moves here, together.",
                "    function show(i) {",
                "        if (i === shown) { paint(); return; }",
                "        shown = i;",
                "        swapPlayer(takes[i]);",
                "        note.textContent = takes[i].note;",
                "        count.textContent = (i + 1) + ' / ' + takes.length;",
                "        stage.setAttribute('aria-label', takes[i].label);",
                "        paint();",
                "    }",
                "",
                "    // Dissolve, then mint. Dissolving releases the old iframe from the DOM,",
                "    // which destroys the player and stops its audio without asking it to.",
                "    function swapPlayer(take) {",
                "        if (player) { player.dissolve(); player = null; frame = null; }",
                "        player = branch.createBranch('player' + (++seq));",
                "        player.activate(owner);",
                "        frame = player.createElement('frame', 'iframe');",
                "        css.addClass(frame, vid_frame);",
                "        frame.src = " + js(ORIGIN) + " + '/embed/' + take.id + '?enablejsapi=1&rel=0';",
                "        frame.title = take.note + ' — YouTube video player';",
                "        // No autoplay: a pane that starts talking when you open it is hostile",
                "        // in a workspace that may have four of them.",
                "        frame.allow = 'accelerometer; clipboard-write; encrypted-media; gyroscope; "
                        + "picture-in-picture; web-share; fullscreen';",
                "        frame.referrerPolicy = 'strict-origin-when-cross-origin';",
                "        frame.allowFullscreen = true;",
                "        stage.appendChild(frame);",
                "    }",
                "",
                "    // The player is a control plane we do not own: we cannot read its state,",
                "    // only command it. So setActive PAUSES and never resumes - coming back to",
                "    // a tab must not start audio the user did not ask for.",
                "    function pause() {",
                "        try {",
                "            if (!frame || !frame.contentWindow) return;",
                "            frame.contentWindow.postMessage(",
                "                '{\"event\":\"command\",\"func\":\"pauseVideo\",\"args\":\"\"}', "
                        + js(ORIGIN) + ");",
                "        } catch (e) { /* not loaded yet, or navigated away - nothing to pause */ }",
                "    }",
                "",
                "    show(0);",
                "",
                "    return {",
                "        root: root,",
                "        setActive: function (active) { if (!active) pause(); }",
                "    };"
        ));
        return List.copyOf(js);
    }

    /** A JS string literal, quoted and escaped — the src is never concatenated raw. */
    private static String js(String s) {
        return "'" + s.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }
}
