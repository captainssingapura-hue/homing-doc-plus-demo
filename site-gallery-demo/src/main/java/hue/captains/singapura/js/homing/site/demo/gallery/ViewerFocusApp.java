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
 * The viewers-in-focus page, an experiment: SVG viewers as widgets under the
 * focus model - members of their own focus parties, the viewport inside each
 * the native world - at the root, in a panel that catches and in one that
 * lets pass, with a home at the root: where the keys go when a viewport lets
 * them go, with the monitors and a log of every grant, release, mark and key
 * route.
 */
public record ViewerFocusApp() implements AppModule<AppModule._None, ViewerFocusApp> {

    public static final ViewerFocusApp INSTANCE = new ViewerFocusApp();

    record appMain() implements AppModule._AppMain<AppModule._None, ViewerFocusApp> {}
    /** The app as a widget by the base's contract: {@code new ViewerFocusWidget(branch, params)}; appMain delegates to it. */
    public record ViewerFocusWidget() implements BranchComponent<ViewerFocusApp> {
        @Override public String summary() { return "An SVG viewer, alone, as a widget under the focus model as designed; the monitors and a log of where the keys go."; }
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
                .add(new ModuleImports<>(List.of(new ViewerSceneModule.SvgViewer()), ViewerSceneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ViewerDrawings.focusTree()), ViewerDrawings.INSTANCE))
                .add(new ModuleImports<>(List.of(new FocusMonitorModule.FocusMonitor()), FocusMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StewardMonitorModule.StewardMonitor()), StewardMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_focus(),
                        new GalleryStyles.ga_focus_scene(),
                        new GalleryStyles.ga_focus_monitor(),
                        new GalleryStyles.ga_log()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ViewerFocusApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new ViewerFocusWidget()));
    }
}
