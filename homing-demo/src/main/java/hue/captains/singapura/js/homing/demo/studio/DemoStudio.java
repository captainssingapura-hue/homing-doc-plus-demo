package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.demo.playground.AnimalPlaygroundSpec;
import hue.captains.singapura.js.homing.workspace.shell.GenericWorkspace;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.base.app.DocTreeViewer;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedViewer;
import hue.captains.singapura.js.homing.studio.base.image.ImageViewer;
import hue.captains.singapura.js.homing.studio.base.table.TableViewer;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;
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
                // RFC 0051 Phase 6 - the placement names the viewer. A doc no
                // longer says how it opens; this catalogue does, for the doc it
                // places here.
                Entry.of(this, DocReader.INSTANCE,
                        new DocReader.Params(DemoIntroDoc.INSTANCE.uuid().toString()),
                        DemoIntroDoc.INSTANCE),
                Entry.of(this, ComposedViewer.INSTANCE,
                        new ComposedViewer.Params(ComposedDemoDoc.INSTANCE.uuid().toString()),
                        ComposedDemoDoc.INSTANCE),
                Entry.of(this, TableViewer.INSTANCE,
                        new TableViewer.Params(TableDemoDoc.INSTANCE.uuid().toString()),
                        TableDemoDoc.INSTANCE),
                Entry.of(this, ImageViewer.INSTANCE,
                        new ImageViewer.Params(ImageDemoDoc.INSTANCE.uuid().toString()),
                        ImageDemoDoc.INSTANCE),
                // A dedicated demo: this studio's own catalogue tree mirrored into
                // a foldable RigidDoc, each node given a body by an external
                // content provider (SimpleListSegment + rotating SVG on leaves).
                Entry.of(this, DocTreeViewer.INSTANCE,
                        new DocTreeViewer.Params(DemoContentTreeDoc.INSTANCE.uuid().toString()),
                        DemoContentTreeDoc.INSTANCE),
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
                        "The substrate's single composition-model workspace app — one AppModule mounts any registered WorkspaceSpec by ws_kind. Opens the Animals Playground spec."))
        );
    }

    @Override public List<? extends L1_Catalogue<DemoStudio, ?>> subCatalogues() {
        return List.of(AnimalGamesCatalogue.INSTANCE, GridDemosCatalogue.INSTANCE);
    }

    @Override public List<Doc> docs() {
        return List.of(DemoIntroDoc.INSTANCE, DemoContentTreeDoc.INSTANCE);
    }
}
