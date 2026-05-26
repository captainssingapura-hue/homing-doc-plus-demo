package hue.captains.singapura.js.homing.blocks.multitabpane;

import hue.captains.singapura.js.homing.blocks.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;

import java.util.List;

/**
 * Block 7 — MultiTabPane primitive. Layers tabs on top of {@link
 * hue.captains.singapura.js.homing.studio.base.ui.layout.SplitPaneModule}
 * with a <b>conserved budget</b>: total tab capacity across the workspace
 * is fixed (default 16), and per-pane capacity is derived from depth in
 * the split tree as {@code budget / 2^depth}.
 *
 * <p>Two operations — {@code split} and {@code merge} — are mathematical
 * inverses. Splitting halves capacity for each child; merging two leaf
 * siblings doubles the surviving pane's capacity. There is no separate
 * close-pane op; the only way to reduce pane count is merge, and the
 * halving math is its own rescue.</p>
 *
 * <p>The demo opens with the 2x2 default starter (depth 2, capacity 4
 * per pane) and exposes split/merge via three corner buttons per pane
 * (⇆ split-H, ⇅ split-V, ⤢ merge-with-sibling). Each pane shows a
 * capacity pill (e.g., "3 / 4") so the budget is always visible.</p>
 */
public record MultiTabPaneCatalogue()
        implements L1_Catalogue<BuildingBlocksCatalogue, MultiTabPaneCatalogue>, DocProvider {

    public static final MultiTabPaneCatalogue INSTANCE = new MultiTabPaneCatalogue();

    @Override public BuildingBlocksCatalogue parent() { return BuildingBlocksCatalogue.INSTANCE; }
    @Override public String name()    { return "MultiTabPane"; }
    @Override public String summary() {
        return "Tabs layered on SplitPane with a conserved budget. Total tab capacity is "
             + "fixed (default 16); per-pane capacity is derived from depth as "
             + "budget/2^depth. Split halves capacity; merge doubles it. The two are "
             + "mathematical inverses — no separate close-pane op. 2x2 starter default.";
    }
    @Override public String badge()   { return "BLOCK"; }
    @Override public String icon()    { return "🗂"; }

    @Override public List<Entry<MultiTabPaneCatalogue>> leaves() {
        return List.of(
                Entry.of(this, MultiTabPaneGuideDoc.INSTANCE),
                Entry.of(this, new Navigable<>(
                        MultiTabPaneDemoApp.INSTANCE,
                        new MultiTabPaneDemoApp.Params(),
                        "MultiTabPane — Live Demo",
                        "Workspace-style 2x2 starter (depth 2, capacity 4 per pane). Split, "
                      + "merge, add and close tabs. Watch the capacity pill stay honest as "
                      + "the layout reshuffles."))
        );
    }

    @Override public List<Doc> docs() {
        return List.of(MultiTabPaneGuideDoc.INSTANCE);
    }
}
