package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.taxonomy.Component;
import hue.captains.singapura.js.homing.core.ModuleImports;
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
        return List.of(
                // the elements: the six buttons are one button, each in its own colour
                new Demo(HouseControls.PlainButton.INSTANCE, button),
                new Demo(HouseControls.PrimaryButton.INSTANCE, button),
                new Demo(HouseControls.SecondaryButton.INSTANCE, button),
                new Demo(HouseControls.DangerButton.INSTANCE, button),
                new Demo(HouseControls.WarningButton.INSTANCE, button),
                new Demo(HouseControls.SuccessButton.INSTANCE, button),
                new Demo(HouseMarks.Icon.INSTANCE, new ModuleImports<>(List.of(new IconDemoModule.IconDemo()), IconDemoModule.INSTANCE)),
                new Demo(HouseContainers.SummaryCard.INSTANCE,
                        new ModuleImports<>(List.of(new SummaryCardDemoModule.SummaryCardDemo()), SummaryCardDemoModule.INSTANCE)),
                new Demo(HouseControls.Slider.INSTANCE, new ModuleImports<>(List.of(new SliderDemoModule.SliderDemo()), SliderDemoModule.INSTANCE)),
                new Demo(HouseContainers.SliderGroup.INSTANCE,
                        new ModuleImports<>(List.of(new SliderGroupDemoModule.SliderGroupDemo()), SliderGroupDemoModule.INSTANCE)),
                new Demo(HouseContainers.Panel.INSTANCE, new ModuleImports<>(List.of(new PanelDemoModule.PanelDemo()), PanelDemoModule.INSTANCE)),
                new Demo(HouseContainers.EdgeStrip.INSTANCE,
                        new ModuleImports<>(List.of(new EdgeStripDemoModule.EdgeStripDemo()), EdgeStripDemoModule.INSTANCE)));
    }

    /** Shown by the page around the demos - the workspace they stand in, the page's chrome, the preferences it opens - and where. */
    public List<ShownAround> aroundIt() {
        return List.of(
                // the workspace
                new ShownAround(HouseContainers.DockGrid.INSTANCE, "the workspace itself: its regions, each a dock, the lines between them dragged to re-share"),
                new ShownAround(HouseContainers.MultiTabPane.INSTANCE, "each region of the workspace: its tabs, one in front"),
                new ShownAround(HouseContainers.TabStrip.INSTANCE, "the bar of tabs atop each region: its chips, its plus, the count of those out of sight"),
                new ShownAround(HouseContainers.TabPane.INSTANCE, "every tab of the workspace: its chip and what it holds, which travel together"),
                new ShownAround(HouseContainers.TabPicker.INSTANCE, "a region with no tab left: what it offers to open"),
                new ShownAround(HouseContainers.SingleTabPane.INSTANCE, "a tab torn off its bar: the float that carries it"),
                // the page's chrome
                new ShownAround(HouseContainers.MpaChrome.INSTANCE, "the bar at the top of the page: the site's name, the trail, the preferences"),
                new ShownAround(HouseControls.PreferencesButton.INSTANCE, "the preferences button at the end of the bar at the top of the page"),
                // the preferences it opens
                new ShownAround(HouseContainers.PreferencesView.INSTANCE, "the preferences, opened from the bar: the settings listed, the one chosen beside them"),
                new ShownAround(HouseRegions.WidgetSlot.INSTANCE, "the preferences' list and the setting beside it: each a slot a widget is loaded into"),
                new ShownAround(HouseContainers.PreferenceField.INSTANCE, "every setting in the preferences: where it belongs, its title, a note, the way back to its default"),
                new ShownAround(HouseContainers.OverviewWidget.INSTANCE, "a group of settings in the preferences: each setting listed with its value"),
                new ShownAround(HouseContainers.ThemeWidget.INSTANCE, "the theme in the preferences: a style, and a palette to wear it in"),
                new ShownAround(HouseContainers.ChoiceWidget.INSTANCE, "a setting in the preferences chosen from a few options"),
                new ShownAround(HouseContainers.ScaleWidget.INSTANCE, "a setting in the preferences set along a scale"),
                new ShownAround(HouseContainers.ToggleWidget.INSTANCE, "a setting in the preferences that is on or off"));
    }

    /** Realized, their demos still to come: the overlays, menus and splits, and what the page around does not show. */
    public List<Component<?>> pending() {
        return List.of(
                HouseContainers.Dialog.INSTANCE, HouseContainers.ContextMenu.INSTANCE, HouseContainers.ContextMenuSteward.INSTANCE,
                HouseContainers.FloatingPane.INSTANCE, HouseContainers.FloatLayer.INSTANCE,
                HouseContainers.SplitPane.INSTANCE, HouseContainers.SplitGrid.INSTANCE, HouseContainers.SplitGridMirror.INSTANCE,
                HouseContainers.TabOpener.INSTANCE, HouseContainers.AddTab.INSTANCE, HouseContainers.PaneThumbs.INSTANCE,
                HouseContainers.ListMasterWidget.INSTANCE, HouseContainers.SvgPanZoom.INSTANCE, HouseContainers.PanZoomBar.INSTANCE,
                HouseContainers.FocusMonitor.INSTANCE, HouseContainers.StewardMonitor.INSTANCE);
    }

    /** Realized by nothing yet: an owner mints them, or nothing does. What they mean is all there is to show. */
    public List<Component<?>> unrealized() {
        return List.of(
                // controls
                HouseControls.ToggleButton.INSTANCE, HouseControls.IconButton.INSTANCE, HouseControls.CloseButton.INSTANCE,
                HouseControls.Knob.INSTANCE, HouseControls.Grip.INSTANCE, HouseControls.Divider.INSTANCE, HouseControls.Link.INSTANCE,
                HouseControls.Crumb.INSTANCE, HouseControls.Tab.INSTANCE, HouseControls.Range.INSTANCE, HouseControls.Select.INSTANCE,
                HouseControls.Switch.INSTANCE, HouseControls.PaneThumb.INSTANCE, HouseControls.Brand.INSTANCE,
                // items
                HouseItems.MenuItem.INSTANCE, HouseItems.ListRow.INSTANCE, HouseItems.ChoiceOption.INSTANCE, HouseItems.SettingRow.INSTANCE,
                // containers and regions
                HouseContainers.PlainCard.INSTANCE, HouseContainers.Scrim.INSTANCE, HouseContainers.ActionBar.INSTANCE,
                HouseContainers.Trail.INSTANCE, HouseRegions.Section.INSTANCE, HouseRegions.GridCell.INSTANCE,
                // text
                HouseText.Label.INSTANCE, HouseText.Heading.INSTANCE, HouseText.Kicker.INSTANCE, HouseText.Caption.INSTANCE,
                HouseText.Lede.INSTANCE, HouseText.Readout.INSTANCE, HouseText.Badge.INSTANCE, HouseText.Pill.INSTANCE,
                HouseText.Wordmark.INSTANCE,
                // marks and tracks
                HouseMarks.Detent.INSTANCE, HouseMarks.Tick.INSTANCE, HouseMarks.Separator.INSTANCE, HouseMarks.Lamp.INSTANCE,
                HouseMarks.DropMark.INSTANCE, HouseMarks.Indicator.INSTANCE, HouseMarks.Groove.INSTANCE, HouseMarks.Fill.INSTANCE);
    }
}
