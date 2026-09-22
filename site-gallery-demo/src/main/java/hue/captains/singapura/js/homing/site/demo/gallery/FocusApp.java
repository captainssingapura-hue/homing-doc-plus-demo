package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.KeyboardStewardModule;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.component.keyboard.focusParty;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.focus.FocusMonitorModule;

import java.util.List;

/**
 * The focus page: who is in focus. The logical-focus tree in its simplest
 * form — two panels holding a branch each, leaves in them, a loose leaf —
 * with the monitor beside it, every claim by the mouse, no yield yet.
 */
public record FocusApp() implements AppModule<AppModule._None, FocusApp> {

    public static final FocusApp INSTANCE = new FocusApp();

    record appMain() implements AppModule._AppMain<AppModule._None, FocusApp> {}
    /** The app as a widget by the base's contract: {@code new FocusWidget(branch, params)}; appMain delegates to it. The leaves and panels in it are the page's own members. */
    public record FocusWidget() implements BranchComponent<FocusApp>, NeedKeyboard {
        @Override public String summary() { return "Who is in focus: the logical-focus tree and its monitor; two panels, four leaves, claims by the mouse."; }
        /** The leaves' and the panels': the arrows, counted by a leaf that holds, taken and dropped by a panel. */
        @Override public List<KeyBinding> keys() { return KeyBinding.each("counted by the leaf that holds; taken and dropped by a panel that holds", Key.ARROW_UP, Key.ARROW_DOWN); }
    }

    @Override public String title()      { return "Focus"; }
    @Override public String simpleName() { return "focus"; }

    @Override
    public ImportsFor<FocusApp> imports() {
        return ImportsFor.<FocusApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParty()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeyboardStewardModule.KeyboardStewardInstance()), KeyboardStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new FocusMonitorModule.FocusMonitor()), FocusMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_focus(),
                        new GalleryStyles.ga_focus_scene(),
                        new GalleryStyles.ga_focus_monitor(),
                        new GalleryStyles.ga_panel(),
                        new GalleryStyles.ga_panel_header(),
                        new GalleryStyles.ga_leaf(),
                        new GalleryStyles.ga_holds(),
                        new GalleryStyles.ga_leaf_count(),
                        new GalleryStyles.ga_log()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<FocusApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new FocusWidget()));
    }
}
