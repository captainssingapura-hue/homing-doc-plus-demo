package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/** {@code ComponentLogSecretary}: a component-log party's secretary - the last lines kept, each told; cleared on command. */
public record ComponentLogSecretaryModule() implements EsModule<ComponentLogSecretaryModule> {

    public static final ComponentLogSecretaryModule INSTANCE = new ComponentLogSecretaryModule();

    public record ComponentLogSecretary() implements Exportable._Constant<ComponentLogSecretaryModule> {}

    @Override public ImportsFor<ComponentLogSecretaryModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<ComponentLogSecretaryModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ComponentLogSecretary())); }
}
