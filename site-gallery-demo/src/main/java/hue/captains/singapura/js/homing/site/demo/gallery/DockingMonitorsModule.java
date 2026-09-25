package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.focusParty;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.focus.FocusMonitorModule;
import hue.captains.singapura.js.homing.ui.focus.StewardMonitorModule;
import hue.captains.singapura.js.homing.ui.docking.FloaterModule;
import hue.captains.singapura.js.homing.ui.floating.FloatLayerModule;
import hue.captains.singapura.js.homing.ui.panes.TabRegisterModule;

import java.util.List;

/**
 * The instruments, each a tab's widget: the logical focus tree, the steward's
 * lamp, the DomOps party as it stands, and what the page reports line by
 * line. They watch the page from inside it, so they obey the same law
 * everything else in a dock obeys — a member of the dock's branch, answering
 * {@code activate()}, its Escape yielding to the bar — and they hold nothing
 * live of what they render: the DomOps one reads the party's own snapshot,
 * which is plain data by construction.
 */
public record DockingMonitorsModule() implements DomModule<DockingMonitorsModule> {

    /** The focus tree in a tab: {@code new FocusTab(branch, { focus })}. */
    public record FocusTab() implements BranchComponent<DockingMonitorsModule>, NeedKeyboard {
        @Override public String summary() { return "The page's logical focus tree in a tab of the instruments' dock: every member, and the one holding the keys."; }
        @Override public List<KeyBinding> keys() { return ESCAPE; }
    }
    /** The steward's lamp in a tab: {@code new StewardTab(branch, { focus })}. */
    public record StewardTab() implements BranchComponent<DockingMonitorsModule>, NeedKeyboard {
        @Override public String summary() { return "The keyboard steward's activeness as one lamp, in a tab: active for the holder, or dormant on what has the native focus."; }
        @Override public List<KeyBinding> keys() { return ESCAPE; }
    }
    /** The DomOps party in a tab: {@code new DomOpsTab(branch, { focus })}; {@code refresh()} redraws it. */
    public record DomOpsTab() implements BranchComponent<DockingMonitorsModule>, NeedKeyboard {
        @Override public String summary() { return "The DomOps party as it stands, from its own snapshot: one row per branch, indented by depth, with what it holds."; }
        @Override public List<KeyBinding> keys() { return ESCAPE; }
    }
    /** What the page reports, in a tab: {@code new EventsTab(branch, { focus })}; {@code say(line)}. */
    public record EventsTab() implements BranchComponent<DockingMonitorsModule>, NeedKeyboard {
        @Override public String summary() { return "What the page reports, line by line: the docks' events, the desk's, the menus' and the arrangement's."; }
        @Override public List<KeyBinding> keys() { return ESCAPE; }
    }

    /** The four together: a float of their own on a desk and in a register of their own — {@code new Instruments(branch, { host, keyboard, onEvent })}. */
    public record Instruments() implements Exportable._Class<DockingMonitorsModule> {}
    /** What every instrument answers: the keys go back to the bar. */
    static final List<KeyBinding> ESCAPE = List.of(KeyBinding.of(Key.ESCAPE, "the keys go back to the dock's bar"));

    public static final DockingMonitorsModule INSTANCE = new DockingMonitorsModule();

    @Override
    public ImportsFor<DockingMonitorsModule> imports() {
        return ImportsFor.<DockingMonitorsModule>builder()
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new FocusMonitorModule.FocusMonitor()), FocusMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StewardMonitorModule.StewardMonitor()), StewardMonitorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new FloatLayerModule.FloatLayer()), FloatLayerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new FloaterModule.Floater()), FloaterModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TabRegisterModule.TabRegister()), TabRegisterModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParty()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_monitor(),
                        new GalleryStyles.ga_domops_row(),
                        new GalleryStyles.ga_domops_count(),
                        new GalleryStyles.ga_log()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DockingMonitorsModule> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new FocusTab(), new StewardTab(), new DomOpsTab(), new EventsTab(), new Instruments()));
    }
}
