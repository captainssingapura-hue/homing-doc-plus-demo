package hue.captains.singapura.js.homing.demo.workspacewidgets.platformer;

import hue.captains.singapura.js.homing.core.DerivedCss;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.SelfRenderedCss;
import hue.captains.singapura.js.homing.ui.look.HouseDesigns;

import java.util.List;

/**
 * The house's platformer scene as a sheet: a rule for every class its concrete leaves wear, in the theme a page is
 * worn in. A derived sheet (RFC 0064, D25): it renders itself per theme, and the theme's owner fetches it again on a
 * switch.
 */
public record HousePlatformerSceneSheet() implements DerivedCss<HousePlatformerSceneSheet>, SelfRenderedCss {

    public static final HousePlatformerSceneSheet INSTANCE = new HousePlatformerSceneSheet();

    /** What the design's object imports the sheet by, so that it loads: no element wears it. */
    public record house_platformer_scene_sheet() implements Exportable._Constant<HousePlatformerSceneSheet> {}

    @Override public ExportsOf<HousePlatformerSceneSheet> exports() { return new ExportsOf<>(INSTANCE, List.of(new house_platformer_scene_sheet())); }

    @Override public String css(String themeSlug) { return HouseDesigns.INSTANCE.sheet(HousePlatformerScene.INSTANCE, themeSlug); }
}
