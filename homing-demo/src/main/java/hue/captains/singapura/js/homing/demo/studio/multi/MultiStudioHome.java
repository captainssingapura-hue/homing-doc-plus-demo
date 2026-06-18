package hue.captains.singapura.js.homing.demo.studio.multi;

import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;

import java.util.List;

/**
 * Multi-studio launcher — the L0 home of the demo server, composing source
 * studios into typed L1 categories. Each category holds {@code StudioProxy}
 * tiles (RFC 0011) wrapping the source studio's L0; the breadcrumb chain
 * spans the boundary automatically.
 *
 * <p>RFC 0009 / 0010 / 0011 simultaneous worked example. As of 0.0.111 the
 * scaffolding hosts just one category — Learning — containing the demo
 * studio itself. The Tooling category (which previously held a SkillsHome
 * proxy) was retired with the public skills module. The shape stays in
 * place as illustration of how a downstream user adapts the umbrella with
 * additional categories pointing at their own studios.</p>
 */
public record MultiStudioHome() implements L0_Catalogue<MultiStudioHome> {

    public static final MultiStudioHome INSTANCE = new MultiStudioHome();

    @Override public String name()    { return "Homing Studios"; }
    @Override public String summary() { return "Categorised studios composed onto one server."; }
    @Override public String icon()    { return "🌐"; }

    @Override public List<? extends L1_Catalogue<MultiStudioHome, ?>> subCatalogues() {
        return List.of(
                LearningStudioCategory.INSTANCE,
                ReleasesCatalogue.INSTANCE
        );
    }
}
