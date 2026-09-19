package hue.captains.singapura.js.homing.site.demo.gallery.prefs;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.preferences.PreferencesRegistry;
import hue.captains.singapura.js.homing.preferences.PreferencesView;
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
    /** The app as a widget by the base's contract: construct(branch, params) → { root, dispose }; appMain delegates to it. */
    public record construct() implements Exportable._Constant<PreferencesApp> {}

    @Override public String title()      { return "Preferences"; }
    @Override public String simpleName() { return "preferences"; }

    @Override
    public ImportsFor<PreferencesApp> imports() {
        return ImportsFor.<PreferencesApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PreferencesView.mountPreferencesView()), PreferencesView.INSTANCE))
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
        return new ExportsOf<>(INSTANCE, List.of(new appMain(), new construct()));
    }
}
