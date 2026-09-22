package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.KeyboardStewardModule;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.component.keyboard.focusParty;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.relgrid.RelGridModule;
import hue.captains.singapura.js.homing.reltree.RelTreeModule;
import hue.captains.singapura.js.homing.ui.focus.FocusMonitorModule;
import hue.captains.singapura.js.homing.ui.focus.StewardMonitorModule;

import java.util.List;

/**
 * The relations-in-focus page: the relation grid and the relation tree, both
 * the native world, under the focus model — unwrapped, straight on the page,
 * and wrapped, each inside a panel of the logical world — with a leaf beside
 * them and the monitors: where the keys go with a host focused, and where
 * they go when it lets go.
 */
public record RelFocusApp() implements AppModule<AppModule._None, RelFocusApp> {

    public static final RelFocusApp INSTANCE = new RelFocusApp();

    record appMain() implements AppModule._AppMain<AppModule._None, RelFocusApp> {}
    /** The app as a widget by the base's contract: {@code new RelFocusWidget(branch, params)}; appMain delegates to it. */
    public record RelFocusWidget() implements BranchComponent<RelFocusApp>, NeedKeyboard {
        @Override public String summary() { return "The relation tree and grid, wrapped in panels of the logical world and unwrapped, under the focus model; the monitors beside them."; }
        /** The page's own seam: Escape from a wrapped host, wired by its panel. The grid's, the tree's, the panel's and the leaf's keys are declared by them. */
        @Override public List<KeyBinding> keys() {
            return List.of(KeyBinding.of(Key.ESCAPE, "in a wrapped grid or tree, when the host did not want it: the host let go, the panel has the keys"));
        }
    }

    @Override public String title()      { return "Relations in focus"; }
    @Override public String simpleName() { return "relfocus"; }

    @Override
    public ImportsFor<RelFocusApp> imports() {
        return ImportsFor.<RelFocusApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParty()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeyboardStewardModule.KeyboardStewardInstance()), KeyboardStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new FocusSceneModule.Leaf(), new FocusSceneModule.Panel()), FocusSceneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new FocusMonitorModule.FocusMonitor()), FocusMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StewardMonitorModule.StewardMonitor()), StewardMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridModule.RelGrid()), RelGridModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelTreeModule.RelTree()), RelTreeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryRelations.BooksStore(),
                        new GalleryRelations.BooksRelation(),
                        new GalleryRelations.ShelfTreeRelation()
                ), GalleryRelations.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_focus(),
                        new GalleryStyles.ga_focus_scene(),
                        new GalleryStyles.ga_focus_monitor(),
                        new GalleryStyles.ga_panel_header(),
                        new GalleryStyles.ga_rel_group(),
                        new GalleryStyles.ga_rel_host(),
                        new GalleryStyles.ga_log()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<RelFocusApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new RelFocusWidget()));
    }
}
