package hue.captains.singapura.js.homing.blocks.composed;

import hue.captains.singapura.js.homing.studio.base.composed.CodeSegment;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.composed.MarkdownSegment;

import java.util.List;
import java.util.Optional;

/**
 * Live demo ComposedDoc — the doc you read at this catalogue entry IS
 * this ComposedDoc, constructed from a small mix of Markdown + Code
 * segments. The Java source IS the canonical example.
 *
 * <p>Note: this class is a holder for the {@code INSTANCE} constant
 * (which is the actual {@link ComposedDoc}); it isn't itself a {@code Doc}.
 * The catalogue references {@code ComposedDocDemo.INSTANCE} directly.</p>
 *
 * <p>Visual segments (SvgSegment, TableSegment, ImageSegment) are demoed
 * in their own per-block catalogues (SvgDoc, TableDoc, ImageDoc blocks).
 * Here we keep the demo text-only so the focus stays on the segment
 * mechanic rather than on visual setup.</p>
 */
public final class ComposedDocDemo {

    private ComposedDocDemo() {}

    public static final ComposedDoc INSTANCE = build();

    private static ComposedDoc build() {
        var intro = new MarkdownSegment(
                """
                This page is itself a `ComposedDoc` — an ordered list of typed segments.
                The body you're reading is composed of:

                1. **This MarkdownSegment** — free-form prose, headings, lists, links.
                2. **A CodeSegment** — Java code rendered verbatim with the `language-java` class.
                3. **A second MarkdownSegment** — discussing the segment mechanic.

                Each segment renders via its own typed renderer; the orchestrator dispatches
                from a sealed exhaustive switch.

                ## Why ComposedDoc

                Mixed-media docs are common: a tutorial with code samples, a reference
                with embedded diagrams, a case study with tables. ComposedDoc gives you
                a typed shape for those.

                ## TOC

                The framework's TOC builder walks this segment list and contributes
                entries from each segment's caption + (for markdown) extracted H1-H4
                headings. You can see the sidebar on the left of this page.
                """,
                Optional.of("What this demo shows"));

        var code = new CodeSegment(
                """
                public static final ComposedDoc INSTANCE = build();

                private static ComposedDoc build() {
                    var intro = new MarkdownSegment(
                            "...markdown body...",
                            Optional.of("What this demo shows"));

                    var code = new CodeSegment(
                            "...code...",
                            "java",
                            Optional.of("Construction code"));

                    var discussion = new MarkdownSegment(
                            "...more markdown...",
                            Optional.of("Notes"));

                    return ComposedDoc.of(
                            ComposedDoc.deterministicUuid("blocks:composed-demo"),
                            "ComposedDoc — Demo",
                            "Live demo of the ComposedDoc + Segment pattern.",
                            "DEMO",
                            List.of(intro, code, discussion));
                }
                """,
                "java",
                Optional.of("Construction code (this very doc)"));

        var discussion = new MarkdownSegment(
                """
                ## Notes on the construction

                The class itself is `final` with a `private` constructor — it's a
                holder for the `INSTANCE` constant rather than a Doc record. This
                is the convention for ComposedDocs:

                - **Records can't be ComposedDoc** because `ComposedDoc` is itself
                  a concrete record (not an interface). Subtyping doesn't apply.
                - **`build()` is private static** to avoid static initialisation
                  order surprises when segments reference other typed artifacts.
                - **The catalogue registers `INSTANCE` directly** via
                  `Entry.of(catalogue, ComposedDocDemo.INSTANCE)`.

                ## What's missing from this demo

                Visual segments — SvgSegment, TableSegment, ImageSegment — aren't
                shown here. They each require an additional registered Doc
                (SvgDoc, TableDoc, ImageDoc respectively); demonstrating them
                cleanly belongs in their own per-block catalogues. The mechanic
                is the same: construct the segment with a reference to the
                wrapped Doc, append it to the segments list.

                Recursive embedding (ComposedSegment containing another ComposedDoc)
                isn't shown either — it's the focus of RFC 0024 P1c and demoed in
                the self-studio's ProjectJourneyReflection embedding the
                WhyWeDitchedHtmlCaseStudy.
                """,
                Optional.of("Construction notes"));

        return ComposedDoc.of(
                ComposedDoc.deterministicUuid("blocks:composed-demo"),
                "ComposedDoc — Demo",
                "Live demo of the ComposedDoc + Segment pattern using Markdown + Code segments.",
                "DEMO",
                List.of(intro, code, discussion));
    }
}
