package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.Modifier;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.menu.ContextMenuStewardModule;
import hue.captains.singapura.js.homing.ui.menu.NeedContextMenu;
import hue.captains.singapura.js.homing.ui.menu.tree.ContextMenuKind;

import java.util.List;
import java.util.Set;

/**
 * The context menus page: one steward with the gallery's kinds, three hypothetical cells each asking for a menu of its
 * kind — the animal's second level, the swatch's checked colour and toggle, the counter's disabled reset and checked
 * step — and the log of every open, pick and close.
 */
public record ContextMenusApp() implements AppModule<AppModule._None, ContextMenusApp> {

    public static final ContextMenusApp INSTANCE = new ContextMenusApp();

    record appMain() implements AppModule._AppMain<AppModule._None, ContextMenusApp> {}
    /** The app as a widget by the base's contract: {@code new ContextMenusWidget(branch, params)}; appMain delegates to it. */
    public record ContextMenusWidget() implements BranchComponent<ContextMenusApp>, NeedContextMenu, NeedKeyboard {
        /** The page holds the keys for its cells: the menu key on the cell the focus is on. */
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.CONTEXT_MENU, "the cell's menu"), KeyBinding.of(Key.F10, Modifier.SHIFT, "the cell's menu")); }
        @Override public String summary() { return "Three cells, each opening a menu of its kind; the kinds a specimen each."; }
        @Override public Set<ContextMenuKind<?>> required() { return Set.of(GalleryMenus.AnimalMenu.INSTANCE, GalleryMenus.SwatchMenu.INSTANCE, GalleryMenus.CounterMenu.INSTANCE); }
    }

    @Override public String title()      { return "Context menus"; }
    @Override public String simpleName() { return "menus"; }

    @Override
    public ImportsFor<ContextMenusApp> imports() {
        return ImportsFor.<ContextMenusApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ContextMenuStewardModule.ContextMenuSteward()), ContextMenuStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new GalleryMenus.MENUS()), GalleryMenus.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_menu_cells(),
                        new GalleryStyles.ga_cell(),
                        new GalleryStyles.ga_cell_face(),
                        new GalleryStyles.ga_cell_caption(),
                        new GalleryStyles.ga_swatch_primary(),
                        new GalleryStyles.ga_swatch_success(),
                        new GalleryStyles.ga_swatch_warning(),
                        new GalleryStyles.ga_swatch_danger(),
                        new GalleryStyles.ga_swatch_inverted(),
                        new GalleryStyles.ga_specimens(),
                        new GalleryStyles.ga_specimen_name(),
                        new GalleryStyles.ga_log()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ContextMenusApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new ContextMenusWidget()));
    }
}
