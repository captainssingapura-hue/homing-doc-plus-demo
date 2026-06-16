package hue.captains.singapura.js.homing.blocks.rigid;

import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.rigid.RigidDoc;

import java.util.List;

/**
 * RFC 0042 — a worked demo of the leveled tree-builder DSL. A {@link RigidDoc}
 * authored entirely through {@code RigidDoc.root(...).l1(...).l2(...)...}, used
 * as a Building Block so the rigid-tree doc viewer (RFC 0039 + the in-sync TOC
 * fold) has a genuinely <i>nested</i> doc to render — the multi-level fold that
 * a flat {@code ComposedDoc} could never show.
 *
 * <p>It exercises the DSL's range: multi-level nesting (L1→L2→L3), a branch with
 * a lead-in above its children, every inline content kind (prose / markdown /
 * code / table), and a single leaf holding a multi-segment bundle.</p>
 *
 * @since homing-blocks — RFC 0042 demo
 */
public final class RigidDocKitDoc {

    private RigidDocKitDoc() {}

    public static final RigidDoc INSTANCE = build();

    private static RigidDoc build() {
        return RigidDoc.root(
                    ComposedDoc.deterministicUuid("rigid:demo-kit"),
                    "RigidDoc — A Leveled Tree-Builder Demo",
                    "ComposedDoc's successor: a document authored as a leveled tree, so it nests and folds at every level.",
                    "BLOCKS")

            // ── L1: motivation — a branch that is itself a leaf (content, no children) ──
            .l1("Why a leveled tree")
                .text("""
                        A flat `ComposedDoc` is one ordered list of content. It renders, but it has exactly **one** foldable node — its root — so "fold this section" can only mean "collapse the whole document".

                        A `RigidDoc` is a *tree* of titled sections, authored with the leveled builder. Because it nests, it folds at every level — which is the whole reason this demo exists.""")
            .l1build()

            // ── L1: the DSL, with nested L2/L3 children (a branch with a lead-in) ──
            .l1("The DSL")
                .text("Four moves, each spelling the level. The indented call-chain draws the tree it builds.")
                .l2("doc / branch / leaf")
                    .text("`.lN(title)` opens a node one level down; content factories attach to the current node; `.lNbuild()` closes back to the parent; `.build()` on the root yields the doc.")
                    .code("""
                            RigidDoc.root(uuid, "Title", "summary", "DEMO")
                                .l1("Section A")
                                    .text("lead-in prose")
                                    .l2("A.1")
                                        .code("x = 1", "java")
                                    .l2build()
                                .l1build()
                                .build();""", "java")
                    .l2build()
                .l2("Content kinds")
                    .text("A leaf carries one *or more* segments — a bundle. Each kind has its own factory:")
                    .l3("Prose")
                        .text("`.text(...)` is a `.mdad+` paragraph: **bold**, *italic*, `inline code`, and lists are all in the grammar.")
                        .l3build()
                    .l3("Markdown")
                        .markdown("`.markdown(...)` accepts full CommonMark when you need a table or a quote the strict grammar omits.")
                        .l3build()
                    .l3("Code")
                        .text("`.code(body, language)` is a verbatim listing; the language drives the `language-X` class.")
                        .code("public record DocNode(String title, List<Segment> content, List<DocNode> children) {}", "java")
                        .l3build()
                    .l3("Tables")
                        .text("`.relation(headers, rows, caption)` is a typed table:")
                        .relation(
                            List.of("Move", "Opens", "Returns"),
                            List.of(
                                List.of("`.lN(t)`",      "a child node",      "the child builder"),
                                List.of("`.lNbuild()`",  "—",                 "the parent builder"),
                                List.of("`.build()`",    "—",                 "the `RigidDoc` (root only)")),
                            "The leveled builder's vocabulary")
                        .l3build()
                    .l2build()
                .l1build()

            // ── L1: folding — multi-level, to show the fold at depth ──
            .l1("Folding in sync with the TOC")
                .text("Collapsing a node in the table of contents hides its whole subtree's body while the node's own heading and lead-in stay. Try it on the sections below.")
                .l2("Collapse a single section")
                    .text("This L2 section folds on its own — its caret in the TOC hides just this body.")
                    .l2build()
                .l2("A subtree that folds as one")
                    .text("Collapsing *this* node hides everything beneath it, including the deeper section below.")
                    .l3("A deep leaf at L3")
                        .text("This L3 leaf disappears when its L2 ancestor is collapsed, and returns — still in its own fold state — when the ancestor re-expands.")
                        .l3build()
                    .l2build()
                .l1build()

            // ── L1: a single leaf holding a multi-segment bundle ──
            .l1("A leaf with a content bundle")
                .text("This one leaf holds three segments in order — prose, then code, then prose. None of them is a TOC node; they are this node's body (a `ComposedLeaf`).")
                .code("""
                        // a leaf's segments render in order, below the structure, never in the TOC
                        .l1("A leaf with a content bundle")
                            .text("...").code("...", "java").text("...")
                        .l1build()""", "java")
                .text("The flatness that limited a whole `ComposedDoc` is exactly right *here*, at one leaf.")
            .l1build()

            .l1("Summary")
                .text("""
                        - **Structure** is the `.lN` tree — titled, navigable, in the TOC.
                        - **Content** is the segments on each node — anonymous, in the body, never in the TOC.
                        - The compiler enforces the levels: a skipped level, an unclosed node, or a level past the cap will not compile.

                        This is `ComposedDoc`'s successor — and the document you are reading is one.""")
            .l1build()

            .build();
    }
}
