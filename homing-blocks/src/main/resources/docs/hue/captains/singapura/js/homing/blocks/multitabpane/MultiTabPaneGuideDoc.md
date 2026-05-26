# MultiTabPane — Guide

`MultiTabPane` is the second of the workspace primitives — it layers **tabs** on top of [SplitPane](#splitpane) with a **conserved budget**:

> The total tab capacity across the workspace is fixed (default 16). It does not grow when you split. It does not shrink when you merge. It is *conserved*.

## The math

Per-pane capacity is derived from depth, never stored:

```
capacity(pane) = floor(budget / 2 ^ depth)
```

For the default `budget = 16`:

| Depth | Capacity / pane | Max panes at this depth | Total budget |
|-------|----------------:|------------------------:|-------------:|
| 0 (single root) | 16 | 1 | 16 |
| 1               |  8 | 2 | 16 |
| 2 (2x2 starter) |  4 | 4 | **16** |
| 3               |  2 | 8 | 16 |
| 4 (deepest)     |  1 | 16 | 16 |

Splitting is forbidden when capacity would drop **below 1**, so the deepest pane stays at capacity 1. The cap on total panes is therefore **16**.

## Why 16

The cap is principled, not arbitrary:

1. **Cognitive ceiling.** Past ~16 simultaneous panes, a workspace is no longer a workspace — it's a thicket. The user can't find anything.
2. **Performance ceiling.** Tab strips, content areas, and divider hit-tests all scale linearly with pane count. 16 keeps interactions snappy on commodity hardware.
3. **Forced consolidation.** When the budget pushes back, the user closes a tab they didn't really need. This is a feature.
4. **Multi-window is the right escape hatch.** Genuine use cases with > 16 widgets coordinating belong in [RFC 0025](#rfc-0025)'s future inter-connected multi-window feature, not in a single dense workspace.

Raising the budget is a knob, but rarely the right answer. Reach for a multi-workspace instead.

## Two operations — split and merge

```js
mt.split(slotId, orientation)   // 'horizontal' or 'vertical'
mt.merge(slotId)                // merges this leaf with its leaf sibling
```

They are **mathematical inverses**:

- **Split** — the pane at `slotId` becomes a 2-child split. Existing tabs stay in the originating child; the new sibling starts empty. Each child's capacity halves automatically (depth grew by 1).
- **Merge** — the pane at `slotId` absorbs its leaf sibling. The sibling's tabs concatenate after the originating pane's active tab; the originating pane's active stays active. Capacity doubles (depth shrank by 1).

There is **no separate close-pane op**. The only way to reduce pane count is `merge`, and the halving math is its own rescue: a merged pane always has room for the union of its children's tabs (since merged-capacity = 2 × child-capacity, and each child holds ≤ its own capacity).

### Split gates — only one

`canSplit(slotId)` returns `{ allowed, reason }`. Single failure mode:

- **Minimum capacity reached.** Pane is at capacity 1; splitting would give two capacity-0 panes that can hold nothing. (Depth-4 panes in default budget.)

**Tab count does not gate splits.** A pane with N tabs at capacity C, when split, produces two children each with capacity C/2. The originating child keeps all N tabs — even if N > C/2, putting it temporarily over its new capacity. The capacity pill turns red to surface this; the user can close tabs to clear it, or live with it. `addTab` is still gated by capacity (no growth via reshuffle), so the budget invariant survives where it matters.

The design intent: **every pane is always splittable** as long as the workspace hasn't reached the 16-pane ceiling. The deepest panes (cap 1) can't split further because no useful child capacity would remain.

### Merge gates

`canMerge(slotId)` returns `{ allowed, reason }`. Two failure modes:

- **Root pane.** Already a single pane; nothing to merge with.
- **Sibling is a split.** The sibling is itself a subtree, not a leaf. Merge inside that subtree first.

The second gate is deliberate: V1 only merges leaf-with-leaf. Merging a complex subtree into a single pane would silently collapse the user's nested layout. Future versions may relax this with explicit confirmation.

## Tabs

```js
mt.addTab(slotId, { id, title, render })   // throws if pane at capacity
mt.removeTab(slotId, tabId)                // safe if missing
mt.switchTab(slotId, tabId)                // makes a tab active
```

`render(contentEl)` is called when the tab becomes active. The primitive does not preserve content DOM between switches — that's the consumer's call. (A tab hosting a Widget will use detach + re-anchor of the widget's root to preserve state.)

## Where the chrome lives

`MultiTabPane` ships a default per-pane chrome: a tab strip + a capacity pill + corner buttons (⇆ ⇅ ⤢) for split/merge. The demo uses these as-is.

In a real workspace, the residing app owns the chrome:

- A workspace shell might put split/merge in a context menu or a keyboard shortcut.
- A custom app might disable all layout changes and treat the configuration as fixed.
- The `+` button to open new widgets goes on the tab strip alongside the existing tabs — RFC 0025 Phase W6 deliverable.

For now, the default chrome is the demo's chrome too — primitive-isolation convenience.

## Constructor

```js
new MultiTabPane({
  container     : HTMLElement,       // required
  budget        : 16,                // default 16; rarely override
  initialLayout : { ... }            // SplitPane-shaped tree; default = 2x2 with empty leaves
  onChange      : (state) => {...}   // fires on every state change
});
```

Default `initialLayout` is the 2x2:

```js
{
  kind: 'split', orientation: 'vertical',
  children: [
    { ratio: 0.5, pane: { kind: 'split', orientation: 'horizontal', children: [
        { ratio: 0.5, pane: { kind: 'leaf', slotId: 'tl' } },
        { ratio: 0.5, pane: { kind: 'leaf', slotId: 'tr' } }
    ]}},
    { ratio: 0.5, pane: { kind: 'split', orientation: 'horizontal', children: [
        { ratio: 0.5, pane: { kind: 'leaf', slotId: 'bl' } },
        { ratio: 0.5, pane: { kind: 'leaf', slotId: 'br' } }
    ]}}
  ]
}
```

The starter slots (`tl`, `tr`, `bl`, `br`) all begin empty. The app fills them via `addTab()` calls after construction.

## Controller summary

| Method                                  | Purpose |
|-----------------------------------------|---------|
| `addTab(slotId, tab)`                   | Append a tab; throws if pane at capacity. |
| `removeTab(slotId, tabId)`              | Close a tab; if pane empties, it stays empty. |
| `switchTab(slotId, tabId)`              | Make a tab active. |
| `split(slotId, orientation)`            | Split a leaf pane; gated by `canSplit`. |
| `merge(slotId)`                         | Merge with leaf sibling; gated by `canMerge`. |
| `canSplit(slotId)` / `canMerge(slotId)` | Predicate forms — `{ allowed, reason }`. |
| `capacityOf(slotId)`                    | Depth-derived capacity number. |
| `getState()`                            | Snapshot of layout + tab state. |
| `destroy()`                             | Remove listeners; clear container. |

## Live demo

The sibling **MultiTabPane — Live Demo** tile opens a top-level app showing the 2x2 starter with one tab pre-loaded per pane. Try:

1. **Reach 16 panes.** Click ⇆ or ⇅ on each pane in turn — original keeps the tab, new sibling is empty. Repeat on every pane (originals and new siblings alike) until the workspace is fully subdivided. Total: 12 split clicks from the 4-pane starter.
2. **Watch the budget pills.** Every pane shows `tabs / cap`. After full subdivision, panes that started with a tab show `1 / 1`; new siblings show `0 / 1`. Total used = 4 of 16; nothing was created or lost.
3. **Try over-capacity.** Add two more tabs to a starter pane (it's at `1/4`; cap allows up to 4). Now split it — the original child has `3 / 2`, pill red. `addTab` refuses further additions; close one to clear.
4. **Merge back.** Click ⤢ on any leaf sibling of another leaf — they fuse, tabs concatenate, capacity doubles. Walk back to the 2x2 starter by repeated merges.
5. **Merge gate at split boundaries.** Try to merge a pane whose sibling is itself a split — the ⤢ button hides. Merge inside the sibling first.
6. **Drag a tab between panes.** Hold a chip, drag onto another pane's strip — drop indicator (theme-accent line) shows the landing position. Release to commit; release in empty space to snap back.
7. **Detach to Modal.** Drag a chip past 40px from any strip — it pops out into a floating Modal. The Modal is live (the tab's content renders into it). Drag the Modal's title bar back over a strip and release to re-dock.

## See also

- **SplitPane** (Block 6) — the layout substrate this primitive layers on. Standalone consumers (slideshows, before/after viewers) use SplitPane without MultiTabPane.
- **Modal** (Block 8) — the floating-panel primitive used for tab detach. Independent of MultiTabPane; consumers wire `Modal` directly for dialogs / palettes / inspectors.
- **WorkspaceMPA** (in `homing-studio-base`) — the abstract `SingleWidgetMPA` extension for full-viewport apps. The MultiTabPane demo subclasses it; the future Workspace shell will too.
- **Case studies** (under Case Studies → Architecture in the self-studio): *Drag-Drop Bug Class* documents the nine structural bugs found during this primitive's drag-and-drop work; *Symmetry, Not Special Cases* explains the `/app-refs` endpoint that makes MultiTabPane demo's breadcrumb chain work the same way as doc-based viewers.
