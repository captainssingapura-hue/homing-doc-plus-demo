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
    @Override public Class<Params> paramsType() { return Params.class; }
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
