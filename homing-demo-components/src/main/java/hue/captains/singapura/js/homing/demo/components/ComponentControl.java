package hue.captains.singapura.js.homing.demo.components;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * What a component-control party carries: the options of the house's control catalogue, set and
 * done - one vocabulary for all of them, an option named by its name in the catalogue, sent by
 * what its means carries. A member does - {@link SetDegree}, {@link SetSwitch}, {@link Invoke},
 * {@link StateRequested}, {@link Reset}; the party says - {@link DegreeSet}, {@link SwitchSet},
 * {@link Invoked} to every member, {@link State} to one that asks and to all on a reset. What is
 * set is kept by option, not by component: a size set on one is the size of the next.
 */
public sealed interface ComponentControl {

    /** A degree set: an option controlled by degree, to a number from −1 to 1. */
    record SetDegree(String option, double value) implements ComponentControl {}

    /** A switch switched: an option controlled by switching, on or off. */
    record SetSwitch(String option, boolean on) implements ComponentControl {}

    /** An option done, or asked: one controlled by acting, or by asking. */
    record Invoke(String option) implements ComponentControl {}

    /** A member asks what is set - one that joins late - and is answered alone. */
    record StateRequested() implements ComponentControl {}

    /** Everything back to rest. */
    record Reset() implements ComponentControl {}

    /** The party says: everything set - an option not in it is at its rest. */
    record State(List<Degree> degrees, List<Switch> switches) implements ComponentControl {}

    /** The party says: a degree is set. */
    record DegreeSet(String option, double value) implements ComponentControl {}

    /** The party says: a switch is switched. */
    record SwitchSet(String option, boolean on) implements ComponentControl {}

    /** The party says: an option is done, or asked. */
    record Invoked(String option) implements ComponentControl {}

    /** A degree, as {@link State} holds it. */
    record Degree(String option, double value) {}

    /** A switch, as {@link State} holds it. */
    record Switch(String option, boolean on) {}

    /** The type: {@code component-control}, served as {@code COMPONENT_CONTROL}; its secretary {@code ComponentControlSecretary}. */
    PartyType<ComponentControl> TYPE = new PartyType<>("component-control", ComponentControl.class)
            .servedFrom(new ModuleImports<>(List.of(new ComponentControlModule.COMPONENT_CONTROL()), ComponentControlModule.INSTANCE))
            .withSecretary(new ModuleImports<>(List.of(new ComponentControlSecretaryModule.ComponentControlSecretary()), ComponentControlSecretaryModule.INSTANCE));
}
