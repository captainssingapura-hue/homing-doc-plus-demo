package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.component.taxonomy.Component;

import java.util.Objects;

/**
 * A leaf of the house shown by the page around the demos rather than by one of its own: the
 * workspace the demos stand in, the page's chrome, the preferences it opens - each a house
 * component in action already, and where on that page it is.
 *
 * @param leaf  the house's leaf
 * @param where where on the page around it the user finds it, said to them
 */
public record ShownAround(Component<?> leaf, String where) {

    public ShownAround {
        Objects.requireNonNull(leaf, "ShownAround.leaf");
        if (where == null || where.isBlank()) throw new IllegalArgumentException(leaf.getClass().getSimpleName() + ": say where it is shown");
    }
}
