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
            new Demo("docking", "Dock and undock", "One dock, a desk over it, tabs that float and land.",
                    "The multi-tab pane is the dock; the desk floats over it. A tab is one record — id, title, widget — with one "
                    + "placement at a time: in the dock's strip, or afloat in a frame of its own. A drag along the strip reorders, on "
                    + "its rail; pulling a tab off to float is being worked out on the tab strip page and comes here after — a holder "
                    + "may undock by call meanwhile. Drag a float over the strip and the dock wears the drop-target word and marks where "
                    + "the tab would land; let go there and it is a tab, let go over content and it stays afloat. Every chip is in the tab "
                    + "order, and the design draws its hover, its press, the selected one and the focus ring. The chip is Control.Tab "
                    + "to the design — like a button, but a hard frame whose measure is the design's, wide and low as a browser's tab, "
                    + "the label ellipsised within; the sliders set the tabs' size and aspect, 0 the design's. A float stays within the "
                    + "box. Every step is data on one sink: Undocked and Docked from the docking, Opened, Released and the rest from "
                    + "the desk, TabAttached and the rest from the dock.",
                    "/docking", DockingApp.INSTANCE, "DockingWidget", Map.of()),
            new Demo("splitgrid", "Split grid", "Rows and columns of cells, arranged; what is in them, the page's.",
                    "The split grid is a container in the relation grid's sense: the page mints what goes in a cell, the grid arranges "
                    + "the cells — a tree of rows and columns sharing their space by ratio, a divider between neighbours — and reports "
                    + "every change of arrangement as data: TracksChanged, Subdivided, Removed. Subdivide beside a cell and it gets a "
                    + "sibling in the same row or column, or becomes a split of two when the orientation differs; remove one and its "
                    + "room goes to its neighbour, a split of one giving way. A cell's element is minted once and kept through every "
                    + "re-arrangement, so what the page put in it stays put; the last cell cannot go. Under the grid, its mirror: the "
                    + "same arrangement drawn from the geometry — headless, the same rectangles flex computes — at a scale the slider "
                    + "sets, with a cursor the arrows move from cell to cell by the workspace's rule once the mirror has focus; the page "
                    + "marks that cell current in the grid. A widget switcher in the making: a shortcut raises the mirror, the arrows "
                    + "pick a pane, the owner makes it active.",
                    "/splitgrid", SplitGridApp.INSTANCE, "SplitGridWidget", Map.of()),
            new Demo("tabstrip", "Tab strip", "The strip alone: the drag that is a browser's, along a rail.",
                    "The tab strip without a pane, to see the drag on its own. Press a chip and it is selected and lifted, before any "
                    + "release — pressed is grabbed. Drag it and it goes where the hand goes along the row — the press is remembered as "
                    + "an offset within the chip, so the chip is placed and the hand never asked where on it the press was — kept within "
                    + "the row and on its rail however the hand wanders; the slot it is nearest is where it will land, and the chips "
                    + "between step aside, live, as it passes them. Let go and it settles onto its slot, eased as the design eases it, "
                    + "the others stepping back at once. Leaving the row — the tab that detaches and floats — is being worked out here "
                    + "next; a dock takes a tab by call meanwhile. TabHand is the hand, TabDrag the arithmetic, headless: the bar as "
                    + "slots at one pitch, the nearest slot, who steps aside.",
                    "/tabstrip", TabStripApp.INSTANCE, "TabStripWidget", Map.of()),
            new Demo("sliders", "Sliders", "A number set by a knob on a track; every part the design's.",
                    "The slider the other pages set their size, aspect and extent with, on its own: the track sunk, the fill from "
                    + "the detent to the value, the knob raised and ringed when it has the focus, the notch where the knob rests — "
                    + "every part a real element wearing a design word, so no browser's slider shows through. Press anywhere on a "
                    + "rail and it jumps and grabs, the pointer captured; the knob takes the keys. Two events, live and on release. "
                    + "The three axes, a plain range with a unit, one that is off, and the slider at its three sizes.",
                    "/sliders", SlidersApp.INSTANCE, "SlidersWidget", Map.of()),
            new Demo("keyboard", "Keyboard", "Who has the keys: one party per page, one holder or none.",
                    "The keyboard party on view. Every component that takes keys is a member of the page's party; a press in it, "
                    + "or the focus arriving, claims the keys — and a claim evicts whoever held, who is told by whom. One steward, "
                    + "the page's, captures keys on the document only while someone holds and asks the holder first: a key it "
                    + "takes stops there, a key it leaves travels on as it would. Five members: a group of sliders, a card, a "
                    + "strip of tabs, a dialog that claims by call on open and gives the keys back on close, and a platformer that "
                    + "holds keys down, so keyup travels through the party as keydown does. The strip shows the holder live; the "
                    + "log says who took the keys from whom. One button shows the bug the design names: the platformer claiming by "
                    + "call from behind a modal — the party is blind, so it holds, and the dialog stops hearing Escape.",
                    "/keyboard", KeyboardApp.INSTANCE, "KeyboardWidget", Map.of()),
            new Demo("menus", "Context menus", "One steward, three cells, each with a menu of its kind.",
                    "The page's context menus: declared once in Java as kinds and items, stamped as data, and held by one "
                    + "steward for the page — lazy, minting a kind's menu at its first open and listening to nothing while none is "
                    + "open. A cell asks for its kind on a right-click or Shift+F10 and is bound to the menu while it is open; the "
                    + "page's handler for the kind gives each row its state for that cell and acts on the pick. The animal cell "
                    + "rotates, flips and changes animal through a second level; the swatch picks its colour through one and "
                    + "toggles its inverted surface, both checked; the counter adds a step, resets — disabled at nought — and picks "
                    + "its step. A press outside closes the menu and is swallowed; Escape closes; arrows, Right, Left and Enter "
                    + "do what a menu's keys do. At most one menu is ever open.",
                    "/menus", ContextMenusApp.INSTANCE, "ContextMenusWidget", Map.of()),
            new Demo("panes", "Panes", "One pane of tabs holding widgets.",
                    "One multi-tab pane. Each tab holds a widget by the base's contract; the plus asks the page and the page asks "
                    + "you through the dialog; a drag on a chip reorders; the cross closes. Every mutation is one event on one "
                    + "sink, written under the pane as the data it is.",
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
