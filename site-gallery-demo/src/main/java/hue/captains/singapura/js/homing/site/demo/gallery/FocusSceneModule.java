package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/**
 * The focus page's scene, in two worlds: {@code Leaf} and {@code Panel} are
 * members of the focus party — never natively focused, keys from the steward;
 * the controls a panel puts inside itself are the native world;
 * {@code ListPanel} picks which of its leaves holds with a native select,
 * wired by a listener on its own root.
 */
public record FocusSceneModule() implements DomModule<FocusSceneModule> {

    /** A leaf of the logical world: {@code new Leaf(branch, host, focusBranch, name, onHold?)}; a press claims, arrows counted while it holds, Escape yields. */
    public record Leaf() implements BranchComponent<FocusSceneModule>, NeedKeyboard {
        @Override public String summary() { return "A leaf of the logical world: a member, never natively focused; a press claims, the arrows count while it holds, Escape yields."; }
        @Override public List<KeyBinding> keys() {
            return List.of(KeyBinding.of(Key.ARROW_UP, "counted, while the leaf holds"), KeyBinding.of(Key.ARROW_DOWN, "counted, while the leaf holds"),
                           KeyBinding.of(Key.ESCAPE, "the leaf yields: the keys go up to the first ancestor that would hold them"),
                           KeyBinding.of(Key.SPACE, "on the checkbox inside a2: the browser's"));
        }
    }
    /** A panel: {@code new Panel(branch, host, focusBranch, name, catches)}; holds a branch, answers wouldHold; puts native controls inside itself. */
    public record Panel() implements BranchComponent<FocusSceneModule>, NeedKeyboard {
        @Override public String summary() { return "A container of the logical world: holds a branch, a press on its header claims, a yield from below it catches or lets pass; wires the native controls it puts inside itself."; }
        @Override public List<KeyBinding> keys() {
            return List.of(KeyBinding.of(Key.ARROW_UP, "taken and dropped, while the panel holds"), KeyBinding.of(Key.ARROW_DOWN, "taken and dropped, while the panel holds"),
                           KeyBinding.of(Key.ESCAPE, "the panel yields; in its text field, the field let go"),
                           KeyBinding.of(Key.ENTER, "on its button: the browser's"), KeyBinding.of(Key.SPACE, "on its button: the browser's"));
        }
    }
    /** Panel C: {@code new ListPanel(branch, host, focusBranch, name)}; a select that picks which of its leaves holds, wired on its own root. */
    public record ListPanel() implements BranchComponent<FocusSceneModule>, NeedKeyboard {
        @Override public String summary() { return "A panel with a native select that picks which of its leaves - members of its branch - holds the keys; Enter and Escape in the list wired on the panel's own root."; }
        @Override public List<KeyBinding> keys() {
            return List.of(KeyBinding.of(Key.ENTER, "in the list: the pick confirmed, the list let go, the leaf told to activate itself - a claim"),
                           KeyBinding.of(Key.ESCAPE, "in the list: the list let go and the panel yielded"),
                           KeyBinding.of(Key.ARROW_UP, "in the list: walks it, natively"), KeyBinding.of(Key.ARROW_DOWN, "in the list: walks it, natively"));
        }
    }

    public static final FocusSceneModule INSTANCE = new FocusSceneModule();

    @Override
    public ImportsFor<FocusSceneModule> imports() {
        return ImportsFor.<FocusSceneModule>builder()
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_panel(),
                        new GalleryStyles.ga_panel_header(),
                        new GalleryStyles.ga_panel_note(),
                        new GalleryStyles.ga_panel_list(),
                        new GalleryStyles.ga_leaf(),
                        new GalleryStyles.ga_leaf_count(),
                        new GalleryStyles.ga_leaf_yield(),
                        new GalleryStyles.ga_field(),
                        new GalleryStyles.ga_button()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<FocusSceneModule> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new Leaf(), new Panel(), new ListPanel()));
    }
}
