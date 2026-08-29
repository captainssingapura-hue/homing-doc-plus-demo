package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.demo.es.animation.CuteAnimal;
import hue.captains.singapura.js.homing.studio.base.SvgDoc;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.composed.Listable;
import hue.captains.singapura.js.homing.studio.base.composed.ParagraphSegment;
import hue.captains.singapura.js.homing.studio.base.composed.RigidNodeContent;
import hue.captains.singapura.js.homing.studio.base.composed.SvgSegment;
import hue.captains.singapura.js.homing.studio.base.composed.UnorderedListSegment;
import hue.captains.singapura.js.homing.studio.base.composed.graph.RigidNode;
import hue.captains.singapura.js.homing.studio.base.composed.text.Line;
import hue.captains.singapura.js.homing.tree.NodeName;
import hue.captains.singapura.js.homing.studio.base.composed.text.Title;
import hue.captains.singapura.js.homing.studio.base.rigid.RigidDocV2;
import hue.captains.singapura.js.homing.studio.base.tree.CatalogueTreeAdapter;
import hue.captains.singapura.js.homing.studio.base.tree.CatalogueTreeNode;
import hue.captains.singapura.js.homing.tree.Category;
import hue.captains.singapura.js.homing.tree.DimensionKey;
import hue.captains.singapura.js.homing.tree.DimensionValue;
import hue.captains.singapura.js.homing.tree.DisplayLabel;
import hue.captains.singapura.js.homing.tree.Kind;
import hue.captains.singapura.js.homing.tree.Summary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * A dedicated demo: the demo studio's <b>catalogue tree</b> (the one the Studio
 * Workspace Navigator draws, via {@link CatalogueTreeAdapter}) mirrored into a
 * foldable {@link RigidDocV2}. It showcases the "bring your own tree" adapter:
 *
 * <ul>
 *   <li><b>Parent-pointer nodes in, a validated tree out</b> — {@link #buildNodes()}
 *       walks the catalogue top-down and emits a flat list of {@link RigidNode}s,
 *       each holding an object reference to its parent (so cycles, multiple
 *       parents, and wrong levels are unrepresentable). The framework inverts and
 *       validates it — no hand-built downward tree, no child-index paths.</li>
 *   <li><b>Name-path identity</b> — content is addressed by the stable
 *       nodeName-chain ({@code "animals/turtle"}), which survives sibling
 *       reordering, rather than a fragile positional path.</li>
 *   <li><b>One content provider</b> — a single {@code Function<CatalogueTreeNode,
 *       RigidNodeContent>} ({@link #contentProvider()}) resolves every node's body
 *       from its source: a leaf gets a captioned {@link UnorderedListSegment} + a
 *       rotating {@link SvgSegment}, a branch a plain {@link ParagraphSegment}.</li>
 * </ul>
 *
 * <p>The graph is built <b>lazily</b> (first request, cached by {@link RigidDocV2})
 * so this doc, which both lives in and mirrors {@link DemoStudio}, defers the walk
 * to request time — dodging a class-init cycle and always reflecting the live tree.</p>
 */
public final class DemoContentTreeDoc {

    private DemoContentTreeDoc() {}

    private static final UUID ID = ComposedDoc.deterministicUuid("rigid:demo-content-tree");
    private static final String TITLE = "Studio Content Tree — mirrored as a RigidDoc";
    private static final String SUMMARY =
            "The demo studio's catalogue tree, mirrored into a foldable RigidDocV2 via the "
          + "nodes+edges adapter: an external content provider gives every node a body — a "
          + "list + illustration on leaves, a summary on branches.";

    /** The doc, backed by the lazily-built node list + the single content provider. */
    public static final RigidDocV2 INSTANCE =
            RigidDocV2.fromNodes(ID, TITLE, SUMMARY, "DEMO",
                    DemoContentTreeDoc::buildNodes, contentProvider());

    /** A few illustrations, used in rotation across leaves (to save effort). */
    private static final List<SvgDoc<CuteAnimal>> ILLUSTRATIONS = List.of(
            illo("Turtle",    new CuteAnimal.turtle()),
            illo("Penguin",   new CuteAnimal.penguin()),
            illo("Crocodile", new CuteAnimal.crocodile()),
            illo("Whale",     new CuteAnimal.whale()),
            illo("Ghost",     new CuteAnimal.ghost()),
            illo("Broom",     new CuteAnimal.broom()));

    private static SvgDoc<CuteAnimal> illo(String name, hue.captains.singapura.js.homing.core.SvgBeing<CuteAnimal> being) {
        return new SvgDoc<>(new SvgRef<>(CuteAnimal.INSTANCE, being), name,
                name + " — a rotating demo illustration.");
    }

    // ── the adapter input: nodes + edges from the catalogue tree ────────────────

    /**
     * Walk the catalogue top-down into a flat list of parent-pointer
     * {@link RigidNode}s: each child is built from its already-constructed parent
     * ({@code parent.child(...)}), so its level is fixed by construction. A slugged,
     * sibling-unique {@link NodeName} gives each node its name-path segment. The
     * normalizer inverts the pointers and validates the whole thing.
     */
    static List<RigidNode<CatalogueTreeNode>> buildNodes() {
        CatalogueTreeNode source = CatalogueTreeAdapter.INSTANCE.adapt(DemoStudio.INSTANCE);
        var all = new ArrayList<RigidNode<CatalogueTreeNode>>();

        // Each node wraps its source catalogue node; content is resolved later by
        // the one provider. The root reads as this demo, not "Demo Studio", and
        // anchors a fixed, readable name-path root.
        RigidNode<CatalogueTreeNode> root = RigidNode.root(source, new NodeName("studio"), title(TITLE));
        all.add(root);
        addChildren(source, root, all);
        return all;
    }

    private static void addChildren(CatalogueTreeNode parentSrc,
            RigidNode<CatalogueTreeNode> parentNode, List<RigidNode<CatalogueTreeNode>> all) {
        var usedNames = new HashSet<String>();
        for (CatalogueTreeNode kidSrc : parentSrc.kids()) {
            String label = dim(kidSrc, DisplayLabel.INSTANCE);
            // The parent object already exists, so parent.child(...) fixes the level
            // (parent.level + 1). The child wraps the source node; no content here.
            RigidNode<CatalogueTreeNode> kidNode =
                    parentNode.child(kidSrc, uniqueSiblingName(label, usedNames), title(label));
            all.add(kidNode);
            addChildren(kidSrc, kidNode, all);
        }
    }

    /**
     * The <b>single</b> content provider — resolves a node's body from its source
     * catalogue node. Dispatches internally (leaf vs branch); a downstream app is
     * free to resolve content however it likes inside this one function.
     */
    private static Function<CatalogueTreeNode, RigidNodeContent> contentProvider() {
        int[] rotation = {0};   // rotates illustrations across leaves, in build order
        return node -> contentFor(node, rotation);
    }

    /** A slugged {@link NodeName}, disambiguated among its siblings ({@code -2}, {@code -3}, …). */
    private static NodeName uniqueSiblingName(String label, Set<String> used) {
        String base = NodeName.slug(label).value();
        if (base.length() > 44) base = base.substring(0, 44).replaceAll("-+$", "");
        if (base.isBlank()) base = "n";
        String candidate = base;
        for (int n = 2; !used.add(candidate); n++) candidate = base + "-" + n;
        return new NodeName(candidate);
    }

    // ── the content provider: one source node → its body ────────────────────────

    /**
     * The content provider — a pure function from a source node to its
     * {@link RigidNodeContent} (caption + segments). It inspects the node but does
     * <b>not</b> build structure: a leaf gets a captioned list + illustration, a
     * branch a plain paragraph.
     */
    private static RigidNodeContent contentFor(CatalogueTreeNode node, int[] rotation) {
        String label    = dim(node, DisplayLabel.INSTANCE);
        String summary  = dim(node, Summary.INSTANCE);
        String kind     = dim(node, Kind.INSTANCE);
        String category = dim(node, Category.INSTANCE);

        if (node.kids().isEmpty()) {
            // Leaf: a homogeneous bullet list (one ParagraphSegment per item) + a rotating illustration.
            var bullets = new ArrayList<Listable>();
            if (!summary.isBlank()) bullets.add(ParagraphSegment.of(summary));
            bullets.add(ParagraphSegment.of("Kind: "     + (kind.isBlank()     ? "—" : kind)));
            bullets.add(ParagraphSegment.of("Category: " + (category.isBlank() ? "—" : category)));
            SvgDoc<CuteAnimal> pick = ILLUSTRATIONS.get(Math.floorMod(rotation[0]++, ILLUSTRATIONS.size()));
            // The optional highlighted caption (the fancy header) — clipped to the Line cap.
            return new RigidNodeContent(Line.optionalPlain(clip(label)), List.of(
                    new UnorderedListSegment(bullets),
                    new SvgSegment(pick, clip(label + " — illustration"))));
        }
        // Branch: a plain paragraph — a simple summary + a line of description.
        String body = (summary.isBlank() ? label : label + " — " + summary)
                + " A section of the demo studio's content tree, mirrored into this RigidDocV2.";
        return new RigidNodeContent(List.of(ParagraphSegment.of(body)));
    }

    private static String dim(CatalogueTreeNode n, DimensionKey key) {
        DimensionValue v = n.dimensions().get(key);
        return v == null ? "" : v.displayText();
    }

    /** Clip to the {@code Line.Plain} cap so a caption/item never overflows. */
    private static String clip(String s) {
        if (s == null) return "";
        return s.length() <= 81 ? s : s.substring(0, 80) + "…";
    }

    /** A node title from a source label, clipped to the {@link Title} cap. */
    private static Title title(String s) {
        String t = (s == null || s.isBlank()) ? "(untitled)" : s;
        if (t.length() > Title.MAX_CHARS) {
            t = t.substring(0, Title.MAX_CHARS - 1) + "…";
        }
        return new Title(t);
    }
}
