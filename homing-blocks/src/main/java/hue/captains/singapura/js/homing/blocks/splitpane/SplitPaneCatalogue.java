package hue.captains.singapura.js.homing.blocks.splitpane;

import hue.captains.singapura.js.homing.blocks.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;

import java.util.List;

/**
 * Block 6 — SplitPane primitive. First of the three independent workspace
 * primitives (the others being MultiTabPane and Modal). A recursive 2D
 * split-pane layout manager — pure layout, no opinions about content.
 *
 * <p>Ships a guide doc + a top-level demo app. The demo is registered as
 * a {@link Navigable} leaf pointing at {@link SplitPaneDemoApp} — clicking
 * it opens the {@code SingleWidgetMPA} shell that mounts a SplitPane with
 * a fixed 4-quadrant layout (colour swatch, counter button, log panel,
 * prose block). Promoting the demo to a real top-level app — rather than
 * embedding it inside a {@code DocumentaryWidget} segment — matches the
 * shape the future workspace will take: SplitPane lives <i>inside</i> an
 * AppModule's main slot and claims as much viewport as the chrome
 * allows.</p>
 */
public record SplitPaneCatalogue()
        implements L1_Catalogue<BuildingBlocksCatalogue, SplitPaneCatalogue>, DocProvider {

    public static final SplitPaneCatalogue INSTANCE = new SplitPaneCatalogue();

    @Override public BuildingBlocksCatalogue parent() { return BuildingBlocksCatalogue.INSTANCE; }
    @Override public String name()    { return "SplitPane"; }
    @Override public String summary() {
        return "Recursive 2D split-pane layout primitive. Plain DOM, no DomOpsParty, no widget "
             + "or tab knowledge. The substrate beneath the flexible workspace shell, but "
             + "directly reusable for slide layouts, before/after comparisons, side-by-side "
             + "doc views, etc.";
    }
    @Override public String badge()   { return "BLOCK"; }
    @Override public String icon()    { return "⫴"; }

    @Override public List<Entry<SplitPaneCatalogue>> leaves() {
        return List.of(
                Entry.of(this, SplitPaneGuideDoc.INSTANCE),
                // Top-level demo app — opens SplitPaneDemoApp's
                // SingleWidgetMPA shell at /app?app=split-pane-demo.
                // Same shape as the future workspace shell will take:
                // SplitPane fills the main slot, not embedded in a doc.
                Entry.of(this, new Navigable<>(
                        SplitPaneDemoApp.INSTANCE,
                        new SplitPaneDemoApp.Params(),
                        "SplitPane — Live Demo",
                        "A SplitPane mounted as a top-level app with a fixed 4-quadrant "
                      + "layout. Drag the dividers to reflow the quadrants without "
                      + "disturbing each slot's internal state."))
        );
    }

    @Override public List<Doc> docs() {
        return List.of(SplitPaneGuideDoc.INSTANCE);
    }
}
