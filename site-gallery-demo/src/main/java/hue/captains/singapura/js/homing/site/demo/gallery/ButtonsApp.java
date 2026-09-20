package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.preferences.PreferencesStyles;
import hue.captains.singapura.js.homing.ui.elements.Elements;

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
    public record ButtonsWidget() implements Exportable._Constant<ButtonsApp> {}

    @Override public String title()      { return "Buttons"; }
    @Override public String simpleName() { return "buttons"; }

    @Override
    public ImportsFor<ButtonsApp> imports() {
        return ImportsFor.<ButtonsApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.ButtonBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new PreferencesStyles.pv_range()), PreferencesStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_buttons(),
                        new GalleryStyles.ga_status(),
                        new GalleryStyles.ga_control(),
                        new GalleryStyles.ga_control_label(),
                        new GalleryStyles.ga_control_readout()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ButtonsApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new ButtonsWidget()));
    }
}
