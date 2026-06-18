package hue.captains.singapura.js.homing.docs.releases;

import hue.captains.singapura.js.homing.studio.base.rigid.RigidDoc;

import java.util.List;
import java.util.UUID;

/**
 * Release notes for 0.5.1 — the Document Pane release. Predecessor: 0.5.0.
 *
 * <p>The first release doc to live in the public {@code homing-docs} module
 * (0.5.0 and earlier remain in {@code homing-self-studio}); from 0.5.1 on the
 * canonical, public release docs are authored here. Dogfood: authored as a
 * {@link RigidDoc} (RFC 0042), so it renders as a foldable tree in the very
 * Document pane this point release adds.</p>
 *
 * <p>0.5.0 made the document a rigid tree with a foldable viewer; 0.5.1 brings
 * that viewer into the workspace as a live pane. A two-tier navigation
 * vocabulary on the tree — cheap <i>select</i> vs intentional <i>open</i> (Enter
 * / double-click) — lets the new {@code DocContentWidget} render the opened doc
 * in place, beside the Navigator, paying the expensive render only on intent.
 * Plus leveled-Open refinements: clickable breadcrumbs and a content-only HTML
 * export that keeps the TOC.</p>
 */
public final class Release0_5_1Doc {

    private Release0_5_1Doc() {}

    public static final RigidDoc INSTANCE = build();

    private static RigidDoc build() {
        return RigidDoc.root(
                    UUID.fromString("f6a7b8c9-d0e1-4345-8a6b-c7d8e9f0a1b2"),
                    "0.5.1 — The Document Pane",
                    "Point release on 0.5.0. The rigid-tree doc viewer comes into the workspace as a live pane: a new "
                  + "DocContentWidget joins the Studio Workspace (📖 Document) and renders the OPENED document in place "
                  + "— foldable TOC + synced-highlight body — beside the Navigator. The enabling move is a two-tier "
                  + "navigation vocabulary on the tree: cheap select (arrow / single click) publishes NodeSelected -> "
                  + "NavigateTo for the Summary pane, while intentional open (Enter / double-click, via TreeRenderer's "
                  + "new onActivate callback) publishes NodeOpened -> OpenDoc for the Document pane — so the expensive "
                  + "fetch + render is paid on intent, never on browsing. Each open dissolves the previous render's "
                  + "DomOpsParty sub-branch and mounts a fresh one (No DOM Destruction), with a sequence guard dropping "
                  + "a superseded fetch; only composed kinds render in place, others degrade to a hint. Leveled-Open "
                  + "refinements: ForestPathResolver + /open-refs now emit a Crumb(text, href) trail so breadcrumbs link "
                  + "to ancestor catalogues (consistent with uuid-open), and a content-only HTML export keeps the full "
                  + "two-pane TOC with a data-export-width hint so the sidebar no longer crushes the body. The pane "
                  + "styles entirely through typed StudioStyles classes (st_doc_pane, st_doc_empty) — no inline styles. "
                  + "No breaking changes: everything is additive. This release doc is itself a RigidDoc, dogfooding the "
                  + "pane it announces — and the first release doc authored in the public homing-docs module.",
                    "RELEASE")

            // ── At a glance ──
            .relation(
                List.of("Field", "Value"),
                List.of(
                    List.of("**Version**", "0.5.1 *(point release — the doc viewer becomes a live workspace pane)*"),
                    List.of("**Released**", "2026-06-18"),
                    List.of("**Predecessor**", "0.5.0 — The Rigid-Tree Document"),
                    List.of("**Breaking**", "None — purely additive")),
                "At a glance")
            .text("""
                    0.5.0 made the *document* a rigid tree and gave it a foldable viewer; 0.5.1 brings that viewer **into the workspace as a live pane**. Select a doc in the Navigator to preview it cheaply, then *open* it — Enter or double-click — to read the whole thing rendered in place, beside the tree, its table of contents folding in sync.

                    The through-line: browsing stays cheap, and the expensive render happens only on intent — *including the rendering of this very document, opened in the pane it describes*.""")

            // ── L1: the Document pane ──
            .l1("The in-workspace Document pane")
                .text("A new `DocContentWidget` joins the Studio Workspace as a third pickable pane (📖 Document), alongside the Navigator and Summary. It renders the **opened** document in place — the same foldable TOC + synced-highlight body the standalone viewer ships — so a doc reads live next to the tree, no new tab.")
                .l2("Two-tier navigation — select vs open")
                    .text("The enabling move is a two-tier vocabulary on the tree, so a pane pays only for what the user means: cheap, high-frequency **select** for re-labelling, and deliberate, expensive **open** for rendering.")
                    .relation(
                        List.of("Tier", "Trigger", "Message", "Consumer"),
                        List.of(
                            List.of("**Select**", "arrow keys / single click", "`NodeSelected` → `NavigateTo`", "Summary (cheap re-label)"),
                            List.of("**Open**", "**Enter** / **double-click**", "`NodeOpened` → `OpenDoc`", "Document (expensive render)")),
                        "The two navigation tiers")
                    .text("`TreeRenderer` gains an `onActivate` callback — Enter on the selected row, or a double-click — distinct from the high-frequency `onSelect`. `TreeWidget` publishes `NodeOpened`; the `NavigatorSecretary` redirects it as `OpenDoc`; the Document pane reacts to `OpenDoc` **only**, never to browsing.")
                    .l2build()
                .l2("Render discipline")
                    .text("Each open dissolves the previous render's DomOpsParty sub-branch and mounts a fresh, uniquely-named one (No DOM Destruction), so a re-open never collides element names or leaks listeners; a monotonic sequence guard drops a stale fetch whose open was superseded. Kind-gated: `composed` docs (ComposedDoc + RigidDoc, both `/doc-tree`-resolvable) render in place; other kinds degrade to a hint pointing at the Summary pane's Open button.")
                    .l2build()
                .l1build()

            // ── L1: leveled-Open refinements ──
            .l1("Refinements to leveled Open")
                .text("""
                        - **Clickable breadcrumbs.** `ForestPathResolver` returns a `Crumb(text, href)` trail and `/open-refs` emits the catalogue browse URL per crumb, so a leveled-Open page's breadcrumb links to its ancestor catalogues — link-for-link consistent with uuid-open (both use `CatalogueAppHost.urlFor`).
                        - **HTML export keeps the TOC.** A content-only export (page chrome excluded) retains the full two-pane document — TOC + body — and a `data-export-width` hint widens the standalone `<main>` so the sidebar no longer crushes the body.""")
                .l1build()

            // ── L1: internal ──
            .l1("Internal — typed CSS, no inline styles")
                .text("The new pane styles entirely through typed classes: `StudioStyles` gains `st_doc_pane` (scroll container) and `st_doc_empty` (muted empty-state), both on theme tokens, applied via `css.addClass`. No inline styles remain in the Document pane, so the conformance raw-CSS scan stays green — the pane now models the typed-class discipline `SummaryWidget` / `TreeWidget` have yet to adopt.")
                .l1build()

            // ── L1: what's next ──
            .l1("Known limitations and what's next")
                .text("""
                        - The Document pane renders `composed` kinds in place; other kinds (svg / plan / app) still degrade to a hint rather than rendering with a kind-matched widget.
                        - `SummaryWidget` and `TreeWidget` still style inline — candidates for the same typed-class refactor the Document pane now models.

                        **What's next.** Render non-composed kinds in the pane via a kind→widget map; carry the typed-class refactor across the remaining workspace widgets.""")
            .l1build()

            .build();
    }
}
