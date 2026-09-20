package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.relgrid.RelGridModule;

import java.util.List;

/**
 * The relation grid as a page: the book store's relation in a {@code RelGrid},
 * under the chrome, wearing whatever design the page wears. The grid comes
 * from its own repo and imports nothing of the site; the page hands it a
 * branch and a relation, and that is the whole of their acquaintance.
 */
public record GridApp() implements AppModule<AppModule._None, GridApp> {

    public static final GridApp INSTANCE = new GridApp();

    record appMain() implements AppModule._AppMain<AppModule._None, GridApp> {}
    /** The app as a widget by the base's contract: {@code new GridWidget(branch, params)}; appMain delegates to it. */
    public record GridWidget() implements Exportable._Constant<GridApp> {}

    @Override public String title()      { return "Grid"; }
    @Override public String simpleName() { return "grid"; }

    @Override
    public ImportsFor<GridApp> imports() {
        return ImportsFor.<GridApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridModule.RelGrid()), RelGridModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryRelations.BooksStore(),
                        new GalleryRelations.BooksRelation()
                ), GalleryRelations.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_host(),
                        new GalleryStyles.ga_status()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<GridApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new GridWidget()));
    }
}
