# Welcome to the Homing Demo Studio

A small studio running entirely on the public **`homing-studio-base`** API — the same machinery any downstream consumer gets when they depend on the artefact.

## What this is

This studio exists to **dogfood** the framework. Per the [First-User Discipline](#) doctrine over in `homing-studio`, every framework primitive must have a real consumer that isn't `homing-studio` itself. This is that consumer.

The whole studio is configured in **one file** — `DemoStudioServer.java` — with:

- one home `Catalogue` (`DemoStudio.java`),
- one typed `Doc` (this page),
- a `StudioBrand` whose logo is a typed `SvgRef` pointing at the **turtle** SvgBeing already declared by `CuteAnimal` for the SVG demos,
- the same `CatalogueAppHost` / `PlanAppHost` / `DocReader` / `DocBrowser` AppModules every downstream studio uses.

That's it. No per-app `appMain`, no hand-rolled HTML, no theme registry of its own (it inherits the four bundled themes — Default / Forest / Sunset / Bauhaus). The whole boot is a single `StudioBootstrap.start(...)` call.

## What the turtle is doing here

The brand mark in the header is the same turtle SVG the `DancingAnimals` and `MovingAnimal` games use — typed via `SvgRef<>(CuteAnimal.INSTANCE, new CuteAnimal.turtle())`. Two birds, one asset:

- The demo gets a friendly mark with personality — easier to tell apart from the main studio at a glance.
- The framework's `SvgRef` primitive gets exercised against an asset *not designed for it* — a 800×800 illustrative SVG with hardcoded dimensions, originally drawn for the SVG-extruder demos. If the framework can host that as a brand logo without per-asset work, the primitive is robust.

## What's not here

- **Plan trackers for the demo's own implementation** — the demo studio doesn't track its own implementation. It's a *consumer* of the primitives, not a project artefact. (Plan trackers live in the framework's own self-studio, where they document the framework's evolution.)

## Apps and docs as siblings — the Site-in-a-Jar POC

Three animal-game SPAs (`MovingAnimal` — platform game; `DancingAnimals` — 5×5 keyboard dance grid; `SpinningAnimals` — auto-rotating gallery) ship as catalogue leaves alongside the documentation. The flagship — `MovingAnimal` — also gets a top-level tile next to the doc tiles. Click any of them: full SPA in the same chrome (header, breadcrumb, theme picker, audio cues).

This is the Site-in-a-Jar shape RFC 0022 + `SiteInAJarPlanData` describe: one Maven jar serving applications and documentation as sibling kinds of typed catalogue leaves. The demo studio is the framework's first dogfood worked example of that shape — proving the typed Catalogue / DocViewer / AppModule machinery doesn't care which kind any given leaf is.

## Try it

- **Theme picker**: top-right of the header. Flip between Default / Forest / Sunset / Bauhaus and watch the chrome retint. The turtle stays the turtle (a logo is identity).
- **`/`**: redirects here.
- **`/app?app=catalogue&id=...DemoStudio`**: this catalogue page.
- **`/brand`**: JSON payload showing what the framework serves to consumer modules — note `logo` carries the full inline SVG markup.
- **`/themes`**: JSON catalogue of every registered theme with palette swatches.

## How to read the source

Three files, all under `homing-demo/src/main/java/.../demo/studio/`:

| File | What it is |
|---|---|
| `DemoStudio.java` | The home `Catalogue` — name, summary, list of entries. |
| `DemoIntroDoc.java` | This doc — UUID + title + summary + classpath markdown. |
| `DemoStudioServer.java` | `main()` — wires AppHosts + catalogues + brand + boots `StudioBootstrap`. |

Plus the markdown body you're reading now, at `homing-demo/src/main/resources/docs/.../demo/studio/DemoIntroDoc.md`.

That's the full surface area of a downstream studio. Copy these four files, rename, and you have your own.
