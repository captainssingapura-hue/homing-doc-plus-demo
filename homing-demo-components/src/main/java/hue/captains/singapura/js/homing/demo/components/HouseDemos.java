package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.taxonomy.Component;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.preferences.HousePreferences;
import hue.captains.singapura.js.homing.site.mpa.HouseSite;
import hue.captains.singapura.js.homing.ui.controlpanel.HouseControlPanel;
import hue.captains.singapura.js.homing.ui.monitors.HouseMonitors;
import hue.captains.singapura.js.homing.ui.menu.HouseMenus;
import hue.captains.singapura.js.homing.ui.panes.HousePanes;
import hue.captains.singapura.js.homing.ui.panzoom.HousePanZoom;
import hue.captains.singapura.js.homing.ui.splitgrid.HouseSplitGrid;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseContainers;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseControls;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseItems;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseMarks;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseRegions;
import hue.captains.singapura.js.homing.ui.taxonomy.HouseText;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * Which of the house's leaves can be seen in action, and how. Every leaf of the house's taxonomy
 * is in exactly one of four lists: a {@link #demos() demo} that shows it live; {@link #aroundIt()
 * shown by the page around} the demos - the workspace they stand in, the page's chrome, the
 * preferences it opens; realized, its demo {@link #pending() still to come}; or {@link
 * #unrealized() realized by nothing} yet, so its meaning is all there is to show. Held against the
 * house's taxonomy by its test, so a leaf added to the house is placed here before anything ships.
 */
public record HouseDemos() implements StatelessFunctionalObject {

    public static final HouseDemos INSTANCE = new HouseDemos();

    /** The leaves seen in action, each with the demo that shows it live. */
    public List<Demo> demos() {
        var button = new ModuleImports<>(List.of(new ButtonDemoModule.ButtonDemo()), ButtonDemoModule.INSTANCE);
        var text = new ModuleImports<>(List.of(new TextDemoModule.TextDemo()), TextDemoModule.INSTANCE);
        return List.of(
                // the elements: the nine buttons are one button, each as its leaf says
                new Demo(HouseControls.PlainButton.INSTANCE, button),
                new Demo(HouseControls.PrimaryButton.INSTANCE, button),
                new Demo(HouseControls.SecondaryButton.INSTANCE, button),
                new Demo(HouseControls.DangerButton.INSTANCE, button),
                new Demo(HouseControls.WarningButton.INSTANCE, button),
                new Demo(HouseControls.SuccessButton.INSTANCE, button),
                new Demo(HouseControls.ToggleButton.INSTANCE, button),
                new Demo(HouseControls.CloseButton.INSTANCE, button),
                new Demo(HouseControls.IconButton.INSTANCE, button),
                // the nine texts are one text, each as its leaf says
                new Demo(HouseText.Heading.INSTANCE, text),
                new Demo(HouseText.Kicker.INSTANCE, text),
                new Demo(HouseText.Caption.INSTANCE, text),
                new Demo(HouseText.Lede.INSTANCE, text),
                new Demo(HouseText.Label.INSTANCE, text),
                new Demo(HouseText.Readout.INSTANCE, text),
                new Demo(HouseText.Badge.INSTANCE, text),
                new Demo(HouseText.Pill.INSTANCE, text),
                new Demo(HouseText.Wordmark.INSTANCE, text),
                // a way to somewhere else
                new Demo(HouseControls.Link.INSTANCE, new ModuleImports<>(List.of(new LinkDemoModule.LinkDemo()), LinkDemoModule.INSTANCE)),
                new Demo(HouseMarks.Icon.INSTANCE, new ModuleImports<>(List.of(new IconDemoModule.IconDemo()), IconDemoModule.INSTANCE)),
                new Demo(HouseContainers.SummaryCard.INSTANCE,
                        new ModuleImports<>(List.of(new SummaryCardDemoModule.SummaryCardDemo()), SummaryCardDemoModule.INSTANCE)),
                new Demo(HouseControls.Slider.INSTANCE, new ModuleImports<>(List.of(new SliderDemoModule.SliderDemo()), SliderDemoModule.INSTANCE)),
                new Demo(HouseContainers.SliderGroup.INSTANCE,
                        new ModuleImports<>(List.of(new SliderGroupDemoModule.SliderGroupDemo()), SliderGroupDemoModule.INSTANCE)),
                new Demo(HouseContainers.Panel.INSTANCE, new ModuleImports<>(List.of(new PanelDemoModule.PanelDemo()), PanelDemoModule.INSTANCE)),
                new Demo(HouseContainers.EdgeStrip.INSTANCE,
                        new ModuleImports<>(List.of(new EdgeStripDemoModule.EdgeStripDemo()), EdgeStripDemoModule.INSTANCE)),
                // overlays, menus and splits
                new Demo(HouseContainers.Dialog.INSTANCE, new ModuleImports<>(List.of(new DialogDemoModule.DialogDemo()), DialogDemoModule.INSTANCE)),
                new Demo(HouseContainers.ContextMenu.INSTANCE,
                        new ModuleImports<>(List.of(new ContextMenuDemoModule.ContextMenuDemo()), ContextMenuDemoModule.INSTANCE)),
                new Demo(HouseMenus.ContextMenuSteward.INSTANCE,
                        new ModuleImports<>(List.of(new ContextMenuStewardDemoModule.ContextMenuStewardDemo()), ContextMenuStewardDemoModule.INSTANCE)),
                new Demo(HouseContainers.FloatingPane.INSTANCE,
                        new ModuleImports<>(List.of(new FloatingPaneDemoModule.FloatingPaneDemo()), FloatingPaneDemoModule.INSTANCE)),
                new Demo(HouseContainers.FloatLayer.INSTANCE,
                        new ModuleImports<>(List.of(new FloatLayerDemoModule.FloatLayerDemo()), FloatLayerDemoModule.INSTANCE)),
                new Demo(HouseSplitGrid.SplitGrid.INSTANCE,
                        new ModuleImports<>(List.of(new SplitGridDemoModule.SplitGridDemo()), SplitGridDemoModule.INSTANCE)),
                new Demo(HouseSplitGrid.SplitGridMirror.INSTANCE,
                        new ModuleImports<>(List.of(new SplitGridMirrorDemoModule.SplitGridMirrorDemo()), SplitGridMirrorDemoModule.INSTANCE)),
                // what the page around them does not show: the tab controls, the list master, the pictures, the monitors
                new Demo(HousePanes.TabOpener.INSTANCE,
                        new ModuleImports<>(List.of(new TabOpenerDemoModule.TabOpenerDemo()), TabOpenerDemoModule.INSTANCE)),
                new Demo(HousePreferences.ListMasterWidget.INSTANCE,
                        new ModuleImports<>(List.of(new ListMasterWidgetDemoModule.ListMasterWidgetDemo()), ListMasterWidgetDemoModule.INSTANCE)),
                new Demo(HousePanZoom.SvgPanZoom.INSTANCE,
                        new ModuleImports<>(List.of(new SvgPanZoomDemoModule.SvgPanZoomDemo()), SvgPanZoomDemoModule.INSTANCE)),
                new Demo(HousePanZoom.PanZoomBar.INSTANCE,
                        new ModuleImports<>(List.of(new PanZoomBarDemoModule.PanZoomBarDemo()), PanZoomBarDemoModule.INSTANCE)),
                new Demo(HouseMonitors.FocusMonitor.INSTANCE,
                        new ModuleImports<>(List.of(new FocusMonitorDemoModule.FocusMonitorDemo()), FocusMonitorDemoModule.INSTANCE)),
                new Demo(HouseMonitors.StewardMonitor.INSTANCE,
                        new ModuleImports<>(List.of(new StewardMonitorDemoModule.StewardMonitorDemo()), StewardMonitorDemoModule.INSTANCE)));
    }

    /** Shown by the page around the demos - the workspace they stand in, the panel beside them, the page's chrome, the preferences it opens - and where. */
    public List<ShownAround> aroundIt() {
        return List.of(
                // the workspace
                new ShownAround(HousePanes.MultiTabPane.INSTANCE, "each region of the workspace: its tabs, one in front"),
                new ShownAround(HouseContainers.TabStrip.INSTANCE, "the bar of tabs atop each region: its chips, its plus, the count of those out of sight"),
                new ShownAround(HousePanes.TabPane.INSTANCE, "every tab of the workspace: its chip and what it holds, which travel together"),
                new ShownAround(HouseMarks.Separator.INSTANCE, "between the sections of a tab's menu, opened by a right-click on its chip"),
                new ShownAround(HousePanes.TabPicker.INSTANCE, "a region with no tab left: what it offers to open"),
                new ShownAround(HousePanes.SingleTabPane.INSTANCE, "a tab torn off its bar: the float that carries it"),
                new ShownAround(HouseContainers.TitleBar.INSTANCE, "the head of a pane that floats - a dialog's window, the preferences' among them: its title and its close, where the hand takes it"),
                new ShownAround(HouseContainers.PanelBar.INSTANCE, "the head of a panel, in the panel's demo: its title, the line under it, and what acts on the panel at its end"),
                // the panel beside the demos
                new ShownAround(HouseControlPanel.ControlPanel.INSTANCE, "the control panel beside the demos: a slider for each degree the picked component varies by, a button for each switch, action and question"),
                // the page's chrome
                new ShownAround(HouseContainers.Ground.INSTANCE, "the page itself: what everything on it stands on, its ink and its face"),
                new ShownAround(HouseSite.MpaChrome.INSTANCE, "the page's chrome: its bar at the top, over the page's own room"),
                new ShownAround(HouseContainers.SiteBar.INSTANCE, "the bar at the top of the page: the site's name, the trail, the preferences"),
                new ShownAround(HouseControls.Brand.INSTANCE, "the site's mark and name at the start of the bar at the top of the page: the way home"),
                new ShownAround(HouseContainers.Trail.INSTANCE, "the way to this page, across the bar at the top of the page"),
                new ShownAround(HouseControls.PreferencesButton.INSTANCE, "the preferences button at the end of the bar at the top of the page"),
                new ShownAround(HouseControls.Crumb.INSTANCE, "each step of the trail in the bar at the top of the page: the ways back, the last the page itself, set heavier"),
                new ShownAround(HouseMarks.Delimiter.INSTANCE, "between the steps of the trail in the bar at the top of the page: a sign, a slash unless the style draws another"),
                // the preferences it opens
                new ShownAround(HousePreferences.PreferencesView.INSTANCE, "the preferences, opened from the bar: the settings listed, the one chosen beside them"),
                new ShownAround(HouseContainers.Scrim.INSTANCE, "the veil over the page behind the preferences, opened from the bar: a press on it closes them"),
                new ShownAround(HouseRegions.WidgetSlot.INSTANCE, "the preferences' list and the setting beside it: each a slot a widget is loaded into"),
                new ShownAround(HousePreferences.PreferenceField.INSTANCE, "every setting in the preferences: where it belongs, its title, a note, the way back to its default"),
                new ShownAround(HousePreferences.OverviewWidget.INSTANCE, "a group of settings in the preferences: each setting listed with its value"),
                new ShownAround(HouseSite.ThemeWidget.INSTANCE, "the theme in the preferences: a style, and a palette to wear it in"),
                new ShownAround(HousePreferences.ChoiceWidget.INSTANCE, "a setting in the preferences chosen from a few options"),
                new ShownAround(HousePreferences.ScaleWidget.INSTANCE, "a setting in the preferences set along a scale"),
                new ShownAround(HousePreferences.ToggleWidget.INSTANCE, "a setting in the preferences that is on or off"));
    }

    /** Realized, their demos still to come: none now. */
    public List<Component<?>> pending() { return List.of(); }

    /** Realized by nothing yet: an owner mints them, or nothing does. What they mean is all there is to show. */
    public List<Component<?>> unrealized() {
        return List.of(
                // controls
                HouseControls.Knob.INSTANCE, HouseControls.Grip.INSTANCE,
                HouseControls.Tab.INSTANCE, HouseControls.Range.INSTANCE, HouseControls.Select.INSTANCE,
                HouseControls.Switch.INSTANCE, HouseControls.PaneThumb.INSTANCE,
                // items
                HouseItems.MenuItem.INSTANCE, HouseItems.ListRow.INSTANCE, HouseItems.ChoiceOption.INSTANCE, HousePreferences.SettingRow.INSTANCE,
                // containers and regions
                HouseContainers.PlainCard.INSTANCE, HouseContainers.ActionBar.INSTANCE,
                HouseRegions.Section.INSTANCE, HouseSplitGrid.GridCell.INSTANCE,
                // retired for now: adding a tab from outside a pane - which pane, and what - comes back on the panes' logical model
                HousePanes.AddTab.INSTANCE, HousePanes.PaneThumbs.INSTANCE,
                // marks and tracks
                HouseMarks.Detent.INSTANCE, HouseMarks.Tick.INSTANCE, HouseMarks.Lamp.INSTANCE, HouseMarks.BoundaryLine.INSTANCE,
                HouseMarks.DropMark.INSTANCE, HouseMarks.Indicator.INSTANCE, HouseMarks.Groove.INSTANCE, HouseMarks.Fill.INSTANCE);
    }
}
