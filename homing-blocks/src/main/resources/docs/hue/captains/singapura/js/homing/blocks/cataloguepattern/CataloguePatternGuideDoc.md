# Catalogue Pattern — Guide

> **A studio's content is organised as a typed catalogue tree — `L0_Catalogue` at the root, `L1_Catalogue<L0Parent>` one level down, `L2_Catalogue<L1Parent>` two levels down, up to `L8`. Each catalogue's `parent()` is statically typed; the framework walks the chain to build breadcrumbs, validate composition, and route URLs.**

## The level hierarchy

```
L0_Catalogue<Self>                    (the studio's root — no parent)
  ↓
L1_Catalogue<L0, Self>                (one level down)
  ↓
L2_Catalogue<L1, Self>                (two levels down)
  ↓
...
L8_Catalogue<L7, Self>                (terminal — no further nesting)
```

Each level's interface has two type parameters: the parent catalogue's class + the catalogue's own class (CRTP). The framework's parent() method is statically typed; downstream code can't claim a parent of the wrong level.

## The Entry variants

A catalogue's `leaves()` returns `List<Entry<Self>>`. Entry is a sealed type with three variants:

| Variant | What it wraps | Used for |
|---|---|---|
| `Entry.OfDoc(Doc)` | A typed `Doc` (ProseDoc, ComposedDoc, SvgDoc, etc.) | Most catalogue leaves — the standard doc tile |
| `Entry.OfIllustration(CatalogueIllustration)` | Decorative content (an inline figure, a header banner) | Visual elements that aren't addressable docs |
| `Entry.OfStudio(StudioProxy<L0>)` | A reference to another studio's L0 root | Cross-studio composition (RFC 0011) |

Construct entries via the convenience factory:

```java
@Override public List<Entry<MyCatalogue>> leaves() {
    return List.of(
            Entry.of(this, SomeDoc.INSTANCE),                  // → OfDoc(SomeDoc)
            Entry.of(this, new MyIllustration("Banner")),      // → OfIllustration
            Entry.of(this, new StudioProxy<>(OtherStudio.INSTANCE))  // → OfStudio
    );
}
```

The factory dispatches by the argument's type. The first argument is `this` (the catalogue) for the framework's type-system glue.

## subCatalogues vs leaves

A catalogue has two children-shaped methods:

- **`subCatalogues()`** — returns `List<? extends L<N+1>_Catalogue<Self, ?>>`. Each entry is itself a Catalogue, navigable via its own tile and the breadcrumb chain extends through it.
- **`leaves()`** — returns `List<Entry<Self>>`. Each entry is a leaf — a Doc, an Illustration, or a Studio proxy. Leaves don't have children.

Both can be empty. A catalogue that's purely a navigation hub has empty `leaves()` and non-empty `subCatalogues()`. A catalogue that's purely a doc collection has the opposite.

## DocProvider integration

A catalogue can implement `DocProvider` to contribute docs to the framework's `DocRegistry`:

```java
public record MyCatalogue() implements L1_Catalogue<MyL0, MyCatalogue>, DocProvider {
    // ... catalogue methods ...

    @Override public List<Doc> docs() {
        return List.of(DocOne.INSTANCE, DocTwo.INSTANCE);
    }
}
```

The `docs()` list is what `Bootstrap` harvests into the doc registry; without this contribution, the docs aren't reachable by UUID.

Conventional discipline: every doc that appears in `leaves()` (via `Entry.OfDoc`) should also appear in `docs()`. The framework also harvests catalogue-leaf docs automatically via `harvestSyntheticFromLeaves`, so listing in `leaves()` alone usually works — but explicit `docs()` is clearer.

## Breadcrumb construction

The framework walks `parent()` recursively from any catalogue back to the L0 root (whose `parent()` returns null). This produces the breadcrumb chain rendered on every doc viewer:

```
Studio Home  →  L1 Category  →  L2 Sub-Category  →  Doc Title
```

The chain is automatic. No catalogue code needs to construct it.

## Composition with sub-studios

When a studio composes another studio (RFC 0011), the umbrella catalogue's `leaves()` includes `Entry.OfStudio(new StudioProxy<>(OtherStudio.INSTANCE))`. The framework treats the proxied studio as a hosted source — its L0 + descendants surface in the host's breadcrumb chain prefixed with the umbrella's path.

Example: the demo studio composes `MultiStudio` + `DemoBaseStudio` as umbrella entries; their L0 catalogues both become first-class children of the multi-studio shell's root.

## Gotchas

- **Type parameters MUST match exactly.** `L1_Catalogue<MyL0, MyL1>` paired with `MyL1 implements L1_Catalogue<MyL0, MyL1>` — the framework's generic plumbing enforces this.
- **`parent()` must return the INSTANCE singleton.** Not a fresh constructor call. The framework uses identity comparison in some places.
- **Catalogues must be records or follow the record-equivalent singleton pattern.** Stateless; value-equality-able.
- **`subCatalogues()` returns a list of catalogues, not their classes.** The instances must be in the static registry the framework's `CatalogueRegistry` builds at boot.

## See also

- **Catalogue Pattern — L2 Leaf Doc** — a live demo showing a doc three levels deep. The breadcrumb chain on that doc walks four levels.
- **RFC 0005-ext2** (in self-studio) — the typed parent-chain RFC. The structural decision that made everything else above type-safe.
- **RFC 0011** (in self-studio) — cross-studio refs via `StudioProxy`. The umbrella composition pattern.
