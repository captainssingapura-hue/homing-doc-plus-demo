package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.focusParty;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.docking.DeskModule;
import hue.captains.singapura.js.homing.ui.docking.DockGridModule;
import hue.captains.singapura.js.homing.ui.menu.ContextMenuStewardModule;
import hue.captains.singapura.js.homing.ui.panes.MultiTabPaneModule;
import hue.captains.singapura.js.homing.ui.dialog.DialogModule;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;

import java.util.List;

/**
 * The docking page: two docks side by side in a split grid and a desk over both; a tab detached by its menu floats,
 * a float dropped on either strip docks there, with the log of every mutation the desk, the docks and the docking report.
 */
public record DockingApp() implements AppModule<AppModule._None, DockingApp> {

    public static final DockingApp INSTANCE = new DockingApp();

    record appMain() implements AppModule._AppMain<AppModule._None, DockingApp> {}
    /** The app as a widget by the base's contract: {@code new DockingWidget(branch, params)}; appMain delegates to it. */
    public record DockingWidget() implements BranchComponent<DockingApp> {
        @Override public String summary() { return "Docks in a split grid over a desk: tabs travel between them, and a tab bar's own ground parts the room or closes a region."; }
    }

    @Override public String title()      { return "Dock and undock"; }
    @Override public String simpleName() { return "docking"; }

    @Override
    public ImportsFor<DockingApp> imports() {
        return ImportsFor.<DockingApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new DockingSceneModule.PictureTab(),
                        new DockingSceneModule.RegionList()
                ), DockingSceneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DockingMonitorsModule.Instruments()), DockingMonitorsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new GalleryRelations.BooksStore()), GalleryRelations.INSTANCE))
                .add(new ModuleImports<>(List.of(new DeskModule.Desk()), DeskModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DockGridModule.DockGrid()), DockGridModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new MultiTabPaneModule.MultiTabPane()), MultiTabPaneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParty()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ContextMenuStewardModule.ContextMenuSteward()), ContextMenuStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new GalleryMenus.MENUS()), GalleryMenus.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.SliderBuilder()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DialogModule.Dialog()), DialogModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DockingTabsModule.DockingTabs()), DockingTabsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_count(),
                        new GalleryStyles.ga_dock_box(),
                        new GalleryStyles.ga_log(),
                        new GalleryStyles.ga_buttons(),
                        new GalleryStyles.ga_floor(),
                        new GalleryStyles.ga_region()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DockingApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new DockingWidget()));
    }
}
