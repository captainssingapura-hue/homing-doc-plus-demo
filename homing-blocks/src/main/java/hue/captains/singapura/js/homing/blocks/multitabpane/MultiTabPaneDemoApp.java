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
    @Override public Class<Params> paramsType() { return Params.class; }
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
