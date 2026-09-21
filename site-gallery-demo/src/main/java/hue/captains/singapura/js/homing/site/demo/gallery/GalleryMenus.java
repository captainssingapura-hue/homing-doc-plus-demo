package hue.captains.singapura.js.homing.site.demo.gallery;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SelfContent;
import hue.captains.singapura.js.homing.design.Icon;
import hue.captains.singapura.js.homing.ui.menu.ContextMenuRegistry;
import hue.captains.singapura.js.homing.ui.menu.tree.ContextMenuKind;
import hue.captains.singapura.js.homing.ui.menu.tree.M1_Node;
import hue.captains.singapura.js.homing.ui.menu.tree.M2_Node;

import java.util.List;

/**
 * The gallery's context menus, declared once as typed trees — a class per
 * node, a kind per cell — validated in Java and stamped into one JS module,
 * {@code MENUS}, for the page's steward. The kinds live here; which of them
 * the page holds is not listed but derived: the components the gallery's
 * catalogue lists say what they need ({@code NeedContextMenu}), and the
 * registry is their union over the crate closure. What a pick does is the page's
 * handler for the kind; what a row's state is for a given object is asked
 * of that handler at bind. Three kinds for three hypothetical cells, each
 * with a peculiarity: the animal's second level, the swatch's checked colour
 * and toggle, the counter's disabled reset and checked step. A row's icon is
 * a word of the design's vocabulary; the design draws it.
 */
public record GalleryMenus() implements EsModule<GalleryMenus>, SelfContent {

    /** The registry as data: kind → { kind, nodes }. */
    public record MENUS() implements Exportable._Constant<GalleryMenus> {}

    public static final GalleryMenus INSTANCE = new GalleryMenus();

    // ── animal: rotate, flip; then the animal, through a second level ────
    public record AnimalMenu() implements ContextMenuKind<AnimalMenu> {
        public static final AnimalMenu INSTANCE = new AnimalMenu();
        @Override public List<? extends M1_Node<AnimalMenu, ?>> children() { return List.of(Rotate.INSTANCE, Flip.INSTANCE, Animal.INSTANCE); }

        public record Rotate() implements M1_Node<AnimalMenu, Rotate> {
            public static final Rotate INSTANCE = new Rotate();
            @Override public AnimalMenu parent() { return AnimalMenu.INSTANCE; }
            @Override public String label() { return "Rotate"; }
            @Override public Class<? extends Icon> icon() { return Icon.Rotate.class; }
            @Override public String hint() { return "a quarter turn"; }
        }
        public record Flip() implements M1_Node<AnimalMenu, Flip> {
            public static final Flip INSTANCE = new Flip();
            @Override public AnimalMenu parent() { return AnimalMenu.INSTANCE; }
            @Override public String label() { return "Flip"; }
            @Override public Class<? extends Icon> icon() { return Icon.Flip.class; }
            @Override public String hint() { return "mirror it"; }
        }
        public record Animal() implements M1_Node<AnimalMenu, Animal> {
            public static final Animal INSTANCE = new Animal();
            @Override public AnimalMenu parent() { return AnimalMenu.INSTANCE; }
            @Override public String label() { return "Animal"; }
            @Override public int section() { return 1; }
            @Override public List<? extends M2_Node<Animal, ?>> children() { return List.of(Cat.INSTANCE, Dog.INSTANCE, Owl.INSTANCE, Fox.INSTANCE); }

            public record Cat() implements M2_Node<Animal, Cat> {
                public static final Cat INSTANCE = new Cat();
                @Override public Animal parent() { return Animal.INSTANCE; }
                @Override public String label() { return "Cat"; }
            }
            public record Dog() implements M2_Node<Animal, Dog> {
                public static final Dog INSTANCE = new Dog();
                @Override public Animal parent() { return Animal.INSTANCE; }
                @Override public String label() { return "Dog"; }
            }
            public record Owl() implements M2_Node<Animal, Owl> {
                public static final Owl INSTANCE = new Owl();
                @Override public Animal parent() { return Animal.INSTANCE; }
                @Override public String label() { return "Owl"; }
            }
            public record Fox() implements M2_Node<Animal, Fox> {
                public static final Fox INSTANCE = new Fox();
                @Override public Animal parent() { return Animal.INSTANCE; }
                @Override public String label() { return "Fox"; }
            }
        }
    }

    // ── swatch: the colour through a second level; then the inverted toggle ─
    public record SwatchMenu() implements ContextMenuKind<SwatchMenu> {
        public static final SwatchMenu INSTANCE = new SwatchMenu();
        @Override public List<? extends M1_Node<SwatchMenu, ?>> children() { return List.of(Colour.INSTANCE, Invert.INSTANCE); }

        public record Colour() implements M1_Node<SwatchMenu, Colour> {
            public static final Colour INSTANCE = new Colour();
            @Override public SwatchMenu parent() { return SwatchMenu.INSTANCE; }
            @Override public String label() { return "Colour"; }
            @Override public List<? extends M2_Node<Colour, ?>> children() { return List.of(Primary.INSTANCE, Success.INSTANCE, Warning.INSTANCE, Danger.INSTANCE); }

            public record Primary() implements M2_Node<Colour, Primary> {
                public static final Primary INSTANCE = new Primary();
                @Override public Colour parent() { return Colour.INSTANCE; }
                @Override public String label() { return "Primary"; }
            }
            public record Success() implements M2_Node<Colour, Success> {
                public static final Success INSTANCE = new Success();
                @Override public Colour parent() { return Colour.INSTANCE; }
                @Override public String label() { return "Success"; }
            }
            public record Warning() implements M2_Node<Colour, Warning> {
                public static final Warning INSTANCE = new Warning();
                @Override public Colour parent() { return Colour.INSTANCE; }
                @Override public String label() { return "Warning"; }
            }
            public record Danger() implements M2_Node<Colour, Danger> {
                public static final Danger INSTANCE = new Danger();
                @Override public Colour parent() { return Colour.INSTANCE; }
                @Override public String label() { return "Danger"; }
            }
        }
        public record Invert() implements M1_Node<SwatchMenu, Invert> {
            public static final Invert INSTANCE = new Invert();
            @Override public SwatchMenu parent() { return SwatchMenu.INSTANCE; }
            @Override public String label() { return "Inverted"; }
            @Override public String hint() { return "the design's inverted surface"; }
            @Override public int section() { return 1; }
        }
    }

    // ── counter: add, reset; then the step, through a second level ───────
    public record CounterMenu() implements ContextMenuKind<CounterMenu> {
        public static final CounterMenu INSTANCE = new CounterMenu();
        @Override public List<? extends M1_Node<CounterMenu, ?>> children() { return List.of(Add.INSTANCE, Reset.INSTANCE, Step.INSTANCE); }

        public record Add() implements M1_Node<CounterMenu, Add> {
            public static final Add INSTANCE = new Add();
            @Override public CounterMenu parent() { return CounterMenu.INSTANCE; }
            @Override public String label() { return "Add a step"; }
            @Override public Class<? extends Icon> icon() { return Icon.Add.class; }
        }
        public record Reset() implements M1_Node<CounterMenu, Reset> {
            public static final Reset INSTANCE = new Reset();
            @Override public CounterMenu parent() { return CounterMenu.INSTANCE; }
            @Override public String label() { return "Reset"; }
            @Override public Class<? extends Icon> icon() { return Icon.Reset.class; }
            @Override public String hint() { return "back to nought"; }
        }
        public record Step() implements M1_Node<CounterMenu, Step> {
            public static final Step INSTANCE = new Step();
            @Override public CounterMenu parent() { return CounterMenu.INSTANCE; }
            @Override public String label() { return "Step"; }
            @Override public Class<? extends Icon> icon() { return Icon.Settings.class; }
            @Override public int section() { return 1; }
            @Override public List<? extends M2_Node<Step, ?>> children() { return List.of(S1.INSTANCE, S5.INSTANCE, S10.INSTANCE); }

            public record S1() implements M2_Node<Step, S1> {
                public static final S1 INSTANCE = new S1();
                @Override public Step parent() { return Step.INSTANCE; }
                @Override public String label() { return "1"; }
            }
            public record S5() implements M2_Node<Step, S5> {
                public static final S5 INSTANCE = new S5();
                @Override public Step parent() { return Step.INSTANCE; }
                @Override public String label() { return "5"; }
            }
            public record S10() implements M2_Node<Step, S10> {
                public static final S10 INSTANCE = new S10();
                @Override public Step parent() { return Step.INSTANCE; }
                @Override public String label() { return "10"; }
            }
        }
    }

    /** Derived: the kinds the gallery's catalogued components need — the context menus widget names all three — not listed here. */
    public static final ContextMenuRegistry REGISTRY = ContextMenuRegistry.requiredBy(List.of(GalleryCrate.INSTANCE));

    @Override public ImportsFor<GalleryMenus> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<GalleryMenus> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new MENUS()));
    }

    @Override
    public List<String> selfContent(ModuleNameResolver resolver) { return REGISTRY.js(); }
}
