package hue.captains.singapura.js.homing.site.demo.gallery.prefs;

import hue.captains.singapura.js.homing.component.Widget;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.relgrid.protocol.RelGridProtocolModule;
import hue.captains.singapura.js.homing.reltree.RelTreeModule;
import hue.captains.singapura.js.homing.reltree.RelTreeStockCellsModule;

import java.util.List;

/**
 * The master: the relation tree, from its own repo, wrapped as a widget
 * over the rigid tree the registry stamped into its params. Offers the
 * view the surface a tree widget offers — {@code onSelect(fn)},
 * {@code select(path)} — and nothing else; the tree's own keys move the
 * cursor, and moving the cursor is choosing.
 */
public record PreferencesTreeWidgetModule() implements Widget<Widget._None, PreferencesTreeWidgetModule> {

    /** The class. */
    public record PreferencesTreeWidget() implements Widget._Class<Widget._None, PreferencesTreeWidgetModule> {}

    public static final PreferencesTreeWidgetModule INSTANCE = new PreferencesTreeWidgetModule();

    @Override public String title() { return "Preferences tree"; }

    @Override
    public ImportsFor<PreferencesTreeWidgetModule> imports() {
        return ImportsFor.<PreferencesTreeWidgetModule>builder()
                .add(new ModuleImports<>(List.of(new RelTreeModule.RelTree()), RelTreeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelTreeStockCellsModule.RelTreeTextCell()), RelTreeStockCellsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new RelGridProtocolModule.RelTreeView(),
                        new RelGridProtocolModule.RelTreeUnfold(),
                        new RelGridProtocolModule.RelTreeFold()
                ), RelGridProtocolModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PreferencesTreeWidgetModule> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new PreferencesTreeWidget()));
    }
}
