package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.dialog.DialogModule;
import hue.captains.singapura.js.homing.ui.elements.Elements;
import hue.captains.singapura.js.homing.ui.panes.MultiTabPaneModule;

import java.util.List;

/**
 * One multi-tab pane as a page. The page is the holder: it constructs a
 * widget on a branch of its own for each tab, hands the pane the widget,
 * tells the widget it is active from the pane's report, and dissolves the
 * branch when the pane reports the tab closed. The plus opens a picker in
 * the dialog; every mutation the pane reports goes on the log under it.
 */
public record PanesApp() implements AppModule<AppModule._None, PanesApp> {

    public static final PanesApp INSTANCE = new PanesApp();

    record appMain() implements AppModule._AppMain<AppModule._None, PanesApp> {}
    /** The app as a widget by the base's contract: {@code new PanesWidget(branch, params)}; appMain delegates to it. */
    public record PanesWidget() implements Exportable._Constant<PanesApp> {}

    @Override public String title()      { return "Panes"; }
    @Override public String simpleName() { return "panes"; }

    @Override
    public ImportsFor<PanesApp> imports() {
        return ImportsFor.<PanesApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new MultiTabPaneModule.MultiTabPane()), MultiTabPaneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DialogModule.Dialog()), DialogModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.Button(), new Elements.Card()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_buttons(),
                        new GalleryStyles.ga_pane_host(),
                        new GalleryStyles.ga_log(),
                        new GalleryStyles.ga_status(),
                        new GalleryStyles.ga_count()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PanesApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new PanesWidget()));
    }
}
