package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/** The platformer's terrain: {@code PlatformEngine} - platforms ahead of the camera, made by chance, only by the player's engine; placed as they are by a watcher's. Pure: no DOM. */
public record PlatformEngineModule() implements EsModule<PlatformEngineModule> {

    public record PlatformEngine() implements Exportable._Class<PlatformEngineModule> {}

    public static final PlatformEngineModule INSTANCE = new PlatformEngineModule();

    @Override public ImportsFor<PlatformEngineModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<PlatformEngineModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlatformEngine())); }
}
