package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;

import java.util.List;

/**
 * The sliders page: the slider as the gallery's own control, on view — the
 * three axes the demos set (size, aspect, extent), a plain range with a
 * unit, one that is off, and the slider at its three sizes; every value on
 * the log, live and on release, so the two events are seen apart.
 */
public record SlidersApp() implements AppModule<AppModule._None, SlidersApp> {

    public static final SlidersApp INSTANCE = new SlidersApp();

    record appMain() implements AppModule._AppMain<AppModule._None, SlidersApp> {}
    /** The app as a widget by the base's contract: {@code new SlidersWidget(branch, params)}; appMain delegates to it. */
    public record SlidersWidget() implements BranchComponent<SlidersApp> {}

    @Override public String title()      { return "Sliders"; }
    @Override public String simpleName() { return "sliders"; }

    @Override
    public ImportsFor<SlidersApp> imports() {
        return ImportsFor.<SlidersApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.SliderBuilder()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_specimens(),
                        new GalleryStyles.ga_specimen_name(),
                        new GalleryStyles.ga_mixer(),
                        new GalleryStyles.ga_log()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<SlidersApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new SlidersWidget()));
    }
}
