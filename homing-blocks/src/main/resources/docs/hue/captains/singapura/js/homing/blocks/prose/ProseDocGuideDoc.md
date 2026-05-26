# ProseDoc — Guide

> **A ProseDoc is a Doc whose body is a markdown file on the classpath. You implement `ClasspathMarkdownDoc` as a Java record + place a matching `.md` file alongside on the classpath. The framework auto-resolves the markdown by the Java type's package path.**

## The two artifacts

For every ProseDoc, two files:

**`MyDoc.java`** — the Java record:

```java
public record MyDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("...");
    public static final MyDoc INSTANCE = new MyDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "My Document"; }
    @Override public String summary() { return "One-line summary..."; }
    @Override public String category(){ return "DOC"; }
    @Override public List<Reference> references() { return List.of(); }
}
```

**`MyDoc.md`** — the markdown body, placed at:

```
src/main/resources/docs/<same-package-path>/MyDoc.md
```

For a Java file at `src/main/java/com/example/docs/MyDoc.java`, the markdown goes at `src/main/resources/docs/com/example/docs/MyDoc.md`.

## What each field does

- **`uuid()`** — stable, unique identifier. Once published, never changes (it's how cross-doc references find this doc). Use `UUID.fromString("...")` with a hardcoded UUID; generate a fresh one (e.g., `uuidgen`) on first creation.
- **`title()`** — visible label. Used in the doc-reader chrome, breadcrumbs, listings.
- **`summary()`** — one-line description. Used in catalogue card listings + introspection.
- **`category()`** — badge label. Conventional values: `"DOC"` (default), `"GUIDE"`, `"DEMO"`, `"CASE_STUDY"`, `"RFC"`, `"DOCTRINE"`, `"RELEASE"`. The renderer picks a CSS badge class from this; arbitrary strings render as plain badges.
- **`references()`** — typed cross-references. Doc bodies use `[label](#ref:name)` markdown syntax to cite; the references list resolves `name` to a typed Doc / external URL / image.

## Markdown body conventions

The body is plain markdown — what marked.js renders. Headings, lists, code blocks, links, emphasis. The framework's theme styles the result (typed CSS classes via RFC 0017).

Common idioms:

- **Lead with a blockquote** for the doc's thesis. The framework's theme styles `<blockquote>` distinctly; readers see the headline.
- **Use H2 for major sections, H3 for sub-sections.** The framework's TOC builder (when this doc is embedded in a ComposedDoc) extracts headings.
- **Code blocks** with language tags — ` ```java ` etc. — render with the language class attached; downstream highlighters work without framework cooperation.
- **References** — `[doctrine](#ref:doc-mfs)` resolves to the entry named `doc-mfs` in the `references()` list. See the References block for typed `Reference` variants.

## Registering with a catalogue

ProseDocs surface in the studio when:

1. **Listed in a Catalogue's `leaves()`** via `Entry.of(catalogue, MyDoc.INSTANCE)`. The catalogue card lists the doc; clicking opens it.
2. **Returned by `DocProvider.docs()`** on a catalogue. This contributes the doc to the `DocRegistry` so `/doc?id=<uuid>` resolves it. Without this, the doc isn't reachable by UUID.

Studios typically have the catalogue implement both `L<N>_Catalogue` and `DocProvider` and list the doc in both `leaves()` and `docs()`.

## Gotchas

- **UUID collisions are detected at boot.** Two Docs with the same UUID fail `DocRegistry` validation. Use fresh UUIDs.
- **Markdown path must mirror Java package path.** A ProseDoc at `com.foo.bar.MyDoc` needs its markdown at `src/main/resources/docs/com/foo/bar/MyDoc.md`. Mismatches surface as a runtime 404 when the doc is opened.
- **The Java record needs an INSTANCE singleton.** Catalogue leaves reference `MyDoc.INSTANCE`. The record itself has no state; the singleton is convention.
- **Don't put logic in the record.** ProseDocs are value records — title, summary, category, references all return constants. State and behaviour live in the catalogue or in widgets.

## See also

- **ProseDoc — Demo** (sibling doc) — a live ProseDoc you can read; its own markdown source is the canonical example.
- **References** block (future) — typed cross-references in depth.
- **ComposedDoc** block — when prose isn't enough; use ComposedDoc for content mixing prose with structured segments (SVG, table, code, image).
