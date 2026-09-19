package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.relgrid.RelGridStockCellsModule;
import hue.captains.singapura.js.homing.relgrid.protocol.RelGridProtocolModule;
import hue.captains.singapura.js.homing.reltree.RelTreeStockCellsModule;

import java.util.List;

/**
 * The gallery's domain: a small book store and the two relations the grid
 * and the tree are given over it. Domain code — it builds cells from the
 * family's stock cells and answers the tree's questions with the protocol's
 * own classes, and never names a component.
 */
public record GalleryRelations() implements DomModule<GalleryRelations> {

    public record createBooksStore()         implements Exportable._Constant<GalleryRelations> {}
    public record createBooksRelation()      implements Exportable._Constant<GalleryRelations> {}
    public record createShelfTreeRelation()  implements Exportable._Constant<GalleryRelations> {}

    public static final GalleryRelations INSTANCE = new GalleryRelations();

    @Override
    public ImportsFor<GalleryRelations> imports() {
        return ImportsFor.<GalleryRelations>builder()
                .add(new ModuleImports<>(List.of(new RelGridStockCellsModule.RelGridTextCell()), RelGridStockCellsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelTreeStockCellsModule.RelTreeTextCell()), RelTreeStockCellsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new RelGridProtocolModule.RelTreeView(),
                        new RelGridProtocolModule.RelTreeUnfold(),
                        new RelGridProtocolModule.RelTreeFold()
                ), RelGridProtocolModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<GalleryRelations> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new createBooksStore(), new createBooksRelation(), new createShelfTreeRelation()));
    }
}
