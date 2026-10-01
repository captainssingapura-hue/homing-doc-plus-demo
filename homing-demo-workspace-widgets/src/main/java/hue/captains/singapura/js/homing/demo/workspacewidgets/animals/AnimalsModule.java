package hue.captains.singapura.js.homing.demo.workspacewidgets.animals;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/**
 * The animals a demo widget can show: {@code Animals} - each an id, a name and
 * its picture - and the picture as a CSS image a typed class draws, never
 * markup parsed into the page. Pure: no DOM.
 */
public record AnimalsModule() implements EsModule<AnimalsModule> {

    public record Animals() implements Exportable._Class<AnimalsModule> {}

    public static final AnimalsModule INSTANCE = new AnimalsModule();

    @Override
    public ImportsFor<AnimalsModule> imports() {
        return ImportsFor.<AnimalsModule>builder()
                .add(new ModuleImports<>(List.of(new CuteAnimals.turtle(), new CuteAnimals.ghost(), new CuteAnimals.broom(),
                        new CuteAnimals.penguin(), new CuteAnimals.crocodile(), new CuteAnimals.whale()), CuteAnimals.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<AnimalsModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new Animals())); }
}
