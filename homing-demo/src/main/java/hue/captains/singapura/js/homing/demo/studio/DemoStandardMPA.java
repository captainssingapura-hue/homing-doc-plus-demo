package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Widget;
import hue.captains.singapura.js.homing.studio.base.widget.StandardMPA;
import hue.captains.singapura.js.homing.studio.base.widget.SvgWidget;

import java.util.List;

/**
 * RFC 0024 Phase P1b — the demo's concrete StandardMPA shell. Hosts
 * {@link SvgWidget} as the first widget routed through the new
 * Widget contract.
 *
 * <p>URL: {@code /app?app=demo-standard-mpa&widget=svg-widget&id=<svg-uuid>}.
 * The legacy {@code SvgViewer} continues to serve
 * {@code /app?app=svg-viewer&id=<svg-uuid>} unchanged — sibling
 * cohabitation per RFC 0024's migration strategy.</p>
 *
 * <p>The shell exists in the demo studio (not framework-side) because
 * each studio decides which widgets to host. {@link StandardMPA} is the
 * abstract base; this is one concrete subclass with one declared
 * widget. P1c+ will add more widgets as they're ported.</p>
 *
 * @since RFC 0024 Phase P1b
 */
public final class DemoStandardMPA extends StandardMPA<AppModule._None, DemoStandardMPA> {

    public static final DemoStandardMPA INSTANCE = new DemoStandardMPA();

    private DemoStandardMPA() {}

    public record appMain() implements AppModule._AppMain<AppModule._None, DemoStandardMPA> {}
    public record link() implements hue.captains.singapura.js.homing.core.AppLink<DemoStandardMPA> {}

    @Override public String simpleName() { return "demo-standard-mpa"; }
    @Override public String title() { return "Homing · demo"; }

    @Override
    protected AppModule._AppMain<AppModule._None, DemoStandardMPA> appMain() {
        return new appMain();
    }

    @Override
    protected List<? extends Widget<?, ?>> widgets() {
        return List.of(SvgWidget.INSTANCE);
    }
}
