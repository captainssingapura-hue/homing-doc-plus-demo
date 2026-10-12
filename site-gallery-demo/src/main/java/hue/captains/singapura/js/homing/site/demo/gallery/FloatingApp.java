package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.ui.elements.LookModule;
import hue.captains.singapura.js.homing.ui.elements.CastingsModule;
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
import hue.captains.singapura.js.homing.ui.floating.FloatLayerModule;
import hue.captains.singapura.js.homing.ui.elements.StatesAndEventsModule;
import hue.captains.singapura.js.homing.ui.elements.PlainButtonModule;
import hue.captains.singapura.js.homing.ui.elements.PrimaryButtonModule;
import hue.captains.singapura.js.homing.ui.elements.SummaryCardModule;

import java.util.List;

/**
 * The floating page: a desk filling its box, buttons opening panes that hold widgets by the base's contract, and the
 * log of every mutation the desk reports.
 */
public record FloatingApp() implements AppModule<AppModule._None, FloatingApp> {

    public static final FloatingApp INSTANCE = new FloatingApp();

    record appMain() implements AppModule._AppMain<AppModule._None, FloatingApp> {}
    /** The app as a widget by the base's contract: {@code new FloatingWidget(branch, params)}; appMain delegates to it. */
    public record FloatingWidget() implements BranchComponent<FloatingApp> {}

    @Override public String title()      { return "Floating panes"; }
    @Override public String simpleName() { return "floating"; }

    @Override
    public ImportsFor<FloatingApp> imports() {
        return ImportsFor.<FloatingApp>builder()
                // its status and its log, the house's block of code
                .add(new ModuleImports<>(List.of(new LookModule.Look()), LookModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CastingsModule.ROLE()), CastingsModule.INSTANCE))
                // its words the house's texts
                .add(new ModuleImports<>(List.of(new LedeModule.Lede(), new LedeModule.LedeFactory()), LedeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Heading1Module.Heading1(), new Heading1Module.Heading1Factory()), Heading1Module.INSTANCE))
                .add(new ModuleImports<>(List.of(new KickerModule.Kicker(), new KickerModule.KickerFactory()), KickerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StatesAndEventsModule.EVENT()), StatesAndEventsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PrimaryButtonModule.PrimaryButton(), new PrimaryButtonModule.PrimaryButtonFactory()), PrimaryButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlainButtonModule.PlainButton(), new PlainButtonModule.PlainButtonFactory()), PlainButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SummaryCardModule.SummaryCard()), SummaryCardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new FloatLayerModule.FloatLayer()), FloatLayerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_count(),
                        new GalleryStyles.ga_pane_host(),
                        new GalleryStyles.ga_log(),
                        new GalleryStyles.ga_buttons()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<FloatingApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new FloatingWidget()));
    }
}
