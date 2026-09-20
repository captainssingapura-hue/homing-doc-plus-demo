package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SelfContent;
import hue.captains.singapura.js.homing.core.StampedParams;
import hue.captains.singapura.js.homing.site.demo.gallery.prefs.PreferencesApp;
import hue.captains.singapura.js.homing.site.demo.gallery.prefs.PreferencesTreeWidgetModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The gallery's demos, stamped into one JS module for the shell: each demo
 * by its slug, with its label, its summary, its explanation and the widget
 * that shows it — a demo app's widget class, by served address — plus
 * the rigid tree and the labels the navigator is built from. The shell
 * imports a demo's module only when the demo is first chosen.
 *
 * <pre>
 *   DEMOS = {
 *     label:  "Gallery",
 *     tree:   { segment: "gallery", children: [ { segment: "<slug>" } … ] },
 *     labels: { "gallery/<slug>": "<label>" … },
 *     navigator: { module, export: "PreferencesTreeWidget" },
 *     demos:  { "gallery/<slug>": { label, summary, explanation, page, widget: { module, export: "<WidgetClass>", params } } }
 *   }
 * </pre>
 */
public record GalleryDemos() implements EsModule<GalleryDemos>, SelfContent {

    public record DEMOS() implements Exportable._Constant<GalleryDemos> {}

    public static final GalleryDemos INSTANCE = new GalleryDemos();

    /** One demo: its slug, what the navigator and the explanation say, the page it also is, the app and the class in it that shows it. */
    public record Demo(String slug, String label, String summary, String explanation, String page,
                       EsModule<?> app, String widget, Map<String, String> params) {}

    public static final List<Demo> DEMOS = List.of(
            new Demo("welcome", "Welcome", "The gallery's cards.",
                    "The pages of this gallery as cards, each a link to the page it names. This is the page the gallery opened on "
                    + "before it was a shell: the same module, now a widget in the demo pane, constructed on a branch the shell "
                    + "handed it and disposed when another demo is chosen.",
                    "/welcome", WelcomeApp.INSTANCE, "WelcomeWidget", Map.of()),
            new Demo("counter", "Counter", "A JS app with typed params.",
                    "A counter that starts where its params say. As a page, the router binds the start off the path and the server "
                    + "stamps it in through the app's own codec; as a widget here, the shell hands the same params to construct. "
                    + "One module, two hosts, no branch of its own to mint: the branch comes in.",
                    "/counter/7", CounterApp.INSTANCE, "CounterWidget", Map.of("start", "7")),
            new Demo("grid", "Grid", "The relation grid, from its own repo.",
                    "The relation grid over twelve books. Titles and ratings edit; the status line under it counts the cells minted "
                    + "and follows the cursor. The grid is a layer-2 component on core and design-core alone; the page hands it a "
                    + "branch and a relation and nothing else.",
                    "/grid", GridApp.INSTANCE, "GridWidget", Map.of()),
            new Demo("tree", "Tree", "The same books as shelf → book.",
                    "The relation tree over the same store: shelves that fold and unfold, each unfold a question the relation "
                    + "answers. Arrow keys move the cursor, Enter activates; the status line says what the tree did.",
                    "/tree", TreeApp.INSTANCE, "TreeWidget", Map.of()),
            new Demo("dialog", "Dialog", "A frame that owns the screen until dismissed.",
                    "Three ways to open a dialog: modal with actions, non-modal, and modal with a control inside that takes its "
                    + "own keys. Modal: the page behind goes inert, keys are captured, Escape cancels, Enter confirms, and the "
                    + "focus comes back to where it was. The dialog is built on a child of the caller's branch and dissolved with it.",
                    "/dialog", DialogApp.INSTANCE, "DialogWidget", Map.of()),
            new Demo("preferences", "Preferences", "A rigid tree, a widget per node.",
                    "The site's preferences as a master and a detail: a rigid tree on the left from the relation tree, the chosen "
                    + "node's widget on the right, loaded when first chosen and kept after. Every widget writes through the "
                    + "steward and follows it, so a theme picked here is worn by this shell as it is picked. The same view is "
                    + "behind the bar's Preferences button.",
                    "/preferences", PreferencesApp.INSTANCE, "PreferencesWidget", Map.of()),
            new Demo("buttons", "Buttons", "The button, through its builder.",
                    "Every colour word the builder knows — plain, primary, secondary, danger, warning, success — under one extent "
                    + "slider: at 1 the word as the design binds it, at 0 the design's neutral, at −1 the meaning turned the other way. "
                    + "A colour word is a semantic surface complete: the surface, the ink on it and the edge move together, each along "
                    + "the anchors its design gives it. The live one is a danger button whose extent follows the rows an action would "
                    + "touch: safe at none, dangerous at all. The size is the other number: 0 regular, 1 the biggest, −1 the smallest, "
                    + "exponential, and every length the design gives a button — its inset, gap, least width and type — grows by the "
                    + "ratio the design gives that length; the five sit at −1, −½, 0, ½ and 1, and the slider moves the rest.",
                    "/buttons", ButtonsApp.INSTANCE, "ButtonsWidget", Map.of()),
            new Demo("cards", "Cards", "The card, through its builder.",
                    "A card is Container.Card.Base to the design: a raised box whose measure is its own. The design gives it its "
                    + "measure, grown by its size, and its aspect — square at 0, the design's widest at 1, its tallest at −1 — and what is "
                    + "inside fits it — the head and the foot are fixed, the body "
                    + "scrolls beyond what they leave. It lifts on hover and presses as an enlarged button would, and with an "
                    + "action it is a button to the keyboard too and rings on focus. The size slider grows every length the "
                    + "design gives a card and its parts, each by its own ratio; the proportion holds.",
                    "/cards", CardsApp.INSTANCE, "CardsWidget", Map.of()),
            new Demo("floating", "Floating panes", "A desk, and the panes that float on it.",
                    "A floating pane is Container.Pane.Floating: a container's corner, rule and ring, the overlay's shadow, the "
                    + "pane's air on its head — a larger, movable card whose place and measure are its user's, not the design's. "
                    + "The desk owns the stack: open a pane holding a widget by the base's contract, drag it by the head, size it "
                    + "by the corner, press one to raise it, close with the cross or Escape; the active one is the ring drawn now. "
                    + "Every mutation is one FloatEvents object on one sink — Opened, Moved, Resized, Raised, Closed — reported "
                    + "once when it happened, never per pixel.",
                    "/floating", FloatingApp.INSTANCE, "FloatingWidget", Map.of()),
            new Demo("docking", "Dock and undock", "Three docks, a desk over them, tabs that float and land.",
                    "The multi-tab pane is the dock; the split only subdivides; the desk floats over both. A tab is one record — "
                    + "id, title, widget — with one placement at a time: in a dock's strip, or afloat in a frame of its own. Pull a chip "
                    + "off a strip and the tab floats under the same hand, widget and all; drag a float over a dock and the dock wears "
                    + "the drop-target word and marks where the tab would land on the strip; let go there and it is a tab, let go over content and it stays afloat; "
                    + "let go and it is a tab there. A float stays within the box. Every step is data on one sink: Undocked and Docked "
                    + "from the docking, Opened, Released and the rest from the desk, TabAttached and the rest from the docks.",
                    "/docking", DockingApp.INSTANCE, "DockingWidget", Map.of()),
            new Demo("panes", "Panes", "One pane of tabs holding widgets.",
                    "One multi-tab pane. Each tab holds a widget by the base's contract; the plus asks the page and the page asks "
                    + "you through the dialog; a drag on a chip reorders; the cross closes. Every mutation is one event on one "
                    + "sink, written under the pane as the data it is. The splitter this shell is laid out with is the pane's "
                    + "sibling module.",
                    "/panes", PanesApp.INSTANCE, "PanesWidget", Map.of())
    );

    public static String pathOf(Demo d) { return "gallery/" + d.slug(); }

    @Override public ImportsFor<GalleryDemos> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<GalleryDemos> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new DEMOS()));
    }

    @Override
    public List<String> selfContent(ModuleNameResolver resolver) {
        var lines = new ArrayList<String>();
        lines.add("// Generated from GalleryDemos - do not hand-edit. A demo's module is named here and");
        lines.add("// imported by the shell only when the demo is first chosen.");
        lines.add("const DEMOS = Object.freeze(" + json(resolver) + ");");
        return lines;
    }

    /** The registry as JSON; what the module holds, for a test or another writer. */
    public String json(ModuleNameResolver resolver) {
        var sb = new StringBuilder("{");
        sb.append("\"label\":\"Gallery\",");
        sb.append("\"tree\":{\"segment\":\"gallery\",\"children\":[");
        for (int i = 0; i < DEMOS.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append("{\"segment\":").append(StampedParams.jsString(DEMOS.get(i).slug())).append(",\"children\":[]}");
        }
        sb.append("]},\"labels\":{\"gallery\":\"Gallery\"");
        for (Demo d : DEMOS) sb.append(',').append(StampedParams.jsString(pathOf(d))).append(':').append(StampedParams.jsString(d.label()));
        sb.append("},\"navigator\":{\"module\":").append(StampedParams.jsString(resolver.resolve(PreferencesTreeWidgetModule.INSTANCE).basePath()))
          .append(",\"export\":\"PreferencesTreeWidget\"},\"demos\":{");
        boolean first = true;
        for (Demo d : DEMOS) {
            if (!first) sb.append(',');
            first = false;
            sb.append(StampedParams.jsString(pathOf(d))).append(":{")
              .append("\"label\":").append(StampedParams.jsString(d.label()))
              .append(",\"summary\":").append(StampedParams.jsString(d.summary()))
              .append(",\"explanation\":").append(StampedParams.jsString(d.explanation()))
              .append(",\"page\":").append(StampedParams.jsString(d.page()))
              .append(",\"widget\":{\"module\":").append(StampedParams.jsString(resolver.resolve(d.app()).basePath()))
              .append(",\"export\":").append(StampedParams.jsString(d.widget())).append(",\"params\":{");
            boolean firstParam = true;
            for (var e : d.params().entrySet()) {
                if (!firstParam) sb.append(',');
                firstParam = false;
                sb.append(StampedParams.jsString(e.getKey())).append(':').append(StampedParams.jsString(e.getValue()));
            }
            sb.append("}}}");
        }
        return sb.append("}}").toString();
    }
}
