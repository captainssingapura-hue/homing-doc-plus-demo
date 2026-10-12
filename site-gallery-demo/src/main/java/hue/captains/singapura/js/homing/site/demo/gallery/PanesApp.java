package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.ui.elements.LookModule;
import hue.captains.singapura.js.homing.ui.elements.CastingsModule;
import hue.captains.singapura.js.homing.ui.elements.KickerModule;
import hue.captains.singapura.js.homing.ui.elements.Heading1Module;
import hue.captains.singapura.js.homing.ui.elements.LedeModule;
import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.component.WidgetSlotModule;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.monitors.FocusMonitorModule;
import hue.captains.singapura.js.homing.ui.monitors.StewardMonitorModule;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.dialog.DialogModule;
import hue.captains.singapura.js.homing.ui.elements.StatesAndEventsModule;
import hue.captains.singapura.js.homing.ui.elements.PlainButtonModule;
import hue.captains.singapura.js.homing.ui.elements.PrimaryButtonModule;
import hue.captains.singapura.js.homing.ui.elements.SummaryCardModule;
import hue.captains.singapura.js.homing.ui.panes.MultiTabPaneModule;
import hue.captains.singapura.js.homing.ui.docking.DeskModule;

import java.util.List;

/**
 * One multi-tab pane as a page. The page is the holder: it constructs a
 * widget on a branch of its own for each tab, hands the pane the widget,
 * tells the widget it is active from the pane's report, and dissolves the
 * branch when the pane reports the tab closed. The plus opens a picker in
 * the dialog; every mutation the pane reports goes on the log under it.
 */
public record PanesApp() implements AppModule<AppModule._None, PanesApp> {

    public static final PanesApp INSTANCE = new PanesApp();

    record appMain() implements AppModule._AppMain<AppModule._None, PanesApp> {}
    /** The app as a widget by the base's contract: {@code new PanesWidget(branch, params)}; appMain delegates to it. */
    public record PanesWidget() implements BranchComponent<PanesApp> {}

    @Override public String title()      { return "Panes"; }
    @Override public String simpleName() { return "panes"; }

    @Override
    public ImportsFor<PanesApp> imports() {
        return ImportsFor.<PanesApp>builder()
                // its status and its log, the house's block of code
                .add(new ModuleImports<>(List.of(new LookModule.Look()), LookModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CastingsModule.ROLE()), CastingsModule.INSTANCE))
                // its words the house's texts
                .add(new ModuleImports<>(List.of(new LedeModule.Lede(), new LedeModule.LedeFactory()), LedeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Heading1Module.Heading1(), new Heading1Module.Heading1Factory()), Heading1Module.INSTANCE))
                .add(new ModuleImports<>(List.of(new KickerModule.Kicker(), new KickerModule.KickerFactory()), KickerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StatesAndEventsModule.EVENT()), StatesAndEventsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new MultiTabPaneModule.MultiTabPane()), MultiTabPaneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DeskModule.Desk()), DeskModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new FocusMonitorModule.FocusMonitor()), FocusMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetSlotModule.WidgetSlot()), WidgetSlotModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StewardMonitorModule.StewardMonitor()), StewardMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DialogModule.Dialog()), DialogModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PrimaryButtonModule.PrimaryButton(), new PrimaryButtonModule.PrimaryButtonFactory()), PrimaryButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlainButtonModule.PlainButton(), new PlainButtonModule.PlainButtonFactory()), PlainButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SummaryCardModule.SummaryCard()), SummaryCardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_buttons(),
                        new GalleryStyles.ga_pane_host(),
                        new GalleryStyles.ga_focus(),
                        new GalleryStyles.ga_focus_monitor(),
                        new GalleryStyles.ga_log(),
                        new GalleryStyles.ga_status(),
                        new GalleryStyles.ga_count()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PanesApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new PanesWidget()));
    }
}
