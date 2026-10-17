package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.ui.look.IconsModule;
import hue.captains.singapura.js.homing.ui.look.LookModule;
import hue.captains.singapura.js.homing.ui.look.CastingsModule;
import hue.captains.singapura.js.homing.ui.elements.KickerModule;
import hue.captains.singapura.js.homing.ui.elements.Heading1Module;
import hue.captains.singapura.js.homing.ui.elements.LedeModule;
import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.elements.SliderGroupModule;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;

import java.util.List;

/**
 * The sliders page: the slider as the gallery's own control, on view — the
 * three axes the demos set (size, aspect, extent), a plain range with a
 * unit, one that is off, and the slider at its three sizes; every value on
 * the log, live and on release, so the two events are seen apart; the mixer
 * as a slider group, and the keys through the page's keyboard party, the
 * holder on the log.
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
                .add(new ModuleImports<>(List.of(new IconsModule.ICON()), IconsModule.INSTANCE))
                // its status and its log, the house's block of code
                .add(new ModuleImports<>(List.of(new LookModule.Look()), LookModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CastingsModule.ROLE()), CastingsModule.INSTANCE))
                // its words the house's texts
                .add(new ModuleImports<>(List.of(new LedeModule.Lede(), new LedeModule.LedeFactory()), LedeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Heading1Module.Heading1(), new Heading1Module.Heading1Factory()), Heading1Module.INSTANCE))
                .add(new ModuleImports<>(List.of(new KickerModule.Kicker(), new KickerModule.KickerFactory()), KickerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.Slider()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderGroupModule.SliderGroup()), SliderGroupModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_specimens(),
                        new GalleryStyles.ga_specimen_name(),
                        new GalleryStyles.ga_log()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<SlidersApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new SlidersWidget()));
    }
}
