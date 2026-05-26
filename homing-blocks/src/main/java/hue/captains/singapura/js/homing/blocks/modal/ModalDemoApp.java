package hue.captains.singapura.js.homing.blocks.modal;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Widget;
import hue.captains.singapura.js.homing.studio.base.widget.WorkspaceMPA;

/** SingleWidgetMPA shell for the Modal demo. URL: /app?app=modal-demo. */
public final class ModalDemoApp extends WorkspaceMPA<ModalDemoApp.Params, ModalDemoApp> {

    public static final ModalDemoApp INSTANCE = new ModalDemoApp();
    private ModalDemoApp() {}

    public record Params()  implements AppModule._Param {}
    public record appMain() implements AppModule._AppMain<Params, ModalDemoApp> {}
    public record link()    implements AppLink<ModalDemoApp> {}

    @Override public String simpleName() { return "modal-demo"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title() { return "Modal Demo"; }

    @Override
    protected AppModule._AppMain<Params, ModalDemoApp> appMain() {
        return new appMain();
    }

    @Override
    protected Widget<?, ?> widget() {
        return ModalDemoWidget.INSTANCE;
    }
}
