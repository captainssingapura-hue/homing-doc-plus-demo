package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.demo.es.DancingAnimals;
import hue.captains.singapura.js.homing.demo.es.DecomposedSvgDemo;
import hue.captains.singapura.js.homing.demo.es.ExtrudedSvgDemo;
import hue.captains.singapura.js.homing.demo.es.ExtrudedTurtleDemo;
import hue.captains.singapura.js.homing.demo.es.MovingAnimal;
import hue.captains.singapura.js.homing.demo.es.SpinningAnimals;
import hue.captains.singapura.js.homing.demo.playground.AnimalsPlayground;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;

import java.util.List;

/**
 * RFC 0012 — the Demo studio's typed bundle. Home is {@link DemoStudio};
 * standalone brand labelled "Homing · demo".
 *
 * <p>Intrinsic apps: the three 3D ES-module demos
 * ({@link ExtrudedTurtleDemo} — coin gallery; {@link ExtrudedSvgDemo}
 * — pure SVG extrusion; {@link DecomposedSvgDemo} — SVG decomposition),
 * registered here so they're routable from the studio server and so they
 * can be hosted as {@code DocumentaryWidget}-shaped catalogue leaves
 * (today via {@code AppDoc}) alongside the {@link AnimalsTree}'s 2D
 * SvgDoc leaves. The first batch of widget-shaped Docs in the studio.</p>
 */
public record DemoBaseStudio() implements Studio<DemoStudio> {

    public static final DemoBaseStudio INSTANCE = new DemoBaseStudio();

    @Override
    public DemoStudio home() { return DemoStudio.INSTANCE; }

    @Override
    public List<AppModule<?, ?>> apps() {
        return List.of(
                ExtrudedTurtleDemo.INSTANCE,
                ExtrudedSvgDemo.INSTANCE,
                DecomposedSvgDemo.INSTANCE,
                // Animal-game SPAs surfaced under the AnimalGamesCatalogue
                // sub-catalogue + MovingAnimal as a top-level tile. Site-in-a-Jar
                // POC — apps and docs side by side under the same chrome.
                MovingAnimal.INSTANCE,
                DancingAnimals.INSTANCE,
                SpinningAnimals.INSTANCE,
                // RFC 0024 Phase P1b — the new shell + widget path. Hosts
                // SvgWidget; cohabits with the legacy SvgViewer.
                DemoStandardMPA.INSTANCE,
                // RFC 0025 Ext1b POC — the workspace demo. Hosts a
                // WidgetPicker over MultiTabPane, with the pinned
                // DocViewWidget intro + a SpinningAnimalsWidget tile.
                AnimalsPlayground.INSTANCE
        );
    }

    @Override
    public StudioBrand standaloneBrand() {
        return new StudioBrand("Homing · demo", DemoStudio.class);
    }
}
