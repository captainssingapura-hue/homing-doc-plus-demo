package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.component.taxonomy.ComponentModule;
import hue.captains.singapura.js.homing.design.styling.BaseModule;
import hue.captains.singapura.js.homing.design.styling.StyleBundle;
import hue.captains.singapura.js.homing.ui.styles.Base;
import hue.captains.singapura.js.homing.ui.styles.BaseScheme;

import java.util.List;

/**
 * The platformer's scene as an instance of the house registers it (RFC 0067, D51): a base module of the scene's
 * component module, which bundles no look - its leaves are virtual, drawn by a design, never by a style. A site that
 * serves the platformer hands it to the house's styling ({@code HouseStyles.INSTANCE.styling(List.of(..., PlatformerSceneBaseModule.INSTANCE))}),
 * so the scene is in its taxonomy and its design reads.
 */
public record PlatformerSceneBaseModule() implements BaseModule<BaseScheme, Base> {

    public static final PlatformerSceneBaseModule INSTANCE = new PlatformerSceneBaseModule();

    @Override public Base style() { return Base.INSTANCE; }

    @Override public ComponentModule components() { return PlatformerSceneComponentModule.INSTANCE; }

    @Override public List<StyleBundle<BaseScheme, Base>> bundles() { return List.of(); }
}
