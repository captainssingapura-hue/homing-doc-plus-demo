package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SelfContent;
import hue.captains.singapura.js.homing.design.styling.InstalledStyling;

import java.util.List;

/**
 * The house's platformer scene as a page has it: one stateless object, {@code HousePlatformerScene}, generated from
 * the local style. It wears and sheds a leaf of the scene on an element, asked by the leaf's constant, and refuses a leaf
 * of another branch; it imports the local style's sheet, so the sheet loads wherever the local style is used. A DOM
 * module, honestly: what it does is put its classes on an element, as the grid's local style does its own.
 */
public record HousePlatformerSceneModule() implements DomModule<HousePlatformerSceneModule>, SelfContent {

    public static final HousePlatformerSceneModule INSTANCE = new HousePlatformerSceneModule();

    /** The local style's object: {@code HousePlatformerScene.wear(PLATFORMER_SCENE.PLATFORM, el)}. */
    public record HousePlatformerScene() implements Exportable._Constant<HousePlatformerSceneModule> {}

    @Override
    public ImportsFor<HousePlatformerSceneModule> imports() {
        return ImportsFor.<HousePlatformerSceneModule>builder()
                .add(new ModuleImports<>(List.of(new HousePlatformerSceneSheet.house_platformer_scene_sheet()), HousePlatformerSceneSheet.INSTANCE))
                .build();
    }

    @Override public ExportsOf<HousePlatformerSceneModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new HousePlatformerScene())); }

    @Override
    public List<String> selfContent(ModuleNameResolver resolver) {
        return InstalledStyling.get().localStyleObject(hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.HousePlatformerScene.INSTANCE,
                HousePlatformerSceneSheet.INSTANCE);
    }
}
