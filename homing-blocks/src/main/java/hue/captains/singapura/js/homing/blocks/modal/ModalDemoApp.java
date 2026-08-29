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
    /** RFC 0051 A9 - an empty Params record is genuinely paramless, but cannot
     *  use ParamCodec.None, which is typed to _None. Without this the app had
     *  no codec at all and its address was minted by reflection. */
    public static final hue.captains.singapura.js.homing.core.ParamCodec<Params> CODEC =
            hue.captains.singapura.js.homing.core.ParamCodec.ofEmpty(Params::new);

    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public hue.captains.singapura.js.homing.core.ParamCodec<Params> paramCodec() { return CODEC; }
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
