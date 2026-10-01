package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/**
 * The platformer party's secretary: {@code initial} and {@code
 * behavior(state, envelope) → { newState, actions }} - each event of the run
 * said on to every member as it came, in order, and counted; never kept.
 */
public record PlatformerSecretaryModule() implements EsModule<PlatformerSecretaryModule> {

    public record PlatformerSecretary() implements Exportable._Constant<PlatformerSecretaryModule> {}

    public static final PlatformerSecretaryModule INSTANCE = new PlatformerSecretaryModule();

    @Override public ImportsFor<PlatformerSecretaryModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<PlatformerSecretaryModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlatformerSecretary())); }
}
