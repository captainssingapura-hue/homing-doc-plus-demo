package hue.captains.singapura.js.homing.demo.workspacewidgets.animals;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;
import java.util.Set;

import static hue.captains.singapura.js.homing.design.Box.Control;
import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Interaction.Selectable;
import static hue.captains.singapura.js.homing.design.Target.Affordance;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Motion;
import static hue.captains.singapura.js.homing.design.Target.Shape;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Caption;
import static hue.captains.singapura.js.homing.design.Text.Heading;

/**
 * The animal selector's sheet, in the design's words and nothing of its own:
 * the bodies lay out, the designs colour, set and shape. The animals are a
 * wrapping grid of tabs-as-the-design-draws-them, each its picture over its
 * name; the one chosen says so by aria-selected, and the design draws it.
 */
public record AnimalStyles() implements CssGroup<AnimalStyles> {

    public static final AnimalStyles INSTANCE = new AnimalStyles();

    /** The selector: its head, then the animals - one column, filling its root. */
    public record an_root() implements CssClass<AnimalStyles> {
        @Override public String body() { return """
            gap: 8px;
            padding: 12px 16px;
            """;
        }
    }

    /** "Animal", in the design's heading voice. */
    public record an_title() implements CssClass<AnimalStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Heading.class, Type.Face.class), of(Heading.class, Type.Scale.class), of(Heading.class, Type.Weight.class), of(Heading.class, Color.Ink.class)); }
        @Override public String body() { return """
            flex: 0 0 auto;
            margin: 0;
            """;
        }
    }

    /** Which is chosen, in words, quietly. */
    public record an_note() implements CssClass<AnimalStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return """
            flex: 0 0 auto;
            margin: 0;
            """;
        }
    }

    /** The animals: as many to a row as the width holds, the rest scrolling. */
    public record an_list() implements CssClass<AnimalStyles> {
        @Override public String body() { return """
            flex: 1 1 auto;
            min-height: 0;
            overflow-y: auto;
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(76px, 1fr));
            align-content: start;
            gap: 6px;
            """;
        }
    }

    /**
     * An animal: a tab, as the design draws one - shaped as a tab, coloured as a
     * selectable at rest, its ring a control's; the one chosen by aria-selected,
     * drawn by the design's selected state. Its picture over its name.
     */
    public record an_option() implements CssClass<AnimalStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Control.Tab.class, Shape.Rule.class), of(Control.Tab.class, Shape.Corner.class), of(Control.class, Color.Edge.class),
                           of(Selectable.Tab.class, Color.Surface.class), of(Selectable.Tab.class, Color.Ink.class), of(Selectable.Tab.class, Color.Edge.class),
                           of(Selectable.class, Motion.Ease.class), of(Selectable.class, Affordance.Cursor.class),
                           of(Caption.class, Type.Scale.class));
        }
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 4px;
            padding: 8px 4px 6px;
            font-family: inherit;
            min-width: 0;
            """;
        }
    }

    /** The animal's picture: its art, handed in as {@code --an-art}, contained in a square. */
    public record an_picture() implements CssClass<AnimalStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--an-art")); }
        @Override public String body() { return """
            width: 44px;
            height: 44px;
            background: var(--an-art) center / contain no-repeat;
            """;
        }
    }

    /** The animal's name, on one line. */
    public record an_name() implements CssClass<AnimalStyles> {
        @Override public String body() { return """
            max-width: 100%;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            """;
        }
    }

    @Override
    public List<CssClass<AnimalStyles>> cssClasses() {
        return List.of(new an_root(), new an_title(), new an_note(), new an_list(), new an_option(), new an_picture(), new an_name());
    }
}
