package hue.captains.singapura.js.homing.docs.migrations;

import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.rigid.RigidDoc;

import java.util.List;

/**
 * Migration — upgrading a downstream studio to Homing 0.8.0.
 *
 * <p>Scoped to the <b>release</b>, not to the RFC that motivated it. The first
 * version of this guide covered RFC 0053's tree changes only, and the first
 * downstream to use it planned a two-file job that turned out to touch ten.
 * A downstream upgrades a version number; every break in that version is theirs
 * to hit.</p>
 *
 * <p>0.8.0 removes 37 public types and moves 2. Most downstreams touch a handful
 * of them, and all but one break loudly.</p>
 */
public final class MigrateTo0_8_0Doc {

    private MigrateTo0_8_0Doc() {}

    public static final RigidDoc INSTANCE = build();

    private static RigidDoc build() {
        return RigidDoc.root(
                    ComposedDoc.deterministicUuid("migration:homing-0-8-0"),
                    "Migrating to Homing 0.8.0",
                    "The full breaking-change inventory for 0.8.0, scoped to the RELEASE rather than "
                  + "to RFC 0053 which motivated it. 37 public types removed, 2 moved. "
                  + "Six clusters: the tree substrate gains a segment and an identity while the "
                  + "dimension vocabulary retires; placement changes, so Entry.of(catalogue, doc), "
                  + "Entry.OfDoc, AppDoc and Doc.url() are all gone; the ContentViewer registry and "
                  + "its seven implementations retire because a catalogue entry already declares what "
                  + "opens a doc; the whole tree app goes with the /tree route; the DocBrowser kit "
                  + "goes because the catalogue tree is the only index; and the legacy catalogue "
                  + "adapter cluster goes with the tile grid. "
                  + "Everything is compiler-caught EXCEPT one client-side change: if a widget read a "
                  + "machine key off sel.summary, it now receives prose, the lookup misses, and "
                  + "selection silently stops driving anything while the tree still draws. Grep your "
                  + "widget JS for summary before you start. "
                  + "Validated by the fin-dash port: ten files, green from clean, about eleven "
                  + "minutes.",
                    "MIGRATION")

            .l1("Scope — the release, not the RFC")
                .markdown("""
                        This guide covers **everything breaking in 0.8.0**. That framing is the result
                        of getting it wrong once: the first version was written around RFC 0053, the
                        first downstream to follow it planned a two-file job, and its clean build
                        surfaced three further breaks from elsewhere in the release.

                        A downstream upgrades a **version**. The RFC that motivated a change is
                        history the upgrader does not have.

                        **Do the whole thing on a clean build.** `mvn clean install`. An incremental
                        build does not recompile sources whose own files have not changed, so a
                        downstream can report green three times while being entirely broken. That is
                        not hypothetical — it happened during this release's development.""")
            .l1build()

            .l1("Triage — what applies to you")
                .relation(
                    List.of("If your studio…", "Affected"),
                    List.of(
                        List.of("only declares Catalogues, Docs, Plans and Entries", "yes — Entry.of(catalogue, doc) is gone; see Placement"),
                        List.of("CONSTRUCTS NormalizedNode or NormalizedNode.leaf", "yes — both signatures changed"),
                        List.of("fills a Map<DimensionKey, DimensionValue>", "yes — three keys and three value types deleted"),
                        List.of("overrides Doc.url()", "yes — deleted"),
                        List.of("implements ContentViewer or subclasses one", "yes — the whole registry retired"),
                        List.of("registers a tree via TreeRegistry, or links /tree", "yes — the tree app is gone"),
                        List.of("uses the DocBrowser kit", "yes — retired; the catalogue is the index"),
                        List.of("READS a machine key off sel.summary in widget JS", "YES, AND SILENTLY — the one break that compiles"),
                        List.of("uses the workspace, widgets, themes, conformance", "no"),
                        List.of("only consumes the studio as a reader", "no")),
                    "Nine of ten rows fail the build. The eighth does not.")
            .l1build()

            .l1("A — The tree substrate (RFC 0053)")
                .markdown("""
                        **A node now carries its segment and its identity.**

                        ```java
                        // before
                        new NormalizedNode(TreeLevel.L1.INSTANCE, dims(...), children)
                        NormalizedNode.leaf(TreeLevel.L2.INSTANCE, dims(...))

                        // after
                        new NormalizedNode(level, NodeName.slug(label), identity, Map.of(), children)
                        NormalizedNode.leaf(level, NodeName.slug(label), identity, Map.of())
                        ```

                        `NodeName` is the path segment — charset `[A-Za-z0-9._-]`, 48 characters,
                        sibling-unique or the boot fails. **It also lower-cases**, which matters in
                        section B.

                        `NodeIdentity` is an open interface, so bring your own record. Two rules:
                        **intrinsically global, never positional** — derive it from what the node *is*,
                        never from where it sits — and **equality is the contract**, because it is a
                        map key.

                        **The dimension vocabulary retires.** Display leaves the node entirely.""")
                .relation(
                    List.of("Was", "Now"),
                    List.of(
                        List.of("DisplayLabel dimension", "RowDisplay.label"),
                        List.of("Summary dimension (DELETED)", "RowDisplay.note"),
                        List.of("Category dimension (DELETED)", "RowDisplay.badge"),
                        List.of("Kind dimension (DELETED)", "RowDisplay.kind"),
                        List.of("LevelDepth (DELETED)", "depth lives in level() and always did"),
                        List.of("CategoryValue, KindValue, DepthValue (DELETED)", "no wrappers — RowDisplay is four strings"),
                        List.of("NodeKey dimension", "survives; the node's segment supersedes it"),
                        List.of("Map<DimensionKey, DimensionValue> on the node", "Map.of()")),
                    "DimensionKey itself survives — delete members, not the import")
                .markdown("""
                        Model what the node **is** in a type of your own, and render at the edge:

                        ```java
                        var details = new LinkedHashMap<NodeIdentity, MyDetails>();
                        NormalizedNode root = build(details);        // fill both in ONE walk
                        RowDisplaySource rows = node -> node instanceof NormalizedNode n
                                && details.get(n.identity()) != null
                                ? details.get(n.identity()).row() : null;
                        writer.write(root, rows);                   // the TWO-ARG overload
                        ```

                        **`write(root)` still exists and still compiles.** It simply emits no display
                        block, so the tree renders unlabelled. Use the two-argument overload.

                        Keep counts as `int` and enums as enums inside your details record — but fold
                        the count into the **label**, not only the note: `TreeRenderer` gates the note
                        behind `showNote`, which is false unless asked for, so a navigator tree draws
                        no note at all.

                        `NodeName` and `TreeNode` **moved module** — same names, new imports.""")
            .l1build()

            .l1("B — The one break that does not fail the build")
                .markdown("""
                        Read this section even if you skip the rest.

                        Before 0.8.0 the only display channel on a node was the dimension map, so
                        actions routinely smuggled a machine key through the `Summary` slot — a
                        portfolio id, a type name — and a widget read it back on selection:

                        ```js
                        var id = sel.summary;      // the node's machine field
                        var meta = index[id];
                        if (!meta) return;         // <- now always taken
                        ```

                        After the upgrade `sel.summary` is fed by `RowDisplay.note`, so it returns
                        prose. The lookup misses, the handler returns early, and **selection stops
                        driving anything while the tree still draws and still highlights.** No error,
                        nothing in the console.

                        The replacement is `sel.namePath`, which the renderer documents as *the RFC
                        0053 address and the one to prefer*. Two details that cost the first port
                        real time:

                        - **`namePath` excludes the root's own segment**, so the root selects as `''`.
                          A widget that addressed the root by id must handle that case explicitly.
                        - **`NodeName.slug()` lower-cases.** camelCase ids (`PairId`) become `pairid`,
                          so a direct `index[segment]` lookup misses. Fold case through a
                          `segment → id` map built from your index.

                        **The audit is one command:** grep your widget JS for `summary`. Every read
                        that wanted a key rather than a sentence is a silent defect after this
                        upgrade.""")
            .l1build()

            .l1("C — Placement: Entry and Doc")
                .relation(
                    List.of("Removed", "Replacement"),
                    List.of(
                        List.of("Entry.of(catalogue, doc)", "of(C, M, P, Doc) — the catalogue names the viewer"),
                        List.of("Entry.OfDoc variant", "OfLeaf, which binds a Navigable to content"),
                        List.of("AppDoc", "Entry.of(host, nav) — a Navigable is placeable directly"),
                        List.of("Doc.url()", "nothing — a doc no longer knows how it is viewed")),
                    "One idea, four removals: the doc stops choosing its own viewer")
                .markdown("""
                        These four are the same change seen from four sides, and they are the ones a
                        studio that *only declares content* still hits.

                        ```java
                        // before
                        Entry.of(this, UiStudyDoc.INSTANCE)

                        // after
                        Entry.of(this, DocReader.INSTANCE,
                                new DocReader.Params(UiStudyDoc.INSTANCE.uuid().toString()),
                                UiStudyDoc.INSTANCE)
                        ```

                        `AppDoc` was the wrapper a `Navigable` had to become in order to be placeable,
                        and it paid for that with a uuid seeded from display framing. `Doc.url()` was
                        how the framework asked a doc how it opens — the wrong thing to ask, since the
                        placement already knows.

                        **Known stale documentation:** `CatalogueIllustration`'s class javadoc still
                        shows `Entry.of(this, SomeDoc.INSTANCE)`, which no longer compiles. Do not
                        copy from it.""")
            .l1build()

            .l1("D — Retired subsystems")
                .relation(
                    List.of("Cluster", "Why it went"),
                    List.of(
                        List.of("ContentViewer + 7 impls, ContentRef, ContentTree", "a catalogue entry already declares what opens a doc"),
                        List.of("TreeAppHost, TreeGetAction, TreeRegistry, TreeBranch, TreeLeaf", "the tree app rendered nothing of its own"),
                        List.of("DocBrowser kit — 5 types", "RFC 0051: the catalogue tree is the only index"),
                        List.of("CatalogueTreeAdapter, CatalogueTreeGetAction, CatalogueTreeNode", "the legacy adapter cluster, with the tile grid"),
                        List.of("CatalogueAugmentation, SyntheticEntry", "the tile grid retired; the tree is the entry payload"),
                        List.of("DiagnosticsHub", "diagnostics reused the content catalogue; the workspace fits better"),
                        List.of("AppRefsGetAction", "/app-refs retired — the chrome reads the stamp"),
                        List.of("ParamsWriter", "an app is handed its params, never derives them")),
                    "Most are internal. ContentViewer is the one downstreams subclassed.")
                .markdown("""
                        **`ContentViewer` is the cluster most likely to affect you**, because the demo
                        studio itself had a subclass. It was RFC 0015's mechanism for *"this kind of
                        content opens with this app"*, and a typed catalogue entry now declares exactly
                        that at the placement — so the registry had nothing left to do.

                        Its own javadoc admitted it never routed: routing went through `Doc.url()`
                        polymorphism, and kind-to-viewer was never a function anyway — three docs
                        answer `composed`, two viewers serve them, and no viewer for `doc-tree-viewer`
                        was ever registered.

                        The rest of the table is framework interior. If you never named these types,
                        their removal costs you nothing beyond the routes in section E.""")
            .l1build()

            .l1("E — Routes and URLs")
                .relation(
                    List.of("Route", "Status"),
                    List.of(
                        List.of("/tree and /app?app=tree&id=…", "gone with the tree app"),
                        List.of("/app-refs", "retired — the chrome reads the stamp"),
                        List.of("moved /cat paths", "several nodes gained or changed a rung")),
                    "Links in prose are not compiler-caught — check them by hand")
                .markdown("""
                        These are the only breaks that neither the compiler nor a test will find for
                        you, because a link in a markdown segment is a string. If your docs cross-link
                        by path rather than by `#ref:`, walk them.

                        Cross-references written as `#ref:name` resolve by uuid and are unaffected.""")
            .l1build()

            .l1("Order of work, and what it cost")
                .markdown("""
                        The order that worked, from the first port:

                        1. bump `homing.core.version` to `0.8.0`;
                        2. `mvn clean install` and read the errors — they are the true blast radius,
                           and they arrive module by module;
                        3. fix placement first (section C), because those are one-liners and they
                           unblock the modules that hold your tree code;
                        4. port the actions (section A), minting one identity record per action;
                        5. **then grep your widget JS for `summary`** (section B) — nothing will remind
                           you;
                        6. clean build again, and exercise every tree selection by hand.

                        **Measured:** ten files for a studio of eleven Maven modules — two actions, two
                        new identity records, two tree widgets, two catalogues, one Doc subtype and the
                        pom. Green from clean in about eleven minutes, of which the tree substrate
                        itself was three. The conformance gate was unchanged by the port.

                        Step 5 is the one that is skipped. In the first port both tree widgets were
                        silently dead until they were checked by hand.""")
            .l1build()

            .build();
    }
}
