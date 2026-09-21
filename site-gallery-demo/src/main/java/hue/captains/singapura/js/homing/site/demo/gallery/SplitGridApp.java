package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.preferences.PreferencesStyles;
import hue.captains.singapura.js.homing.ui.splitgrid.SplitGridMirrorModule;
import hue.captains.singapura.js.homing.ui.splitgrid.SplitGridModule;
import hue.captains.singapura.js.homing.ui.elements.Elements;

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
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.ButtonBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new SplitGridModule.SplitGrid()), SplitGridModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SplitGridMirrorModule.SplitGridMirror()), SplitGridMirrorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PreferencesStyles.pv_range()), PreferencesStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_grid_cell(),
                        new GalleryStyles.ga_grid_cell_current(),
                        new GalleryStyles.ga_control(),
                        new GalleryStyles.ga_control_label(),
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
