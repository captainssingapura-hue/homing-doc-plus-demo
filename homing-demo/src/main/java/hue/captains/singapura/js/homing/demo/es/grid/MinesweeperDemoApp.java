package hue.captains.singapura.js.homing.demo.es.grid;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Widget;
import hue.captains.singapura.js.homing.studio.base.widget.WorkspaceMPA;

/**
 * App shell hosting {@link MinesweeperWidget}. URL grammar:
 *
 * <pre>/app?app=minesweeper</pre>
 */
public final class MinesweeperDemoApp extends WorkspaceMPA<MinesweeperDemoApp.Params,
                                                           MinesweeperDemoApp> {

    public static final MinesweeperDemoApp INSTANCE = new MinesweeperDemoApp();
    private MinesweeperDemoApp() {}

    public record Params() implements AppModule._Param {}
    public record appMain() implements AppModule._AppMain<Params, MinesweeperDemoApp> {}
    public record link()    implements AppLink<MinesweeperDemoApp> {}

    @Override public String simpleName() { return "minesweeper"; }
    /** RFC 0051 A9 - an empty Params record is genuinely paramless, but cannot
     *  use ParamCodec.None, which is typed to _None. Without this the app had
     *  no codec at all and its address was minted by reflection. */
    public static final hue.captains.singapura.js.homing.core.ParamCodec<Params> CODEC =
            hue.captains.singapura.js.homing.core.ParamCodec.ofEmpty(Params::new);

    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public hue.captains.singapura.js.homing.core.ParamCodec<Params> paramCodec() { return CODEC; }
    @Override public String title() { return "Minesweeper"; }

    @Override
    protected AppModule._AppMain<Params, MinesweeperDemoApp> appMain() {
        return new appMain();
    }

    @Override
    protected Widget<?, ?> widget() {
        return MinesweeperWidget.INSTANCE;
    }
}
