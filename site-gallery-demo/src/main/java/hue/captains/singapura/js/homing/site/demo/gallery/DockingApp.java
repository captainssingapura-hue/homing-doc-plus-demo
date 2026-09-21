package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.component.keyboard.KeyboardStewardModule;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.docking.DockingModule;
import hue.captains.singapura.js.homing.ui.menu.ContextMenuStewardModule;
import hue.captains.singapura.js.homing.ui.panes.MultiTabPaneModule;
import hue.captains.singapura.js.homing.ui.elements.Elements;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;

import java.util.List;

/**
 * The docking page: one dock and a desk over it, tabs pulled down off the strip to float and floats dropped on the
 * strip to dock, with the log of every mutation the desk, the dock and the docking report.
 */
public record DockingApp() implements AppModule<AppModule._None, DockingApp> {

    public static final DockingApp INSTANCE = new DockingApp();

    record appMain() implements AppModule._AppMain<AppModule._None, DockingApp> {}
    /** The app as a widget by the base's contract: {@code new DockingWidget(branch, params)}; appMain delegates to it. */
    public record DockingWidget() implements BranchComponent<DockingApp> {}

    @Override public String title()      { return "Dock and undock"; }
    @Override public String simpleName() { return "docking"; }

    @Override
    public ImportsFor<DockingApp> imports() {
        return ImportsFor.<DockingApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.ButtonBuilder(), new Elements.CardBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new DockingModule.Docking()), DockingModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new MultiTabPaneModule.MultiTabPane()), MultiTabPaneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ContextMenuStewardModule.ContextMenuSteward()), ContextMenuStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new GalleryMenus.MENUS()), GalleryMenus.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.SliderBuilder()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeyboardStewardModule.KeyboardSteward()), KeyboardStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_count(),
                        new GalleryStyles.ga_dock_box(),
                        new GalleryStyles.ga_log(),
                        new GalleryStyles.ga_buttons()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DockingApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new DockingWidget()));
    }
}
