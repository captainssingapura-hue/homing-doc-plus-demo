package hue.captains.singapura.js.homing.mermaidmirror;

import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;

/** The tiny Mermaid-mirror studio (port 1). One catalogue, one Mermaid doc. */
public record MermaidDemoStudio() implements Studio<MermaidDemoCatalogue> {

    public static final MermaidDemoStudio INSTANCE = new MermaidDemoStudio();

    @Override public MermaidDemoCatalogue home() { return MermaidDemoCatalogue.INSTANCE; }

    @Override
    public StudioBrand standaloneBrand() {
        return new StudioBrand("Mermaid Mirror Demo", MermaidDemoCatalogue.class);
    }
}
