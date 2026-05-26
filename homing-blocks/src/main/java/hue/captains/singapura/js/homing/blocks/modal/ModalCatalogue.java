package hue.captains.singapura.js.homing.blocks.modal;

import hue.captains.singapura.js.homing.blocks.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;

import java.util.List;

/**
 * Block 8 — Modal primitive. A standalone draggable, resizable floating
 * panel. Self-contained: no dependency on SplitPane or MultiTabPane.
 *
 * <p>Reusable beyond the workspace. The MultiTabPane's detach-to-modal
 * feature is one consumer, but the primitive itself has no awareness of
 * tabs. Other use cases:</p>
 *
 * <ul>
 *   <li>Settings dialogs — opened from a chrome toolbar, contains a form.</li>
 *   <li>Floating tool palettes — kept on-screen while interacting with
 *       primary content; user repositions and resizes.</li>
 *   <li>Picture-in-picture viewers — secondary content the user wants to
 *       glance at without context-switching.</li>
 *   <li>Detached widgets — torn off from a tabbed container.</li>
 *   <li>Custom confirmation prompts — when a default browser confirm()
 *       is too coarse.</li>
 *   <li>Inspector windows — devtools-style panels showing live state.</li>
 * </ul>
 *
 * <p>The demo shows three of these use cases side-by-side in one app so
 * the reusability is visible at a glance.</p>
 */
public record ModalCatalogue()
        implements L1_Catalogue<BuildingBlocksCatalogue, ModalCatalogue>, DocProvider {

    public static final ModalCatalogue INSTANCE = new ModalCatalogue();

    @Override public BuildingBlocksCatalogue parent() { return BuildingBlocksCatalogue.INSTANCE; }
    @Override public String name()    { return "Modal"; }
    @Override public String summary() {
        return "A standalone draggable, resizable floating panel. Self-contained — no "
             + "dependency on SplitPane or MultiTabPane. Reusable for settings dialogs, "
             + "tool palettes, picture-in-picture viewers, detached widgets, custom "
             + "prompts, inspector windows. The Workspace's tab-detach feature is one "
             + "consumer; the primitive itself has no awareness of tabs.";
    }
    @Override public String badge()   { return "BLOCK"; }
    @Override public String icon()    { return "🪟"; }

    @Override public List<Entry<ModalCatalogue>> leaves() {
        return List.of(
                Entry.of(this, ModalGuideDoc.INSTANCE),
                Entry.of(this, new Navigable<>(
                        ModalDemoApp.INSTANCE,
                        new ModalDemoApp.Params(),
                        "Modal — Live Demo",
                        "Three modals open side-by-side showcasing different use cases: a "
                      + "settings dialog, a floating colour palette, and a live inspector. "
                      + "Drag the title bars to reposition; drag the corners or edges to "
                      + "resize. Each modal is an independent instance — no coupling."))
        );
    }

    @Override public List<Doc> docs() {
        return List.of(ModalGuideDoc.INSTANCE);
    }
}
