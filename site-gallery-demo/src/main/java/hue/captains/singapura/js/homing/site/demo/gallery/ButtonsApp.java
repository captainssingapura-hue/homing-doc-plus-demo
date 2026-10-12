package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.ui.elements.LookModule;
import hue.captains.singapura.js.homing.ui.elements.CastingsModule;
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
import hue.captains.singapura.js.homing.ui.elements.StatesAndEventsModule;
import hue.captains.singapura.js.homing.ui.elements.DangerButtonModule;
import hue.captains.singapura.js.homing.ui.elements.PlainButtonModule;
import hue.captains.singapura.js.homing.ui.elements.PrimaryButtonModule;
import hue.captains.singapura.js.homing.ui.elements.SecondaryButtonModule;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;
import hue.captains.singapura.js.homing.ui.elements.SuccessButtonModule;
import hue.captains.singapura.js.homing.ui.elements.WarningButtonModule;

import java.util.List;

/**
 * The button, exercised as a page: every button leaf in a row under one
 * extent slider; a danger button whose extent follows the data; and off for
 * all. Each made as its leaf class, as every button is: the rank is the class.
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
                // its status and its log, the house's block of code
                .add(new ModuleImports<>(List.of(new LookModule.Look()), LookModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CastingsModule.ROLE()), CastingsModule.INSTANCE))
                // its words the house's texts
                .add(new ModuleImports<>(List.of(new KickerModule.Kicker(), new KickerModule.KickerFactory()), KickerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new LedeModule.Lede(), new LedeModule.LedeFactory()), LedeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Heading1Module.Heading1(), new Heading1Module.Heading1Factory()), Heading1Module.INSTANCE))
                .add(new ModuleImports<>(List.of(new StatesAndEventsModule.EVENT()), StatesAndEventsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlainButtonModule.PlainButton(), new PlainButtonModule.PlainButtonFactory()), PlainButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PrimaryButtonModule.PrimaryButton(), new PrimaryButtonModule.PrimaryButtonFactory()), PrimaryButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SecondaryButtonModule.SecondaryButton(), new SecondaryButtonModule.SecondaryButtonFactory()), SecondaryButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DangerButtonModule.DangerButton(), new DangerButtonModule.DangerButtonFactory()), DangerButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WarningButtonModule.WarningButton(), new WarningButtonModule.WarningButtonFactory()), WarningButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SuccessButtonModule.SuccessButton(), new SuccessButtonModule.SuccessButtonFactory()), SuccessButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.Slider()), SliderModule.INSTANCE))
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
