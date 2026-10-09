package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.taxonomy.Component;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.Objects;

/**
 * A leaf of the house's taxonomy seen in action: the leaf, and the demo that shows it - a widget
 * of this module, made {@code new X(container, { leaf })}. One demo may show several leaves: the
 * six buttons are one button, each in its own colour.
 *
 * @param leaf       the house's leaf it shows
 * @param constructs the one class that shows it, and the module that exports it
 */
public record Demo(Component<?> leaf, ModuleImports<?> constructs) {

    public Demo {
        Objects.requireNonNull(leaf, "Demo.leaf");
        Objects.requireNonNull(constructs, "Demo.constructs");
        if (constructs.allImports().size() != 1)
            throw new IllegalArgumentException(leaf.getClass().getSimpleName() + ": a demo is one class, not " + constructs.allImports().size());
    }

    /** The class's name, as the page has it. */
    public String className() { return constructs.allImports().get(0).getClass().getSimpleName(); }

    /** The module that exports it - loaded by the page when the leaf is first shown, never before. */
    public EsModule<?> module() {
        if (!(constructs.from() instanceof EsModule<?> m))
            throw new IllegalStateException(leaf.getClass().getSimpleName() + ": a demo's class comes from a module, not " + constructs.from());
        return m;
    }
}
