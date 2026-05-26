# Catalogue Pattern — L2 Leaf Doc

> **This doc sits three levels deep in the catalogue tree. Look at the breadcrumb chain above — it walks the typed `parent()` pointers from this doc's containing catalogue all the way to the L0 root.**

## The chain you should see

```
Homing · Building Blocks   (L0)
  → Catalogue Pattern      (L1)
    → L2 Demo Sub-Catalogue (L2)
      → Catalogue Pattern — L2 Leaf Doc  (this doc)
```

The framework's `CatalogueRegistry.breadcrumbs(doc.uuid())` produces this chain by walking `Catalogue.parent()` recursively. Each catalogue's `parent()` is statically typed — `L2_Catalogue<Parent, Self>` requires the parent's type — so the chain can't drift.

## Why this matters

In a deep documentation site, **deep links are useful** — but only if the chain leading there is comprehensible. The framework's typed parent-chain means:

1. **The breadcrumb is correct by construction.** Compile-time typing prevents a child catalogue from claiming a parent it doesn't actually live under.
2. **Refactoring is safe.** Moving a doc between catalogues is a typed change; the compiler flags every place the old chain was assumed.
3. **The renderer doesn't need to know about levels.** It just walks `parent()` until null. No special cases for L1 vs L2.

## See the structure

The Java source at `CataloguePatternDemoSubCatalogue.java` declares:

```java
@Override public CataloguePatternCatalogue parent() {
    return CataloguePatternCatalogue.INSTANCE;
}
```

That's the entire `parent()` implementation. The type system does the rest.

## Back to the guide

See the **Catalogue Pattern — Guide** (sibling of this catalogue) for the full pattern.
