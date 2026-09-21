package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.server.HtmlPageContent;
import hue.captains.singapura.js.homing.site.Navigable;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Router;
import hue.captains.singapura.js.homing.site.Site;
import hue.captains.singapura.js.homing.site.Trail;
import hue.captains.singapura.js.homing.site.demo.gallery.prefs.GalleryPreferences;
import hue.captains.singapura.js.homing.site.demo.gallery.prefs.PreferencesApp;
import hue.captains.singapura.js.homing.site.mpa.AppPage;
import hue.captains.singapura.js.homing.site.mpa.Brand;
import hue.captains.singapura.js.homing.site.mpa.StandardMpa;
import hue.captains.singapura.js.homing.studio.themes.StudioThemeRegistry;

import java.util.Optional;

/**
 * The gallery: a hand-written router over two JS pages and one plain one,
 * with the standard MPA wearing the seven studio designs.
 *
 * <p>No catalogue anywhere. {@code /counter/7} shows what a router does for
 * a placed page: it reads the start off the path, binds it, and tells the
 * page its trail — {@code Gallery / Counter} — which the chrome draws. The
 * MPA never learns where the trail came from. {@code /grid} and {@code /tree}
 * are the relation grid and its tree, from their own repo, as pages.</p>
 */
public record GallerySite() implements Site {

    public static final GallerySite INSTANCE = new GallerySite();

    public static final StandardMpa MPA = StandardMpa.of(
            Brand.of("Gallery"), StudioThemeRegistry.INSTANCE, GalleryPreferences.INSTANCE, GalleryCrate.INSTANCE);

    static final AppPage<?, ?> SHELL   = MPA.page(GalleryShellApp.INSTANCE, new GalleryShellApp.Params(""));
    static final AppPage<?, ?> WELCOME = MPA.page(WelcomeApp.INSTANCE);
    static final AppPage<?, ?> GRID    = MPA.page(GridApp.INSTANCE);
    static final AppPage<?, ?> TREE    = MPA.page(TreeApp.INSTANCE);
    static final AppPage<?, ?> DIALOG  = MPA.page(DialogApp.INSTANCE);
    static final AppPage<?, ?> PANES   = MPA.page(PanesApp.INSTANCE);
    static final AppPage<?, ?> BUTTONS = MPA.page(ButtonsApp.INSTANCE);
    static final AppPage<?, ?> CARDS   = MPA.page(CardsApp.INSTANCE);
    static final AppPage<?, ?> FLOATING = MPA.page(FloatingApp.INSTANCE);
    static final AppPage<?, ?> DOCKING  = MPA.page(DockingApp.INSTANCE);
    static final AppPage<?, ?> SPLITGRID = MPA.page(SplitGridApp.INSTANCE);
    static final AppPage<?, ?> TABSTRIP = MPA.page(TabStripApp.INSTANCE);
    static final AppPage<?, ?> MENUS   = MPA.page(ContextMenusApp.INSTANCE);
    static final AppPage<?, ?> SLIDERS = MPA.page(SlidersApp.INSTANCE);
    static final AppPage<?, ?> PREFS   = MPA.page(PreferencesApp.INSTANCE);

    static final Navigable PLAIN = q -> new HtmlPageContent("""
            <!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>Plain · Gallery</title></head>
            <body style="font: 16px/1.5 Georgia, serif; max-width: 40rem; margin: 3rem auto; padding: 0 1rem">
            <h1>A plain page</h1>
            <p>Served by the same router as the JS pages, but not under the chrome: a string, no module,
               no theme. The MPA is opt-in per page — a page is under it because the site made it so
               with <code>MPA.page(app)</code>, not because the site has one.</p>
            <p><a href="/">Back to the gallery</a></p>
            </body></html>
            """);

    @Override public String name() { return "gallery"; }

    @Override
    public Router router() {
        return path -> switch (path.head().orElse("")) {
            case ""        -> path.isRoot() ? Optional.of(SHELL) : Optional.empty();
            case "welcome" -> path.depth() == 1 ? Optional.of(placed(WELCOME, "Welcome", path)) : Optional.empty();
            case "plain"   -> path.depth() == 1 ? Optional.of(PLAIN) : Optional.empty();
            case "counter" -> counter(path);
            case "grid"    -> path.depth() == 1 ? Optional.of(placed(GRID, "Grid", path)) : Optional.empty();
            case "tree"    -> path.depth() == 1 ? Optional.of(placed(TREE, "Tree", path)) : Optional.empty();
            case "dialog"  -> path.depth() == 1 ? Optional.of(placed(DIALOG, "Dialog", path)) : Optional.empty();
            case "panes"   -> path.depth() == 1 ? Optional.of(placed(PANES, "Panes", path)) : Optional.empty();
            case "buttons" -> path.depth() == 1 ? Optional.of(placed(BUTTONS, "Buttons", path)) : Optional.empty();
            case "cards"   -> path.depth() == 1 ? Optional.of(placed(CARDS, "Cards", path)) : Optional.empty();
            case "floating" -> path.depth() == 1 ? Optional.of(placed(FLOATING, "Floating panes", path)) : Optional.empty();
            case "docking"  -> path.depth() == 1 ? Optional.of(placed(DOCKING, "Dock and undock", path)) : Optional.empty();
            case "splitgrid" -> path.depth() == 1 ? Optional.of(placed(SPLITGRID, "Split grid", path)) : Optional.empty();
            case "tabstrip" -> path.depth() == 1 ? Optional.of(placed(TABSTRIP, "Tab strip", path)) : Optional.empty();
            case "menus"   -> path.depth() == 1 ? Optional.of(placed(MENUS, "Context menus", path)) : Optional.empty();
            case "sliders" -> path.depth() == 1 ? Optional.of(placed(SLIDERS, "Sliders", path)) : Optional.empty();
            case "preferences" -> path.depth() == 1 ? Optional.of(placed(PREFS, "Preferences", path)) : Optional.empty();
            default        -> Optional.empty();
        };
    }

    /** A page one below the root, told the two-crumb trail the site's router knows. */
    private static Navigable placed(AppPage<?, ?> page, String name, Path path) {
        var trail = Trail.NONE.then("Gallery", "/").then(name, path.toString());
        return q -> page.html(trail, q);
    }

    /** {@code /counter} starts at 0; {@code /counter/<n>} binds n off the path. Either is told its trail. */
    private static Optional<Navigable> counter(Path path) {
        int start;
        switch (path.depth()) {
            case 1 -> start = 0;
            case 2 -> {
                try { start = Integer.parseInt(path.segments().get(1)); }
                catch (NumberFormatException notANumber) { return Optional.empty(); }
            }
            default -> { return Optional.empty(); }
        }
        var page  = MPA.page(CounterApp.INSTANCE, new CounterApp.Params(start));
        var trail = Trail.NONE.then("Gallery", "/").then("Counter", path.toString());
        return Optional.of(q -> page.html(trail, q));
    }
}
