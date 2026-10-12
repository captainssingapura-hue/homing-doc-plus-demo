package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.ui.elements.Heading1Module;
import hue.captains.singapura.js.homing.ui.elements.LedeModule;
import hue.captains.singapura.js.homing.ui.elements.KickerModule;
import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;
import hue.captains.singapura.js.homing.ui.elements.SummaryCardModule;

import java.util.List;

/**
 * The cards page: a grid of summary cards — plain, one that scrolls, one with a caller's own body, one with an
 * action — under a size slider, and one card at three sizes. Every card is the shared {@code SummaryCard}.
 */
public record CardsApp() implements AppModule<AppModule._None, CardsApp> {

    public static final CardsApp INSTANCE = new CardsApp();

    record appMain() implements AppModule._AppMain<AppModule._None, CardsApp> {}
    /** The app as a widget by the base's contract: {@code new CardsWidget(branch, params)}; appMain delegates to it. */
    public record CardsWidget() implements BranchComponent<CardsApp> {}

    @Override public String title()      { return "Cards"; }
    @Override public String simpleName() { return "cards"; }

    @Override
    public ImportsFor<CardsApp> imports() {
        return ImportsFor.<CardsApp>builder()
                // its words the house's texts
                .add(new ModuleImports<>(List.of(new KickerModule.Kicker(), new KickerModule.KickerFactory()), KickerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new LedeModule.Lede(), new LedeModule.LedeFactory()), LedeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Heading1Module.Heading1(), new Heading1Module.Heading1Factory()), Heading1Module.INSTANCE))
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SummaryCardModule.SummaryCard()), SummaryCardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.Slider()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_cards(),
                        new GalleryStyles.ga_card_list(),
                        new GalleryStyles.ga_status()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CardsApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new CardsWidget()));
    }
}
