package hue.captains.singapura.js.homing.blocks.multitabpane;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Widget;
import hue.captains.singapura.js.homing.studio.base.widget.WorkspaceMPA;

/**
 * Fake-AppModule shell hosting {@link MultiTabPaneDemoWidget}. URL:
 * {@code /app?app=multi-tab-pane-demo}.
 */
public final class MultiTabPaneDemoApp extends WorkspaceMPA<MultiTabPaneDemoApp.Params,
                                                             MultiTabPaneDemoApp> {

    public static final MultiTabPaneDemoApp INSTANCE = new MultiTabPaneDemoApp();
    private MultiTabPaneDemoApp() {}

    public record Params()  implements AppModule._Param {}
    public record appMain() implements AppModule._AppMain<Params, MultiTabPaneDemoApp> {}
    public record link()    implements AppLink<MultiTabPaneDemoApp> {}

    @Override public String simpleName() { return "multi-tab-pane-demo"; }
    /** RFC 0051 A9 - an empty Params record is genuinely paramless, but cannot
     *  use ParamCodec.None, which is typed to _None. Without this the app had
     *  no codec at all and its address was minted by reflection. */
    public static final hue.captains.singapura.js.homing.core.ParamCodec<Params> CODEC =
            hue.captains.singapura.js.homing.core.ParamCodec.ofEmpty(Params::new);

    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public hue.captains.singapura.js.homing.core.ParamCodec<Params> paramCodec() { return CODEC; }
    @Override public String title() { return "MultiTabPane Demo"; }

    @Override
    protected AppModule._AppMain<Params, MultiTabPaneDemoApp> appMain() {
        return new appMain();
    }

    @Override
    protected Widget<?, ?> widget() {
        return MultiTabPaneDemoWidget.INSTANCE;
    }
}
