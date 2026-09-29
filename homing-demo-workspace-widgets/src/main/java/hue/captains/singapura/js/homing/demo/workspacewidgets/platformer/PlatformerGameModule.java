package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/**
 * The platformer's world and its rules, apart from any page: {@code
 * PlatformerGame} - stepped a frame at a time, so the same actions at the
 * same frames make the same world; chance makes only the terrain, and only
 * the player makes it. Pure: no DOM.
 */
public record PlatformerGameModule() implements EsModule<PlatformerGameModule> {

    public record PlatformerGame() implements Exportable._Class<PlatformerGameModule> {}

    public static final PlatformerGameModule INSTANCE = new PlatformerGameModule();

    @Override
    public ImportsFor<PlatformerGameModule> imports() {
        return ImportsFor.<PlatformerGameModule>builder()
                .add(new ModuleImports<>(List.of(new PlatformEngineModule.PlatformEngine()), PlatformEngineModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new JumpPhysicsModule.JumpPhysics()), JumpPhysicsModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlatformerGameModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlatformerGame())); }
}
