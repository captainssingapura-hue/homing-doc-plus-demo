# Studio Scaffold — Demo

The Building Blocks studio you're reading right now is itself a worked example of the Studio Scaffold pattern. Five files in `homing-blocks/src/main/java/hue/captains/singapura/js/homing/blocks/`:

## 1. The Studio implementation — `BuildingBlocksStudio.java`

```java
public record BuildingBlocksStudio() implements Studio<BuildingBlocksCatalogue> {
    public static final BuildingBlocksStudio INSTANCE = new BuildingBlocksStudio();

    @Override
    public BuildingBlocksCatalogue home() { return BuildingBlocksCatalogue.INSTANCE; }

    @Override
    public List<AppModule<?, ?>> apps() { return List.of(); }

    @Override
    public StudioBrand standaloneBrand() {
        return new StudioBrand("Homing · Building Blocks", BuildingBlocksCatalogue.class);
    }
}
```

Three methods, one INSTANCE singleton. No state, no constructor logic — a pure record.

## 2. The L0 root catalogue — `BuildingBlocksCatalogue.java`

```java
public record BuildingBlocksCatalogue()
        implements L0_Catalogue<BuildingBlocksCatalogue>, DocProvider {

    public static final BuildingBlocksCatalogue INSTANCE = new BuildingBlocksCatalogue();

    @Override public String name()    { return "Homing · Building Blocks"; }
    @Override public String summary() { return "Reference catalogue ..."; }
    @Override public String badge()   { return "STUDIO"; }
    @Override public String icon()    { return "🧱"; }

    @Override public List<Entry<BuildingBlocksCatalogue>> leaves() {
        return List.of(Entry.of(this, BuildingBlocksIntroDoc.INSTANCE));
    }

    @Override public List<? extends L1_Catalogue<BuildingBlocksCatalogue, ?>> subCatalogues() {
        return List.of(
                StudioScaffoldCatalogue.INSTANCE,
                ProseDocCatalogue.INSTANCE,
                ComposedDocCatalogue.INSTANCE,
                SvgDocCatalogue.INSTANCE,
                CataloguePatternCatalogue.INSTANCE
        );
    }

    @Override public List<Doc> docs() { return List.of(BuildingBlocksIntroDoc.INSTANCE); }
}
```

The L0 has `leaves()` (direct entries) + `subCatalogues()` (L1 children) + `docs()` (DocProvider contribution).

## 3. The Fixtures implementation — `BuildingBlocksFixtures.java`

```java
public record BuildingBlocksFixtures<S extends Studio<?>>(Umbrella<S> umbrella)
        implements Fixtures<S>, ValueObject {

    public BuildingBlocksFixtures {
        Objects.requireNonNull(umbrella);
    }

    @Override public List<AppModule<?, ?>> harnessApps() {
        return new DefaultFixtures<>(umbrella).harnessApps();
    }

    @Override public NodeChrome chromeFor(Umbrella<S> node) {
        return new DefaultFixtures<>(umbrella).chromeFor(node);
    }
}
```

Plain delegation to `DefaultFixtures`. Studios that need custom harness apps, custom trees, custom chrome override what they need; the rest delegates.

## 4. The server entry point — `BuildingBlocksServer.java`

```java
public final class BuildingBlocksServer {
    private BuildingBlocksServer() {}

    public static void main(String[] args) {
        Umbrella<Studio<?>> umbrella =
                new Umbrella.Solo<>(BuildingBlocksStudio.INSTANCE);

        new Bootstrap<>(
                new BuildingBlocksFixtures<>(umbrella),
                new DefaultRuntimeParams(8083)
        ).start();
    }
}
```

A `Solo<>` umbrella (single-studio) wrapping the studio's INSTANCE. The Bootstrap composes everything; `.start()` launches Vert.x on port 8083.

## 5. The landing doc — `BuildingBlocksIntroDoc.java`

```java
public record BuildingBlocksIntroDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b10c4500-0000-4001-8000-000000000001");
    public static final BuildingBlocksIntroDoc INSTANCE = new BuildingBlocksIntroDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Building Blocks — Read Me First"; }
    @Override public String summary() { return "..."; }
    @Override public String category(){ return "DOC"; }
    @Override public List<Reference> references() { return List.of(); }
}
```

The doc record + a companion `.md` file on the classpath. `ClasspathMarkdownDoc` finds the markdown by convention from the Java type's package path.

## To run it

```bash
mvn -pl homing-blocks exec:java -Dexec.mainClass="hue.captains.singapura.js.homing.blocks.BuildingBlocksServer"
```

Then open `http://localhost:8083`. The studio's home appears; sub-catalogues are this block + the other four; clicking each surfaces its guide + demo.

## What you'd change for your own studio

Rename everything `BuildingBlocks` → `YourStudio`. Change the brand label. Change the L0 catalogue's `name()`, `summary()`, `icon()`. Add your sub-catalogues + leaves. The five files' shapes don't change.
