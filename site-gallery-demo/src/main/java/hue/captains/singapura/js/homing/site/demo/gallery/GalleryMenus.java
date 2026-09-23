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
 * and toggle, the counter's disabled reset and checked step; and a fourth,
 * the split menu a dock's tab bar opens, whose rows are about the room the
 * dock sits in. A row's icon is
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

    // ── split: where a dock sits — part the room, or give it back ────────
    /**
     * The menu a right-click on a dock's tab bar opens, on the ground the
     * chips leave. What it offers is about the room the dock sits in, not
     * about the dock: the page placed it, so the page says this. The object
     * bound is {@code { pane }} — the dock the strip belongs to; the page
     * finds the region it is in and parts, merges or closes that.
     *
     * <p>The four directions are one row each and the page hides the ones
     * that have nowhere to go: a region can be merged only into a pane
     * across a splitter of its own, and it has at most two of those. A
     * region further off is named instead, through {@code Merge into…},
     * which asks with the same list the switcher uses — the menu's rows are
     * fixed at build, so a list of regions cannot be rows.</p>
     */
    public record SplitMenu() implements ContextMenuKind<SplitMenu> {
        public static final SplitMenu INSTANCE = new SplitMenu();
        @Override public List<? extends M1_Node<SplitMenu, ?>> children() {
            return List.of(Beside.INSTANCE, Below.INSTANCE,
                           MergeLeft.INSTANCE, MergeRight.INSTANCE, MergeUp.INSTANCE, MergeDown.INSTANCE, MergeInto.INSTANCE,
                           Close.INSTANCE);
        }

        public record Beside() implements M1_Node<SplitMenu, Beside> {
            public static final Beside INSTANCE = new Beside();
            @Override public SplitMenu parent() { return SplitMenu.INSTANCE; }
            @Override public String label() { return "Split beside"; }
            @Override public Class<? extends Icon> icon() { return Icon.Column.class; }
            @Override public String hint() { return "a region of its own, to the right"; }
        }
        public record Below() implements M1_Node<SplitMenu, Below> {
            public static final Below INSTANCE = new Below();
            @Override public SplitMenu parent() { return SplitMenu.INSTANCE; }
            @Override public String label() { return "Split below"; }
            @Override public Class<? extends Icon> icon() { return Icon.Row.class; }
            @Override public String hint() { return "a region of its own, underneath"; }
        }
        /** The four ways a region can be merged: the page hides the ones with no pane across a splitter of their own. */
        public record MergeLeft() implements M1_Node<SplitMenu, MergeLeft> {
            public static final MergeLeft INSTANCE = new MergeLeft();
            @Override public SplitMenu parent() { return SplitMenu.INSTANCE; }
            @Override public String label() { return "Merge left"; }
            @Override public Class<? extends Icon> icon() { return Icon.Merge.class; }
            @Override public int section() { return 1; }
            @Override public String hint() { return "its tabs and its room to the pane beside it"; }
        }
        public record MergeRight() implements M1_Node<SplitMenu, MergeRight> {
            public static final MergeRight INSTANCE = new MergeRight();
            @Override public SplitMenu parent() { return SplitMenu.INSTANCE; }
            @Override public String label() { return "Merge right"; }
            @Override public Class<? extends Icon> icon() { return Icon.Merge.class; }
            @Override public int section() { return 1; }
            @Override public String hint() { return "its tabs and its room to the pane beside it"; }
        }
        public record MergeUp() implements M1_Node<SplitMenu, MergeUp> {
            public static final MergeUp INSTANCE = new MergeUp();
            @Override public SplitMenu parent() { return SplitMenu.INSTANCE; }
            @Override public String label() { return "Merge up"; }
            @Override public Class<? extends Icon> icon() { return Icon.Merge.class; }
            @Override public int section() { return 1; }
            @Override public String hint() { return "its tabs and its room to the pane above it"; }
        }
        public record MergeDown() implements M1_Node<SplitMenu, MergeDown> {
            public static final MergeDown INSTANCE = new MergeDown();
            @Override public SplitMenu parent() { return SplitMenu.INSTANCE; }
            @Override public String label() { return "Merge down"; }
            @Override public Class<? extends Icon> icon() { return Icon.Merge.class; }
            @Override public int section() { return 1; }
            @Override public String hint() { return "its tabs and its room to the pane under it"; }
        }
        /** A region further off, named from the same list the switcher uses: its tabs go there, its room to the neighbour. */
        public record MergeInto() implements M1_Node<SplitMenu, MergeInto> {
            public static final MergeInto INSTANCE = new MergeInto();
            @Override public SplitMenu parent() { return SplitMenu.INSTANCE; }
            @Override public String label() { return "Merge into…"; }
            @Override public Class<? extends Icon> icon() { return Icon.Merge.class; }
            @Override public int section() { return 1; }
            @Override public String hint() { return "choose the region its tabs go to"; }
        }
        public record Close() implements M1_Node<SplitMenu, Close> {
            public static final Close INSTANCE = new Close();
            @Override public SplitMenu parent() { return SplitMenu.INSTANCE; }
            @Override public String label() { return "Close this region"; }
            @Override public Class<? extends Icon> icon() { return Icon.Close.class; }
            @Override public int section() { return 2; }
            @Override public String hint() { return "its tabs go where its room goes"; }
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
