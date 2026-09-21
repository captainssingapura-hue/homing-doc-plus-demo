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
 * The button, exercised as a page: every colour word the builder knows in
 * a row under one extent slider; a danger button whose extent follows the
 * data; and off for all. Built through {@code ButtonBuilder}, as every
 * button is.
 */
public record ButtonsApp() implements AppModule<AppModule._None, ButtonsApp> {

    public static final ButtonsApp INSTANCE = new ButtonsApp();

    record appMain() implements AppModule._AppMain<AppModule._None, ButtonsApp> {}
    /** The app as a widget by the base's contract: {@code new ButtonsWidget(branch, params)}; appMain delegates to it. */
    public record ButtonsWidget() implements BranchComponent<ButtonsApp> {}

    @Override public String title()      { return "Buttons"; }
    @Override public String simpleName() { return "buttons"; }

    @Override
    public ImportsFor<ButtonsApp> imports() {
        return ImportsFor.<ButtonsApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.ButtonBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.SliderBuilder()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeyboardStewardModule.KeyboardSteward()), KeyboardStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_buttons(),
                        new GalleryStyles.ga_status()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ButtonsApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new ButtonsWidget()));
    }
}
