package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgGroup;
import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.demo.es.CuteAnimal;
import hue.captains.singapura.js.homing.studio.base.SvgDoc;
import hue.captains.singapura.js.homing.studio.base.app.tree.ContentTree;
import hue.captains.singapura.js.homing.studio.base.app.tree.TreeBranch;
import hue.captains.singapura.js.homing.studio.base.app.tree.TreeLeaf;
import hue.captains.singapura.js.homing.studio.base.app.tree.TreeNode;

import java.util.List;

/**
 * RFC 0016 demo — categorises the {@link CuteAnimal} SVG beings into two
 * sub-branches under one ContentTree: <b>Animals</b> and <b>Halloween</b>.
 * Each leaf wraps an {@link SvgDoc} rendered by the framework's
 * registered {@code SvgViewer}.
 *
 * <p>The interactive 3D counterparts to these SVGs live not as tree
 * leaves but as {@link InteractiveAnimalDoc} entries — one {@code ComposedDoc}
 * per animal, each embedding the canonical SVG together with three typed
 * {@code DocumentaryWidget} segments (coin gallery, extruder,
 * decomposer) pre-selected to that animal via typed {@code Params}.
 * The Doc-shaped framing carries the framework's chrome around all
 * the typed segments uniformly; widgets are isolated islands inside
 * that frame.</p>
 *
 * <p>Tree shape:</p>
 * <pre>
 *   animals (tree id; root)
 *   ├── animals       (2D SVG leaves — SvgDoc kind)
 *   │   ├── turtle / penguin / crocodile / whale
 *   └── halloween     (2D SVG leaves — SvgDoc kind)
 *       ├── ghost / broom
 * </pre>
 */
public final class AnimalsTree {

    private AnimalsTree() {}

    public static final ContentTree INSTANCE = build();

    private static ContentTree build() {
        var animalsBranch = new TreeBranch(
                "animals", "Animals",
                "Cute critters from around the world. Static 2D SVG leaves — each is an SvgDoc rendered by the framework's SvgViewer.",
                "CATEGORY", "🐾",
                List.<TreeNode>of(
                        svgLeaf("turtle",    "Turtle",    "Slow, steady, ancient.",        new CuteAnimal.turtle()),
                        svgLeaf("penguin",   "Penguin",   "Cold-weather waddler.",         new CuteAnimal.penguin()),
                        svgLeaf("crocodile", "Crocodile", "Patient, toothy, prehistoric.", new CuteAnimal.crocodile()),
                        svgLeaf("whale",     "Whale",     "Largest animal alive.",         new CuteAnimal.whale())
                ));

        var halloweenBranch = new TreeBranch(
                "halloween", "Halloween",
                "Spooky companions for the season. Same shape as the Animals branch.",
                "CATEGORY", "🎃",
                List.<TreeNode>of(
                        svgLeaf("ghost", "Ghost", "Boo.",          new CuteAnimal.ghost()),
                        svgLeaf("broom", "Broom", "Witch's ride.", new CuteAnimal.broom())
                ));

        var root = new TreeBranch(
                "", "Animals & Halloween",
                "Cute SVG critters, categorised. The interactive 3D counterparts live in the \"Turtle, Interactive\" ComposedDoc — see the home tile.",
                "TREE", "🌳",
                List.of(animalsBranch, halloweenBranch));

        return new ContentTree("animals", root);
    }

    /** Build a tree leaf wrapping an {@link SvgDoc} that points at the given
     *  {@link SvgBeing}. The leaf's metadata is used for the tile display; the
     *  SvgDoc's URL routes to {@code SvgViewer} via the polymorphic doc viewer. */
    private static <G extends SvgGroup<G>> TreeLeaf svgLeaf(
            String slug, String name, String summary, SvgBeing<G> being) {
        @SuppressWarnings("unchecked")
        SvgRef<G> ref = new SvgRef<>((G) CuteAnimal.INSTANCE, being);
        var doc = new SvgDoc<>(ref, name, summary);
        return new TreeLeaf(slug, name, summary, "ANIMAL", "", doc);
    }
}
