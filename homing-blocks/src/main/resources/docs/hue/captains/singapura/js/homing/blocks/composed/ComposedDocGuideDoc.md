# ComposedDoc — Guide

> **A ComposedDoc is an ordered list of typed `Segment`s — the framework's default Doc shape for content richer than plain markdown. Each segment kind has a typed renderer; the renderer dispatches via an exhaustive sealed switch.**

## When to use a ComposedDoc

- You're mixing prose with code blocks, SVGs, tables, images, or other typed visuals
- You want a TOC sidebar generated from the doc's structure
- You want one of the segments to embed another ComposedDoc (recursive composition — see RFC 0024 P1c)
- You want a doc that doesn't have a single content type

If your doc is "just markdown" — use [ProseDoc](#) instead. ComposedDoc is for the mixed case.

## The shape

```java
public final class MyDoc {
    public static final ComposedDoc INSTANCE = build();

    private static ComposedDoc build() {
        var introMd = new MarkdownSegment(
                "Some markdown body...",
                Optional.of("Introduction"));

        var codeListing = new CodeSegment(
                "public record HelloWorld() { ... }",
                "java",
                Optional.of("Example"));

        var prose = new TextSegment.Builder()
                .h2("Discussion")
                .p("Plain paragraph in the typed text grammar.")
                .build();

        return ComposedDoc.of(
                ComposedDoc.deterministicUuid("my-studio:my-doc"),
                "My Document",
                "One-line summary.",
                "DOC",
                List.of(introMd, codeListing, prose));
    }
}
```

The class isn't itself a Doc — it's a holder for the `INSTANCE` constant that *is* the Doc. The `build()` pattern keeps construction logic out of static initialisation order traps.

## The sealed Segment hierarchy

Every Segment is one of these (sealed in `homing-studio-base/.../composed/Segment.java`):

| Segment | Fields | Use for |
|---|---|---|
| `MarkdownSegment` | `(body, Optional<title>)` | Free-form markdown — paragraphs, headings (H1-H4 auto-anchored), lists, code blocks, links |
| `TextSegment` | `(parsed AST, Optional<title>)` | Strict typed prose — paragraphs, lists, quotes, inline emphasis/code/refs. No HTML escape hatch. RFC 0018. |
| `CodeSegment` | `(body, language, Optional<title>)` | Verbatim code listing with `language-X` class for downstream highlighters |
| `SvgSegment` | `(SvgDoc, Optional<captionOverride>)` | Reference to a registered SvgDoc, rendered inline with caption |
| `TableSegment` | `(TableDoc, Optional<captionOverride>)` | Reference to a registered TableDoc, rendered inline |
| `ImageSegment` | `(ImageDoc, Optional<captionOverride>)` | Reference to a registered ImageDoc, rendered inline |
| `ComposedSegment` | `(ComposedDoc, Optional<captionOverride>)` | Recursive — embed another ComposedDoc inline. Cycle-detected. RFC 0024 P1c. |
| `DocumentaryWidget` | `(widget, params, Optional<captionOverride>)` | Embed an interactive AppModule into a sub-region |

Every dispatch is an exhaustive switch on the sealed permits; new variants extend the permits list and the compiler tells you everywhere that needs updating.

## TOC building

ComposedDoc's `buildToc()` walks the segments and produces a TOC sidebar:

- **MarkdownSegment** — contributes its title (if any) + extracted H1-H4 headings from the body
- **TextSegment** — contributes its title (if any). No internal-heading extraction (T0-T4 grammar has no headings)
- **CodeSegment** — contributes its title (if any). Code body is opaque to TOC.
- **SvgSegment / TableSegment / ImageSegment / ComposedSegment** — contribute their `resolvedCaption()` (the override or the wrapped doc's title)
- **DocumentaryWidget** — contributes its `resolvedCaption()`

The renderer (ComposedWidget) emits the TOC sidebar from this list. Anchors match the segments' generated IDs (`seg-N` for top-level, `seg-N-hM` for markdown headings).

## Registering with a catalogue

Same pattern as ProseDoc:

```java
@Override public List<Entry<MyCatalogue>> leaves() {
    return List.of(
            Entry.of(this, MyComposedDoc.INSTANCE));
}

@Override public List<Doc> docs() {
    return List.of(MyComposedDoc.INSTANCE);
}
```

The framework's `Bootstrap.harvestSyntheticFromLeaves` also picks up ComposedDocs that appear ONLY in catalogue `leaves()` (not in any DocProvider's `docs()`) — but it's cleaner to explicitly list them in both.

## Gotchas

- **Static init order.** `INSTANCE = build()` references later segments that reference other typed artifacts — order them so each reference is to an already-initialised field. The `build()` private-static-method pattern is the canonical fix.
- **Segment IDs are sequential.** `seg-0`, `seg-1`, etc. — generated from list position. Reordering segments changes the anchor URLs. Stable anchors require stable order.
- **References from text use the doc's `references()` list.** `[label](#ref:name)` resolves `name` to a `Reference` entry. Cross-segment references inside a single ComposedDoc are by `seg-N-hM` anchor IDs.
- **`ComposedDoc.deterministicUuid("seed")` produces a stable UUID from a string seed.** Use it when the doc isn't catalogue-authored from an external system; the seed is a stable identity hint.

## See also

- **ComposedDoc — Demo** (sibling) — a live ComposedDoc with one of each segment kind. The Java source IS the canonical "copy this" reference.
- **RFC 0019** (in self-studio) — the ComposedDoc RFC; deep design context.
- **RFC 0024 P1c** — recursive composedDoc via the `ComposedSegment` variant.
