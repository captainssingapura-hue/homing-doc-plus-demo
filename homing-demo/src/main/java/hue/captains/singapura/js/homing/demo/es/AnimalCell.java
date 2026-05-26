package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;

import java.util.List;

public record AnimalCell() implements DomModule<AnimalCell> {

    record createAnimalCell() implements Exportable._Constant<AnimalCell> {}
    record createAnimalSelector() implements Exportable._Constant<AnimalCell> {}
    /** Per-call scope factory — one scope per widget instance so the selector
     *  in widget A doesn't refresh cells in widget B. Returns {selectedIndex,
     *  cells}; pass into createAnimalCell(className, scope) and
     *  createAnimalSelector(scope). Omit the scope for legacy single-instance
     *  callers (they share the module-level default scope). */
    public record createAnimalScope() implements Exportable._Constant<AnimalCell> {}
    /** RFC 0028 cycle 4 — set a scope's selected animal index and refresh
     *  all cells in one call. Used by Animal widgets' AnimalsParty reactor
     *  to apply a workspace-wide AnimalChanged broadcast to the widget's
     *  per-instance scope. */
    public record setScopeAnimal() implements Exportable._Constant<AnimalCell> {}

    public static final AnimalCell INSTANCE = new AnimalCell();

    @Override
    public ImportsFor<AnimalCell> imports() {
        return ImportsFor.<AnimalCell>builder()
                .add(new ModuleImports<>(List.of(
                        new CuteAnimal.turtle(),
                        new CuteAnimal.ghost(),
                        new CuteAnimal.broom(),
                        new CuteAnimal.penguin(),
                        new CuteAnimal.crocodile(),
                        new CuteAnimal.whale()
                ), CuteAnimal.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<AnimalCell> exports() {
        return new ExportsOf<>(INSTANCE, List.of(
                new createAnimalCell(), new createAnimalSelector(),
                new createAnimalScope(), new setScopeAnimal()));
    }
}
