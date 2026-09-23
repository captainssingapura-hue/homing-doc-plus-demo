package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.elements.Elements;
import hue.captains.singapura.js.homing.ui.elements.PanelModule;
import hue.captains.singapura.js.homing.ui.elements.SliderModule;

import java.util.List;

/**
 * The sheets page: the panel as a sheet, at its three registers, read against
 * each other on one ground — sunken, flat, elevated — with a fourth on the
 * controls, and a sheet lying on a sheet beneath them.
 *
 * <p>Nothing on this page links a register to a state. It sets them because
 * it is a page about registers; an app links its own states to one, in its
 * own code, which is the whole point of the panel offering two axes and
 * joining neither to anything. The two controls are two because the axes
 * are: where a sheet sits, and whether it is marked as the current one.</p>
 */
public record SheetsApp() implements AppModule<AppModule._None, SheetsApp> {

    public static final SheetsApp INSTANCE = new SheetsApp();

    record appMain() implements AppModule._AppMain<AppModule._None, SheetsApp> {}
    /** The app as a widget by the base's contract: {@code new SheetsWidget(branch, params)}; appMain delegates to it. */
    public record SheetsWidget() implements BranchComponent<SheetsApp> {}

    @Override public String title()      { return "Sheets"; }
    @Override public String simpleName() { return "sheets"; }

    @Override
    public ImportsFor<SheetsApp> imports() {
        return ImportsFor.<SheetsApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PanelModule.PanelBuilder()), PanelModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.ButtonBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new SliderModule.SliderBuilder()), SliderModule.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_buttons(),
                        new GalleryStyles.ga_sheet_ground(),
                        new GalleryStyles.ga_sheet_slot(),
                        new GalleryStyles.ga_sheet_note(),
                        new GalleryStyles.ga_sheet_lines()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<SheetsApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new SheetsWidget()));
    }
}
