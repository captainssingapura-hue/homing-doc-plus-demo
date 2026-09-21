package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.component.keyboard.KeyboardStewardModule;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.elements.Elements;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;

import java.util.List;

/**
 * The cards page: a grid of cards through the builder — plain, one that scrolls, one with a caller's own body, one
 * with an action — under a size slider, and one card at three sizes. Every card is the shared {@code Card}.
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
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.CardBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.SliderBuilder()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeyboardStewardModule.KeyboardSteward()), KeyboardStewardModule.INSTANCE))
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
