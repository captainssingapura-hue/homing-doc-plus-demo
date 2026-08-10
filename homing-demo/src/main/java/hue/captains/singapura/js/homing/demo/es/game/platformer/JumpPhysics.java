package hue.captains.singapura.js.homing.demo.es.game.platformer;

import hue.captains.singapura.js.homing.core.*;

import java.util.List;

public record JumpPhysics() implements EsModule<JumpPhysics> {

    public record createJumpPhysics() implements Exportable._Constant<JumpPhysics> {}

    public static final JumpPhysics INSTANCE = new JumpPhysics();

    @Override
    public ImportsFor<JumpPhysics> imports() {
        return ImportsFor.<JumpPhysics>builder().build();
    }

    @Override
    public ExportsOf<JumpPhysics> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new createJumpPhysics()));
    }
}
