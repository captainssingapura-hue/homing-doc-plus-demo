package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.reltree.RelTreeModule;

import java.util.List;

/**
 * The relation tree as a page: the book store as shelf → book in a
 * {@code RelTree}, under the chrome. An unfold and a fold are questions on
 * the ask channel; this page wires the tree to the relation's answer and
 * nothing else.
 */
public record TreeApp() implements AppModule<AppModule._None, TreeApp> {

    public static final TreeApp INSTANCE = new TreeApp();

    record appMain() implements AppModule._AppMain<AppModule._None, TreeApp> {}
    /** The app as a widget by the base's contract: {@code new TreeWidget(branch, params)}; appMain delegates to it. */
    public record TreeWidget() implements BranchComponent<TreeApp> {}

    @Override public String title()      { return "Tree"; }
    @Override public String simpleName() { return "tree"; }

    @Override
    public ImportsFor<TreeApp> imports() {
        return ImportsFor.<TreeApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelTreeModule.RelTree()), RelTreeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryRelations.BooksStore(),
                        new GalleryRelations.ShelfTreeRelation()
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
    public ExportsOf<TreeApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new TreeWidget()));
    }
}
