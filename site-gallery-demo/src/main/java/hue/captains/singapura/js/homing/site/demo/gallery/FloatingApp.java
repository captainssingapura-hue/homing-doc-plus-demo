package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.floating.DeskModule;
import hue.captains.singapura.js.homing.ui.elements.Elements;

import java.util.List;

/**
 * The floating page: a desk filling its box, buttons opening panes that hold widgets by the base's contract, and the
 * log of every mutation the desk reports.
 */
public record FloatingApp() implements AppModule<AppModule._None, FloatingApp> {

    public static final FloatingApp INSTANCE = new FloatingApp();

    record appMain() implements AppModule._AppMain<AppModule._None, FloatingApp> {}
    /** The app as a widget by the base's contract: {@code new FloatingWidget(branch, params)}; appMain delegates to it. */
    public record FloatingWidget() implements Exportable._Constant<FloatingApp> {}

    @Override public String title()      { return "Floating panes"; }
    @Override public String simpleName() { return "floating"; }

    @Override
    public ImportsFor<FloatingApp> imports() {
        return ImportsFor.<FloatingApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.ButtonBuilder(), new Elements.CardBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new DeskModule.Desk()), DeskModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_count(),
                        new GalleryStyles.ga_pane_host(),
                        new GalleryStyles.ga_log(),
                        new GalleryStyles.ga_buttons()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<FloatingApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new FloatingWidget()));
    }
}
