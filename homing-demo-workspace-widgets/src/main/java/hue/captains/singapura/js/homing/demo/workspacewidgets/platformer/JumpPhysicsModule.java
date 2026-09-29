package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/** The platformer's vertical: {@code JumpPhysics} - a jump's push up, gravity's pull down, counted in frames. Pure: no DOM. */
public record JumpPhysicsModule() implements EsModule<JumpPhysicsModule> {

    public record JumpPhysics() implements Exportable._Class<JumpPhysicsModule> {}

    public static final JumpPhysicsModule INSTANCE = new JumpPhysicsModule();

    @Override public ImportsFor<JumpPhysicsModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<JumpPhysicsModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new JumpPhysics())); }
}
