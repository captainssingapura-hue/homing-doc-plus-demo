package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SelfContent;
import hue.captains.singapura.js.homing.ui.menu.ContextMenuRegistry;

import java.util.List;

import static hue.captains.singapura.js.homing.ui.menu.ContextMenuRegistry.ContextMenuType;
import static hue.captains.singapura.js.homing.ui.menu.ContextMenuRegistry.MenuItem;

/**
 * The gallery's context menus, declared once as data and stamped into one JS
 * module, {@code MENUS}, for the page's steward: the kinds the gallery
 * offers and the items each carries. What a pick does is the page's handler
 * for the kind; what a row's state is for a given object is asked of that
 * handler at bind. Three kinds for three hypothetical cells, each with a
 * peculiarity: the animal's second level, the swatch's checked colour and
 * toggle, the counter's disabled reset and checked step.
 */
public record GalleryMenus() implements EsModule<GalleryMenus>, SelfContent {

    /** The registry as data: kind → { kind, items }. */
    public record MENUS() implements Exportable._Constant<GalleryMenus> {}

    public static final GalleryMenus INSTANCE = new GalleryMenus();

    public static final ContextMenuRegistry REGISTRY = ContextMenuRegistry.of(
            ContextMenuType.of("animal",
                    MenuItem.of("rotate", "Rotate", "a quarter turn"),
                    MenuItem.of("flip", "Flip", "mirror it"),
                    MenuItem.divider(),
                    MenuItem.submenu("animal", "Animal",
                            MenuItem.of("cat", "Cat"), MenuItem.of("dog", "Dog"), MenuItem.of("owl", "Owl"), MenuItem.of("fox", "Fox"))),
            ContextMenuType.of("swatch",
                    MenuItem.submenu("colour", "Colour",
                            MenuItem.of("primary", "Primary"), MenuItem.of("success", "Success"), MenuItem.of("warning", "Warning"), MenuItem.of("danger", "Danger")),
                    MenuItem.divider(),
                    MenuItem.of("invert", "Inverted", "the design's inverted surface")),
            ContextMenuType.of("counter",
                    MenuItem.of("add", "Add a step"),
                    MenuItem.of("reset", "Reset", "back to nought"),
                    MenuItem.divider(),
                    MenuItem.submenu("step", "Step", MenuItem.of("s1", "1"), MenuItem.of("s5", "5"), MenuItem.of("s10", "10"))));

    @Override public ImportsFor<GalleryMenus> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<GalleryMenus> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new MENUS()));
    }

    @Override
    public List<String> selfContent(ModuleNameResolver resolver) { return REGISTRY.js(); }
}
