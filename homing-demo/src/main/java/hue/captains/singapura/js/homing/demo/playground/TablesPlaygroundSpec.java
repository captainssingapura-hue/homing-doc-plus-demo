package hue.captains.singapura.js.homing.demo.playground;

import hue.captains.singapura.js.homing.workspace.RibbonItem;
import hue.captains.singapura.js.homing.workspace.WidgetEntry;
import hue.captains.singapura.js.homing.workspace.WidgetGroup;
import hue.captains.singapura.js.homing.workspace.WidgetIcon;
import hue.captains.singapura.js.homing.workspace.WidgetLabel;
import hue.captains.singapura.js.homing.workspace.shell.ActionDispatch;
import hue.captains.singapura.js.homing.workspace.shell.Arrangement;
import hue.captains.singapura.js.homing.workspace.shell.PaneArrangements;
import hue.captains.singapura.js.homing.workspace.shell.PartyDecl;
import hue.captains.singapura.js.homing.workspace.shell.WidgetCodecRef;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceSpec;
import hue.captains.singapura.js.homing.workspace.shell.WorkspaceSpecRegistry;

import java.util.List;
import java.util.Map;

/**
 * The Tables Playground — plain HTML tables under the design substrate, at
 * {@code ?app=genericWorkspace&ws_kind=tablesPlayground}: two columns, the
 * same tables lifted by state on the left and by class on the right, so the
 * two routes a component may take sit side by side under any design.
 * Switch the theme to see what each design makes of a selected row.
 *
 * <p>Pure declaration, registered on class load like
 * {@link AnimalPlaygroundSpec}.</p>
 */
public final class TablesPlaygroundSpec implements WorkspaceSpec {

    public static final TablesPlaygroundSpec INSTANCE;

    static {
        INSTANCE = new TablesPlaygroundSpec();
        WorkspaceSpecRegistry.INSTANCE.register(INSTANCE);
    }

    private TablesPlaygroundSpec() {}

    @Override public String kind()  { return "tablesPlayground"; }
    @Override public String title() { return "Tables Playground"; }
    @Override public String section() { return "Design"; }

    @Override
    public List<WidgetEntry> widgetEntries() {
        return List.of(
                WidgetEntry.of(StateTableWidget.class, WidgetLabel.of("Table — by state"))
                        .withIcon(new WidgetIcon.Emoji("🎛️"))
                        .withGroup(WidgetGroup.of("Tables")),
                WidgetEntry.of(ClassTableWidget.class, WidgetLabel.of("Table — by class"))
                        .withIcon(new WidgetIcon.Emoji("🎨"))
                        .withGroup(WidgetGroup.of("Tables")));
    }

    @Override public List<RibbonItem> ribbonItems() { return List.of(); }
    @Override public List<PartyDecl> parties() { return List.of(); }
    @Override public Map<String, ActionDispatch> actionDispatch() { return Map.of(); }
    @Override public List<WidgetCodecRef> widgetCodecs() { return List.of(); }

    @Override
    public Arrangement arrangement() {
        return PaneArrangements.COLUMNS.allocate()
                .place(PaneArrangements.Columns.LEFT,  StateTableWidget.class)
                .place(PaneArrangements.Columns.RIGHT, ClassTableWidget.class)
                .build();
    }
}
