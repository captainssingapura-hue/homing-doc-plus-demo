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
import hue.captains.singapura.js.homing.relgrid.RelGridModule;
import hue.captains.singapura.js.homing.reltree.RelTreeModule;

import java.util.List;

/**
 * What the docking page's tabs hold: the books as a relation grid, the
 * shelves as a relation tree, a picture, and a note for the one that floats.
 * Each is a tab's widget by the law of §15 — a member of the branch it is
 * handed, answering {@code activate()}, its Escape yielding — and each fills
 * the tab, since a dock gives its widget the whole room.
 *
 * <p>The two relation widgets are the native world inside a logical member:
 * the host takes the browser's focus and the keys are the grid's or the
 * tree's own, so the widget says its keys are <em>lent</em> while that is so.
 * The seam is wired by the widget, as a panel wires the controls it puts
 * inside itself: an Escape the host did not want blurs it, and the next
 * Escape is the widget's own and yields.</p>
 */
public record DockingSceneModule() implements DomModule<DockingSceneModule> {

    /** The books in a dock's tab: {@code new BooksTab(branch, { focus, store, domain })}. */
    public record BooksTab() implements BranchComponent<DockingSceneModule>, NeedKeyboard {
        @Override public String summary() { return "The books as a relation grid in a dock's tab: the grid's keys are its own, and the tab's Escape gives the keys back to the dock."; }
        @Override public List<KeyBinding> keys() { return TAB_KEYS; }
    }
    /** The shelves in a dock's tab: {@code new ShelvesTab(branch, { focus, store, domain })}. */
    public record ShelvesTab() implements BranchComponent<DockingSceneModule>, NeedKeyboard {
        @Override public String summary() { return "The same books as shelf to book, in the relation tree: an unfold is a question the relation answers."; }
        @Override public List<KeyBinding> keys() { return TAB_KEYS; }
    }
    /** A picture in a dock's tab: {@code new PictureTab(branch, { focus, title? })}. */
    public record PictureTab() implements BranchComponent<DockingSceneModule>, NeedKeyboard {
        @Override public String summary() { return "A picture that fits the tab it is given and is zoomed by the keys: a tab that is not text."; }
        @Override public List<KeyBinding> keys() {
            return List.of(KeyBinding.of(Key.ESCAPE, "the keys go back to the dock"),
                           KeyBinding.of(Key.PLUS, "zoom in"), KeyBinding.of(Key.MINUS, "zoom out"), KeyBinding.of(Key.DIGIT_0, "the picture as it fits"));
        }
    }
    /** A note in a dock's tab: {@code new NoteTab(branch, { focus, text })}. */
    public record NoteTab() implements BranchComponent<DockingSceneModule>, NeedKeyboard {
        @Override public String summary() { return "A paragraph in a tab, for the one that floats: it says what to try."; }
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.ESCAPE, "the keys go back to whoever holds the branch")); }
    }

    /** The switcher's rows: {@code new RegionList(branch, host, regions, at, onPick)}; the arrows walk them, Enter picks. */
    public record RegionList() implements BranchComponent<DockingSceneModule>, NeedKeyboard {
        @Override public String summary() { return "One row per region, in the switcher: the arrows walk them and Enter picks the one the cursor is on."; }
        @Override public List<KeyBinding> keys() {
            return List.of(KeyBinding.of(Key.ARROW_DOWN, "the next region"), KeyBinding.of(Key.ARROW_UP, "the one before"), KeyBinding.of(Key.ENTER, "go to it"));
        }
    }

    /** What every tab here answers: the keys are the widget's while it holds them, and Escape hands them back. */
    static final List<KeyBinding> TAB_KEYS = List.of(
            KeyBinding.of(Key.ESCAPE, "in the host: the host lets go, and the keys are the tab's; again, the tab yields to the dock"));

    public static final DockingSceneModule INSTANCE = new DockingSceneModule();

    @Override
    public ImportsFor<DockingSceneModule> imports() {
        return ImportsFor.<DockingSceneModule>builder()
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridModule.RelGrid()), RelGridModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelTreeModule.RelTree()), RelTreeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryRelations.BooksRelation(),
                        new GalleryRelations.ShelfTreeRelation()
                ), GalleryRelations.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_tab_fill(),
                        new GalleryStyles.ga_tab_host(),
                        new GalleryStyles.ga_picture(),
                        new GalleryStyles.ga_plate(),
                        new GalleryStyles.ga_plate_sun(),
                        new GalleryStyles.ga_plate_far(),
                        new GalleryStyles.ga_plate_near(),
                        new GalleryStyles.ga_plate_ground(),
                        new GalleryStyles.ga_picture_note(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_switch_list(),
                        new GalleryStyles.ga_switch_row(),
                        new GalleryStyles.ga_switch_row_at(),
                        new GalleryStyles.ga_switch_hint()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DockingSceneModule> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new BooksTab(), new ShelvesTab(), new PictureTab(), new NoteTab(), new RegionList()));
    }
}
