package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.component.taxonomy.ConcreteComponent;
import hue.captains.singapura.js.homing.design.semantic.DesignExtension;

import java.util.List;

/** The house's design of the platformer's scene, as this module adds it: a concrete leaf for every leaf of the branch. */
public record HousePlatformerSceneExtension() implements DesignExtension {

    @Override public List<ConcreteComponent<?, ?, ?>> concretes() {
        return List.of(HousePlatformerScene.Backdrop.INSTANCE, HousePlatformerScene.Sky.INSTANCE, HousePlatformerScene.Platform.INSTANCE,
                       HousePlatformerScene.PlatformUnderfoot.INSTANCE, HousePlatformerScene.Lava.INSTANCE, HousePlatformerScene.ScoreCard.INSTANCE);
    }
}
