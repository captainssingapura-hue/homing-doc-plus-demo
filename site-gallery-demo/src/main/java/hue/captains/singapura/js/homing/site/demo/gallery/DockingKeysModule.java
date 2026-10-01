package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.ui.panes.PaneKeysModule;
import hue.captains.singapura.js.homing.ui.panes.PaneStyles;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/**
 * {@code KeysPicker}: which key scheme the workspace's docks answer, swapped
 * while you are standing in it. A pane's schemes are a list it is given and
 * {@code pane.keys(...)} changes it live, so the whole of this is a list of
 * choices and a loop.
 *
 * <p>Three rows: the two schemes that ship, and both together — which is the
 * point of a list, since arrows and control chords do not overlap. One of them
 * cannot be pressed in a browser tab: a browser keeps Ctrl+Tab for its own
 * tabs and hands it to nobody, which is why that scheme answers Ctrl+Shift+←
 * and Ctrl+Shift+→ as well.</p>
 */
public record DockingKeysModule() implements DomModule<DockingKeysModule> {

    /** The class: {@code new KeysPicker(branch, { host, panes })}. */
    public record KeysPicker() implements Exportable._Constant<DockingKeysModule> {}

    public static final DockingKeysModule INSTANCE = new DockingKeysModule();

    @Override
    public ImportsFor<DockingKeysModule> imports() {
        return ImportsFor.<DockingKeysModule>builder()
                .add(new ModuleImports<>(List.of(new PaneKeysModule.PaneKeys(), new PaneKeysModule.BrowserKeys()), PaneKeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PaneStyles.mtp_new_pick()), PaneStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new GalleryStyles.ga_control(), new GalleryStyles.ga_control_label(), new GalleryStyles.ga_control_readout()), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DockingKeysModule> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new KeysPicker()));
    }
}
