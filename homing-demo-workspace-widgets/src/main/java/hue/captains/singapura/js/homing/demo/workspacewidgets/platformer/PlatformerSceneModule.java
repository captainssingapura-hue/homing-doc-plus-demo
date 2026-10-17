package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SelfContent;
import hue.captains.singapura.js.homing.design.semantic.LocalStyleLeaves;
import hue.captains.singapura.js.homing.design.styling.InstalledStyling;

import java.util.List;

/**
 * The platformer's scene as a page has it: {@code PLATFORMER_SCENE}, one frozen constant per leaf of the
 * {@link PlatformerScene} branch, generated from the installed instance's taxonomy - by which the stage asks the local
 * style it is drawn with to wear a leaf on an element, never by a string.
 */
public record PlatformerSceneModule() implements EsModule<PlatformerSceneModule>, SelfContent {

    public static final PlatformerSceneModule INSTANCE = new PlatformerSceneModule();

    /** Every leaf of the scene: {@code PLATFORMER_SCENE.PLATFORM}. */
    public record PLATFORMER_SCENE() implements Exportable._Constant<PlatformerSceneModule> {}

    @Override public ImportsFor<PlatformerSceneModule> imports() { return ImportsFor.noImports(); }

    @Override public ExportsOf<PlatformerSceneModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PLATFORMER_SCENE())); }

    @Override public List<String> selfContent(ModuleNameResolver resolver) {
        return LocalStyleLeaves.INSTANCE.lines(InstalledStyling.get().taxonomy(), PlatformerScene.INSTANCE);
    }
}
