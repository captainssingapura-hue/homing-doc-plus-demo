package hue.captains.singapura.js.homing.conformance.ext;

import hue.captains.singapura.js.homing.core.JsModuleType;

/**
 * RFC 0044 extension demo — a <b>hypothetical downstream</b> JS module type.
 *
 * <p>Imagine a component library that ships browser games. It has a role the
 * framework's {@code StandardJsModuleType} set does not know about: the
 * per-frame animation driver — a <b>game loop</b>. Because {@link JsModuleType}
 * is an <i>open interface</i>, the library does not fork the framework or edit an
 * enum; it just implements the interface. The type co-exists with the library's
 * own code.</p>
 *
 * <p>This is the <b>unsealed extension branch</b> of the type system: framework
 * code keeps its exhaustive {@code switch} over the sealed {@code
 * StandardJsModuleType}; this downstream type dispatches instead through the
 * {@code CompositeJsRulePolicy} dictionary. See {@link GameLoopConformance} for
 * how it is registered with a rule set.</p>
 */
public enum GameLoopModuleType implements JsModuleType {

    /** A per-frame animation driver — animated on the compositor clock. */
    GAME_LOOP;

    @Override public String slug()  { return "game-loop"; }
    @Override public String label() { return "Game loop"; }
}
