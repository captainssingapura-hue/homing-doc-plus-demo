package hue.captains.singapura.js.homing.site.demo.gallery.prefs;

import hue.captains.singapura.js.homing.preferences.ChoiceWidget;
import hue.captains.singapura.js.homing.preferences.OverviewWidget;
import hue.captains.singapura.js.homing.preferences.PreferenceNode;
import hue.captains.singapura.js.homing.preferences.PreferenceTree;
import hue.captains.singapura.js.homing.preferences.PreferencesRegistry;
import hue.captains.singapura.js.homing.preferences.ScaleWidget;
import hue.captains.singapura.js.homing.preferences.ToggleWidget;
import hue.captains.singapura.js.homing.preferences.WidgetProvider;
import hue.captains.singapura.js.homing.site.mpa.ThemeWidget;

import java.util.List;
import java.util.Map;

/**
 * The gallery's preferences: a rigid tree with a widget on every node,
 * stamped into this module. The theme is the MPA's own widget over
 * {@code /themes}; the rest are the generic kinds. The master is the
 * relation tree, from its own repo, wrapped as a widget. Given to the MPA,
 * so the bar's button opens this tree; also a page of its own.
 *
 * <p>Only the theme is read by anything today. The locale and the editor
 * settings are kept by the steward and shown here so that the tree, the
 * kinds and the overview are proved on more than one preference; the
 * widgets say so.</p>
 */
public final class GalleryPreferences extends PreferencesRegistry {

    static final PreferenceTree TREE = PreferenceTree.of(
            PreferenceNode.of("preferences", "Preferences", "What this site remembers for you, in this browser.",
                    overview("Preferences", "What this site remembers for you, in this browser: the theme it wears, and, once the site reads them, your locale and how the editor shows text.",
                             setting("theme", "Theme"), setting("locale/language", "Language"),
                             setting("editor/font-size", "Font size"), setting("editor/wrap", "Wrap lines")),
                    PreferenceNode.of("theme", "Theme", "The design this site wears, and the colours it wears it in.",
                            WidgetProvider.of(ThemeWidget.INSTANCE, Map.of("name", "theme", "label", "Theme",
                                    "summary", "Seven designs, each offered in the colours that suit it. A pick switches every sheet on the page; another tab follows."))),
                    PreferenceNode.of("locale", "Locale", "Language, numbers and dates.",
                            overview("Locale", "How the site would speak to you - once it does. The value is kept and rides on every module address today; nothing reads it yet.",
                                     setting("locale/language", "Language")),
                            PreferenceNode.of("language", "Language", "",
                                    WidgetProvider.of(ChoiceWidget.INSTANCE, Map.of("name", "locale/language", "label", "Language",
                                            "summary", "Kept by the steward and sent with every module the page loads; the site does not yet vary by it.",
                                            "default", "en",
                                            "options", List.of(option("en", "English", "the site's own"), option("fr", "Français", ""),
                                                               option("de", "Deutsch", ""), option("ja", "日本語", "")))))),
                    PreferenceNode.of("editor", "Editor", "How text is shown where you edit it.",
                            overview("Editor", "Two settings that nothing reads yet, here to prove a scale and a toggle end to end.",
                                     setting("editor/font-size", "Font size"), setting("editor/wrap", "Wrap lines")),
                            PreferenceNode.of("font-size", "Font size", "",
                                    WidgetProvider.of(ScaleWidget.INSTANCE, Map.of("name", "editor/font-size", "label", "Font size",
                                            "summary", "In pixels. The default is the site's.",
                                            "min", 12, "max", 24, "step", 1, "unit", "px", "default", 16))),
                            PreferenceNode.of("wrap", "Wrap lines", "",
                                    WidgetProvider.of(ToggleWidget.INSTANCE, Map.of("name", "editor/wrap", "label", "Wrap lines",
                                            "summary", "Whether long lines fold at the edge or run off it.",
                                            "default", "true", "on", "Lines wrap", "off", "Lines run off the edge"))))));

    private static WidgetProvider overview(String label, String summary, Map<String, String>... settings) {
        return WidgetProvider.of(OverviewWidget.INSTANCE, Map.of("label", label, "summary", summary, "settings", List.of(settings)));
    }

    // Declared after the tree it is built from: a static initialiser runs in order.
    public static final GalleryPreferences INSTANCE = new GalleryPreferences();

    private GalleryPreferences() {
        super(TREE, WidgetProvider.of(PreferencesTreeWidget.INSTANCE, Map.of("folder", true)));
    }

    private static Map<String, String> setting(String name, String label) { return Map.of("name", name, "label", label); }

    private static Map<String, String> option(String value, String label, String note) {
        return Map.of("value", value, "label", label, "note", note);
    }
}
