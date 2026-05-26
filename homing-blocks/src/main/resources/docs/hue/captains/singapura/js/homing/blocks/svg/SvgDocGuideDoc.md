# SvgDoc — Guide

> **An `SvgDoc<G>` is a Doc wrapping a typed `SvgRef<G>` — an SvgGroup + an SvgBeing within it + a classpath `.svg` resource. The framework's SvgViewer renders the inlined SVG with full theme participation via `currentColor`.**

## The three artifacts

For each set of related SVGs, three files:

**1. The `SvgGroup` Java record** declares the group + each SvgBeing variant:

```java
public record MyShapes() implements SvgGroup<MyShapes> {
    public record star()  implements SvgBeing<MyShapes> {}
    public record heart() implements SvgBeing<MyShapes> {}
    public record house() implements SvgBeing<MyShapes> {}

    public static final MyShapes INSTANCE = new MyShapes();

    @Override public List<SvgBeing<MyShapes>> svgBeings() {
        return List.of(new star(), new heart(), new house());
    }

    @Override public ExportsOf<MyShapes> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
```

Each `SvgBeing` variant is a nested record. The group + the variant compose into a typed `SvgRef`.

**2. The SVG files** on the classpath, one per SvgBeing variant:

```
src/main/resources/homing/svg/<package-path>/MyShapes/star.svg
src/main/resources/homing/svg/<package-path>/MyShapes/heart.svg
src/main/resources/homing/svg/<package-path>/MyShapes/house.svg
```

The path convention is `homing/svg/<Java-package-as-path>/<group-simple-name>/<being-simple-name>.svg`. For a Java record at `com.example.shapes.MyShapes.star`, the SVG goes at `src/main/resources/homing/svg/com/example/shapes/MyShapes/star.svg`.

**3. The SvgDoc instance(s)** wrapping each SvgRef:

```java
public static final SvgDoc<MyShapes> STAR_DOC = new SvgDoc<>(
        new SvgRef<>(MyShapes.INSTANCE, new MyShapes.star()),
        "Star",
        "A simple themed star.");
```

The SvgDoc's UUID is derived deterministically from the SvgRef's classpath resource path (`UUID.nameUUIDFromBytes("svg:" + path)`), so the same shape always gets the same UUID across rebuilds.

## Theme integration via `currentColor`

The framework's themes change CSS `color` per theme. An SVG that uses `fill="currentColor"` (or `stroke="currentColor"`) inherits the theme's color automatically. Recommended SVG attributes:

```xml
<svg viewBox="0 0 100 100">
    <polygon points="..." fill="currentColor" stroke="currentColor" stroke-width="2"/>
    <circle cx="50" cy="50" r="20" fill="currentColor" opacity="0.4"/>
</svg>
```

For multi-color graphics, use specific colors deliberately or use `var(--color-X)` CSS custom properties that the framework's theme set defines.

**Don't bake in dark backgrounds.** The framework's chrome is themed; the SVG should let the theme set the canvas. Transparent backgrounds + `currentColor` foregrounds is the idiomatic choice.

## Where SvgDocs surface

Same registration pattern as ProseDoc:

- **Catalogue leaves**: `Entry.of(catalogue, MY_SVG_DOC)`
- **DocProvider.docs()**: include the SvgDoc in the list so `DocRegistry` knows about it by UUID
- **`SvgContentViewer`** is the framework's default routing — kind `"svg"` → `SvgViewer` at `/app?app=svg-viewer&id=<uuid>`. Pre-registered in `DefaultFixtures.contentViewers()`.

Tree-leaf usage is also common:

```java
new TreeLeaf("star-leaf", "Star", "A simple star.", "ICON", "",
             STAR_DOC)
```

## When to use SvgDoc vs other shapes

- **SvgDoc** — citable, viewable, themable vector graphic. Single subject per doc; first-class catalogue tile.
- **`SvgSegment` inside ComposedDoc** — referencing an existing SvgDoc inline within a richer doc. The SvgDoc renders inside the composed doc's body.
- **Inline `<svg>` in markdown** — DON'T. Markdown's HTML escape hatch was specifically retired per RFC 0019; embedded HTML in markdown is anti-doctrinal.
- **Direct DOM ops in a Widget** — for animated/interactive SVG (game canvases etc.), build a Widget instead. SvgDoc is for static visuals.

## Gotchas

- **The classpath path must mirror the Java package path.** A typo and the SVG resource resolves to `Optional.empty()`; the SvgDoc renders empty.
- **SvgDoc requires the SvgBeing nested-record style.** `SvgBeing<G>` is parameterised on the group; the group instance + being instance compose the typed ref.
- **The group's `svgBeings()` must list every SvgBeing variant.** The framework iterates this for the JS-side typed export wiring.
- **UUIDs are deterministic.** Don't try to override; just rely on the framework's path-derived UUID. Cross-doc references survive renames as long as the classpath path is stable.

## See also

- **SvgDoc — Demo** (sibling) — a live SvgDoc rendering a small themed star. Java source: `BlocksShape.java` + `star.svg` + `SvgDocDemoDoc.java`.
- **RFC 0017** (in self-studio) — the theme-participation invariant; `currentColor` discipline for cross-theme SVG content.
