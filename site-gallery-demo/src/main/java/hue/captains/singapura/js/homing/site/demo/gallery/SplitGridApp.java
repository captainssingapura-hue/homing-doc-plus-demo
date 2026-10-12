package hue.captains.singapura.js.homing.site.demo.gallery;

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
import hue.captains.singapura.js.homing.ui.splitgrid.SplitGridMirrorModule;
import hue.captains.singapura.js.homing.ui.splitgrid.SplitGridModule;
import hue.captains.singapura.js.homing.ui.look.StatesAndEventsModule;
import hue.captains.singapura.js.homing.ui.elements.DangerButtonModule;
import hue.captains.singapura.js.homing.ui.elements.PlainButtonModule;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;

import java.util.List;

/**
 * The split grid page: one cell to start, a card in each cell that asks the grid to split beside it or remove it, the
 * dividers to drag, and the log of every change of arrangement with the layout as data.
 */
public record SplitGridApp() implements AppModule<AppModule._None, SplitGridApp> {

    public static final SplitGridApp INSTANCE = new SplitGridApp();

    record appMain() implements AppModule._AppMain<AppModule._None, SplitGridApp> {}
    /** The app as a widget by the base's contract: {@code new SplitGridWidget(branch, params)}; appMain delegates to it. */
    public record SplitGridWidget() implements BranchComponent<SplitGridApp> {}

    @Override public String title()      { return "Split grid"; }
    @Override public String simpleName() { return "splitgrid"; }

    @Override
    public ImportsFor<SplitGridApp> imports() {
        return ImportsFor.<SplitGridApp>builder()
                // its status and its log, the house's block of code
                .add(new ModuleImports<>(List.of(new LookModule.Look()), LookModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CastingsModule.ROLE()), CastingsModule.INSTANCE))
                // its words the house's texts
                .add(new ModuleImports<>(List.of(new LedeModule.Lede(), new LedeModule.LedeFactory()), LedeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Heading1Module.Heading1(), new Heading1Module.Heading1Factory()), Heading1Module.INSTANCE))
                .add(new ModuleImports<>(List.of(new KickerModule.Kicker(), new KickerModule.KickerFactory()), KickerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StatesAndEventsModule.EVENT()), StatesAndEventsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlainButtonModule.PlainButton(), new PlainButtonModule.PlainButtonFactory()), PlainButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DangerButtonModule.DangerButton(), new DangerButtonModule.DangerButtonFactory()), DangerButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SplitGridModule.SplitGrid()), SplitGridModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SplitGridMirrorModule.SplitGridMirror()), SplitGridMirrorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.Slider()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_grid_cell(),
                        new GalleryStyles.ga_grid_cell_current(),
                        new GalleryStyles.ga_control_readout(),
                        new GalleryStyles.ga_pane_host(),
                        new GalleryStyles.ga_log(),
                        new GalleryStyles.ga_buttons()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<SplitGridApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new SplitGridWidget()));
    }
}
