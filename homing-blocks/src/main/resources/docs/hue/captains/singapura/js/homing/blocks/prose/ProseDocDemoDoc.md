# ProseDoc — Demo

> **This doc is itself a ProseDoc. The Java record `ProseDocDemoDoc` declares the metadata; this markdown file is its body. The framework's `ClasspathMarkdownDoc` resolved the .md by the Java record's package path; the renderer parsed it with marked.js into the theme-styled output you're reading.**

## Why this is the demo

You don't need a separate "rendered preview" abstraction — what you see *is* the framework's actual rendering. Every Doc in this Building Blocks studio is rendered through the same pipeline; this doc demonstrates the simplest variant of that pipeline (markdown body, no segments).

## What's happening structurally

1. The catalogue `ProseDocCatalogue` lists this doc in its `leaves()` via `Entry.of(this, ProseDocDemoDoc.INSTANCE)`.
2. The catalogue also lists it in `docs()` (via `DocProvider`) so it's registered in `DocRegistry` by UUID `b10c4500-0002-4001-8000-000000000002`.
3. When you click the doc's tile, the framework navigates to `/app?app=composed-viewer&id=<this-uuid>`. (Yes — even ProseDoc renders through ComposedViewer; ProseDoc's content is converted to a single MarkdownSegment.)
4. The fake-AppModule shell wraps the chrome; the body fetches this doc's markdown and renders it.

## Sample markdown features

Just to show the rendering covers the common cases:

### Inline emphasis + code

You can use *italic*, **bold**, `inline code`, and combinations like ***bold italic***.

### Lists

Unordered:

- First item
- Second item
- Third item

Ordered:

1. Step one
2. Step two
3. Step three

### Block quote

> Block quotes appear in italic with a left-border treatment — the framework's theme handles this.

### Fenced code

```java
public record HelloWorld() {
    public static final HelloWorld INSTANCE = new HelloWorld();
    public String greet() { return "Hello, world."; }
}
```

### Links

Both inline and to other Docs (in the same studio):

- An external link: [the Homing project](https://github.com/captainssingapura-hue/japjs)
- An anchor inside this doc: jump to [Sample markdown features](#sample-markdown-features) — the framework's TOC builder picks up H2/H3 headings as anchors

### Tables

| Column A | Column B | Column C |
|---|---|---|
| 1 | one | I |
| 2 | two | II |
| 3 | three | III |

Markdown tables render natively. For more typed tabular content, use `TableDoc` instead (separate block).

## What's NOT in this demo

- **No typed Params.** ProseDocs aren't parameterised — they're static documents. If you need parameterised content (URL takes `?id=...`), use a Doc kind that supports it (e.g., a wrapped Widget).
- **No segments.** This is a single markdown body. For mixed-media content (prose + SVG + code + tables), use `ComposedDoc` — separate block in this catalogue.
- **No references.** This doc's `references()` returns an empty list. If you cite other docs, use `DocReference` entries; the markdown syntax is `[label](#ref:name)`.

## How you'd write one

1. Create `YourDoc.java` implementing `ClasspathMarkdownDoc`. Pick a fresh UUID. Set title, summary, category.
2. Create `src/main/resources/docs/<your-package-path>/YourDoc.md`. Write markdown.
3. Register the doc in some catalogue's `leaves()` (for the catalogue card) and `docs()` (for DocRegistry).
4. Build. The doc is live at `/app?app=composed-viewer&id=<uuid>`.

That's the whole pattern. See `ProseDocDemoDoc.java` + `ProseDocDemoDoc.md` in this repo for the literal source.
