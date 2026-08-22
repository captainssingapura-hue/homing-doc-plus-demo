package hue.captains.singapura.js.homing.demo.es.grid;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Widget;
import hue.captains.singapura.js.homing.studio.base.widget.WorkspaceMPA;

/**
 * App shell hosting {@link DishListDemoWidget}. URL grammar:
 *
 * <pre>/app?app=dish-list-demo</pre>
 */
public final class DishListDemoApp extends WorkspaceMPA<DishListDemoApp.Params,
                                                        DishListDemoApp> {

    public static final DishListDemoApp INSTANCE = new DishListDemoApp();
    private DishListDemoApp() {}

    public record Params() implements AppModule._Param {}
    public record appMain() implements AppModule._AppMain<Params, DishListDemoApp> {}
    public record link()    implements AppLink<DishListDemoApp> {}

    @Override public String simpleName() { return "dish-list-demo"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title() { return "Dish List"; }

    @Override
    protected AppModule._AppMain<Params, DishListDemoApp> appMain() {
        return new appMain();
    }

    @Override
    protected Widget<?, ?> widget() {
        return DishListDemoWidget.INSTANCE;
    }
}
