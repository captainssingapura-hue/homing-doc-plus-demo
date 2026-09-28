package hue.captains.singapura.js.homing.demo.workspacewidgets.video;

import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.component.keyboard.focusParties;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParties;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/**
 * A short playlist: {@code new EmbeddedVideo(container, params)} - five cooks,
 * one dish, a player over a strip of takes. A switch destroys the player and
 * mints a fresh one, paused; the widget pauses when it is not seen, and never
 * starts audio by itself.
 */
public record EmbeddedVideoModule() implements DomModule<EmbeddedVideoModule> {

    public record EmbeddedVideo() implements SelfContainedWidget<EmbeddedVideoModule>, NeedKeyboard {
        @Override public String summary() { return "A short playlist - a player over a strip of takes; a switch mints a fresh player, paused, and it pauses when it is not seen."; }
        @Override public List<KeyBinding> keys() {
            return List.of(
                    KeyBinding.of(Key.ARROW_RIGHT, "the next take, browsed - into the strip, when the widget holds the keys"),
                    KeyBinding.of(Key.ARROW_DOWN, "the next take, browsed"),
                    KeyBinding.of(Key.ARROW_LEFT, "the previous take, browsed"),
                    KeyBinding.of(Key.ARROW_UP, "the previous take, browsed"),
                    KeyBinding.of(Key.HOME, "the first take, browsed"),
                    KeyBinding.of(Key.END, "the last take, browsed"),
                    KeyBinding.of(Key.ENTER, "the take browsed, on the stage"),
                    KeyBinding.of(Key.SPACE, "the take browsed, on the stage"),
                    KeyBinding.of(Key.ESCAPE, "the keys given back"));
        }
    }

    public static final EmbeddedVideoModule INSTANCE = new EmbeddedVideoModule();

    @Override
    public ImportsFor<EmbeddedVideoModule> imports() {
        return ImportsFor.<EmbeddedVideoModule>builder()
                .add(new ModuleImports<>(List.of(new domOpsParties()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParties()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_fill()), WidgetStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new EmbeddedVideoStyles.vd_root(), new EmbeddedVideoStyles.vd_head(), new EmbeddedVideoStyles.vd_title(),
                        new EmbeddedVideoStyles.vd_count(), new EmbeddedVideoStyles.vd_note(), new EmbeddedVideoStyles.vd_stage(),
                        new EmbeddedVideoStyles.vd_frame(), new EmbeddedVideoStyles.vd_rail(), new EmbeddedVideoStyles.vd_take()), EmbeddedVideoStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<EmbeddedVideoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new EmbeddedVideo())); }
}
