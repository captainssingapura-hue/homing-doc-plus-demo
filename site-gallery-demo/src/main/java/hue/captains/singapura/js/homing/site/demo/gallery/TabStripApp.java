package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.ui.elements.LookModule;
import hue.captains.singapura.js.homing.ui.elements.CastingsModule;
import hue.captains.singapura.js.homing.ui.elements.KickerModule;
import hue.captains.singapura.js.homing.ui.elements.Heading1Module;
import hue.captains.singapura.js.homing.ui.elements.LedeModule;
import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;
import hue.captains.singapura.js.homing.ui.panes.TabStripModule;

import java.util.List;

/**
 * The tab strip page: the strip alone, no pane — five chips, the drag that is a browser's along a rail, a slider
 * for the chips' size, and the log of every step.
 */
public record TabStripApp() implements AppModule<AppModule._None, TabStripApp> {

    public static final TabStripApp INSTANCE = new TabStripApp();

    record appMain() implements AppModule._AppMain<AppModule._None, TabStripApp> {}
    /** The app as a widget by the base's contract: {@code new TabStripWidget(branch, params)}; appMain delegates to it. */
    public record TabStripWidget() implements BranchComponent<TabStripApp> {}

    @Override public String title()      { return "Tab strip"; }
    @Override public String simpleName() { return "tabstrip"; }

    @Override
    public ImportsFor<TabStripApp> imports() {
        return ImportsFor.<TabStripApp>builder()
                // its status and its log, the house's block of code
                .add(new ModuleImports<>(List.of(new LookModule.Look()), LookModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CastingsModule.ROLE()), CastingsModule.INSTANCE))
                // its words the house's texts
                .add(new ModuleImports<>(List.of(new LedeModule.Lede(), new LedeModule.LedeFactory()), LedeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Heading1Module.Heading1(), new Heading1Module.Heading1Factory()), Heading1Module.INSTANCE))
                .add(new ModuleImports<>(List.of(new KickerModule.Kicker(), new KickerModule.KickerFactory()), KickerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TabStripModule.TabStrip()), TabStripModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.Slider()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_strip_box(),
                        new GalleryStyles.ga_shelf(),
                        new GalleryStyles.ga_log(),
                        new GalleryStyles.ga_buttons()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<TabStripApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new TabStripWidget()));
    }
}
