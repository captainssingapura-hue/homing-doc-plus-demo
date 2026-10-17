package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.component.taxonomy.Component;
import hue.captains.singapura.js.homing.component.taxonomy.ComponentModule;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerScene.Backdrop;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerScene.Lava;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerScene.Platform;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerScene.PlatformUnderfoot;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerScene.ScoreCard;
import hue.captains.singapura.js.homing.demo.workspacewidgets.platformer.PlatformerScene.Sky;

import java.util.List;

/**
 * The platformer's scene as a component module (RFC 0067, D51): the {@link PlatformerScene} branch's virtual leaves,
 * which an instance registers beside the house's modules wherever it serves the platformer - nothing found on the
 * classpath. Its leaves are drawn by local styles, never by a style: a base module of it bundles no look.
 */
public record PlatformerSceneComponentModule() implements ComponentModule {

    public static final PlatformerSceneComponentModule INSTANCE = new PlatformerSceneComponentModule();

    /** Every leaf of the scene, in the order a local style draws them. */
    @Override public List<Component<?>> components() {
        return List.of(Backdrop.INSTANCE, Sky.INSTANCE, Platform.INSTANCE, PlatformUnderfoot.INSTANCE, Lava.INSTANCE, ScoreCard.INSTANCE);
    }
}
