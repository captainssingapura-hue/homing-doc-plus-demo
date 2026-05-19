package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.demo.es.CuteAnimal;
import hue.captains.singapura.js.homing.studio.base.SvgDoc;
import hue.captains.singapura.js.homing.studio.base.composed.CodeSegment;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.composed.ImageSegment;
import hue.captains.singapura.js.homing.studio.base.composed.SvgSegment;
import hue.captains.singapura.js.homing.studio.base.composed.TableSegment;
import hue.captains.singapura.js.homing.studio.base.composed.TextSegment;

import java.util.List;
import java.util.Optional;

/**
 * RFC 0019 — composed document interleaving prose with every typed segment
 * kind: {@link TextSegment}, {@link SvgSegment}, {@link TableSegment},
 * and {@link ImageSegment}. After Phase 4 the prose segments are
 * strict-typed (no markdown escape hatch, no body headings) — every
 * section heading flows through the segment title field; lists, inline
 * emphasis, and blockquotes all parse under the audit-driven
 * {@code .mdad+} grammar.
 *
 * <p>Wired into {@link DemoStudio} as a catalogue leaf; reached through
 * the framework's polymorphic doc viewer at
 * {@code /app?app=composed-viewer&id=<uuid>}.</p>
 */
public final class ComposedDemoDoc {

    private ComposedDemoDoc() {}

    public static final ComposedDoc INSTANCE = build();

    private static ComposedDoc build() {
        // Same SvgDoc instance metadata as the one wrapped by AnimalsTree's
        // turtle leaf (RFC 0016) — keeps DocRegistry's record-equality
        // collision check happy when both contribute the same UUID.
        var turtleRef = new SvgRef<>(CuteAnimal.INSTANCE, new CuteAnimal.turtle());
        var turtleDoc = new SvgDoc<>(turtleRef, "Turtle", "Slow, steady, ancient.");

        var intro = new TextSegment(
                """
                This page is a single **ComposedDoc** (RFC 0019), interleaving every typed segment kind the framework supports — text, SVG, table, image — with no HTML escape hatch anywhere on the page.
                """,
                Optional.of("Introduction"));

        var lookingAt = new TextSegment(
                """
                - The prose blocks are `TextSegment`s, parsed server-side under the strict `.mdad+` grammar; the renderer is a tiny AST walk.
                - The turtle below is an `SvgSegment` — a thin proxy to a registered `SvgDoc`. The canonical SVG isn't duplicated; the segment carries the reference plus an optional caption.
                - The status grid is a `TableSegment` proxying a registered `TableDoc` — typed cells with status badges, no HTML hand-rolled into the prose.
                - The label image is an `ImageSegment` proxying a registered `ImageDoc` — bytes shipped on the classpath, served as a base64 data URL.
                - The TOC sidebar is **server-derived** (built by `ComposedDoc.buildToc()` in Java) and shipped as part of the JSON payload. No client-side heading-walking.
                """,
                Optional.of("What you're looking at"));

        var turtleSeg = new SvgSegment(
                turtleDoc,
                Optional.of("Figure 1 — the demo studio mascot, themed via currentColor (RFC 0017)."));

        var whyComposed = new TextSegment(
                """
                The doctrine *Typed Content Vocabulary* — "you don't really need HTML, just SVGs" — says: pick a small set of typed content kinds, ban the HTML escape hatch, and prose stays portable. Text for words, SVG for visuals, table-as-JSON for tabular data, images by reference. That's it.
                """,
                Optional.of("Why composed instead of HTML?"));

        var props = new TextSegment(
                """
                1. **Themability** — SVG fragments inherit the active theme via `currentColor` and `var(--color-*)` (RFC 0017). Table cells inherit the theme through framework tokens. Raster images are Raw tier (no theming attempted).
                2. **Scannability** — the `TextSegment` parser is the conformance gate. Anything outside the audit-driven grammar fails at construction time with a precise line and column.
                3. **Reusability** — every visual is its own registered Doc, citable by UUID from anywhere.
                """,
                Optional.of("Properties earned by the discipline"));

        var statusGrid = new TextSegment(
                """
                Below, the same `TableDoc` that powers the standalone "Phase Status" tile on the demo home — embedded here as a `TableSegment`. The canonical artifact is the TableDoc; the segment is the per-appearance proxy.
                """,
                Optional.of("The status grid"));

        var tableSeg = new TableSegment(
                TableDemoDoc.INSTANCE,
                Optional.of("Figure 2 — the phase-rollout grid, embedded inline."));

        var imagePrologue = new TextSegment(
                """
                Inline `ImageSegment` — proxying the same `ImageDoc` that also opens standalone from the home tile. Bytes ship on the classpath; the viewer reads them via a base64 data URL in the JSON envelope.
                """,
                Optional.of("And one image"));

        var imageSeg = new ImageSegment(
                ImageDemoDoc.INSTANCE,
                Optional.of("Figure 3 — the ImageDoc kind, in action."));

        var codePrologue = new TextSegment(
                """
                A `CodeSegment` carries a source-code listing verbatim. Theme styling (monospace font, inverted surface, horizontal overflow) is inherited from the chrome — no per-segment CSS. The `language` field becomes a `class="language-X"` on the `<code>` element; downstream that wants syntax highlighting can attach a highlighter without framework cooperation.
                """,
                Optional.of("And one code listing"));

        var codeSeg = new CodeSegment(
                """
                public record ComposedDoc(
                        UUID            uuid,
                        String          title,
                        String          summary,
                        String          category,
                        List<Segment>   segments,
                        List<Reference> references
                ) implements Doc {
                    @Override public String kind() { return "composed"; }
                    @Override public String url()  { return "/app?app=composed-viewer&id=" + uuid; }
                }
                """,
                "java",
                Optional.of("Figure 4 — `ComposedDoc` itself, as a `CodeSegment`."));

        var whatsNext = new TextSegment(
                """
                - Phase 5: rewrite the "Why we ditched HTML" case study entirely in this vocabulary — self-proof.
                - Phase 6: retrofit selected existing ProseDocs to ComposedDoc where they'd benefit.
                """,
                Optional.of("What's next"));

        return ComposedDoc.of(
                ComposedDoc.deterministicUuid("demo:composed:phase1"),
                "Composed Doc — Phase 4 (strict text)",
                "Strict-typed text + SVG + table + image segments, end-to-end. Every prose block parses under the audit-driven .mdad+ grammar.",
                "DEMO",
                List.of(intro, lookingAt, turtleSeg, whyComposed, props,
                        statusGrid, tableSeg, imagePrologue, imageSeg,
                        codePrologue, codeSeg, whatsNext));
    }
}
