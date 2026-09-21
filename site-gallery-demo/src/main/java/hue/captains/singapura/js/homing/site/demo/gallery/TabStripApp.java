package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.preferences.PreferencesStyles;
import hue.captains.singapura.js.homing.ui.panes.TabStripModule;

import java.util.List;

/**
 * The tab strip page: the strip alone, no pane — five chips, the drag that is a browser's along a rail, a slider
 * for the chips' size, and the log of every step.
 */
public record TabStripApp() implements AppModule<AppModule._None, TabStripApp> {

    public static final TabStripApp INSTANCE = new TabStripApp();

    record appMain() implements AppModule._AppMain<AppModule._None, TabStripApp> {}
    /** The app as a widget by the base's contract: {@code new TabStripWidget(branch, params)}; appMain delegates to it. */
    public record TabStripWidget() implements BranchComponent<TabStripApp> {}

    @Override public String title()      { return "Tab strip"; }
    @Override public String simpleName() { return "tabstrip"; }

    @Override
    public ImportsFor<TabStripApp> imports() {
        return ImportsFor.<TabStripApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TabStripModule.TabStrip()), TabStripModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PreferencesStyles.pv_range()), PreferencesStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_control(),
                        new GalleryStyles.ga_control_label(),
                        new GalleryStyles.ga_control_readout(),
                        new GalleryStyles.ga_strip_box(),
                        new GalleryStyles.ga_shelf(),
                        new GalleryStyles.ga_log(),
                        new GalleryStyles.ga_buttons()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<TabStripApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new TabStripWidget()));
    }
}
