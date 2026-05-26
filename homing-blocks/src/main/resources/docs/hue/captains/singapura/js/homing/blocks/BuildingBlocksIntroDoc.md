# Building Blocks — Read Me First

> **This studio is a reference catalogue for the framework's main primitives. Each block has a guide doc (how to use) and at least one live demo doc (what it looks like working). Read both side-by-side; the demo's Java source IS the canonical example.**

## Who this is for

You — if you're authoring a new studio on top of Homing. You might be:

- A developer wiring up a documentation site
- An LLM agent generating a downstream Maven module
- Someone evaluating Homing for a fit decision

Each block presented here is a primitive the framework provides. You'll learn:

1. **What it is** — the type, its purpose, when to use it
2. **How to use it** — the construction pattern, common gotchas, idioms
3. **What it looks like working** — a live, registered, rendered demo in this very catalogue

## How to read this catalogue

Each block lives as its own L1 sub-catalogue under this L0 root. The sub-catalogue contains:

- **`<Block>GuideDoc`** — markdown prose explaining the block
- **`<Block>DemoDoc`** — a live, rendered example you can browse to

The guide tells you the *why*; the demo shows you the *what*. The demo's Java source — visible in the homing-blocks repo — is the canonical "copy this" reference.

## Foundational blocks (the first slice)

| # | Block | When you need it |
|---|---|---|
| 1 | **Studio Scaffold** | Setting up a new studio — Studio class, Fixtures, Bootstrap, server startup |
| 2 | **ProseDoc** | Basic markdown content — the simplest doc kind |
| 3 | **ComposedDoc + Segments** | Rich content combining prose, code, visuals, and embedded artifacts |
| 4 | **SvgDoc** | Vector content as a first-class doc kind, themable via `currentColor` |
| 5 | **Catalogue Pattern** | Organising docs into a typed L0 → L1 → ... hierarchy |

## Coming next

The framework has more primitives this catalogue will grow to cover:

- **TableDoc** — tabular content as a Doc
- **ImageDoc** — raster content as a Doc
- **Custom Widget** — write your own widget extending DocWidget (RFC 0024)
- **Plan tracker** — multi-phase journey tracking
- **References** — typed cross-doc references (DocReference, ExternalReference, ImageReference)
- **Theme variants** — declaring themable CSS classes
- **Site-in-a-Jar composition** — multiple studios under one chrome

Each will land as its own L1 sub-catalogue with the same guide-plus-demo discipline.

## A note on the demos

The demo docs are **real Docs registered in this catalogue**. Their UUIDs are stable; their rendering is the framework's actual rendering pipeline. There's no separate "preview" abstraction — what you see is what you'd get in your own studio.

Reading a demo doc's Java source teaches you the construction pattern with no surprises.

## A note on the guides

The guide docs are **plain markdown** (ProseDoc / ClasspathMarkdownDoc). They explain the block's purpose, construction, and idioms. They're not exhaustive — they cover the 80% case clearly and point at the framework's RFCs / case studies for deeper context.

## Where to start

Begin with **Studio Scaffold** — it's the meta-block, showing how the entire homing-blocks studio itself is structured. After that, the other blocks make immediate sense.
