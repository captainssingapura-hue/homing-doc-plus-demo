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
 * member of its own focus party by the design as it stands: the viewport
 * inside it the native world, an Escape it lets through the steward's, the
 * widget's own Escape a yield.
 */
public record ViewerSceneModule() implements DomModule<ViewerSceneModule> {

    /** {@code new SvgViewer(container, { svg, label? })}: an SVG viewer, a member of its own focus party; the viewport native inside it. */
    public record SvgViewer() implements BranchComponent<ViewerSceneModule>, NeedKeyboard {
        @Override public String summary() { return "An SVG viewer as a widget: its own parties, offered as roots; a member of its own focus party, the viewport inside it the native world."; }
        @Override public List<KeyBinding> keys() {
            var keys = new ArrayList<KeyBinding>();
            for (KeyBinding k : SvgPanZoomModule.SvgPanZoom.KEYS)
                keys.add(KeyBinding.of(k.key(), k.meaning() + " - natively in the viewport; through the party while the viewer holds with nothing focused"));
            keys.add(KeyBinding.of(Key.ESCAPE, "in the viewport: the steward's - the viewport lets go, the viewer holds; while the viewer holds: it yields, up the tree"));
            return List.copyOf(keys);
        }
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
                .add(new ModuleImports<>(List.of(new GalleryStyles.ga_panel(), new GalleryStyles.ga_panel_header()), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ViewerSceneModule> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new SvgViewer()));
    }
}
