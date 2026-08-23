package hue.captains.singapura.js.homing.demo.es.grid;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/**
 * RFC 0050 Appendix B — the Dish List as an in-memory Relation:
 * {@code createDishListRelation()} yields a RelationAdapterContract-shaped
 * object over the canonical six dishes plus the live {@code popularity}
 * column. {@code push} is the domain feed (ticks); {@code update} is the
 * grid's commit target and echoes through the feed, so the grid's direct
 * {@code (PK, col)} path repaints every write.
 */
public record DishListRelation() implements DomModule<DishListRelation> {

    public record createDishListRelation() implements Exportable._Constant<DishListRelation> {}

    public static final DishListRelation INSTANCE = new DishListRelation();

    @Override
    public ImportsFor<DishListRelation> imports() {
        return ImportsFor.noImports();
    }

    @Override
    public ExportsOf<DishListRelation> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new createDishListRelation()));
    }
}
