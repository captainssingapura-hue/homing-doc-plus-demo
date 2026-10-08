package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.ui.controlpanel.UiControlPanelCrate;
import hue.captains.singapura.js.homing.ui.elements.UiElementsCrate;
import hue.captains.singapura.js.homing.ui.icons.UiIconsCrate;
import hue.captains.singapura.js.homing.workspace.widgets.WorkspaceWidgetsCrate;

import java.util.List;

/**
 * The {@link Crate} for {@code homing-demo-components}: the parties a demo meets its panels in -
 * their types and their secretaries - the demo every one extends, the demos, and the registry a
 * page looks them up in; and every crate it imports from named in {@link #requires()}.
 */
public final class DemoComponentsCrate implements Crate {

    public static final DemoComponentsCrate INSTANCE = new DemoComponentsCrate();

    private DemoComponentsCrate() {}

    @Override public String name() { return "homing-demo-components"; }

    @Override
    public List<Crate> requires() {
        return List.of(
                CoreJsCrate.INSTANCE,
                ServerCrate.INSTANCE,
                DesignCrate.INSTANCE,
                // What is shown, and what of it is controlled.
                UiElementsCrate.INSTANCE,
                UiIconsCrate.INSTANCE,
                UiControlPanelCrate.INSTANCE,
                // The widgets' contract.
                WorkspaceWidgetsCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(DemoStyles.INSTANCE),
                // The parties: what controls a demo, and the two logs - their types and secretaries.
                CrateEntry.of(ComponentControlModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(ComponentControlSecretaryModule.INSTANCE, StandardJsModuleType.SECRETARY),
                CrateEntry.of(ComponentDemoLogModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(ComponentControlLogModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(ComponentLogSecretaryModule.INSTANCE, StandardJsModuleType.SECRETARY),
                // The demo every one extends, and the demos.
                CrateEntry.of(ComponentDemoModule.INSTANCE),
                CrateEntry.of(ButtonDemoModule.INSTANCE),
                CrateEntry.of(IconDemoModule.INSTANCE),
                CrateEntry.of(SummaryCardDemoModule.INSTANCE),
                CrateEntry.of(SliderDemoModule.INSTANCE),
                CrateEntry.of(SliderGroupDemoModule.INSTANCE),
                CrateEntry.of(PanelDemoModule.INSTANCE),
                CrateEntry.of(EdgeStripDemoModule.INSTANCE),
                // The registry: generated from HouseDemos.
                CrateEntry.of(HouseDemosModule.INSTANCE, StandardJsModuleType.PURE_LOGIC));
    }
}
