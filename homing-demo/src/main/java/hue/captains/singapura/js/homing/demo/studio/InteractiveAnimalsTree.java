package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.studio.base.app.tree.ContentTree;
import hue.captains.singapura.js.homing.studio.base.app.tree.TreeBranch;
import hue.captains.singapura.js.homing.studio.base.app.tree.TreeLeaf;
import hue.captains.singapura.js.homing.studio.base.app.tree.TreeNode;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;

import java.util.List;

/**
 * RFC 0016 — interactive 3D counterpart to {@link AnimalsTree}. Same
 * two-branch shape (Animals + Halloween), but each leaf wraps an
 * {@link InteractiveAnimalDoc} {@code ComposedDoc} rather than a plain
 * 2D {@code SvgDoc}. Clicking a leaf opens the per-animal interactive
 * page (canonical SVG + three typed {@code DocumentaryWidget} segments
 * pre-selected to that animal).
 *
 * <p>Tree shape:</p>
 * <pre>
 *   interactive-animals (tree id; root)
 *   ├── animals       (ComposedDoc leaves — InteractiveAnimalDoc)
 *   │   ├── turtle / penguin / crocodile / whale
 *   └── halloween     (ComposedDoc leaves — InteractiveAnimalDoc)
 *       ├── ghost / broom
 * </pre>
 */
public final class InteractiveAnimalsTree {

    private InteractiveAnimalsTree() {}

    public static final ContentTree INSTANCE = build();

    private static ContentTree build() {
        var animalsBranch = new TreeBranch(
                "animals", "Animals",
                "Interactive 3D versions of the cute critters. Each leaf is a ComposedDoc — canonical SVG plus three typed DocumentaryWidget segments (coin, extruder, decomposer) pre-selected to that animal.",
                "CATEGORY", "🐾",
                List.<TreeNode>of(
                        leafFor("turtle",    InteractiveAnimalDoc.ALL.get(0)),
                        leafFor("penguin",   InteractiveAnimalDoc.ALL.get(1)),
                        leafFor("crocodile", InteractiveAnimalDoc.ALL.get(2)),
                        leafFor("whale",     InteractiveAnimalDoc.ALL.get(3))
                ));

        var halloweenBranch = new TreeBranch(
                "halloween", "Halloween",
                "Spooky companions, fully interactive. Same shape as the Animals branch.",
                "CATEGORY", "🎃",
                List.<TreeNode>of(
                        leafFor("ghost", InteractiveAnimalDoc.ALL.get(4)),
                        leafFor("broom", InteractiveAnimalDoc.ALL.get(5))
                ));

        var root = new TreeBranch(
                "", "Interactive Animals & Halloween",
                "Per-animal DocumentaryWidget demos. The same three widget EsModules serve every leaf — only the typed Params(animal) record varies per call.",
                "TREE", "🪄",
                List.of(animalsBranch, halloweenBranch));

        return new ContentTree("interactive-animals", root);
    }

    private static TreeLeaf leafFor(String slug, ComposedDoc doc) {
        return new TreeLeaf(slug, doc.title(), doc.summary(), "INTERACTIVE", "", doc);
    }
}
