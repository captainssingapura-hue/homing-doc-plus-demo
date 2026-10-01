package hue.captains.singapura.js.homing.demo.workspacewidgets.animals;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/**
 * The animal choice party's secretary: {@code initial} and {@code
 * behavior(state, envelope) → { newState, actions }} - which animal is chosen,
 * said to every member when it changes and to a member that asks; diligent,
 * every kind tested.
 */
public record AnimalChoiceSecretaryModule() implements EsModule<AnimalChoiceSecretaryModule> {

    public record AnimalChoiceSecretary() implements Exportable._Constant<AnimalChoiceSecretaryModule> {}

    public static final AnimalChoiceSecretaryModule INSTANCE = new AnimalChoiceSecretaryModule();

    @Override public ImportsFor<AnimalChoiceSecretaryModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<AnimalChoiceSecretaryModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new AnimalChoiceSecretary())); }
}
