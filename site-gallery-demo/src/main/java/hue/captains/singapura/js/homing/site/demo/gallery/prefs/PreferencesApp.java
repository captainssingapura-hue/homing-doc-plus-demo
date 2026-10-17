package hue.captains.singapura.js.homing.site.demo.gallery.prefs;

import hue.captains.singapura.js.homing.ui.elements.LedeModule;
import hue.captains.singapura.js.homing.ui.elements.Heading1Module;
import hue.captains.singapura.js.homing.ui.elements.KickerModule;
import hue.captains.singapura.js.homing.component.WidgetSlotModule;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.preferences.PreferencesRegistry;
import hue.captains.singapura.js.homing.preferences.PreferencesViewModule;
import hue.captains.singapura.js.homing.site.demo.gallery.GalleryStyles;

import java.util.List;

/**
 * The preferences as a page: the view mounted in the slot, given the
 * gallery's stamped registry. Only the view and the registry are imported
 * here; every widget's module — the tree, the theme, the kinds — is loaded
 * by the view the first time its node is chosen.
 */
public record PreferencesApp() implements AppModule<AppModule._None, PreferencesApp> {

    public static final PreferencesApp INSTANCE = new PreferencesApp();

    record appMain() implements AppModule._AppMain<AppModule._None, PreferencesApp> {}
    /** The app as a widget by the base's contract: {@code new PreferencesWidget(branch, params)}; appMain delegates to it. */
    public record PreferencesWidget() implements Exportable._Constant<PreferencesApp> {}

    @Override public String title()      { return "Preferences"; }
    @Override public String simpleName() { return "preferences"; }

    @Override
    public ImportsFor<PreferencesApp> imports() {
        return ImportsFor.<PreferencesApp>builder()
                // its words the house's texts
                .add(new ModuleImports<>(List.of(new KickerModule.Kicker(), new KickerModule.KickerFactory()), KickerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Heading1Module.Heading1(), new Heading1Module.Heading1Factory()), Heading1Module.INSTANCE))
                .add(new ModuleImports<>(List.of(new LedeModule.Lede(), new LedeModule.LedeFactory()), LedeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PreferencesViewModule.PreferencesView()), PreferencesViewModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetSlotModule.WidgetSlot()), WidgetSlotModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PreferencesRegistry.PREFERENCES()), GalleryPreferences.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new GalleryStyles.ga_kicker(),
                        new GalleryStyles.ga_title(),
                        new GalleryStyles.ga_lede()
                ), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PreferencesApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new PreferencesWidget()));
    }
}
