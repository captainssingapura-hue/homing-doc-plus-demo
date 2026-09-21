package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.dialog.DialogModule;
import hue.captains.singapura.js.homing.ui.elements.Elements;
import hue.captains.singapura.js.homing.ui.elements.SliderGroupModule;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;
import hue.captains.singapura.js.homing.ui.panes.TabStripModule;

import java.util.List;

/**
 * The keyboard page: who has the keys. Five members of the page's party on
 * one page — a slider group, a card, a strip, a dialog, and a platformer
 * that holds keys down — and a strip that shows the holder live; the one
 * bug the design names, shown on a button.
 */
public record KeyboardApp() implements AppModule<AppModule._None, KeyboardApp> {

    public static final KeyboardApp INSTANCE = new KeyboardApp();

    record appMain() implements AppModule._AppMain<AppModule._None, KeyboardApp> {}
    /** The app as a widget by the base's contract: {@code new KeyboardWidget(branch, params)}; appMain delegates to it. The platformer in it is the page's own member. */
    public record KeyboardWidget() implements BranchComponent<KeyboardApp>, NeedKeyboard {
        @Override public String summary() { return "Who has the keys: five members and the holder shown live; the platformer holds keys down."; }
        /** The platformer's: held arrows run, Space or up jumps; the other members declare their own. */
        @Override public List<KeyBinding> keys() {
            return List.of(KeyBinding.of(Key.ARROW_LEFT, "the animal runs left while held"), KeyBinding.of(Key.ARROW_RIGHT, "the animal runs right while held"),
                           KeyBinding.of(Key.SPACE, "the animal jumps"), KeyBinding.of(Key.ARROW_UP, "the animal jumps"));
        }
    }

    @Override public String title()      { return "Keyboard"; }
    @Override public String simpleName() { return "keyboard"; }

    @Override
    public ImportsFor<KeyboardApp> imports() {
        return ImportsFor.<KeyboardApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.SliderBuilder()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderGroupModule.SliderGroupBuilder()), SliderGroupModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.Button(), new Elements.CardBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new TabStripModule.TabStrip()), TabStripModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DialogModule.Dialog()), DialogModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_holders(),
                        new GalleryStyles.ga_holder(),
                        new GalleryStyles.ga_holder_on(),
                        new GalleryStyles.ga_stage(),
                        new GalleryStyles.ga_sprite(),
                        new GalleryStyles.ga_specimens(),
                        new GalleryStyles.ga_specimen_name(),
                        new GalleryStyles.ga_buttons(),
                        new GalleryStyles.ga_log()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<KeyboardApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new KeyboardWidget()));
    }
}
