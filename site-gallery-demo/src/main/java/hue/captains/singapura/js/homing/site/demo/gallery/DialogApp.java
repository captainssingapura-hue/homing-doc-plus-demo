package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.dialog.Dialog;
import hue.captains.singapura.js.homing.ui.elements.Elements;

import java.util.List;

/**
 * The dialog as a page: three ways to open one — modal with actions,
 * non-modal, and modal with a control inside that takes its own keys — and
 * a line that says what the last one answered.
 */
public record DialogApp() implements AppModule<AppModule._None, DialogApp> {

    public static final DialogApp INSTANCE = new DialogApp();

    record appMain() implements AppModule._AppMain<AppModule._None, DialogApp> {}
    /** The app as a widget by the base's contract: construct(branch, params) → { root, dispose }; appMain delegates to it. */
    public record construct() implements Exportable._Constant<DialogApp> {}

    @Override public String title()      { return "Dialog"; }
    @Override public String simpleName() { return "dialog"; }

    @Override
    public ImportsFor<DialogApp> imports() {
        return ImportsFor.<DialogApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Dialog.openDialog()), Dialog.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.Button(), new Elements.Card()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede(),
                        new GalleryStyles.ga_buttons(),
                        new GalleryStyles.ga_status(),
                        new GalleryStyles.ga_cards()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DialogApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new construct()));
    }
}
