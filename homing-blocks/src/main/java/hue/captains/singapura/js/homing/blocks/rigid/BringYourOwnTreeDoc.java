package hue.captains.singapura.js.homing.blocks.rigid;

import hue.captains.singapura.js.homing.blocks.svg.SvgDocDemoDoc;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.image.ImageDoc;
import hue.captains.singapura.js.homing.studio.base.rigid.RigidDoc;

import java.util.Optional;

/**
 * A Building-Blocks guide: <b>Bring Your Own Tree</b> — turning a hierarchy you
 * already own (a catalogue, a filesystem, a query result) into a foldable
 * {@code RigidDocV2} by building a flat list of <b>parent-pointer nodes</b> (each
 * wrapping one of your source nodes) and a single {@code T -> RigidNodeContent}
 * provider. The adapter inverts and validates the tree; content is addressed by a
 * stable <b>name-path</b>, not a child-index.
 *
 * <p>Itself authored through the leveled DSL (a V1 {@code RigidDoc}), but its
 * subject is the general, data-driven V2 path — the DSL is convenient
 * hand-authoring; the adapter is how you mirror an arbitrary tree.</p>
 *
 * @since homing-blocks — RigidDoc "bring your own tree" guide
 */
public final class BringYourOwnTreeDoc {

    private BringYourOwnTreeDoc() {}

    /** A raster {@link ImageDoc} on this module's classpath — the visual-segment demo. */
    private static final ImageDoc DEMO_IMAGE = new ImageDoc(
            "homing-blocks/img/svg-image-dsl-demo.png",
            "image/png",
            "A themed raster asset — a light diamond emblem on an indigo gradient.",
            "A registered ImageDoc, inlined as a data URL.",
            Optional.of(320),
            Optional.of(180));

    public static final RigidDoc INSTANCE = build();

    private static RigidDoc build() {
        return RigidDoc.root(
                    ComposedDoc.deterministicUuid("rigid:bring-your-own-tree"),
                    "RigidDoc — Bring Your Own Tree",
                    "Turn any hierarchy into a foldable doc: build parent-pointer nodes; the adapter inverts and validates the tree.",
                    "BLOCKS")

            .l1("Why bring your own tree")
                .text("""
                        A `RigidDocV2` renders as one **structure** tree plus its **content**, kept apart (RFC 0039). So a hierarchy you already have — a catalogue, a filesystem, a query result — becomes a foldable document without re-authoring it: you build a flat list of **parent-pointer nodes**, answer one question per node (*what is its body?*), and the framework does the rest.""")
            .l1build()

            .l1("Nodes that point to their parent")
                .text("""
                        You build the tree **top-down**, and each node holds an object reference to its **parent** — a root has none. Because a node keeps *one* parent and must be built *after* it, the shape is almost a tree already: multiple parents, cycles, and wrong levels can't even be written down.

                        A node **wraps one of your source nodes** (type `T`) and exposes a URL-safe `NodeName` (its address segment) + a `Title` (its heading). The title is **optional** — omit it and it defaults to the humanized name (`"animals"` → `"Animals"`). No ids: the object *is* the identity, the parent reference *is* the edge. Content isn't stored on the node — it's resolved later, from the source, by one function (next section). Collect every node into a flat list for the adapter.""")
                .code("""
                        var all = new ArrayList<RigidNode<SourceNode>>();

                        // root() is level 0; child() is parent.level + 1, fixed at
                        // construction. Each node WRAPS your source node (a, t).
                        RigidNode<SourceNode> animals = RigidNode.root(a, new NodeName("animals"));   // title -> "Animals"
                        RigidNode<SourceNode> turtle  = animals.child(t, new NodeName("turtle"),      // or pass a Title
                                                                      new Title("Sea Turtle"));
                        all.add(animals);
                        all.add(turtle);""", "java")
            .l1build()

            .l1("The content provider")
                .text("""
                        **One** function `T -> RigidNodeContent`, handed to the adapter and applied to each node's source — structure (the nodes) and content (this function) stay apart. It's free to dispatch however you like inside: a leaf gets a captioned list, a branch a plain paragraph. No per-node content thunks.""")
                .code("""
                        // the single provider — one function, dispatches internally
                        RigidNodeContent contentFor(SourceNode n) {
                            return n.isLeaf()
                                ? new RigidNodeContent(
                                      Line.optionalPlain(n.label()),         // caption (≤ 81)
                                      List.of(new UnorderedListSegment(bullets(n))))
                                : new RigidNodeContent(List.of(ParagraphSegment.of(n.summary())));
                        }""", "java")
            .l1build()

            .l1("Building the doc — the adapter")
                .text("""
                        Hand the node list **and** the one content provider to `RigidDocV2.fromNodes(...)`. `RigidNodeNormalizer` inverts the parent pointers, **validates**, and builds the served `DocTreeV2` — lazily, at first request, resolving each node's body from its source via your function.""")
                .code("""
                        RigidDocV2 doc = RigidDocV2.fromNodes(
                                uuid, "Animals", summary, "GUIDE",
                                () -> all,               // the parent-pointer nodes
                                this::contentFor);       // the one provider: source -> body

                        // register `doc` in your studio's leaves()/docs() like any Doc""", "java")
            .l1build()

            .l1("Name-path identity")
                .text("""
                        Content is addressed by a node's **name-path** — the `/`-joined chain of `NodeName`s from the root (`animals/turtle`) — not a child-index like `1/0`. Insert or reorder siblings and the address is **unchanged**, so anchors, deep-links, and the content lookup all survive edits. Each structure node carries its `nodeName`, and the client rebuilds the same path while walking.""")
            .l1build()

            .l1("What can go wrong — and what can't")
                .text("""
                        The parent-pointer shape makes the expensive faults **unrepresentable** — they never reach validation at all:

                        - **multiple parents** — a node has one `parent` field;
                        - **cycles** — a node is immutable and its parent must exist first, so you can't tie a loop;
                        - a **wrong level** — pinned to `parent.level + 1` at construction time.

                        What remains is checked, each failure a named `MalformedTreeException`:

                        - **not exactly one root** — zero, or more than one, node with no parent;
                        - a node **unreachable** from the root — its parent is outside the list you handed over;
                        - two **siblings sharing a name** — an ambiguous name-path.""")
            .l1build()

            .l1("What a node may hold")
                .text("""
                        A node's content is a bundle of `RigidSegment`s — the inline vocabulary:

                        - **`ParagraphSegment`** — a plain paragraph (a list of `Line.Plain`, flowed).
                        - **`UnorderedListSegment` / `OrderedListSegment`** — a homogeneous list; every item the same `Listable` kind.
                        - **`SvgSegment` / `ImageSegment` / `TableSegment`** — a registered visual `Doc`, by reference, with an optional caption.
                        - **`RelationSegment`** — a typed inline table; **`CodeSegment`** — a verbatim listing.

                        Text is capped by type: a caption or list line is a `Line.Plain` (≤ 81); a title is a `Title` (≤ 66); a `NodeName` is a URL-safe id (≤ 48).

                        The two sub-sections below each host one visual segment, embedded by reference.""")
                .l2("A vector SvgDoc")
                    .text("`.svg(doc)` embeds a registered `SvgDoc` — vector, themable via `currentColor`.")
                    .svg(SvgDocDemoDoc.INSTANCE, "A registered SvgDoc, embedded by reference")
                    .l2build()
                .l2("A raster ImageDoc")
                    .text("`.image(doc)` embeds a registered `ImageDoc` — raster, inlined as a base64 data URL.")
                    .image(DEMO_IMAGE, "A registered ImageDoc, inlined as a data URL")
                    .l2build()
            .l1build()

            .l1("What a node may not hold")
                .text("""
                        The type system fences a node's content. Two things **will not compile** — deliberately, because they are *structure*, and structure is the tree:

                        - a **`ComposedSegment`** (a whole doc grafted inline) — add child nodes instead.
                        - a **`DocumentaryWidget`** (an embedded interactive app) — host widgets in the workspace instead.

                        Lists can't nest either — a list item is a `Listable`, and a list is not — so recursion always lives in the tree, never in a segment.""")
            .l1build()

            .l1("A worked example")
                .text("""
                        The demo studio's `Studio Content Tree — mirrored as a RigidDoc` is exactly this: it walks the studio's own catalogue top-down into a flat list of parent-pointer `RigidNode`s — one per catalogue node, each built from its parent — and serves it via `RigidDocV2`, its content addressed by name-path.

                        Open it from the demo studio's home to see a bring-your-own-tree document rendered by the same viewer as this guide.""")
            .l1build()

            .l1("Summary")
                .text("""
                        - Build your hierarchy top-down as **parent-pointer nodes**, each wrapping a source `T`; the adapter inverts and **validates** it.
                        - The **content provider** is one `T -> RigidNodeContent`, applied to each node's source — structure and content stay apart.
                        - Identity is a stable **name-path**, not a fragile child-index; **nesting is the tree**, never a segment.
                        - The shape makes cycles, multi-parent, and wrong levels *unrepresentable*; `RigidDocV2.fromNodes(...)` serves it, lazily and cached.""")
            .l1build()

            .build();
    }
}
