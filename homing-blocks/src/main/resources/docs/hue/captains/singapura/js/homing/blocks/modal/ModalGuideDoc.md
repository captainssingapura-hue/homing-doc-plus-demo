# Modal — Guide

`Modal` is the third of the three workspace primitives — and the most broadly reusable. It is a **draggable, resizable, floating panel** with no awareness of anything else in the framework.

> **Reusability is the design intent.** Modal is built so that consumers other than the workspace (settings dialogs, tool palettes, detached widgets, custom prompts, inspectors) get the same primitive without conditional behaviour. The MultiTabPane's detach-to-modal feature is one consumer among many.

## Use cases

| Scenario | What Modal provides |
|---|---|
| Settings dialog | Title-bar drag, close button, contains a form element. |
| Floating tool palette | Persistent while interacting with primary content. Resizable to suit. |
| Picture-in-picture | Secondary content kept on-screen without context-switching. |
| Detached widget (tab tear-off) | The MultiTabPane consumer — the live tab content becomes a Modal. |
| Custom confirmation | When the browser's native `confirm()` is too coarse. |
| Live inspector | Devtools-style panel showing real-time state. |

All of these mount the **same primitive**. Modal has no `kind: 'dialog' / 'palette' / 'inspector'` switch — the consumer composes whatever content they want inside, and the primitive handles the floating behaviour.

## Constructor

```js
new Modal({
  container : document.body,       // required; see container constraint below
  title     : 'My Panel',
  content   : someHTMLElement,     // optional initial body content

  x         : 100,                 // initial position (px)
  y         : 80,
  width     : 420,                 // initial size (px)
  height    : 300,
  minWidth  : 180,                 // resize floors
  minHeight : 100,

  resizable : true,                // eight resize handles (default true)
  closable  : true,                // × button in title bar (default true)
  bounds    : someContainerEl,     // optional clamp region; default = viewport

  onClose   : ()      => {},       // after × is clicked
  onMove    : (x, y)  => {},       // after drag ends
  onResize  : (w, h)  => {},       // after resize ends
  onFocus   : ()      => {},       // on any mousedown on the panel
});
```

## Controller API

| Method | Purpose |
|---|---|
| `modal.el` | Root DOM element. For advanced composition. |
| `modal.setTitle(s)` | Update the title bar text. |
| `modal.setContent(el)` | Replace body content. Old element is detached, not destroyed. |
| `modal.moveTo(x, y)` | Programmatic move. Respects `bounds`. |
| `modal.resize(w, h)` | Programmatic resize. Respects `minWidth`/`minHeight`. |
| `modal.open()` | Reveal (after `close()`). Fires `onFocus`. |
| `modal.close()` | Hide. Does NOT destroy — call `destroy()` for cleanup. |
| `modal.toggle()` | Open ↔ close. |
| `modal.isOpen()` | Boolean state. |
| `modal.destroy()` | Remove from DOM, detach all listeners. |

## Container constraint — always `document.body`

Modal converts mouse `clientX/Y` directly into CSS `left/top` on the panel element. This means the panel's positioned parent must have its origin at the viewport origin.

In practice: **always use `document.body`** as the container. Mounting under a nested positioned element with an offset will cause a first-move jump equal to the container's offset — a confusing class of bug.

If your app's structure makes `document.body` mounting awkward (e.g., the modal needs to live under a specific app's root for cleanup), you can still pass `container: document.body` and use `bounds: someApp.root` to clamp the drag region to the app's visible area without mounting inside it.

## What Modal does NOT do

Three deliberate exclusions worth knowing:

- **No z-index / stacking management.** Each Modal is independent — opening a second Modal doesn't automatically bring it to the front of the first. If you need stacking discipline (focus brings to front, etc.), wire the `onFocus` callback to your own z-index manager. The primitive stays decoupled.
- **No backdrop / modal-overlay behaviour.** The name "Modal" follows the floating-panel convention, but Modal is *non-modal* in the dialog sense: clicks pass through to underlying content. Add your own overlay if you want to block interaction with the rest of the page.
- **No animation.** Open / close is instant. Add `transition` CSS to `.hmd-panel` if you want fade or scale.

These exclusions keep the primitive lean. Each is the consumer's call to add when needed.

## Demo

The sibling **Modal — Live Demo** tile opens three modals at once, each a different use case:

1. **Settings dialog** — a form with a few inputs. Drag the title to move; the form stays usable.
2. **Floating colour palette** — picks update a swatch in the main viewport. Demonstrates Modal as a persistent tool window.
3. **Live inspector** — a panel that ticks every second showing the current time and a counter that increments on every drag. Demonstrates the `onMove` / `onResize` callbacks.

All three are independent. Closing one doesn't affect the others. Re-open via the toolbar buttons at the top.

## See also

- **MultiTabPane** (Block 7) — Modal's first internal consumer: tab detach drags a chip out of a tab strip and into a fresh Modal whose title bar can drag the tab back to dock. The Modal stays standalone — MultiTabPane just instantiates it; no reverse coupling.
- **SplitPane** (Block 6) — the layout substrate beneath MultiTabPane. Standalone consumers can use Modal alongside SplitPane without involving MultiTabPane (e.g., a slideshow with a floating notes panel).
- **WorkspaceMPA** (in `homing-studio-base`) — apps that fill the viewport with their own interactive surface should subclass this rather than `SingleWidgetMPA` directly. Modal demo subclasses it; the inspector example shows how to clamp Modal positions to a host region via `bounds`.
