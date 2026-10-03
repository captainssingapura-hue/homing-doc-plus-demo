package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
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
import hue.captains.singapura.js.homing.ui.panzoom.PanZoomBarModule;
import hue.captains.singapura.js.homing.ui.panzoom.SvgPanZoomModule;

import java.util.ArrayList;
import java.util.List;

/**
 * The viewer experiment's widget: {@code SvgViewer}, an SVG that zooms and
 * pans as a widget by the workspace's pattern - its DomOps and focus parties
 * its own, offered as roots for its host to graft - and, for the keys, a
 * member of its own focus party: the viewport inside it the native world, an
 * Escape the viewport has no use for a yield from it, and the widget, asked
 * first, passed by unless made to keep the keys. {@code EscapeLayer} keeps an
 * Escape while it is open, as a stage closing does.
 */
public record ViewerSceneModule() implements DomModule<ViewerSceneModule> {

    /** {@code new SvgViewer(container, { svg, label?, keeps? })}: an SVG viewer, a member of its own focus party; the viewport native inside it; keeps the keys its viewport lets go of only when made to. */
    public record SvgViewer() implements BranchComponent<ViewerSceneModule>, NeedKeyboard {
        @Override public String summary() { return "An SVG viewer as a widget: its own parties, offered as roots; a member of its own focus party, the viewport inside it the native world."; }
        @Override public List<KeyBinding> keys() {
            var keys = new ArrayList<KeyBinding>();
            for (KeyBinding k : SvgPanZoomModule.SvgPanZoom.KEYS)
                keys.add(KeyBinding.of(k.key(), k.meaning() + " - natively in the viewport; through the party while the viewer holds with nothing focused"));
            keys.add(KeyBinding.of(Key.ESCAPE, "in the viewport: lets it go, a yield - the viewer, asked first, passes it on unless made to keep the keys; while the viewer holds: it yields, up the tree"));
            return List.copyOf(keys);
        }
    }

    /** {@code new EscapeLayer(branch, host, { title })}: a layer that keeps an Escape while open, as a stage closing does; a press opens it. */
    public record EscapeLayer() implements BranchComponent<ViewerSceneModule>, NeedKeyboard {
        @Override public String summary() { return "A layer that keeps an Escape while it is open - it closes, and no one under it nor the steward hears of it; closed, it lets every key by."; }
        @Override public List<KeyBinding> keys() { return List.of(KeyBinding.of(Key.ESCAPE, "while open: closes it, kept - the native world's, before the steward")); }
    }

    public static final ViewerSceneModule INSTANCE = new ViewerSceneModule();

    @Override
    public ImportsFor<ViewerSceneModule> imports() {
        return ImportsFor.<ViewerSceneModule>builder()
                .add(new ModuleImports<>(List.of(new domOpsParties()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParties()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SvgPanZoomModule.SvgPanZoom()), SvgPanZoomModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PanZoomBarModule.PanZoomBar()), PanZoomBarModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new GalleryStyles.ga_panel(), new GalleryStyles.ga_panel_header(), new GalleryStyles.ga_viewer_view(),
                        new GalleryStyles.ga_layer(), new GalleryStyles.ga_layer_head(), new GalleryStyles.ga_layer_open()), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ViewerSceneModule> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new SvgViewer(), new EscapeLayer()));
    }
}
