package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.WidgetSlotModule;
import hue.captains.singapura.js.homing.ui.menu.ContextMenuStewardModule;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.ParamCodec;
import hue.captains.singapura.js.homing.core.QueryString;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.server.HrefManager;
import hue.captains.singapura.js.homing.site.mpa.MpaStyles;
import hue.captains.singapura.js.homing.ui.splitgrid.SplitGridModule;

import java.util.List;
import java.util.Map;

/**
 * The gallery as a shell: a split grid of three cells under the chrome,
 * arranged once — the navigator on the left, the chosen demo top right, its
 * explanation under it; the dividers drag, nothing subdivides or goes. The
 * navigator is the relation tree over the demos; the demo is the
 * demo app as a widget, imported when first chosen and kept in a slot; the
 * explanation is what {@link GalleryDemos} says of it, with a link to the
 * page the demo also is. {@code ?demo=<slug>} opens on that demo, and the
 * address follows the choice.
 */
public record GalleryShellApp() implements AppModule<GalleryShellApp.Params, GalleryShellApp> {

    public static final GalleryShellApp INSTANCE = new GalleryShellApp();

    /** The demo to open on: a slug from {@link GalleryDemos}, or empty for the first. */
    public record Params(String demo) implements AppModule._Param {}

    record appMain() implements AppModule._AppMain<Params, GalleryShellApp> {}

    public static final ParamCodec<Params> CODEC = new ParamCodec<>() {
        @Override public Decoded<Params> from(Map<String, List<String>> query) {
            String s = QueryString.first(query, "demo");
            return Decoded.ok(new Params(s == null ? "" : s.trim()));
        }
        @Override public Map<String, List<String>> to(Params params) {
            return params.demo().isEmpty() ? QueryString.params() : QueryString.of("demo", params.demo());
        }
    };

    @Override public String title()      { return "Gallery"; }
    @Override public String simpleName() { return "gallery"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public ParamCodec<Params> paramCodec() { return CODEC; }

    @Override
    public ImportsFor<GalleryShellApp> imports() {
        return ImportsFor.<GalleryShellApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SplitGridModule.SplitGrid()), SplitGridModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetSlotModule.WidgetSlot()), WidgetSlotModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()), HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(new GalleryDemos.DEMOS()), GalleryDemos.INSTANCE))
                .add(new ModuleImports<>(List.of(new GalleryMenus.MENUS()), GalleryMenus.INSTANCE))
                .add(new ModuleImports<>(List.of(new ContextMenuStewardModule.ContextMenuSteward()), ContextMenuStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new MpaStyles.mpa_main_full()), MpaStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_shell(),
                        new GalleryStyles.ga_shell_nav(),
                        new GalleryStyles.ga_shell_demo(),
                        new GalleryStyles.ga_shell_explain(),
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_explain_text(),
                        new GalleryStyles.ga_explain_link()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<GalleryShellApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
