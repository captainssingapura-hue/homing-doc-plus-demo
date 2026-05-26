# SplitPane — Guide

`SplitPane` is the first of three independent **workspace primitives**:

| # | Primitive       | Role                                                 |
|---|-----------------|------------------------------------------------------|
| 1 | **SplitPane**   | recursive 2D layout — panes + dividers, nothing more |
| 2 | MultiTabPane    | tabs + drag-reorder/cross-pane move (uses a SplitPane underneath) |
| 3 | Modal           | floating windows, dragable, dockable                 |

Each is independently useful — `SplitPane` can host a slideshow, a before/after comparison, or a side-by-side doc view *without* tabs or modals in the picture. The flexible workspace shell composes all three.

## Layout tree

The Pane tree is a plain JS object literal. Two node kinds:

```js
// Leaf — the renderable region. `slotId` is a stable, caller-chosen key.
{ kind: 'leaf', slotId: 'editor' }

// Split — a row or column of children, each with a ratio (sums to ~1.0).
{
  kind: 'split',
  orientation: 'horizontal',   // children laid out left-to-right
  children: [
    { pane: { kind: 'leaf', slotId: 'left'  }, ratio: 0.3 },
    { pane: { kind: 'leaf', slotId: 'right' }, ratio: 0.7 },
  ],
}
```

Splits nest freely. A classic 4-quadrant layout:

```js
const fourQuadrants = {
  kind: 'split', orientation: 'vertical',
  children: [
    { ratio: 0.5, pane: {
        kind: 'split', orientation: 'horizontal',
        children: [
          { ratio: 0.5, pane: { kind: 'leaf', slotId: 'tl' } },
          { ratio: 0.5, pane: { kind: 'leaf', slotId: 'tr' } },
        ]
    }},
    { ratio: 0.5, pane: {
        kind: 'split', orientation: 'horizontal',
        children: [
          { ratio: 0.5, pane: { kind: 'leaf', slotId: 'bl' } },
          { ratio: 0.5, pane: { kind: 'leaf', slotId: 'br' } },
        ]
    }},
  ],
};
```

Ratios are **normalised defensively on every layout pass** — they don't have to sum to exactly 1.0, and after a drag the controller writes the actual clamped ratios back into the tree so `getLayout()` reflects on-screen reality.

## Mounting

```js
import { SplitPane } from '.../layout/SplitPaneModule.js';

const sp = new SplitPane({
  container : document.getElementById('host'),
  layout    : fourQuadrants,
  renderSlot: (slotId, paneEl) => {
    // Fill paneEl however you like. Called once per leaf, once per setLayout.
    paneEl.textContent = 'Hello from ' + slotId;
  },
  minPx     : 60,                // optional, default 60px
  onChange  : (newTree) => {},   // optional, called after drag / setLayout
});
```

The constructor returns a **controller**:

| Method                                              | Purpose                                                         |
|-----------------------------------------------------|-----------------------------------------------------------------|
| `setLayout(newTree)`                                | Replace the layout tree and re-render from scratch.             |
| `getLayout()`                                       | Return a deep clone of the current tree (with on-screen ratios).|
| `relayout()`                                        | Re-apply sizes — call after the container's own size changes.   |
| `split(slotId, orientation, newSlotId, side?)`      | Split a leaf in two. The leaf becomes a split node containing the original + a new leaf with `newSlotId`. `side` defaults to `"after"` (right/below); pass `"before"` to put the new leaf left/above. Throws if `slotId` is not found. |
| `destroy()`                                         | Remove all event listeners and clear the container DOM.         |

## Driving splits — the chrome belongs to the residing app

`split()` is the API; **the SplitPane primitive does NOT ship its own UI for invoking it**. That's deliberate.

The demo widget puts small ⇆/⇅ buttons in the corner of each leaf because there's no other chrome to host them. In a real workspace, the residing app owns the chrome and drives splits from there — a `MultiTabPane` (Block 7) puts a split-button group on its tab strip; a slideshow viewer might bind keyboard shortcuts; a custom app might do nothing at all and configure splits programmatically.

The primitive provides the operation; the caller chooses the input method. This is the same shape as `setLayout` — the framework doesn't ship a "save layout" button either; the consumer wires that to whatever input affordance fits.

**State-preservation caveat:** `split()` calls the same re-render path as `setLayout`, which clears the container DOM and re-runs `renderSlot` for every leaf. The primitive does not preserve per-leaf state across re-render — that's the consumer's responsibility. A `MultiTabPane` atop SplitPane will use detach + re-anchor of its widget root to keep widget state alive; a simple demo with stateless content (counters that reset, log panels that re-render) is fine as-is.

## Design notes

**Raw DOM, no DomOpsParty.** SplitPane is a primitive — using DomOpsParty here would make it un-usable outside a workspace branch tree. The whole lifecycle is owned by an explicit `destroy()`.

**Inline scoped CSS.** SplitPane injects a single `<style id="homing-splitpane-style">` tag into `document.head` on first construction (idempotent). No CSS registry dependency. Class names are namespaced `hsp-*`.

**No widget/tab knowledge.** `renderSlot(slotId, paneEl)` is the only seam. A caller that wants tabs hands SplitPane a `renderSlot` that mounts a `MultiTabPane` into each leaf. SplitPane never sees that decision.

**Sizing strategy.** Each split lays out its children as flex items with `flex: 0 0 Npx` so leaves cannot collapse below `minPx`. Remainders from rounding are absorbed into the largest sibling so the visible total always exactly fills the parent.

## Live demo

The sibling **SplitPane — Live Demo** tile opens a top-level app (`SingleWidgetMPA` hosting `SplitPaneDemoWidget`) at `/app?app=split-pane-demo`. The app mounts a SplitPane into the main slot with a **fixed list of four mini-widgets**:

| Slot      | Content                                          |
|-----------|--------------------------------------------------|
| `swatch`  | A coloured solid block (no behaviour).           |
| `counter` | A button-driven counter — local state.           |
| `log`     | A monospace log panel.                           |
| `prose`   | A short HTML-formatted prose block.              |

The demo is a real app rather than an embedded doc segment because that's the shape the workspace shell will eventually take — SplitPane lives *inside* an AppModule's main slot and claims as much viewport as the chrome allows. Studying the demo studies the future workspace's substrate.

The "fixed list" is intentional for this block: dynamic widget management (picking from a registry, opening multiple instances) is RFC 0025 W6's job and arrives with the future Workspace shell.

## See also

- **MultiTabPane** (Block 7) — layers tabs over SplitPane, adds a conserved tab budget, and drives splits from its tab-strip chrome. The natural consumer of SplitPane.
- **Modal** (Block 8) — the floating-panel primitive. MultiTabPane uses it for tab detach; standalone consumers use it for dialogs, palettes, inspectors.
- **WorkspaceMPA** (in `homing-studio-base`) — abstract base for apps that fill the viewport with their own interactive surface. Auto-opts-out of theme backdrop interactivity, applies full-bleed CSS to its main slot. SplitPane demos live under it.
