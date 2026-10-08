package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.panzoom.PanZoomBarModule;
import hue.captains.singapura.js.homing.ui.panzoom.SvgPanZoomModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code PanZoomBarDemo}: the house's pan-zoom bar in action - the controls of a drawing's view, driving it and following it. */
public record PanZoomBarDemoModule() implements DomModule<PanZoomBarDemoModule> {

    public static final PanZoomBarDemoModule INSTANCE = new PanZoomBarDemoModule();

    public record PanZoomBarDemo() implements SelfContainedWidget<PanZoomBarDemoModule> {
        @Override public String summary() { return "The house's pan-zoom bar in action: the controls of a drawing's view - zoom out, the zoom read out, zoom in, back to fit - driving it, and following it when the drawing is zoomed by the wheel."; }
    }

    @Override
    public ImportsFor<PanZoomBarDemoModule> imports() {
        return ImportsFor.<PanZoomBarDemoModule>builder()
                .add(new ModuleImports<>(List.of(new ComponentDemoModule.ComponentDemo()), ComponentDemoModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PanZoomBarModule.PanZoomBar()), PanZoomBarModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SvgPanZoomModule.SvgPanZoom()), SvgPanZoomModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoDrawingsModule.DEMO_DRAWINGS()), DemoDrawingsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoStyles.dm_row(), new DemoStyles.dm_host(), new DemoStyles.dm_fill()), DemoStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<PanZoomBarDemoModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PanZoomBarDemo())); }
}
