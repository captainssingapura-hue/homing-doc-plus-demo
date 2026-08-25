package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.demo.playground.AnimalPlaygroundSpec;
import hue.captains.singapura.js.homing.workspace.shell.GenericWorkspace;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;
import hue.captains.singapura.js.homing.studio.base.app.tree.TreeAppHost;
import hue.captains.singapura.js.homing.studio.base.theme.ThemesIntro;

import java.util.List;

/**
 * Home catalogue for the demo studio. Stateless record per RFC 0005 — the
 * shared {@code CatalogueAppHost} renders it. Also implements {@link DocProvider}
 * so {@link DemoIntroDoc} is reachable through the studio's {@code DocRegistry}
 * (required for {@code Entry.OfDoc} reachability validation at boot).
 */
public record DemoStudio() implements L0_Catalogue<DemoStudio>, DocProvider {

    public static final DemoStudio INSTANCE = new DemoStudio();

    @Override public String name()    { return "Demo Studio"; }
    @Override public String summary() { return "A tiny dogfood studio for homing-studio-base — branded with the turtle, running on its own port, configured in one file."; }

    @Override public List<Entry<DemoStudio>> leaves() {
        return List.of(
                Entry.of(this, DemoIntroDoc.INSTANCE),
                Entry.of(this, ComposedDemoDoc.INSTANCE),
                Entry.of(this, TableDemoDoc.INSTANCE),
                Entry.of(this, ImageDemoDoc.INSTANCE),
                // A dedicated demo: this studio's own catalogue tree mirrored into
                // a foldable RigidDoc, each node given a body by an external
                // content provider (SimpleListSegment + rotating SVG on leaves).
                Entry.of(this, DemoContentTreeDoc.INSTANCE),
                // RFC 0051 — "Moving Animal" used to be echoed here as a featured
                // tile, in addition to its canonical entry in AnimalGamesCatalogue.
                // Under the path axiom a navigable has at most ONE position, so the
                // echo is gone. The Site-in-a-Jar shape it illustrated — apps and
                // docs side by side under one chrome — is still on this very page:
                // the doc tiles above sit beside the app tiles below.
                Entry.of(this, new Navigable<>(
                        ThemesIntro.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Themes",
                        "Palette previews and one-click activation for Default / Forest / Sunset / Bauhaus.")),
                // Post-RFC-0034 — GenericWorkspace is the substrate's single
                // composition-model AppModule for EVERY workspace kind; the
                // ws_kind param selects the WorkspaceSpec. Titled generically
                // because /app-refs keys breadcrumbs by AppModule simpleName,
                // not by ws_kind — so this one AppDoc is the breadcrumb leaf
                // for all kinds (studio, animalPlayground, …). The specific
                // workspace is named by the chrome's subheader (spec.title),
                // not here. This catalogue entry opens the Animals Playground
                // spec as the demo's default landing kind.
                Entry.of(this, new Navigable<>(
                        GenericWorkspace.INSTANCE,
                        new GenericWorkspace.Params(AnimalPlaygroundSpec.INSTANCE.kind()),
                        "Generic Workspace",
                        "The substrate's single composition-model workspace app — one AppModule mounts any registered WorkspaceSpec by ws_kind. Opens the Animals Playground spec.")),
                Entry.of(this, new Navigable<>(
                        TreeAppHost.INSTANCE,
                        new TreeAppHost.Params("animals", null),
                        "Animals & Halloween",
                        "RFC 0016 ContentTree demo — cute SVG critters categorised into two branches.")),
                Entry.of(this, new Navigable<>(
                        TreeAppHost.INSTANCE,
                        new TreeAppHost.Params("interactive-animals", null),
                        "Interactive Animals & Halloween",
                        "Same two-branch shape as Animals & Halloween, but each leaf opens a per-animal ComposedDoc with three DocumentaryWidget segments (coin / extruder / decomposer)."))
        );
    }

    @Override public List<? extends L1_Catalogue<DemoStudio, ?>> subCatalogues() {
        return List.of(AnimalGamesCatalogue.INSTANCE, GridDemosCatalogue.INSTANCE);
    }

    @Override public List<Doc> docs() {
        return List.of(DemoIntroDoc.INSTANCE, DemoContentTreeDoc.INSTANCE);
    }
}
