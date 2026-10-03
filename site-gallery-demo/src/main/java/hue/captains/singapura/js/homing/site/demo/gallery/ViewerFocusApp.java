package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.KeyboardStewardModule;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.focusParty;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.focus.FocusMonitorModule;
import hue.captains.singapura.js.homing.ui.focus.StewardMonitorModule;

import java.util.List;

/**
 * The viewers-in-focus page, a case board: where the keys go when a widget's
 * native control lets them go. Seven cases - the home, a viewer at the root,
 * in a panel that catches, in one that lets pass, one that keeps the keys, one
 * in a layer that keeps Escape, and no home - each with its checks and lamps,
 * lit from the route of every Escape; the monitors and a log beside them.
 */
public record ViewerFocusApp() implements AppModule<AppModule._None, ViewerFocusApp> {

    public static final ViewerFocusApp INSTANCE = new ViewerFocusApp();

    record appMain() implements AppModule._AppMain<AppModule._None, ViewerFocusApp> {}
    /** The app as a widget by the base's contract: {@code new ViewerFocusWidget(branch, params)}; appMain delegates to it. */
    public record ViewerFocusWidget() implements BranchComponent<ViewerFocusApp> {
        @Override public String summary() { return "A case board: SVG viewers as widgets, and where the keys go when a viewport lets them go - each case with its checks and lamps."; }
    }

    @Override public String title()      { return "Viewers in focus"; }
    @Override public String simpleName() { return "viewerfocus"; }

    @Override
    public ImportsFor<ViewerFocusApp> imports() {
        return ImportsFor.<ViewerFocusApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParty()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeyboardStewardModule.KeyboardStewardInstance()), KeyboardStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new FocusSceneModule.Leaf(), new FocusSceneModule.Panel()), FocusSceneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ViewerSceneModule.SvgViewer(), new ViewerSceneModule.EscapeLayer()), ViewerSceneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ViewerCasesModule.ViewerCase(), new ViewerCasesModule.CaseBoard()), ViewerCasesModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ViewerDrawings.focusTree(), new ViewerDrawings.specimen()), ViewerDrawings.INSTANCE))
                .add(new ModuleImports<>(List.of(new FocusMonitorModule.FocusMonitor()), FocusMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StewardMonitorModule.StewardMonitor()), StewardMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_focus(),
                        new GalleryStyles.ga_focus_scene(),
                        new GalleryStyles.ga_focus_monitor(),
                        new GalleryStyles.ga_log(),
                        new GalleryStyles.ga_button(),
                        new GalleryStyles.ga_case_figure()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ViewerFocusApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new ViewerFocusWidget()));
    }
}
