package hue.captains.singapura.js.homing.demo.workspacewidgets.animals;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * What an animal choice party carries: which animal the widgets that meet in it
 * show. A member does - {@link Choose}, {@link CurrentRequested}; the party says -
 * {@link Chosen}. An animal travels by its id ({@code Animals}).
 */
public sealed interface AnimalChoice {

    /** A member chose this animal. */
    record Choose(String animal) implements AnimalChoice {}

    /** A member asks which is chosen - one that joins late - and is answered alone, when one is. */
    record CurrentRequested() implements AnimalChoice {}

    /** The party says: this animal is chosen. */
    record Chosen(String animal) implements AnimalChoice {}

    /**
     * The type: {@code animal-choice}, its identity on a page - its constant
     * served as {@code ANIMAL_CHOICE}, and a root instance's secretary, unless a
     * workspace puts its own, {@code AnimalChoiceSecretary}.
     */
    PartyType<AnimalChoice> TYPE = new PartyType<>("animal-choice", AnimalChoice.class)
            .servedFrom(new ModuleImports<>(List.of(new AnimalChoiceModule.ANIMAL_CHOICE()), AnimalChoiceModule.INSTANCE))
            .withSecretary(new ModuleImports<>(List.of(new AnimalChoiceSecretaryModule.AnimalChoiceSecretary()), AnimalChoiceSecretaryModule.INSTANCE));
}
