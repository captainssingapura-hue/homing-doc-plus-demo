package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.demo.es.CuteAnimal;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocId;
import hue.captains.singapura.js.homing.studio.base.SvgDoc;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.composed.DocTree;
import hue.captains.singapura.js.homing.studio.base.composed.DocTreeJsonWriter;
import hue.captains.singapura.js.homing.studio.base.composed.DocTreeSource;
import hue.captains.singapura.js.homing.studio.base.composed.Listable;
import hue.captains.singapura.js.homing.studio.base.composed.ParagraphSegment;
import hue.captains.singapura.js.homing.studio.base.composed.RigidNodeContent;
import hue.captains.singapura.js.homing.studio.base.composed.SvgSegment;
import hue.captains.singapura.js.homing.studio.base.composed.UnorderedListSegment;
import hue.captains.singapura.js.homing.studio.base.composed.text.Line;
import hue.captains.singapura.js.homing.studio.base.composed.text.Title;
import hue.captains.singapura.js.homing.studio.base.rigid.DocNode;
import hue.captains.singapura.js.homing.studio.base.rigid.RigidDoc;
import hue.captains.singapura.js.homing.studio.base.rigid.RigidDocNormalizer;
import hue.captains.singapura.js.homing.studio.base.tree.CatalogueTreeAdapter;
import hue.captains.singapura.js.homing.studio.base.tree.CatalogueTreeNode;
import hue.captains.singapura.js.homing.tree.Category;
import hue.captains.singapura.js.homing.tree.DimensionKey;
import hue.captains.singapura.js.homing.tree.DimensionValue;
import hue.captains.singapura.js.homing.tree.DisplayLabel;
import hue.captains.singapura.js.homing.tree.Kind;
import hue.captains.singapura.js.homing.tree.Summary;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A dedicated demo: the demo studio's <b>catalogue tree</b> (the one the Studio
 * Workspace Navigator draws, via {@link CatalogueTreeAdapter}) mirrored into a
 * foldable {@link RigidDoc}. It showcases two things at once:
 *
 * <ul>
 *   <li><b>RigidDoc supports any tree</b> — this doc is built by walking an
 *       existing tree straight into {@link DocNode}s ({@link RigidDoc#fromNode}),
 *       not through the leveled DSL. The DSL is just convenient authoring.</li>
 *   <li><b>An external content provider per node</b> — {@link #convert} gives
 *       every node its own body from supported segment kinds only: a leaf gets a
 *       {@link SimpleListSegment} (the inline "simplified composed doc") plus a
 *       rotating {@link SvgSegment} illustration; a branch gets a short
 *       {@link MarkdownSegment} summary + description.</li>
 * </ul>
 *
 * <p>Because this doc both lives in and mirrors {@link DemoStudio}, its tree is
 * built <b>lazily</b> (first {@link #toDocTree()} call, cached) via
 * {@link DocTreeSource} — deferring the catalogue walk to request time avoids a
 * class-init cycle and always reflects the live tree.</p>
 */
public final class DemoContentTreeDoc implements Doc, DocTreeSource {

    public static final DemoContentTreeDoc INSTANCE = new DemoContentTreeDoc();

    private DemoContentTreeDoc() {}

    private static final UUID ID = ComposedDoc.deterministicUuid("rigid:demo-content-tree");
    private static final String TITLE = "Studio Content Tree — mirrored as a RigidDoc";
    private static final String SUMMARY =
            "The demo studio's catalogue tree, mirrored into a foldable RigidDoc: an external "
          + "content provider gives every node a body — a list + illustration on leaves, a summary on branches.";

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

    /** Built once, at first request (No static-init cycle). */
    private volatile DocTree cached;

    @Override
    public DocTree toDocTree() {
        DocTree t = cached;
        if (t == null) {
            t = RigidDocNormalizer.INSTANCE.toDocTree(buildRigid());
            cached = t;
        }
        return t;
    }

    private static RigidDoc buildRigid() {
        CatalogueTreeNode root = CatalogueTreeAdapter.INSTANCE.adapt(DemoStudio.INSTANCE);
        DocNode raw = convert(root, new int[]{0});
        // The root reads as this demo, not as "Demo Studio".
        DocNode docRoot = new DocNode(title(TITLE), raw.content(), raw.children());
        return RigidDoc.fromNode(ID, SUMMARY, "DEMO", docRoot);
    }

    /** The external content provider: a catalogue node → its {@link RigidNodeContent}, recursively. */
    private static DocNode convert(CatalogueTreeNode node, int[] rotation) {
        String label    = dim(node, DisplayLabel.INSTANCE);
        String summary  = dim(node, Summary.INSTANCE);
        String kind     = dim(node, Kind.INSTANCE);
        String category = dim(node, Category.INSTANCE);

        var children = new ArrayList<DocNode>();
        for (CatalogueTreeNode kid : node.kids()) {
            children.add(convert(kid, rotation));
        }

        final RigidNodeContent content;
        if (children.isEmpty()) {
            // Leaf: a homogeneous bullet list (one ParagraphSegment per item) + a rotating illustration.
            var bullets = new ArrayList<Listable>();
            if (!summary.isBlank()) bullets.add(ParagraphSegment.of(summary));
            bullets.add(ParagraphSegment.of("Kind: "     + (kind.isBlank()     ? "—" : kind)));
            bullets.add(ParagraphSegment.of("Category: " + (category.isBlank() ? "—" : category)));
            SvgDoc<CuteAnimal> pick = ILLUSTRATIONS.get(Math.floorMod(rotation[0]++, ILLUSTRATIONS.size()));
            // Leaves carry the optional highlighted caption (the fancy header) on the
            // content wrapper — clipped to the single-plain-line cap.
            content = new RigidNodeContent(Line.optionalPlain(clip(label)), List.of(
                    new UnorderedListSegment(bullets),
                    new SvgSegment(pick, clip(label + " — illustration"))));
        } else {
            // Branch: a plain paragraph — a simple summary + a line of description. No caption.
            String body = (summary.isBlank() ? label : label + " — " + summary)
                    + " A section of the demo studio's content tree, mirrored into this RigidDoc.";
            content = new RigidNodeContent(List.of(ParagraphSegment.of(body)));
        }

        return new DocNode(title(label), content.caption(), content.segments(), children);
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

    /** A node title from a source label, clipped to the {@link Title} cap (a source
     *  label — e.g. an ImageDoc's alt text — can exceed it, so downstream clips). */
    private static Title title(String s) {
        String t = (s == null || s.isBlank()) ? "(untitled)" : s;
        if (t.length() > Title.MAX_CHARS) {
            t = t.substring(0, Title.MAX_CHARS - 1) + "…";
        }
        return new Title(t);
    }

    // ── Doc protocol ──────────────────────────────────────────────────────────
    @Override public UUID    uuid()          { return ID; }
    @Override public DocId   id()            { return new DocId.ByUuid(ID); }
    @Override public String  title()         { return TITLE; }
    @Override public String  summary()       { return SUMMARY; }
    @Override public String  category()      { return "DEMO"; }
    @Override public String  kind()          { return "composed"; }   // routes to the doc-tree viewer
    @Override public String  url()           { return "/app?app=doc-tree-viewer&id=" + ID; }
    @Override public String  contentType()   { return "application/json; charset=utf-8"; }
    @Override public String  fileExtension() { return ""; }

    @Override public String contents() {
        return DocTreeJsonWriter.INSTANCE.write(toDocTree(), ID.toString());
    }
}
