package hue.captains.singapura.js.homing.blocks;

import hue.captains.singapura.js.homing.blocks.modal.ModalDemoApp;
import hue.captains.singapura.js.homing.blocks.multitabpane.MultiTabPaneDemoApp;
import hue.captains.singapura.js.homing.blocks.splitpane.SplitPaneDemoApp;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;

import java.util.List;

/**
 * The Building Blocks reference studio. Standalone studio whose entire
 * content is a structured catalogue of the framework's main primitives —
 * each block presented as a per-block sub-catalogue containing one guide
 * doc plus at least one live demo doc.
 *
 * <p>Audience: downstream agents (LLMs or developers) authoring a new
 * studio on top of the framework. Landing on any block's L1 catalogue
 * presents both the "how to use" prose and a working "this is what it
 * looks like" exhibit. Reading the demo doc's Java source teaches the
 * usage pattern by example.</p>
 *
 * <p>Independent of homing-demo. The demo studio composes this as a
 * sub-studio (RFC 0011 cross-studio refs) so a deployed demo surfaces
 * both the animal-game content AND the building-blocks reference under
 * one chrome.</p>
 */
public record BuildingBlocksStudio() implements Studio<BuildingBlocksCatalogue> {

    public static final BuildingBlocksStudio INSTANCE = new BuildingBlocksStudio();

    @Override
    public BuildingBlocksCatalogue home() { return BuildingBlocksCatalogue.INSTANCE; }

    @Override
    public List<AppModule<?, ?>> apps() {
        // The SplitPane demo embeds a live widget via a DocumentaryWidget
        // segment, which wraps an AppModule — so the demo's SingleWidgetMPA
        // shell must be registered here for the framework to serve its JS.
        return List.of(SplitPaneDemoApp.INSTANCE, MultiTabPaneDemoApp.INSTANCE, ModalDemoApp.INSTANCE);
    }

    @Override
    public StudioBrand standaloneBrand() {
        return new StudioBrand("Homing · Building Blocks", BuildingBlocksCatalogue.class);
    }
}
