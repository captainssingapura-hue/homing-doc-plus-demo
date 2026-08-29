package hue.captains.singapura.js.homing.blocks.splitpane;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Widget;
import hue.captains.singapura.js.homing.studio.base.widget.WorkspaceMPA;

/**
 * Fake-AppModule shell hosting {@link SplitPaneDemoWidget}. URL grammar:
 *
 * <pre>/app?app=split-pane-demo</pre>
 *
 * <p>Embedded inline in {@link SplitPaneDemoDoc} via a {@code
 * DocumentaryWidget} segment so the demo renders inside the building-blocks
 * doc chrome rather than as a standalone page.</p>
 */
public final class SplitPaneDemoApp extends WorkspaceMPA<SplitPaneDemoApp.Params,
                                                          SplitPaneDemoApp> {

    public static final SplitPaneDemoApp INSTANCE = new SplitPaneDemoApp();
    private SplitPaneDemoApp() {}

    public record Params() implements AppModule._Param {}
    public record appMain() implements AppModule._AppMain<Params, SplitPaneDemoApp> {}
    public record link()    implements AppLink<SplitPaneDemoApp> {}

    @Override public String simpleName() { return "split-pane-demo"; }
    /** RFC 0051 A9 - an empty Params record is genuinely paramless, but cannot
     *  use ParamCodec.None, which is typed to _None. Without this the app had
     *  no codec at all and its address was minted by reflection. */
    public static final hue.captains.singapura.js.homing.core.ParamCodec<Params> CODEC =
            hue.captains.singapura.js.homing.core.ParamCodec.ofEmpty(Params::new);

    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public hue.captains.singapura.js.homing.core.ParamCodec<Params> paramCodec() { return CODEC; }
    @Override public String title() { return "SplitPane Demo"; }

    @Override
    protected AppModule._AppMain<Params, SplitPaneDemoApp> appMain() {
        return new appMain();
    }

    @Override
    protected Widget<?, ?> widget() {
        return SplitPaneDemoWidget.INSTANCE;
    }
}
