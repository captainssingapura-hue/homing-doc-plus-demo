package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.demo.es.MovingAnimal;
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
                // Featured top-level app — the flagship interactive demo, given
                // a tile of its own on the home page in addition to its entry
                // inside the AnimalGamesCatalogue sub-catalogue. Demonstrates
                // the Site-in-a-Jar shape: apps and docs side by side under
                // shared chrome.
                Entry.of(this, new Navigable<>(
                        MovingAnimal.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Moving Animal",
                        "Platform game served as a top-level catalogue leaf. The first AppModule on the demo studio's home page — apps and docs side by side, one chrome, one jar.")),
                Entry.of(this, new Navigable<>(
                        ThemesIntro.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Themes",
                        "Palette previews and one-click activation for Default / Forest / Sunset / Bauhaus.")),
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
        return List.of(AnimalGamesCatalogue.INSTANCE);
    }

    @Override public List<Doc> docs() {
        return List.of(DemoIntroDoc.INSTANCE);
    }
}
