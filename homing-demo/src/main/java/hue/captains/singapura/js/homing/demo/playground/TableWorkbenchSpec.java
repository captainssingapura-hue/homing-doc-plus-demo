package hue.captains.singapura.js.homing.demo.playground;

import hue.captains.singapura.js.homing.demo.es.grid.HeaderlessTableWidget;
import hue.captains.singapura.js.homing.demo.es.grid.TallTableWidget;
import hue.captains.singapura.js.homing.demo.es.grid.UnfrozenHeaderTableWidget;
import hue.captains.singapura.js.homing.demo.es.grid.VastTableWidget;
import hue.captains.singapura.js.homing.demo.es.grid.WideTableWidget;
import hue.captains.singapura.js.homing.workspace.RibbonItem;
import hue.captains.singapura.js.homing.workspace.WidgetEntry;
import hue.captains.singapura.js.homing.workspace.WidgetDescription;
import hue.captains.singapura.js.homing.workspace.WidgetGroup;
import hue.captains.singapura.js.homing.workspace.WidgetIcon;
import hue.captains.singapura.js.homing.workspace.WidgetLabel;
import hue.captains.singapura.js.homing.workspace.shell.ActionDispatch;
import hue.captains.singapura.js.homing.workspace.shell.PartyDecl;
import hue.captains.singapura.js.homing.workspace.shell.WidgetCodecRef;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceSpec;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceSpecRegistry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Table Workbench — a bench of Relation Grids in different shapes, at
 * {@code ?app=genericWorkspace&ws_kind=tableWorkbench}.
 *
 * <p>It exists because the grid's two companion demos are both small on
 * purpose. The Dish List is six dishes and Minesweeper is a board that fits;
 * neither has ever had a row below the fold, so neither could have caught a
 * viewport that failed to follow the cursor. A defect nothing in the demo
 * suite can express is a defect that ships.</p>
 *
 * <p>Dock two specimens side by side and the comparisons the panes are for
 * become direct: Tall against Unfrozen header asks whether the sticky inset is
 * conditional, Tall against Headerless asks whether the absent band is handled
 * as an absence rather than a zero, and Wide asks the same questions of the
 * other axis. Each specimen prints its own cursor position, scroll offsets and
 * a VISIBLE / OFF SCREEN verdict, so the two claims stay separable by eye.</p>
 *
 * <p>Pure declaration, like {@link AnimalPlaygroundSpec}: no body JS, no chrome
 * wiring. Registration is class-load — a static initializer registers the
 * singleton on first reference to {@link #INSTANCE}, which
 * {@code DemoBaseStudio} triggers during studio bootstrap.</p>
 */
public final class TableWorkbenchSpec implements WorkspaceSpec {

    public static final TableWorkbenchSpec INSTANCE;

    static {
        INSTANCE = new TableWorkbenchSpec();
        WorkspaceSpecRegistry.INSTANCE.register(INSTANCE);
    }

    private TableWorkbenchSpec() {}

    @Override public String kind()  { return "tableWorkbench"; }
    @Override public String title() { return "Table Workbench"; }

    @Override
    public List<WidgetEntry> widgetEntries() {
        return List.of(
                WidgetEntry.of(DocViewWidget.class, WidgetLabel.of("How to use this bench"))
                        .withIcon(new WidgetIcon.Emoji("📖"))
                        .withGroup(WidgetGroup.of("Reference"))
                        .withDefaults(introDefaults()),
                WidgetEntry.of(TallTableWidget.class, WidgetLabel.of("Tall table"))
                        .withIcon(new WidgetIcon.Emoji("📏"))
                        .withGroup(WidgetGroup.of("Scrolling"))
                        .withDescription(WidgetDescription.of(
                                "400 rows, frozen header. The vertical follow, and the "
                              + "sticky band the cursor must never park underneath.")),
                WidgetEntry.of(WideTableWidget.class, WidgetLabel.of("Wide table"))
                        .withIcon(new WidgetIcon.Emoji("↔️"))
                        .withGroup(WidgetGroup.of("Scrolling"))
                        .withDescription(WidgetDescription.of(
                                "15 rows by 30 columns. Nothing scrolls vertically here, so "
                              + "the horizontal follow has nowhere to hide.")),
                WidgetEntry.of(VastTableWidget.class, WidgetLabel.of("Vast table"))
                        .withIcon(new WidgetIcon.Emoji("🌊"))
                        .withGroup(WidgetGroup.of("Scrolling"))
                        .withDescription(WidgetDescription.of(
                                "2000 by 30 — both axes at a scale where an overshoot is "
                              + "unmissable and the grid's own cost shows up honestly.")),
                WidgetEntry.of(UnfrozenHeaderTableWidget.class, WidgetLabel.of("Unfrozen header"))
                        .withIcon(new WidgetIcon.Emoji("🧊"))
                        .withGroup(WidgetGroup.of("Header postures"))
                        .withDescription(WidgetDescription.of(
                                "The control for the frozen band: the header scrolls away, so "
                              + "the follow must claim the whole port and inset nothing.")),
                WidgetEntry.of(HeaderlessTableWidget.class, WidgetLabel.of("Headerless table"))
                        .withIcon(new WidgetIcon.Emoji("⬜"))
                        .withGroup(WidgetGroup.of("Header postures"))
                        .withDescription(WidgetDescription.of(
                                "No thead is built at all — the Minesweeper shape. An absent "
                              + "band is a different branch from an unfrozen one."))
        );
    }

    /** Nothing to steer from the ribbon: a specimen's knobs are its own toolbar. */
    @Override public List<RibbonItem> ribbonItems() { return List.of(); }

    /** No shared state between specimens — comparing them depends on their
     *  being independent. */
    @Override public List<PartyDecl> parties() { return List.of(); }

    @Override public Map<String, ActionDispatch> actionDispatch() { return Map.of(); }

    /** The substrate's identity codec covers every specimen: the shape is a
     *  Java constant, so a restored tab rebuilds itself from its kind alone. */
    @Override public List<WidgetCodecRef> widgetCodecs() { return List.of(); }

    @Override
    public List<String> pinnedSpawns() {
        // The instructions only. A pinned kind is FILTERED OUT of the picker
        // (PickerTabFlowModule), so pinning a specimen would both cap it at one
        // instance and make it unreachable whenever the pinned path does not
        // run — and docking two of the SAME specimen, one sorted and one not,
        // is a comparison this bench exists to allow.
        return List.of("DocViewWidget");
    }

    private static Map<String, String> introDefaults() {
        var d = new LinkedHashMap<String, String>();
        d.put("title", "Table Workbench");
        d.put("body", String.join("\n",
                "A bench of Relation Grids in shapes the companion demos never take.",
                "The Dish List is six dishes and Minesweeper is a board that fits — so",
                "until this bench existed, **no demo had a row below the fold**, and a",
                "viewport that failed to follow the keyboard could not be seen.",
                "",
                "Open the picker with **➕** and drag a specimen's title bar onto any",
                "pane. Two docked side by side is the point.",
                "",
                "### Giving a specimen the keyboard",
                "",
                "A workspace pane is **inert until you enter it** (RFC 0048's shallow/deep",
                "split): click the pane, press **Enter** to go deep, then click any cell.",
                "The grid has the arrows from then on. Without the Enter the keystrokes",
                "stay with the workspace and the grid looks unresponsive — which is not",
                "the defect this bench is about.",
                "",
                "### What to try",
                "",
                "- Hold **↓** past the bottom edge. The scrollport should come along, one",
                "  row at a time, never a jump.",
                "- Then hold **↑** back to the top. On **Tall**, the cursor should stop",
                "  flush *below* the frozen header — never underneath it. On **Unfrozen",
                "  header**, it should go all the way to the top edge, because there is",
                "  no band in the way.",
                "- **Tab** and **End** on **Wide**, for the same behaviour sideways.",
                "- **Shift+↓** extends a range: the viewport follows the edge you are",
                "  dragging, not the cursor you left behind.",
                "- **Ctrl+A** should move nothing. Neither should **sort by region** —",
                "  a remap is not a place you asked to be taken.",
                "- **scroll away (keep cursor)** desyncs on purpose; **re-reveal cursor**",
                "  demands the follow back.",
                "",
                "Each specimen prints its cursor position, its scroll offsets and a",
                "plain VISIBLE / OFF SCREEN verdict along the bottom. _\"The cursor",
                "moved\"_ and _\"the viewport followed\"_ are two different claims, and",
                "only the second one was ever broken."
        ));
        return d;
    }
}
