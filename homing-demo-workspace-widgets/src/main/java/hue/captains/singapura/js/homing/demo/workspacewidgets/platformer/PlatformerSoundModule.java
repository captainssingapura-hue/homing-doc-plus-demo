package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.libs.ToneJs;

import java.util.List;

/**
 * The platformer's sound, on Tone.js served from the classpath: {@code
 * PlatformerSound} - steps, a jump, a landing's chord, a fall's jingle and a
 * tune under it all; nothing until a person's key or press, and the tune only
 * while the game holds the keys. No DOM.
 */
public record PlatformerSoundModule() implements EsModule<PlatformerSoundModule> {

    public record PlatformerSound() implements Exportable._Class<PlatformerSoundModule> {}

    public static final PlatformerSoundModule INSTANCE = new PlatformerSoundModule();

    @Override
    public ImportsFor<PlatformerSoundModule> imports() {
        return ImportsFor.<PlatformerSoundModule>builder()
                .add(new ModuleImports<>(List.of(new ToneJs.Synth(), new ToneJs.MembraneSynth(), new ToneJs.start()), ToneJs.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlatformerSoundModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlatformerSound())); }
}
