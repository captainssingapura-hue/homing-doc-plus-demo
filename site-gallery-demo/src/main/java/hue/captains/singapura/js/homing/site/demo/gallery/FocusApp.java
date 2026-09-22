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
 * The focus page: who is in focus, in two worlds. Three panels holding a
 * branch each, leaves in them, a loose leaf — the logical world, members of
 * the focus party — and panel C's list and leaves, the native world; the
 * monitor beside them with the steward by state. Claims by a press, Tab over
 * the tree, yields up it to the first ancestor that would hold.
 */
public record FocusApp() implements AppModule<AppModule._None, FocusApp> {

    public static final FocusApp INSTANCE = new FocusApp();

    record appMain() implements AppModule._AppMain<AppModule._None, FocusApp> {}
    /** The app as a widget by the base's contract: {@code new FocusWidget(branch, params)}; appMain delegates to it. The leaves and panels in it are the page's own members. */
    public record FocusWidget() implements BranchComponent<FocusApp>, NeedKeyboard {
        @Override public String summary() { return "Who is in focus, in two worlds: the logical-focus tree and its monitor; panels and leaves as members, panel C's list and leaves native; claims by a press, Tab over the tree, yields up it."; }
        /** The members' keys by the steward, and the native leaves' and the list's own, wired by panel C. */
        @Override public List<KeyBinding> keys() {
            return List.of(KeyBinding.of(Key.ARROW_UP, "counted by the leaf that holds, or the native leaf that is focused; taken and dropped by a panel"), KeyBinding.of(Key.ARROW_DOWN, "counted by the leaf that holds, or the native leaf that is focused; taken and dropped by a panel"),
                           KeyBinding.of(Key.ESCAPE, "the holder yields: the keys go up to the first ancestor that would hold them; in panel C's list, the list let go and the panel yielded; on a native leaf, back to the list"),
                           KeyBinding.of(Key.ENTER, "in panel C's list: the pick confirmed, the leaf told to focus itself"),
                           KeyBinding.of(Key.TAB, "the steward's: the next member of the tree, Shift+Tab the previous, and round"));
        }
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
                        new GalleryStyles.ga_leaf_yield(),
                        new GalleryStyles.ga_leaf_native(),
                        new GalleryStyles.ga_panel_note(),
                        new GalleryStyles.ga_panel_list(),
                        new GalleryStyles.ga_log()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<FocusApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new FocusWidget()));
    }
}
