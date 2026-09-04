package hue.captains.singapura.js.homing.demo.es.grid;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/**
 * The Table Workbench's fixture source: {@code createSpecimenRelation({rows,
 * cols})} yields a RelationAdapterContract-shaped object over a depot
 * inventory grown to the requested shape.
 *
 * <p>Unlike {@link DishListRelation} — six curated dishes, chosen so the whole
 * relation fits on screen — these specimens exist to NOT fit. The first column
 * is the row ordinal and the values are deterministic, because a scroll test
 * bed has to let you say where the viewport actually is.</p>
 */
public record TableSpecimenRelation() implements DomModule<TableSpecimenRelation> {

    public record createSpecimenRelation() implements Exportable._Constant<TableSpecimenRelation> {}

    public static final TableSpecimenRelation INSTANCE = new TableSpecimenRelation();

    @Override
    public ImportsFor<TableSpecimenRelation> imports() {
        return ImportsFor.noImports();
    }

    @Override
    public ExportsOf<TableSpecimenRelation> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new createSpecimenRelation()));
    }
}
