package hue.captains.singapura.js.homing.demo.playground;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;

import java.util.List;

/**
 * The table demo: two plain HTML tables and two routes to a lifted row or
 * cell — by state (an attribute the design answers) or by class (a look the
 * component applies beside the base). Mounted by {@link TableDemoWidget}.
 */
public record TableDemoModule() implements DomModule<TableDemoModule> {

    /** Build the demo into a host, in mode "state" or "class". */
    public record mountTableDemo() implements Exportable._Constant<TableDemoModule> {}

    public static final TableDemoModule INSTANCE = new TableDemoModule();

    @Override
    public ImportsFor<TableDemoModule> imports() {
        return ImportsFor.<TableDemoModule>builder()
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_table(),
                        new StudioStyles.st_thead(),
                        new StudioStyles.st_th(),
                        new StudioStyles.st_tr(),
                        new StudioStyles.st_td()
                ), StudioStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new TableDemoStyles.tdm_note(),
                        new TableDemoStyles.tdm_heading(),
                        new TableDemoStyles.tdm_cell(),
                        new TableDemoStyles.tdm_row_lifted(),
                        new TableDemoStyles.tdm_cell_lifted()
                ), TableDemoStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<TableDemoModule> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new mountTableDemo()));
    }

    // ── what the two widgets share ───────────────────────────────────────

    /** The widgets' imports: this module's mount, and the root class. */
    static List<ModuleImports<? extends Importable>> widgetImports() {
        return List.of(
                new ModuleImports<>(List.of(new mountTableDemo()), INSTANCE),
                new ModuleImports<>(List.of(new TableDemoStyles.tdm_root()), TableDemoStyles.INSTANCE));
    }

    /** The widgets' construct body: a root, the demo in the mode, the controller the chrome expects. */
    static List<String> widgetBody(String mode) {
        return List.of(
                "    var root = branch.createElement('root', 'div');",
                "    css.setClass(root, tdm_root);",
                "    mountTableDemo(branch, root, '" + mode + "');",
                "    return { root: root, setActive: function (active) {} };");
    }
}
