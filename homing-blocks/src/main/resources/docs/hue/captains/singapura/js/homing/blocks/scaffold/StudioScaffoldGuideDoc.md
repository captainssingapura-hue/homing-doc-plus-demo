# Studio Scaffold — Guide

> **A minimal Homing studio is four artifacts: a `Studio<Home>` record declaring home + apps + brand, an L0 catalogue serving as the home, a `Fixtures<S>` implementation (usually just `DefaultFixtures` delegate), and a `main` method calling `Bootstrap.compose().start()`.**

## The four artifacts

```java
public record MyStudio() implements Studio<MyHomeCatalogue> {
    public static final MyStudio INSTANCE = new MyStudio();
    @Override public MyHomeCatalogue home() { return MyHomeCatalogue.INSTANCE; }
    @Override public List<AppModule<?, ?>> apps() { return List.of(); }
    @Override public StudioBrand standaloneBrand() {
        return new StudioBrand("My Studio", MyHomeCatalogue.class);
    }
}
```

```java
public record MyHomeCatalogue() implements L0_Catalogue<MyHomeCatalogue>, DocProvider {
    public static final MyHomeCatalogue INSTANCE = new MyHomeCatalogue();
    @Override public String name()    { return "My Studio · Home"; }
    @Override public String summary() { return "..."; }
    @Override public String badge()   { return "STUDIO"; }
    @Override public String icon()    { return "🏠"; }
    @Override public List<Entry<MyHomeCatalogue>> leaves() { return List.of(); }
    @Override public List<Doc> docs() { return List.of(); }
}
```

```java
public record MyFixtures<S extends Studio<?>>(Umbrella<S> umbrella)
        implements Fixtures<S>, ValueObject {
    @Override public List<AppModule<?, ?>> harnessApps() {
        return new DefaultFixtures<>(umbrella).harnessApps();
    }
    @Override public NodeChrome chromeFor(Umbrella<S> node) {
        return new DefaultFixtures<>(umbrella).chromeFor(node);
    }
}
```

```java
public final class MyServer {
    public static void main(String[] args) {
        Umbrella<Studio<?>> umbrella = new Umbrella.Solo<>(MyStudio.INSTANCE);
        new Bootstrap<>(
                new MyFixtures<>(umbrella),
                new DefaultRuntimeParams(8080)
        ).start();
    }
}
```

## What each artifact does

- **`Studio<Home>`** — declares the studio's identity. The `home()` return type is the studio's L0 root catalogue class; `apps()` lists any intrinsic AppModules (often empty — the framework's `DefaultFixtures` provides the standard harness); `standaloneBrand()` returns the studio's user-visible identity (label, logo, home-catalogue class).
- **L0 root catalogue** — the studio's home page. Must implement `L0_Catalogue<Self>`; the `Self` type parameter is the catalogue's own class (CRTP). Usually also implements `DocProvider` to surface its declared docs into the framework's `DocRegistry`.
- **`Fixtures<S>`** — wraps the `Umbrella` (which holds one or more `Studio`s in a composition tree) and provides the framework's overridable hooks. Most studios delegate to `DefaultFixtures` for everything except whatever they're customising (e.g., `trees()` for ContentTrees, custom `harnessApps()` for special-cased apps).
- **`main` method** — composes the `Umbrella` (typically a `Umbrella.Solo<MyStudio>` for single-studio servers; `Umbrella.Group<>` for multi-studio compositions), then `new Bootstrap<>(...).start()` to launch the Vert.x server on the supplied port.

## Multi-studio composition

For multi-studio servers (RFC 0011 — multiple studios under one chrome), the umbrella becomes a `Group`:

```java
Umbrella<Studio<?>> umbrella = new Umbrella.Group<>(
        "My Multi-Studio Server",
        "Combined studios under one chrome.",
        List.of(
                new Umbrella.Solo<>(StudioA.INSTANCE),
                new Umbrella.Solo<>(StudioB.INSTANCE)
        ));
```

Each contained `Solo` is a participating studio. The framework merges their catalogue closures, doc registries, plans, and apps into one server.

## Gotchas

- **Studio.home() return type must match the L0 catalogue's self type.** `Studio<MyHomeCatalogue>` paired with `MyHomeCatalogue implements L0_Catalogue<MyHomeCatalogue>` — both sides know the same concrete type. The framework's generic plumbing relies on this.
- **`StudioBrand.homeApp()` must reference a registered L0 catalogue class.** The second argument to `new StudioBrand(label, homeAppClass)` is the catalogue type, not an instance. `Bootstrap.compose` validates this at startup; mismatches fail loudly.
- **Don't list catalogue subCatalogues in apps()**. Sub-catalogues come from the L0's `subCatalogues()` method, not from `Studio.apps()`. `apps()` is for `AppModule`s — interactive entries that aren't catalogue pages.
- **The framework provides the harness apps** (`DocBrowser`, `CatalogueAppHost`, `DocReader`, etc.) automatically via `DefaultFixtures.harnessApps()`. You don't list them — your `Studio.apps()` lists only your studio's own intrinsic apps (often empty for doc-only studios).

## See also

- **Studio Scaffold — Demo** (sibling doc in this catalogue) shows the actual code of the `BuildingBlocksStudio` scaffolding.
- **Catalogue Pattern** block (separate L1) covers L0/L1/L2 hierarchy + Entry variants in depth.
