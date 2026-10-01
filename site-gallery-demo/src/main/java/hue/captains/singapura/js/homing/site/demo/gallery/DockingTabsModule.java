package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.panes.AddTabModule;
import hue.captains.singapura.js.homing.ui.panes.TabOpenerModule;
import hue.captains.singapura.js.homing.ui.panes.TabSourceModule;

import java.util.List;

/**
 * {@code DockingTabs}: where the docking page's tabs come from, and the
 * control that puts one somewhere. One object for the whole business, so that
 * the page running a workspace is about the workspace.
 *
 * <p>Every tab on the page comes through the one source — the four it opens
 * with, the ones the control adds, and the ones an opener becomes. One
 * counter, so no two tabs are ever the same name, and the page keeps no second
 * way of making a tab that could drift from this one. The first shape of this
 * page had one, and the ids collided the day a second "books" was asked
 * for.</p>
 */
public record DockingTabsModule() implements DomModule<DockingTabsModule> {

    /** The class: {@code new DockingTabs(branch, { host, panes, store, domain, note })}. */
    public record DockingTabs() implements Exportable._Constant<DockingTabsModule> {}

    public static final DockingTabsModule INSTANCE = new DockingTabsModule();

    @Override
    public ImportsFor<DockingTabsModule> imports() {
        return ImportsFor.<DockingTabsModule>builder()
                .add(new ModuleImports<>(List.of(new TabSourceModule.TabSource()), TabSourceModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new AddTabModule.AddTab()), AddTabModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TabOpenerModule.TabOpener()), TabOpenerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DockingKeysModule.KeysPicker()), DockingKeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new DockingSceneModule.BooksTab(),
                        new DockingSceneModule.ShelvesTab(),
                        new DockingSceneModule.PictureTab(),
                        new DockingSceneModule.NoteTab()
                ), DockingSceneModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DockingTabsModule> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new DockingTabs()));
    }
}
