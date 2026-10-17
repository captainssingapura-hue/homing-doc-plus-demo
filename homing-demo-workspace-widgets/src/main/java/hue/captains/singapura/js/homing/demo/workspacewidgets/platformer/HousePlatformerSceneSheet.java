package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.core.DerivedCss;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.SelfRenderedCss;
import hue.captains.singapura.js.homing.design.styling.InstalledStyling;

import java.util.List;
import java.util.Optional;

/**
 * The house's platformer scene as a sheet: a rule for every class its concrete leaves wear, in the style a page is worn
 * in. A derived sheet that varies with the style (RFC 0067, appendix Two Classes per Leaf): the same in every palette, its
 * palettable colours the palette's variables, so a switch of palette repaints the scene and only a switch of style
 * fetches it again.
 */
public record HousePlatformerSceneSheet() implements DerivedCss<HousePlatformerSceneSheet>, SelfRenderedCss {

    public static final HousePlatformerSceneSheet INSTANCE = new HousePlatformerSceneSheet();

    /** What the local style's object imports the sheet by, so that it loads: no element wears it. */
    public record house_platformer_scene_sheet() implements Exportable._Constant<HousePlatformerSceneSheet> {}

    @Override public ExportsOf<HousePlatformerSceneSheet> exports() { return new ExportsOf<>(INSTANCE, List.of(new house_platformer_scene_sheet())); }

    @Override public VariesWith variesWith() { return VariesWith.STYLE; }

    @Override public Optional<String> fallback() { return InstalledStyling.installed().map(s -> s.defaultStyle().token()); }

    @Override public String css(String style) { return InstalledStyling.get().localStyleSheet(HousePlatformerScene.INSTANCE, style); }
}
