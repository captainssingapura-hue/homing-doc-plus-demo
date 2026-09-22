package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.KeyboardStewardModule;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
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
 * The focus page: who is in focus, in two worlds. Three panels holding a
 * branch each, leaves in them, a loose leaf — the logical world, members of
 * the focus party — and panel C's list and leaves, the native world; the
 * monitors beside them: the tree with the steward by state, and the
 * steward's activeness with where every key went. Claims by a press, Tab
 * over the tree, yields up it to the first ancestor that would hold; more
 * of the native world outside and inside the containers to stress the line.
 */
public record FocusApp() implements AppModule<AppModule._None, FocusApp> {

    public static final FocusApp INSTANCE = new FocusApp();

    record appMain() implements AppModule._AppMain<AppModule._None, FocusApp> {}
    /** The app as a widget by the base's contract: {@code new FocusWidget(branch, params)}; appMain delegates to it. The leaves and panels in it are the page's own members. */
    public record FocusWidget() implements BranchComponent<FocusApp>, NeedKeyboard {
        @Override public String summary() { return "Who is in focus, in two worlds: the logical-focus tree and its monitor; panels and leaves as members, panel C's list and leaves native; claims by a press, Tab over the tree, yields up it."; }
        /** The page's own: Tab, the steward's over the tree, kept by the notes field; the scene's components declare theirs. */
        @Override public List<KeyBinding> keys() {
            return List.of(KeyBinding.of(Key.TAB, "the steward's: the next member of the tree, Shift+Tab the previous, and round; in the notes field, a tab character - the field keeps it"),
                           KeyBinding.of(Key.ENTER, "on the reset button: the browser's"), KeyBinding.of(Key.SPACE, "on the reset button: the browser's"));
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
                .add(new ModuleImports<>(List.of(new FocusSceneModule.Leaf(), new FocusSceneModule.Panel(), new FocusSceneModule.ListPanel()), FocusSceneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new FocusMonitorModule.FocusMonitor()), FocusMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StewardMonitorModule.StewardMonitor()), StewardMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_focus(),
                        new GalleryStyles.ga_focus_scene(),
                        new GalleryStyles.ga_focus_monitor(),
                        new GalleryStyles.ga_focus_column(),
                        new GalleryStyles.ga_focus_tools(),
                        new GalleryStyles.ga_field(),
                        new GalleryStyles.ga_button(),
                        new GalleryStyles.ga_log()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<FocusApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new FocusWidget()));
    }
}
